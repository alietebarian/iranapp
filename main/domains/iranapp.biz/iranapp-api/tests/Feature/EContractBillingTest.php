<?php

namespace Tests\Feature;

use App\Jobs\SendEContractReviewPushNotification;
use App\Models\Admin;
use App\Models\AdminNotification;
use App\Models\EContract;
use App\Models\User;
use App\Support\EContractExpiry;
use App\Support\EContractTemplate;
use App\Support\JalaliDate;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Approving an electronic contract free, or paid: the admin sends an amount and a card number,
 * the user confirms paying in the app, and the admin gives the final approval. Plus the reminder
 * to the user and the admin when fewer than 15 days of a contract are left.
 */
class EContractBillingTest extends TestCase
{
    use RefreshDatabase;

    const CARD = '6104337900074202';

    private Admin $admin;
    private User $user;

    protected function setUp(): void
    {
        parent::setUp();
        EContractTemplate::forgetCurrent();

        $this->admin = new Admin();
        $this->admin->first_name = 'مدیر';
        $this->admin->last_name = 'تست';
        $this->admin->mobile = '09121234567';
        $this->admin->email = 'admin@test.local';
        $this->admin->password = Hash::make('admin12345');
        $this->admin->save();

        $this->user = User::create([
            'first_name' => 'تست',
            'last_name' => 'کاربر',
            'mobile' => '09120000000',
            'password' => Hash::make('secret123'),
            'is_mobile_verified' => 1,
            'type' => 'user',
        ]);
    }

    private function api(): string
    {
        $this->app['auth']->forgetGuards();

        return '?token=' . $this->user->createToken('mobile')->plainTextToken;
    }

    private function submitContract(): EContract
    {
        $this->postJson('/api/e-contracts' . $this->api(), [
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
        ])->assertJsonPath('status', 201);

        return EContract::latest('id')->first();
    }

    private function requestPayment(EContract $contract, array $overrides = [])
    {
        return $this->actingAs($this->admin, 'admin')->put(route('requestEContractPayment', $contract->id), $overrides + [
            'amount' => '1,500,000',
            'card_number' => '6104 3379 0007 4202',
            'card_holder' => 'شرکت ایران اپ',
            'payment_message' => 'پس از واریز، شماره پیگیری را وارد کنید.',
        ]);
    }

    public function test_free_approval(): void
    {
        $contract = $this->submitContract();

        $this->actingAs($this->admin, 'admin')->put(route('approveEContract', $contract->id))
            ->assertSessionHas('success_msg');

        $contract->refresh();
        $this->assertSame(EContract::STATUS_APPROVED, $contract->status);
        $this->assertSame(EContract::BILLING_FREE, $contract->billing);
        $this->assertNull($contract->amount);
        $this->assertTrue($this->user->fresh()->isPro());

        $this->actingAs($this->admin, 'admin')
            ->get(route('showActiveEContractsInAdminPanel', ['billing' => 'free']))
            ->assertOk()
            ->assertSee('پوشاک آریا');
        $this->actingAs($this->admin, 'admin')
            ->get(route('showActiveEContractsInAdminPanel', ['billing' => 'paid']))
            ->assertOk()
            ->assertDontSee('پوشاک آریا');
    }

    public function test_paid_contract_from_request_to_final_approval(): void
    {
        $contract = $this->submitContract();

        // 1. The admin sends the amount and the card.
        $this->requestPayment($contract)->assertSessionHas('success_msg');
        $contract->refresh();
        $this->assertSame(EContract::STATUS_AWAITING_PAYMENT, $contract->status);
        $this->assertSame(EContract::BILLING_PAID, $contract->billing);
        $this->assertSame(1500000, $contract->amount);
        $this->assertSame(self::CARD, $contract->card_number);
        $this->assertFalse($this->user->fresh()->isPro());
        $this->assertStringContainsString('1,500,000 تومان', SendEContractReviewPushNotification::message($contract));

        // 2. The app shows them.
        $this->getJson('/api/e-contracts/current' . $this->api())
            ->assertJsonPath('contract.status', 'awaiting_payment')
            ->assertJsonPath('contract.amount_formatted', '1,500,000')
            ->assertJsonPath('contract.card_number_formatted', '6104 3379 0007 4202')
            ->assertJsonPath('contract.card_holder', 'شرکت ایران اپ')
            ->assertJsonPath('contract.payment_message', 'پس از واریز، شماره پیگیری را وارد کنید.');

        // 3. The user confirms paying, with a tracking number in Persian digits.
        $this->postJson('/api/e-contracts/payment' . $this->api(), ['payment_reference' => '۱۲۳۴۵۶'])
            ->assertJsonPath('status', 200)
            ->assertJsonPath('contract.status', 'payment_submitted');
        $contract->refresh();
        $this->assertSame('123456', $contract->payment_reference);
        $this->assertNotNull($contract->payment_submitted_at);

        // The admin sees it waiting for them.
        $this->actingAs($this->admin, 'admin')
            ->get(route('showEContractInAdminPanel', $contract->id))
            ->assertOk()
            ->assertSee('کاربر اعلام کرده هزینه را پرداخت کرده است.')
            ->assertSee('123456')
            ->assertSee('پرداخت دریافت شد؛ تایید نهایی قرارداد');

        // 4. Final approval.
        $this->actingAs($this->admin, 'admin')->put(route('confirmEContractPayment', $contract->id))
            ->assertSessionHas('success_msg');
        $contract->refresh();
        $this->assertSame(EContract::STATUS_APPROVED, $contract->status);
        $this->assertSame(EContract::BILLING_PAID, $contract->billing);
        $this->assertTrue($this->user->fresh()->isPro());

        $this->actingAs($this->admin, 'admin')
            ->get(route('showActiveEContractsInAdminPanel', ['billing' => 'paid']))
            ->assertOk()
            ->assertSee('پوشاک آریا')
            ->assertSee('1,500,000');
    }

    public function test_a_payment_that_did_not_arrive_goes_back_to_the_user(): void
    {
        $contract = $this->submitContract();
        $this->requestPayment($contract);
        $this->postJson('/api/e-contracts/payment' . $this->api(), ['payment_reference' => '999']);

        $this->actingAs($this->admin, 'admin')
            ->put(route('rejectEContractPayment', $contract->id), ['payment_rejection_reason' => 'مبلغی با این شماره پیگیری واریز نشده است.'])
            ->assertSessionHas('success_msg');

        $contract->refresh();
        $this->assertSame(EContract::STATUS_AWAITING_PAYMENT, $contract->status);
        $this->assertStringContainsString('تایید نشد', SendEContractReviewPushNotification::message($contract));
        $this->getJson('/api/e-contracts/current' . $this->api())
            ->assertJsonPath('contract.payment_rejection_reason', 'مبلغی با این شماره پیگیری واریز نشده است.');

        // The user can confirm again.
        $this->postJson('/api/e-contracts/payment' . $this->api(), ['payment_reference' => '1000'])
            ->assertJsonPath('contract.status', 'payment_submitted');
    }

    public function test_payment_request_is_validated(): void
    {
        $contract = $this->submitContract();

        $this->requestPayment($contract, ['card_number' => '6104337900074203', 'amount' => '500'])
            ->assertSessionHasErrors(['card_number', 'amount']);

        $this->assertSame(EContract::STATUS_PENDING, $contract->fresh()->status);
    }

    public function test_actions_out_of_order_are_refused(): void
    {
        $contract = $this->submitContract();

        // Nothing to confirm yet.
        $this->postJson('/api/e-contracts/payment' . $this->api())->assertJsonPath('status', 409);
        $this->actingAs($this->admin, 'admin')->put(route('confirmEContractPayment', $contract->id))
            ->assertSessionHas('error_msg');
        $this->assertSame(EContract::STATUS_PENDING, $contract->fresh()->status);
        $this->assertFalse($this->user->fresh()->isPro());
    }

    public function test_a_requested_payment_can_be_waived(): void
    {
        $contract = $this->submitContract();
        $this->requestPayment($contract);

        $this->actingAs($this->admin, 'admin')->put(route('approveEContract', $contract->id));

        $contract->refresh();
        $this->assertSame(EContract::STATUS_APPROVED, $contract->status);
        $this->assertSame(EContract::BILLING_FREE, $contract->billing);
        $this->assertNull($contract->card_number);
    }

    public function test_the_sidebar_badge_counts_payments_waiting_for_the_admin(): void
    {
        $contract = $this->submitContract();
        $this->requestPayment($contract);
        $this->postJson('/api/e-contracts/payment' . $this->api());

        $this->actingAs($this->admin, 'admin')->get(route('showEContractsInAdminPanel', ['status' => 'payment_submitted']))
            ->assertOk()
            ->assertSee('پرداخت شده، در انتظار تایید')
            ->assertSee('<span class="label label-danger pull-right">1</span>', false);
    }

    public function test_user_and_admin_are_reminded_once_when_15_days_are_left(): void
    {
        $contract = $this->submitContract();
        $this->actingAs($this->admin, 'admin')->put(route('approveEContract', $contract->id));

        // 12 months to go: nothing yet.
        $this->assertSame(0, EContractExpiry::sendDueReminders());

        // Ten days before the end.
        $this->travelTo(Carbon::parse($contract->fresh()->ends_on->format('Y-m-d') . ' 12:00:00', 'Asia/Tehran')->subDays(10));
        $this->assertSame(1, EContractExpiry::sendDueReminders());
        $this->assertSame(0, EContractExpiry::sendDueReminders(), 'reminded only once');

        $contract->refresh();
        $this->assertNotNull($contract->expiry_notified_at);
        $this->assertSame(10, $contract->daysLeft());
        $this->assertSame(
            'قرارداد الکترونیک شما 10 روز دیگر به پایان می رسد. برای تمدید با ایران اپ تماس بگیرید.',
            SendEContractReviewPushNotification::message($contract, SendEContractReviewPushNotification::EVENT_EXPIRY)
        );

        // The admin's bell shows it and opens the contract.
        $notification = AdminNotification::unread()->firstWhere('e_contract_id', $contract->id);
        $this->assertNotNull($notification);
        $this->actingAs($this->admin, 'admin')->get(route('showActiveEContractsInAdminPanel'))
            ->assertOk()
            ->assertSee('پایان قرارداد: پوشاک آریا')
            ->assertSee('10 روز');
        $this->actingAs($this->admin, 'admin')->get(route('openAdminNotification', $notification->id))
            ->assertRedirect(route('showEContractInAdminPanel', $contract->id));

        // The app shows the days left.
        $this->getJson('/api/e-contracts/current' . $this->api())->assertJsonPath('contract.days_left', 10);
    }

    /** The app asks for ?compact=1 and gets the wording or the stored text, not both. */
    public function test_compact_response_sends_only_what_the_screen_shows(): void
    {
        $this->getJson('/api/e-contracts/current' . $this->api() . '&compact=1')
            ->assertJsonPath('contract', null)
            ->assertJsonPath('template.version', EContractTemplate::DEFAULT_VERSION);

        $contract = $this->submitContract();
        $this->getJson('/api/e-contracts/current' . $this->api() . '&compact=1')
            ->assertJsonPath('template', null)
            ->assertJsonPath('contract.status', 'pending')
            ->assertJsonPath('contract.contract_text', $contract->contract_text);

        $this->actingAs($this->admin, 'admin')->put(route('rejectEContract', $contract->id), ['rejection_reason' => 'لطفا اصلاح کنید.']);
        $this->getJson('/api/e-contracts/current' . $this->api() . '&compact=1')
            ->assertJsonPath('contract.status', 'rejected')
            ->assertJsonPath('contract.contract_text', null)
            ->assertJsonPath('template.version', EContractTemplate::DEFAULT_VERSION);

        // Older apps do not ask for compact and still get everything.
        $this->getJson('/api/e-contracts/current' . $this->api())
            ->assertJsonPath('contract.contract_text', $contract->contract_text)
            ->assertJsonPath('template.version', EContractTemplate::DEFAULT_VERSION);
    }

    public function test_requests_trigger_the_reminders(): void
    {
        $contract = $this->submitContract();
        $this->actingAs($this->admin, 'admin')->put(route('approveEContract', $contract->id));
        $this->travelTo(Carbon::parse($contract->fresh()->ends_on->format('Y-m-d') . ' 12:00:00', 'Asia/Tehran')->subDays(3));

        cache()->forget('e_contract_expiry_check');
        $this->getJson('/api/app-version')->assertOk();

        $this->assertNotNull($contract->fresh()->expiry_notified_at);
    }
}
