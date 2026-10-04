<?php

namespace App\Http\Controllers;

use App\Jobs\SendMembershipCardReviewPushNotification;
use App\Libraries\jdf;
use App\Models\MembershipCard;
use App\Models\User;
use App\Support\JalaliDate;
use App\Support\NationalCode;
use Carbon\Carbon;
use Illuminate\Database\UniqueConstraintViolationException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

/**
 * Membership cards: the app requests one (منو > دیگر > کارت عضویت) and an admin approves or
 * rejects it in the panel. Once approved, the app shows the card.
 */
class MembershipCardController extends Controller
{
    /** Everything the app's card screen needs: the user's card (if any) and today's date. */
    public function current(Request $request)
    {
        /** @var User $user */
        $user = auth('sanctum')->user();
        $card = $user->membershipCard;

        return response()->json([
            'status' => 200,
            'title' => MembershipCard::TITLE,
            'today' => JalaliDate::today(),
            'holder' => trim($user->first_name . ' ' . $user->last_name),
            'min_members' => MembershipCard::MIN_MEMBERS,
            'max_members' => MembershipCard::MAX_MEMBERS,
            'validity_months' => MembershipCard::VALIDITY_MONTHS,
            'card' => $card ? $card->toApiArray() : null,
        ]);
    }

    /**
     * A first request, or a rejected one sent again. Validation failures come back as HTTP 200
     * with status 422 and Persian messages, as in EContractController::submit.
     */
    public function submit(Request $request)
    {
        /** @var User $user */
        $user = auth('sanctum')->user();

        // Blank rows the user added but left empty are ignored rather than rejected.
        $members = $request->input('members');
        $members = is_array($members)
            ? array_values(array_filter(array_map(fn ($m) => is_string($m) ? trim(preg_replace('/\s+/u', ' ', $m)) : $m, $members),
                fn ($m) => $m !== '' && $m !== null))
            : $members;
        $input = [
            'members' => $members,
            // Only the first member, the person the card is issued to, gives a national code.
            'national_code' => NationalCode::normalize($request->input('national_code')),
            'app_version' => $request->input('app_version'),
        ];

        $validator = Validator::make($input, [
            'members' => 'required|array|min:' . MembershipCard::MIN_MEMBERS . '|max:' . MembershipCard::MAX_MEMBERS,
            'members.*' => 'required|string|min:3|max:100',
            'national_code' => ['required', 'digits:10', function ($attribute, $value, $fail) {
                if (! NationalCode::isValid($value)) {
                    $fail('کد ملی عضو اول (صاحب کارت) معتبر نیست.');
                }
            }],
            'app_version' => 'nullable|string|max:40',
        ], [
            'national_code.required' => 'کد ملی عضو اول (صاحب کارت) را وارد کنید.',
            'national_code.digits' => 'کد ملی عضو اول (صاحب کارت) باید 10 رقم باشد.',
            'members.required' => 'نام دست کم یک عضو را وارد کنید.',
            'members.array' => 'نام اعضا نامعتبر است.',
            'members.min' => 'نام دست کم یک عضو را وارد کنید.',
            'members.max' => 'حداکثر ' . MembershipCard::MAX_MEMBERS . ' عضو را می توانید ثبت کنید.',
            'members.*.string' => 'نام اعضا نامعتبر است.',
            'members.*.min' => 'نام و نام خانوادگی عضو شماره :position را کامل وارد کنید.',
            'members.*.max' => 'نام عضو شماره :position طولانی تر از حد مجاز است.',
        ]);

        if ($validator->fails()) {
            return response()->json(['status' => 422, 'errors' => $validator->errors()->all()]);
        }

        // Two users taking the next serial at the same moment: the unique index turns one away,
        // and that one simply takes the number after.
        for ($attempt = 1; ; $attempt++) {
            try {
                return $this->store($user, $input);
            } catch (UniqueConstraintViolationException $e) {
                if ($attempt >= 3) {
                    throw $e;
                }
            }
        }
    }

    private function store(User $user, array $input)
    {
        return DB::transaction(function () use ($user, $input) {
            // Locking the user row serialises double taps on the submit button.
            User::whereKey($user->id)->lockForUpdate()->first();
            $card = MembershipCard::where('user_id', $user->id)->first();

            if ($card && $card->status === MembershipCard::STATUS_PENDING) {
                return response()->json(['status' => 409, 'error' => 'card_pending',
                    'message' => 'درخواست قبلی شما در حال بررسی است.']);
            }
            if ($card && $card->status === MembershipCard::STATUS_APPROVED) {
                return response()->json(['status' => 409, 'error' => 'card_approved',
                    'message' => 'کارت عضویت شما پیش از این تایید شده است.']);
            }

            if (! $card) {
                $card = new MembershipCard(['user_id' => $user->id]);
                $card->serial_number = MembershipCard::nextSerial();
            }
            $card->status = MembershipCard::STATUS_PENDING;
            $card->members = $input['members'];
            $card->national_code = $input['national_code'];
            $card->startMembershipToday();
            $card->submitted_at = Carbon::now();
            $card->app_version = isset($input['app_version']) ? mb_substr((string) $input['app_version'], 0, 40) : null;
            $card->save();

            return response()->json(['status' => 201, 'card' => $card->toApiArray()]);
        });
    }

    /* ----------------------------------------------------------------- admin panel */

    public function showListInAdminPanel(Request $request)
    {
        $status = $request->input('status', MembershipCard::STATUS_PENDING);

        $list = MembershipCard::with('user')->orderBy('submitted_at', 'desc');
        if (array_key_exists($status, MembershipCard::STATUS_LABELS)) {
            $list->where('status', $status);
        }
        if ($request->filled('q')) {
            $q = trim($request->input('q'));
            $serial = preg_replace('/\D/', '', jdf::tr_num($q, 'en'));
            $list->where(function ($query) use ($q, $serial) {
                $query->where('members', 'like', '%' . $q . '%')
                    ->orWhereHas('user', function ($user) use ($q) {
                        $user->where('mobile', 'like', '%' . $q . '%')
                            ->orWhere(DB::raw("CONCAT(first_name, ' ', last_name)"), 'like', '%' . $q . '%');
                    });
                if ($serial !== '') {
                    $query->orWhere('serial_number', 'like', '%' . $serial . '%')
                        ->orWhere('national_code', 'like', '%' . $serial . '%');
                }
            });
        }

        $data['list'] = $list->paginate(15)->appends($request->only(['status', 'q']));
        $data['status'] = $status;
        $data['counts'] = MembershipCard::select('status', DB::raw('count(*) as total'))
            ->groupBy('status')
            ->pluck('total', 'status');

        return view('admin.membership_cards.list')->with($data);
    }

    public function showInAdminPanel(MembershipCard $card)
    {
        $data['card'] = $card->load(['user', 'reviewer']);

        return view('admin.membership_cards.show')->with($data);
    }

    public function approve(MembershipCard $card)
    {
        $approved = DB::transaction(function () use ($card) {
            $card = MembershipCard::whereKey($card->id)->lockForUpdate()->first();
            if (! $card->isPending()) {
                return false;
            }

            $card->status = MembershipCard::STATUS_APPROVED;
            $card->rejection_reason = null;
            $card->reviewed_by = Auth::guard('admin')->id();
            $card->reviewed_at = Carbon::now();
            $card->save();

            return true;
        });

        if (! $approved) {
            return $this->alreadyReviewed($card);
        }

        SendMembershipCardReviewPushNotification::dispatchAfterResponse($card->id);

        return redirect()->route('showMembershipCardInAdminPanel', $card->id)
            ->with('success_msg', self::msg('تایید کارت عضویت', 'کارت عضویت تایید شد و در اپلیکیشن برای کاربر نمایش داده می شود.'));
    }

    public function reject(Request $request, MembershipCard $card)
    {
        $request->validate([
            'rejection_reason' => 'required|string|min:5|max:1000',
        ], [
            'rejection_reason.required' => 'نوشتن توضیح برای کاربر (مواردی که باید اصلاح شود) الزامی است.',
            'rejection_reason.min' => 'توضیح باید حداقل 5 کاراکتر باشد.',
            'rejection_reason.max' => 'توضیح طولانی تر از حد مجاز است.',
        ]);

        $rejected = DB::transaction(function () use ($card, $request) {
            $card = MembershipCard::whereKey($card->id)->lockForUpdate()->first();
            if (! $card->isPending()) {
                return false;
            }

            $card->status = MembershipCard::STATUS_REJECTED;
            $card->rejection_reason = trim($request->input('rejection_reason'));
            $card->reviewed_by = Auth::guard('admin')->id();
            $card->reviewed_at = Carbon::now();
            $card->save();

            return true;
        });

        if (! $rejected) {
            return $this->alreadyReviewed($card);
        }

        SendMembershipCardReviewPushNotification::dispatchAfterResponse($card->id);

        return redirect()->route('showMembershipCardInAdminPanel', $card->id)
            ->with('success_msg', self::msg('رد درخواست', 'درخواست رد شد و توضیح شما برای کاربر نمایش داده می شود.'));
    }

    private function alreadyReviewed(MembershipCard $card)
    {
        return redirect()->route('showMembershipCardInAdminPanel', $card->id)
            ->with('error_msg', self::msg('خطا', 'این درخواست پیش از این بررسی شده است.'));
    }

    private static function msg(string $title, string $text): \stdClass
    {
        $msg = new \stdClass();
        $msg->title = $title;
        $msg->msg = $text;

        return $msg;
    }
}
