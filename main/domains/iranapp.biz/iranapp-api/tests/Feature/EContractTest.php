<?php

namespace Tests\Feature;

use App\Http\Controllers\EContractController;
use App\Models\Admin;
use App\Models\EContract;
use App\Models\User;
use App\Support\EContractTemplate;
use App\Support\JalaliDate;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Electronic contracts: submitted from the app, approved or rejected (with a reason) in the admin
 * panel. Approval is what turns a regular user into a pro user.
 */
class EContractTest extends TestCase
{
    use RefreshDatabase;

    private function makeUser(string $mobile = '09120000000'): User
    {
        return User::create([
            'first_name' => 'تست',
            'last_name' => 'کاربر',
            'mobile' => $mobile,
            'password' => Hash::make('secret123'),
            'is_mobile_verified' => 1,
            'type' => 'user',
        ]);
    }

    private function makeAdmin(): Admin
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        return $admin;
    }

    private function validPayload(array $overrides = []): array
    {
        return array_merge([
            'business_type' => 'store',
            'business_name' => 'پوشاک آریا',
            'manager_title' => 'mr',
            'manager_name' => 'علی رضایی',
            'national_code' => '0012345679',
            'phone' => '03132123456',
            'mobile' => '09131234567',
            'address' => 'اصفهان، خیابان چهارباغ، پلاک 12',
            'subject' => 'پوشاک مردانه',
            'start_date' => JalaliDate::today(),
            'duration_months' => 12,
            'discount_percent' => 15,
            'terms_accepted' => '1',
            'app_version' => '1.0.104',
        ], $overrides);
    }

    private function submit(User $user, array $payload)
    {
        $token = $user->createToken('mobile')->plainTextToken;

        return $this->postJson('/api/e-contracts?token=' . $token, $payload);
    }

    public function test_new_users_are_regular_users(): void
    {
        $user = $this->makeUser()->fresh();

        $this->assertSame(User::ROLE_NORMAL, $user->role);
        $this->assertFalse($user->isPro());
    }

    public function test_current_returns_template_and_no_contract_for_a_new_user(): void
    {
        $user = $this->makeUser();
        $token = $user->createToken('mobile')->plainTextToken;

        $this->getJson('/api/e-contracts/current?token=' . $token)
            ->assertOk()
            ->assertJsonPath('status', 200)
            ->assertJsonPath('role', 'normal')
            ->assertJsonPath('contract', null)
            ->assertJsonPath('today', JalaliDate::today())
            ->assertJsonPath('template.version', EContractTemplate::VERSION)
            ->assertJsonCount(count(EContractTemplate::sections()), 'template.sections');
    }

    public function test_current_requires_login(): void
    {
        $this->getJson('/api/e-contracts/current')->assertJsonPath('error', 'token_expired');
    }

    public function test_a_valid_submission_is_stored_as_pending_with_the_full_text(): void
    {
        $user = $this->makeUser();

        $this->submit($user, $this->validPayload())
            ->assertOk()
            ->assertJsonPath('status', 201)
            ->assertJsonPath('contract.status', 'pending');

        $contract = EContract::sole();
        $start = JalaliDate::parse(JalaliDate::today());
        $this->assertSame($user->id, $contract->user_id);
        $this->assertSame(JalaliDate::today(), $contract->contract_date);
        $this->assertSame(JalaliDate::format(...JalaliDate::addMonths($start, 12)), $contract->end_date);
        $this->assertStringContainsString('فروشگاه پوشاک آریا', $contract->contract_text);
        $this->assertStringContainsString('به مدیریت آقای علی رضایی', $contract->contract_text);
        $this->assertStringContainsString('به میزان 15 درصد', $contract->contract_text);
        $this->assertStringNotContainsString('{', $contract->contract_text);
        $this->assertNotNull($contract->terms_accepted_at);
        $this->assertSame('1.0.104', $contract->app_version);
        $this->assertFalse($user->fresh()->isPro());
    }

    public function test_persian_digits_are_accepted(): void
    {
        $user = $this->makeUser();
        $payload = $this->validPayload([
            'national_code' => '۰۰۱۲۳۴۵۶۷۹',
            'mobile' => '۰۹۱۳۱۲۳۴۵۶۷',
            'start_date' => \App\Libraries\jdf::tr_num(JalaliDate::today(), 'fa'),
        ]);

        $this->submit($user, $payload)->assertJsonPath('status', 201);
        $this->assertSame('0012345679', EContract::sole()->national_code);
    }

    public function test_submission_without_accepting_the_terms_is_refused(): void
    {
        $user = $this->makeUser();

        $this->submit($user, $this->validPayload(['terms_accepted' => '0']))
            ->assertJsonPath('status', 422);
        $this->assertSame(0, EContract::count());
    }

    public function test_invalid_fields_are_reported_in_persian(): void
    {
        $user = $this->makeUser();

        $response = $this->submit($user, $this->validPayload([
            'national_code' => '1234567890',
            'mobile' => '0913',
            'discount_percent' => 0,
            'start_date' => '1400/13/40',
        ]))->assertJsonPath('status', 422);

        $errors = $response->json('errors');
        $this->assertContains('کد ملی وارد شده معتبر نیست.', $errors);
        $this->assertContains('شماره همراه باید 11 رقم و با 09 شروع شود.', $errors);
        $this->assertContains('درصد تخفیف باید بین 1 تا 100 باشد.', $errors);
        $this->assertSame(0, EContract::count());
    }

    public function test_start_date_in_the_past_is_refused(): void
    {
        $user = $this->makeUser();

        $this->submit($user, $this->validPayload(['start_date' => '1400/01/01']))
            ->assertJsonPath('status', 422)
            ->assertJsonPath('errors.0', 'تاریخ شروع قرارداد نمی تواند پیش از امروز باشد.');
    }

    public function test_a_second_submission_while_pending_is_refused(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload())->assertJsonPath('status', 201);

        $this->submit($user, $this->validPayload())
            ->assertJsonPath('status', 409)
            ->assertJsonPath('error', 'contract_pending');
        $this->assertSame(1, EContract::count());
    }

    public function test_approval_makes_the_user_pro(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload());
        $contract = EContract::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->put(route('approveEContract', $contract->id))
            ->assertRedirect(route('showEContractInAdminPanel', $contract->id));

        $contract->refresh();
        $this->assertSame('approved', $contract->status);
        $this->assertSame($admin->id, $contract->reviewed_by);
        $this->assertNotNull($contract->reviewed_at);

        $user->refresh();
        $this->assertTrue($user->isPro());
        $this->assertNotNull($user->pro_since);

        // The app sees the new role, and cannot submit again. (Forgetting the guards drops the
        // user Sanctum cached during the earlier request, as a real new request would.)
        $this->app['auth']->forgetGuards();
        $token = $user->createToken('mobile')->plainTextToken;
        $this->getJson('/api/e-contracts/current?token=' . $token)
            ->assertJsonPath('role', 'pro')
            ->assertJsonPath('contract.status', 'approved');
        $this->submit($user, $this->validPayload())->assertJsonPath('error', 'contract_approved');
    }

    public function test_rejection_requires_a_reason(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload());
        $contract = EContract::sole();

        $this->actingAs($this->makeAdmin(), 'admin')
            ->from(route('showEContractInAdminPanel', $contract->id))
            ->put(route('rejectEContract', $contract->id), ['rejection_reason' => ''])
            ->assertSessionHasErrors('rejection_reason');

        $this->assertSame('pending', $contract->fresh()->status);
    }

    public function test_rejected_user_sees_the_reason_and_can_resubmit(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload());
        $contract = EContract::sole();

        $this->actingAs($this->makeAdmin(), 'admin')
            ->put(route('rejectEContract', $contract->id), ['rejection_reason' => 'کد ملی با نام مدیر مطابقت ندارد.']);

        $this->assertSame('rejected', $contract->fresh()->status);
        $this->assertFalse($user->fresh()->isPro());

        $token = $user->createToken('mobile')->plainTextToken;
        $this->getJson('/api/e-contracts/current?token=' . $token)
            ->assertJsonPath('contract.status', 'rejected')
            ->assertJsonPath('contract.rejection_reason', 'کد ملی با نام مدیر مطابقت ندارد.')
            ->assertJsonPath('contract.business_name', 'پوشاک آریا');

        $this->submit($user, $this->validPayload(['manager_name' => 'علی رضایی نژاد']))
            ->assertJsonPath('status', 201);
        $this->assertSame(2, EContract::count());
        $this->assertSame('rejected', $contract->fresh()->status);
    }

    public function test_a_reviewed_contract_cannot_be_reviewed_again(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload());
        $contract = EContract::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')->put(route('rejectEContract', $contract->id), ['rejection_reason' => 'اطلاعات ناقص است.']);
        $this->actingAs($admin, 'admin')->put(route('approveEContract', $contract->id))
            ->assertSessionHas('error_msg');

        $this->assertSame('rejected', $contract->fresh()->status);
        $this->assertFalse($user->fresh()->isPro());
    }

    public function test_admin_pages_render(): void
    {
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload(['business_name' => 'پوشاک <b>آریا</b>']));
        $contract = EContract::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->get(route('showEContractsInAdminPanel'))
            ->assertOk()
            ->assertSee('قراردادهای الکترونیک')
            ->assertSee('بررسی قرارداد');

        $this->actingAs($admin, 'admin')
            ->get(route('showEContractInAdminPanel', $contract->id))
            ->assertOk()
            ->assertSee('تایید و ثبت قرارداد')
            ->assertSee('رد قرارداد')
            // User input is escaped, not rendered as markup.
            ->assertSee('پوشاک &lt;b&gt;آریا&lt;/b&gt;', false)
            ->assertDontSee('پوشاک <b>آریا</b>', false);

        $this->actingAs($admin, 'admin')->get(route('showUsersListInAdminPanel'))
            ->assertOk()
            ->assertSee('کاربر عادی');
    }

    public function test_admin_pages_are_closed_to_guests(): void
    {
        $this->get(route('showEContractsInAdminPanel'))->assertRedirect(route('showAdminLoginForm'));
    }

    /**
     * Regression: formatting one timestamp once changed PHP's default timezone (via jdf::jdate),
     * so the next one read in the same request was shown 3.5 hours off.
     */
    public function test_submission_and_review_times_are_both_shown_in_tehran_time(): void
    {
        \Carbon\Carbon::setTestNow(\Carbon\Carbon::parse('2026-09-26 09:00:00', 'UTC'));
        $user = $this->makeUser();
        $this->submit($user, $this->validPayload());
        $contract = EContract::sole();

        \Carbon\Carbon::setTestNow(\Carbon\Carbon::parse('2026-09-26 09:30:00', 'UTC'));
        $this->actingAs($this->makeAdmin(), 'admin')->put(route('approveEContract', $contract->id));

        $this->app['auth']->forgetGuards();
        $token = $user->createToken('mobile')->plainTextToken;
        $this->getJson('/api/e-contracts/current?token=' . $token)
            ->assertJsonPath('contract.submitted_at', '1405/07/04 - 12:30')
            ->assertJsonPath('contract.reviewed_at', '1405/07/04 - 13:00');

        \Carbon\Carbon::setTestNow();
    }

    public function test_national_code_checksum(): void
    {
        $this->assertTrue(EContractController::isValidNationalCode('0012345679'));
        $this->assertFalse(EContractController::isValidNationalCode('0012345678'));
        $this->assertFalse(EContractController::isValidNationalCode('1111111111'));
        $this->assertFalse(EContractController::isValidNationalCode('123'));
    }

    public function test_jalali_month_arithmetic_clamps_the_day(): void
    {
        $this->assertSame([1405, 7, 30], JalaliDate::addMonths([1405, 6, 31], 1));
        $this->assertSame([1406, 7, 4], JalaliDate::addMonths([1405, 7, 4], 12));
        $this->assertSame([1406, 1, 15], JalaliDate::addMonths([1405, 10, 15], 3));
        $this->assertNull(JalaliDate::parse('1405/07/31'));
        $this->assertSame([1405, 7, 4], JalaliDate::parse('۱۴۰۵/۷/۴'));
    }
}
