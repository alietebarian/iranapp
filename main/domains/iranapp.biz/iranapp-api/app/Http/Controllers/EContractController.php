<?php

namespace App\Http\Controllers;

use App\Jobs\SendEContractReviewPushNotification;
use App\Libraries\jdf;
use App\Models\EContract;
use App\Models\User;
use App\Support\EContractTemplate;
use App\Support\JalaliDate;
use App\Support\NationalCode;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;

/**
 * Electronic contracts: the app submits them (منو > دیگر > قرارداد الکترونیک) and an admin
 * approves or rejects them in the panel. Approval makes the user a pro user.
 */
class EContractController extends Controller
{
    /**
     * Everything the app's contract screen needs in one call: the user's latest contract (if any),
     * the wording to display, today's date for the header, and the choices for the form.
     */
    public function current(Request $request)
    {
        /** @var User $user */
        $user = auth('sanctum')->user();
        $latest = $user->eContracts()->orderBy('id', 'desc')->first();
        $contract = $latest ? $latest->toApiArray() : null;

        // The app shows either the form (no contract yet, or a rejected one to correct), which
        // needs the wording, or a submitted contract, which shows its stored text — never both.
        // With ?compact=1 only the one needed is sent, halving the response; older app versions
        // do not ask for it and get everything.
        $showsForm = ! $latest || $latest->status === EContract::STATUS_REJECTED;
        $compact = $request->boolean('compact');
        if ($compact && $showsForm && $contract) {
            $contract['contract_text'] = null;
        }

        return response()->json([
            'status' => 200,
            'role' => $user->role,
            'today' => JalaliDate::today(),
            'contract' => $contract,
            'template' => $compact && ! $showsForm ? null : self::templateForApp(),
            'options' => [
                'business_types' => self::keyValueList(EContract::BUSINESS_TYPES),
                'manager_titles' => self::keyValueList(EContract::MANAGER_TITLES),
                'durations' => EContract::DURATIONS,
            ],
        ]);
    }

    /**
     * Stores a new submission. Validation failures come back as HTTP 200 with status 422 and a
     * list of Persian messages, because the app's Volley helper drops the body of error responses.
     */
    public function submit(Request $request)
    {
        /** @var User $user */
        $user = auth('sanctum')->user();

        $input = self::normalizeDigits($request->all());
        $validator = Validator::make($input, self::rules(), self::messages());

        $start = JalaliDate::parse($input['start_date'] ?? null);
        $validator->after(function ($validator) use ($start) {
            if (! $start) {
                return;
            }
            $startsOn = JalaliDate::toCarbon($start);
            $today = Carbon::now('Asia/Tehran')->startOfDay();
            if ($startsOn->lt($today)) {
                $validator->errors()->add('start_date', 'تاریخ شروع قرارداد نمی تواند پیش از امروز باشد.');
            } elseif ($startsOn->gt($today->copy()->addYear())) {
                $validator->errors()->add('start_date', 'تاریخ شروع قرارداد حداکثر می تواند یک سال بعد باشد.');
            }
        });

        if ($validator->fails()) {
            return response()->json(['status' => 422, 'errors' => $validator->errors()->all()]);
        }

        return DB::transaction(function () use ($user, $input, $start, $request) {
            // Locking the user row serialises double taps on the submit button.
            User::whereKey($user->id)->lockForUpdate()->first();
            $latest = $user->eContracts()->orderBy('id', 'desc')->first();
            if ($latest && $latest->status === EContract::STATUS_PENDING) {
                return response()->json(['status' => 409, 'error' => 'contract_pending',
                    'message' => 'درخواست قبلی شما در حال بررسی است.']);
            }
            if ($latest && $latest->status === EContract::STATUS_APPROVED) {
                return response()->json(['status' => 409, 'error' => 'contract_approved',
                    'message' => 'قرارداد شما پیش از این تایید شده است.']);
            }

            // The app sends the version of the wording it displayed. If an admin has edited the
            // text since, the user has not read what would be stored: send the new text back so
            // the app can show it and ask for acceptance again. (Older apps send no version.)
            $template = EContractTemplate::current();
            if (! empty($input['template_version']) && $input['template_version'] !== $template['version']) {
                return response()->json(['status' => 409, 'error' => 'template_changed',
                    'message' => 'متن قرارداد به روز شده است. لطفا متن جدید را مطالعه کنید و دوباره شرایط را بپذیرید.',
                    'template' => self::templateForApp()]);
            }

            $months = (int) $input['duration_months'];
            $end = JalaliDate::addMonths($start, $months);

            $contract = new EContract([
                'user_id' => $user->id,
                'status' => EContract::STATUS_PENDING,
                'business_type' => $input['business_type'],
                'business_name' => trim($input['business_name']),
                'manager_title' => $input['manager_title'],
                'manager_name' => trim($input['manager_name']),
                'national_code' => $input['national_code'],
                'phone' => $input['phone'],
                'mobile' => $input['mobile'],
                'address' => trim($input['address']),
                'subject' => trim($input['subject']),
                'start_date' => JalaliDate::format(...$start),
                'end_date' => JalaliDate::format(...$end),
                'duration_months' => $months,
                'starts_on' => JalaliDate::toCarbon($start)->toDateString(),
                'ends_on' => JalaliDate::toCarbon($end)->toDateString(),
                'discount_percent' => (int) $input['discount_percent'],
                'contract_date' => JalaliDate::today(),
                'template_version' => $template['version'],
                'contract_text' => '',
                // The server's clock, so the acceptance cannot be back-dated by the client.
                'terms_accepted_at' => Carbon::now(),
                'ip_address' => $request->ip(),
                'app_version' => isset($input['app_version']) ? mb_substr((string) $input['app_version'], 0, 40) : null,
                'user_agent' => $request->userAgent() ? mb_substr($request->userAgent(), 0, 255) : null,
            ]);
            $contract->contract_text = EContractTemplate::render($template, $contract->templateValues(), $contract->contract_date);
            $contract->save();

            return response()->json(['status' => 201, 'contract' => $contract->toApiArray()]);
        });
    }

    /* ----------------------------------------------------------------- admin panel */

    public function showListInAdminPanel(Request $request)
    {
        $status = $request->input('status', EContract::STATUS_PENDING);

        $list = EContract::with('user')->orderBy('id', 'desc');
        if (array_key_exists($status, EContract::STATUS_LABELS)) {
            $list->where('status', $status);
        }
        if ($request->filled('q')) {
            $q = trim($request->input('q'));
            $list->where(function ($query) use ($q) {
                $query->where('business_name', 'like', '%' . $q . '%')
                    ->orWhere('manager_name', 'like', '%' . $q . '%')
                    ->orWhere('mobile', 'like', '%' . $q . '%')
                    ->orWhere('national_code', 'like', '%' . $q . '%');
            });
        }

        $data['list'] = $list->paginate(15)->appends($request->only(['status', 'q']));
        $data['status'] = $status;
        $data['counts'] = EContract::select('status', DB::raw('count(*) as total'))
            ->groupBy('status')
            ->pluck('total', 'status');

        return view('admin.e_contracts.list')->with($data);
    }

    public function showInAdminPanel(EContract $contract)
    {
        $data['contract'] = $contract->load(['user', 'reviewer']);
        // Earlier (rejected) submissions by the same user, to compare against.
        $data['history'] = EContract::where('user_id', $contract->user_id)
            ->where('id', '!=', $contract->id)
            ->orderBy('id', 'desc')
            ->get();

        return view('admin.e_contracts.show')->with($data);
    }

    /**
     * Approves the contract free of charge. Also possible while waiting for a payment, to waive it.
     */
    public function approve(EContract $contract)
    {
        $done = $this->transition($contract, [EContract::STATUS_PENDING, EContract::STATUS_AWAITING_PAYMENT], function (EContract $contract) {
            $contract->billing = EContract::BILLING_FREE;
            $contract->amount = null;
            $contract->card_number = null;
            $contract->card_holder = null;
            $contract->payment_message = null;
            $contract->payment_rejection_reason = null;
            self::activate($contract);
        });

        return $this->afterTransition($contract, $done, 'تایید قرارداد', 'قرارداد به صورت رایگان تایید شد و کاربر به کاربر پرو ارتقا یافت.');
    }

    /**
     * Makes the contract paid: the user is shown the amount and the card to pay to, and confirms
     * the payment in the app. Can be sent again (e.g. with a corrected amount) while waiting.
     */
    public function requestPayment(Request $request, EContract $contract)
    {
        $input = self::normalizeDigits($request->all());
        $input['card_number'] = preg_replace('/[\s\-]/', '', (string) ($input['card_number'] ?? ''));
        $input['amount'] = str_replace([',', '٬', ' '], '', (string) ($input['amount'] ?? ''));

        Validator::make($input, [
            'amount' => 'required|integer|min:1000|max:10000000000',
            'card_number' => ['required', 'digits:16', function ($attribute, $value, $fail) {
                if (! self::isValidCardNumber((string) $value)) {
                    $fail('شماره کارت معتبر نیست.');
                }
            }],
            'card_holder' => 'required|string|min:3|max:100',
            'payment_message' => 'nullable|string|max:1000',
        ], [
            'amount.required' => 'مبلغ قرارداد را وارد کنید.',
            'amount.integer' => 'مبلغ باید عدد (به تومان) باشد.',
            'amount.min' => 'مبلغ حداقل 1,000 تومان است.',
            'amount.max' => 'مبلغ بیش از حد مجاز است.',
            'card_number.required' => 'شماره کارت را وارد کنید.',
            'card_number.digits' => 'شماره کارت باید 16 رقم باشد.',
            'card_holder.required' => 'نام صاحب کارت را وارد کنید.',
            'card_holder.min' => 'نام صاحب کارت کوتاه تر از حد مجاز است.',
            'card_holder.max' => 'نام صاحب کارت طولانی تر از حد مجاز است.',
            'payment_message.max' => 'پیام طولانی تر از حد مجاز است.',
        ])->validate();

        $done = $this->transition($contract, [EContract::STATUS_PENDING, EContract::STATUS_AWAITING_PAYMENT], function (EContract $contract) use ($input) {
            $contract->status = EContract::STATUS_AWAITING_PAYMENT;
            $contract->billing = EContract::BILLING_PAID;
            $contract->amount = (int) $input['amount'];
            $contract->card_number = $input['card_number'];
            $contract->card_holder = trim($input['card_holder']);
            $contract->payment_message = isset($input['payment_message']) ? trim((string) $input['payment_message']) ?: null : null;
            $contract->payment_requested_at = Carbon::now();
            $contract->payment_rejection_reason = null;
            $contract->payment_reference = null;
            $contract->payment_submitted_at = null;
        });

        return $this->afterTransition($contract, $done, 'درخواست پرداخت', 'مبلغ و شماره کارت برای کاربر ارسال شد. پس از پرداخت، کاربر آن را در برنامه تایید می کند.');
    }

    /** Final approval of a paid contract once the admin has seen the money arrive. */
    public function confirmPayment(EContract $contract)
    {
        $done = $this->transition($contract, [EContract::STATUS_PAYMENT_SUBMITTED], function (EContract $contract) {
            self::activate($contract);
        });

        return $this->afterTransition($contract, $done, 'تایید نهایی', 'پرداخت تایید شد، قرارداد ثبت شد و کاربر به کاربر پرو ارتقا یافت.');
    }

    /** The user said they paid, but the money did not arrive: back to waiting, with the reason. */
    public function rejectPayment(Request $request, EContract $contract)
    {
        $request->validate([
            'payment_rejection_reason' => 'required|string|min:5|max:1000',
        ], [
            'payment_rejection_reason.required' => 'نوشتن توضیح برای کاربر الزامی است.',
            'payment_rejection_reason.min' => 'توضیح باید حداقل 5 کاراکتر باشد.',
            'payment_rejection_reason.max' => 'توضیح طولانی تر از حد مجاز است.',
        ]);

        $done = $this->transition($contract, [EContract::STATUS_PAYMENT_SUBMITTED], function (EContract $contract) use ($request) {
            $contract->status = EContract::STATUS_AWAITING_PAYMENT;
            $contract->payment_rejection_reason = trim($request->input('payment_rejection_reason'));
        });

        return $this->afterTransition($contract, $done, 'عدم تایید پرداخت', 'کاربر مطلع شد که پرداختش تایید نشده و می تواند دوباره پرداخت و تایید کند.');
    }

    public function reject(Request $request, EContract $contract)
    {
        $request->validate([
            'rejection_reason' => 'required|string|min:5|max:1000',
        ], [
            'rejection_reason.required' => 'نوشتن دلیل رد قرارداد الزامی است.',
            'rejection_reason.min' => 'دلیل رد قرارداد باید حداقل 5 کاراکتر باشد.',
            'rejection_reason.max' => 'دلیل رد قرارداد طولانی تر از حد مجاز است.',
        ]);

        $done = $this->transition(
            $contract,
            [EContract::STATUS_PENDING, EContract::STATUS_AWAITING_PAYMENT, EContract::STATUS_PAYMENT_SUBMITTED],
            function (EContract $contract) use ($request) {
                $contract->status = EContract::STATUS_REJECTED;
                $contract->rejection_reason = trim($request->input('rejection_reason'));
            }
        );

        return $this->afterTransition($contract, $done, 'رد قرارداد', 'قرارداد رد شد و دلیل آن برای کاربر نمایش داده می شود.');
    }

    /**
     * App: the user confirms they have paid the amount the admin asked for. Answers with HTTP 200
     * and a status in the body, like the rest of this API.
     */
    public function submitPayment(Request $request)
    {
        /** @var User $user */
        $user = auth('sanctum')->user();
        $input = self::normalizeDigits($request->all());

        $validator = Validator::make($input, ['payment_reference' => 'nullable|string|max:100'], [
            'payment_reference.max' => 'شماره پیگیری طولانی تر از حد مجاز است.',
        ]);
        if ($validator->fails()) {
            return response()->json(['status' => 422, 'errors' => $validator->errors()->all()]);
        }

        $contract = DB::transaction(function () use ($user, $input) {
            $contract = $user->eContracts()->orderBy('id', 'desc')->lockForUpdate()->first();
            if (! $contract || $contract->status !== EContract::STATUS_AWAITING_PAYMENT) {
                return null;
            }
            $contract->status = EContract::STATUS_PAYMENT_SUBMITTED;
            $contract->payment_reference = isset($input['payment_reference']) ? trim((string) $input['payment_reference']) ?: null : null;
            $contract->payment_submitted_at = Carbon::now();
            $contract->save();

            return $contract;
        });

        if (! $contract) {
            return response()->json(['status' => 409, 'error' => 'not_awaiting_payment',
                'message' => 'این قرارداد در انتظار پرداخت نیست.']);
        }

        return response()->json(['status' => 200, 'contract' => $contract->toApiArray()]);
    }

    /** Admin panel: users with an approved contract, free or paid. */
    public function showActiveInAdminPanel(Request $request)
    {
        $billing = $request->input('billing', EContract::BILLING_FREE) === EContract::BILLING_PAID
            ? EContract::BILLING_PAID : EContract::BILLING_FREE;

        $list = EContract::with('user')
            ->where('status', EContract::STATUS_APPROVED)
            ->where('billing', $billing)
            ->orderBy('ends_on');
        if ($request->filled('q')) {
            $q = trim($request->input('q'));
            $list->where(function ($query) use ($q) {
                $query->where('business_name', 'like', '%' . $q . '%')
                    ->orWhere('manager_name', 'like', '%' . $q . '%')
                    ->orWhere('mobile', 'like', '%' . $q . '%');
            });
        }

        $counts = EContract::where('status', EContract::STATUS_APPROVED)
            ->select('billing', DB::raw('count(*) as total'), DB::raw('sum(amount) as amount_total'))
            ->groupBy('billing')
            ->get()
            ->keyBy('billing');

        return view('admin.e_contracts.active', [
            'list' => $list->paginate(20)->appends($request->only(['billing', 'q'])),
            'billing' => $billing,
            'counts' => $counts,
        ]);
    }

    /**
     * Runs $change on the contract under a row lock if its status is one of $from, and records
     * who reviewed it. Returns false if someone else moved it on in the meantime.
     */
    private function transition(EContract $contract, array $from, callable $change): bool
    {
        return DB::transaction(function () use ($contract, $from, $change) {
            $locked = EContract::whereKey($contract->id)->lockForUpdate()->first();
            if (! in_array($locked->status, $from, true)) {
                return false;
            }

            $change($locked);
            $locked->reviewed_by = Auth::guard('admin')->id();
            $locked->reviewed_at = Carbon::now();
            $locked->save();

            return true;
        });
    }

    /** The contract is in force: approved, and its user is pro. Runs inside transition(). */
    private static function activate(EContract $contract): void
    {
        $contract->status = EContract::STATUS_APPROVED;
        $contract->rejection_reason = null;

        $user = User::whereKey($contract->user_id)->lockForUpdate()->first();
        if (! $user->isPro()) {
            $user->role = User::ROLE_PRO;
            $user->pro_since = Carbon::now();
            $user->save();
        }
    }

    private function afterTransition(EContract $contract, bool $done, string $title, string $text)
    {
        if (! $done) {
            return redirect()->route('showEContractInAdminPanel', $contract->id)
                ->with('error_msg', self::msg('خطا', 'وضعیت این قرارداد تغییر کرده است؛ صفحه را دوباره بررسی کنید.'));
        }

        SendEContractReviewPushNotification::dispatchAfterResponse($contract->id);

        return redirect()->route('showEContractInAdminPanel', $contract->id)
            ->with('success_msg', self::msg($title, $text));
    }

    /** Luhn check; Iranian bank cards (Shetab) follow it. */
    public static function isValidCardNumber(string $number): bool
    {
        if (! preg_match('/^\d{16}$/', $number)) {
            return false;
        }
        $sum = 0;
        for ($i = 0; $i < 16; $i++) {
            $digit = (int) $number[$i];
            if ($i % 2 === 0) {
                $digit *= 2;
                if ($digit > 9) {
                    $digit -= 9;
                }
            }
            $sum += $digit;
        }

        return $sum % 10 === 0;
    }

    /* ----------------------------------------------------------------- helpers */

    private static function rules(): array
    {
        return [
            'business_type' => ['required', Rule::in(array_keys(EContract::BUSINESS_TYPES))],
            'business_name' => 'required|string|min:2|max:200',
            'manager_title' => ['required', Rule::in(array_keys(EContract::MANAGER_TITLES))],
            'manager_name' => 'required|string|min:3|max:200',
            'national_code' => ['required', 'digits:10', function ($attribute, $value, $fail) {
                if (! self::isValidNationalCode((string) $value)) {
                    $fail('کد ملی وارد شده معتبر نیست.');
                }
            }],
            'phone' => ['required', 'regex:/^0\d{9,10}$/'],
            'mobile' => ['required', 'regex:/^09\d{9}$/'],
            'address' => 'required|string|min:10|max:1000',
            'subject' => 'required|string|min:2|max:255',
            'start_date' => ['required', function ($attribute, $value, $fail) {
                if (! JalaliDate::parse($value)) {
                    $fail('تاریخ شروع قرارداد معتبر نیست؛ آن را به شکل 1405/07/01 وارد کنید.');
                }
            }],
            'duration_months' => 'required|integer|min:' . EContract::MIN_DURATION . '|max:' . EContract::MAX_DURATION,
            'discount_percent' => 'required|integer|min:1|max:100',
            'terms_accepted' => 'required|accepted',
            'app_version' => 'nullable|string|max:40',
            'template_version' => 'nullable|string|max:20',
        ];
    }

    private static function messages(): array
    {
        return [
            'business_type.required' => 'نوع کسب و کار (شرکت، موسسه، مجموعه یا فروشگاه) را انتخاب کنید.',
            'business_type.in' => 'نوع کسب و کار نامعتبر است.',
            'business_name.required' => 'نام شرکت / موسسه / مجموعه / فروشگاه را وارد کنید.',
            'business_name.min' => 'نام کسب و کار کوتاه تر از حد مجاز است.',
            'business_name.max' => 'نام کسب و کار طولانی تر از حد مجاز است.',
            'manager_title.required' => 'عنوان مدیر (آقا یا خانم) را انتخاب کنید.',
            'manager_title.in' => 'عنوان مدیر نامعتبر است.',
            'manager_name.required' => 'نام و نام خانوادگی مدیر را وارد کنید.',
            'manager_name.min' => 'نام مدیر کوتاه تر از حد مجاز است.',
            'manager_name.max' => 'نام مدیر طولانی تر از حد مجاز است.',
            'national_code.required' => 'کد ملی را وارد کنید.',
            'national_code.digits' => 'کد ملی باید 10 رقم باشد.',
            'phone.required' => 'شماره تلفن ثابت را وارد کنید.',
            'phone.regex' => 'شماره تلفن ثابت را همراه با کد شهر وارد کنید (مثلا 03132123456).',
            'mobile.required' => 'شماره همراه را وارد کنید.',
            'mobile.regex' => 'شماره همراه باید 11 رقم و با 09 شروع شود.',
            'address.required' => 'آدرس را وارد کنید.',
            'address.min' => 'آدرس را کامل تر وارد کنید.',
            'address.max' => 'آدرس طولانی تر از حد مجاز است.',
            'subject.required' => 'خدمات / محصولات موضوع قرارداد را وارد کنید.',
            'subject.min' => 'موضوع قرارداد کوتاه تر از حد مجاز است.',
            'subject.max' => 'موضوع قرارداد طولانی تر از حد مجاز است.',
            'start_date.required' => 'تاریخ شروع قرارداد را وارد کنید.',
            'duration_months.required' => 'مدت قرارداد را انتخاب کنید.',
            'duration_months.integer' => 'مدت قرارداد نامعتبر است.',
            'duration_months.min' => 'مدت قرارداد نامعتبر است.',
            'duration_months.max' => 'مدت قرارداد حداکثر ' . EContract::MAX_DURATION . ' ماه است.',
            'discount_percent.required' => 'درصد تخفیف را وارد کنید.',
            'discount_percent.integer' => 'درصد تخفیف باید عدد باشد.',
            'discount_percent.min' => 'درصد تخفیف باید بین 1 تا 100 باشد.',
            'discount_percent.max' => 'درصد تخفیف باید بین 1 تا 100 باشد.',
            'terms_accepted.required' => 'برای ارسال قرارداد باید شرایط آن را بپذیرید.',
            'terms_accepted.accepted' => 'برای ارسال قرارداد باید شرایط آن را بپذیرید.',
        ];
    }

    /** Iranian national ID checksum; see App\Support\NationalCode. */
    public static function isValidNationalCode(string $code): bool
    {
        return NationalCode::isValid($code);
    }

    /** Persian and Arabic digits to Latin in every string field. */
    private static function normalizeDigits(array $input): array
    {
        return array_map(fn ($v) => is_string($v) ? jdf::tr_num(strtr($v, [
            '٠' => '0', '١' => '1', '٢' => '2', '٣' => '3', '٤' => '4',
            '٥' => '5', '٦' => '6', '٧' => '7', '٨' => '8', '٩' => '9',
        ]), 'en') : $v, $input);
    }

    /** The current wording, in the shape the app reads. */
    private static function templateForApp(): array
    {
        $template = EContractTemplate::current();

        return [
            'version' => $template['version'],
            'title' => $template['title'],
            'blank' => EContractTemplate::BLANK,
            'sections' => $template['sections'],
        ];
    }

    private static function keyValueList(array $map): array
    {
        $list = [];
        foreach ($map as $key => $label) {
            $list[] = ['key' => $key, 'label' => $label];
        }

        return $list;
    }

    private static function msg(string $title, string $text): \stdClass
    {
        $msg = new \stdClass();
        $msg->title = $title;
        $msg->msg = $text;

        return $msg;
    }
}
