<?php

namespace App\Http\Controllers;

use App\Jobs\SendEContractReviewPushNotification;
use App\Libraries\jdf;
use App\Models\EContract;
use App\Models\User;
use App\Support\EContractTemplate;
use App\Support\JalaliDate;
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

        return response()->json([
            'status' => 200,
            'role' => $user->role,
            'today' => JalaliDate::today(),
            'contract' => $latest ? $latest->toApiArray() : null,
            'template' => [
                'version' => EContractTemplate::VERSION,
                'title' => EContractTemplate::TITLE,
                'blank' => EContractTemplate::BLANK,
                'sections' => EContractTemplate::sections(),
            ],
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
                'template_version' => EContractTemplate::VERSION,
                'contract_text' => '',
                // The server's clock, so the acceptance cannot be back-dated by the client.
                'terms_accepted_at' => Carbon::now(),
                'ip_address' => $request->ip(),
                'app_version' => isset($input['app_version']) ? mb_substr((string) $input['app_version'], 0, 40) : null,
                'user_agent' => $request->userAgent() ? mb_substr($request->userAgent(), 0, 255) : null,
            ]);
            $contract->contract_text = EContractTemplate::render($contract->templateValues(), $contract->contract_date);
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

    public function approve(EContract $contract)
    {
        $approved = DB::transaction(function () use ($contract) {
            $contract = EContract::whereKey($contract->id)->lockForUpdate()->first();
            if (! $contract->isPending()) {
                return false;
            }

            $contract->status = EContract::STATUS_APPROVED;
            $contract->rejection_reason = null;
            $contract->reviewed_by = Auth::guard('admin')->id();
            $contract->reviewed_at = Carbon::now();
            $contract->save();

            $user = User::whereKey($contract->user_id)->lockForUpdate()->first();
            if (! $user->isPro()) {
                $user->role = User::ROLE_PRO;
                $user->pro_since = Carbon::now();
                $user->save();
            }

            return true;
        });

        if (! $approved) {
            return $this->alreadyReviewed($contract);
        }

        SendEContractReviewPushNotification::dispatchAfterResponse($contract->id);

        return redirect()->route('showEContractInAdminPanel', $contract->id)
            ->with('success_msg', self::msg('تایید قرارداد', 'قرارداد تایید شد و کاربر به کاربر پرو ارتقا یافت.'));
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

        $rejected = DB::transaction(function () use ($contract, $request) {
            $contract = EContract::whereKey($contract->id)->lockForUpdate()->first();
            if (! $contract->isPending()) {
                return false;
            }

            $contract->status = EContract::STATUS_REJECTED;
            $contract->rejection_reason = trim($request->input('rejection_reason'));
            $contract->reviewed_by = Auth::guard('admin')->id();
            $contract->reviewed_at = Carbon::now();
            $contract->save();

            return true;
        });

        if (! $rejected) {
            return $this->alreadyReviewed($contract);
        }

        SendEContractReviewPushNotification::dispatchAfterResponse($contract->id);

        return redirect()->route('showEContractInAdminPanel', $contract->id)
            ->with('success_msg', self::msg('رد قرارداد', 'قرارداد رد شد و دلیل آن برای کاربر نمایش داده می شود.'));
    }

    private function alreadyReviewed(EContract $contract)
    {
        return redirect()->route('showEContractInAdminPanel', $contract->id)
            ->with('error_msg', self::msg('خطا', 'این قرارداد پیش از این بررسی شده است.'));
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

    /** Iranian national ID checksum. */
    public static function isValidNationalCode(string $code): bool
    {
        if (! preg_match('/^\d{10}$/', $code) || preg_match('/^(\d)\1{9}$/', $code)) {
            return false;
        }
        $sum = 0;
        for ($i = 0; $i < 9; $i++) {
            $sum += (int) $code[$i] * (10 - $i);
        }
        $remainder = $sum % 11;
        $check = (int) $code[9];

        return $remainder < 2 ? $check === $remainder : $check === 11 - $remainder;
    }

    /** Persian and Arabic digits to Latin in every string field. */
    private static function normalizeDigits(array $input): array
    {
        return array_map(fn ($v) => is_string($v) ? jdf::tr_num(strtr($v, [
            '٠' => '0', '١' => '1', '٢' => '2', '٣' => '3', '٤' => '4',
            '٥' => '5', '٦' => '6', '٧' => '7', '٨' => '8', '٩' => '9',
        ]), 'en') : $v, $input);
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
