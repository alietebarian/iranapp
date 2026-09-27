<?php

namespace Tests\Feature;

use App\Models\Admin;
use App\Models\MembershipCard;
use App\Models\User;
use App\Support\JalaliDate;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Membership cards: requested from the app, approved or rejected (with a note) in the admin
 * panel, and shown in the app as a card once approved.
 */
class MembershipCardTest extends TestCase
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

    private function token(User $user): string
    {
        // Drops the user Sanctum cached during an earlier request, as a real new request would.
        $this->app['auth']->forgetGuards();

        return $user->createToken('mobile')->plainTextToken;
    }

    private function submit(User $user, array $members)
    {
        return $this->postJson('/api/membership-card?token=' . $this->token($user), [
            'members' => $members,
            'app_version' => '1.1.106',
        ]);
    }

    public function test_current_returns_no_card_for_a_new_user(): void
    {
        $user = $this->makeUser();

        $this->getJson('/api/membership-card?token=' . $this->token($user))
            ->assertOk()
            ->assertJsonPath('status', 200)
            ->assertJsonPath('title', 'کارت هدیه معرفی به مراکز طرف قرارداد')
            ->assertJsonPath('today', JalaliDate::today())
            ->assertJsonPath('max_members', 6)
            ->assertJsonPath('card', null);
    }

    public function test_current_requires_login(): void
    {
        $this->getJson('/api/membership-card')->assertJsonPath('error', 'token_expired');
    }

    public function test_a_request_is_stored_as_pending_with_today_and_a_one_year_expiry(): void
    {
        $user = $this->makeUser();

        $this->submit($user, ['علی رضایی', '  مریم   احمدی ', ''])
            ->assertOk()
            ->assertJsonPath('status', 201)
            ->assertJsonPath('card.status', 'pending')
            ->assertJsonPath('card.serial_number', '1394 0010 0363 1900')
            ->assertJsonPath('card.members', ['علی رضایی', 'مریم احمدی']);

        $card = MembershipCard::sole();
        $today = JalaliDate::parse(JalaliDate::today());
        $this->assertSame($user->id, $card->user_id);
        $this->assertSame(JalaliDate::today(), $card->membership_date);
        $this->assertSame(JalaliDate::format(...JalaliDate::addMonths($today, 12)), $card->expiry_date);
        $this->assertSame('1.1.106', $card->app_version);
        // Stored readable, so the admin panel can search member names.
        $this->assertStringContainsString('علی رضایی', $card->getRawOriginal('members'));
    }

    public function test_each_new_user_gets_the_next_serial(): void
    {
        $this->submit($this->makeUser('09120000001'), ['عضو یکم'])->assertJsonPath('card.serial_number', '1394 0010 0363 1900');
        $this->submit($this->makeUser('09120000002'), ['عضو دوم'])->assertJsonPath('card.serial_number', '1394 0010 0363 1901');
        $this->submit($this->makeUser('09120000003'), ['عضو سوم'])->assertJsonPath('card.serial_number', '1394 0010 0363 1902');
    }

    public function test_one_to_six_members_are_accepted(): void
    {
        $this->submit($this->makeUser('09120000001'), [])->assertJsonPath('status', 422);
        $this->submit($this->makeUser('09120000002'), ['', '  '])->assertJsonPath('status', 422);
        $this->submit($this->makeUser('09120000003'), array_fill(0, 7, 'نام عضو'))->assertJsonPath('status', 422);
        $this->submit($this->makeUser('09120000004'), ['ع'])
            ->assertJsonPath('status', 422)
            ->assertJsonPath('errors.0', 'نام و نام خانوادگی عضو شماره 1 را کامل وارد کنید.');
        $this->assertSame(0, MembershipCard::count());

        $this->submit($this->makeUser('09120000005'), array_fill(0, 6, 'نام عضو'))->assertJsonPath('status', 201);
    }

    public function test_a_second_request_while_pending_is_refused(): void
    {
        $user = $this->makeUser();
        $this->submit($user, ['علی رضایی'])->assertJsonPath('status', 201);

        $this->submit($user, ['علی رضایی'])
            ->assertJsonPath('status', 409)
            ->assertJsonPath('error', 'card_pending');
        $this->assertSame(1, MembershipCard::count());
    }

    public function test_approval_shows_the_card_and_blocks_new_requests(): void
    {
        $user = $this->makeUser();
        $this->submit($user, ['علی رضایی']);
        $card = MembershipCard::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->put(route('approveMembershipCard', $card->id))
            ->assertRedirect(route('showMembershipCardInAdminPanel', $card->id));

        $card->refresh();
        $this->assertSame('approved', $card->status);
        $this->assertSame($admin->id, $card->reviewed_by);
        $this->assertNotNull($card->reviewed_at);

        $this->getJson('/api/membership-card?token=' . $this->token($user))
            ->assertJsonPath('card.status', 'approved')
            ->assertJsonPath('card.is_expired', false)
            ->assertJsonPath('card.rejection_reason', null);
        $this->submit($user, ['علی رضایی'])->assertJsonPath('error', 'card_approved');
    }

    public function test_rejection_requires_a_note(): void
    {
        $user = $this->makeUser();
        $this->submit($user, ['علی رضایی']);
        $card = MembershipCard::sole();

        $this->actingAs($this->makeAdmin(), 'admin')
            ->from(route('showMembershipCardInAdminPanel', $card->id))
            ->put(route('rejectMembershipCard', $card->id), ['rejection_reason' => ''])
            ->assertSessionHasErrors('rejection_reason');

        $this->assertSame('pending', $card->fresh()->status);
    }

    public function test_rejected_user_sees_the_note_and_resubmits_keeping_the_serial(): void
    {
        $this->submit($this->makeUser('09120000001'), ['عضو دیگر']);
        $user = $this->makeUser();
        $this->submit($user, ['علی']);
        $card = MembershipCard::where('user_id', $user->id)->sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->put(route('rejectMembershipCard', $card->id), ['rejection_reason' => 'نام خانوادگی عضو را کامل وارد کنید.']);
        $this->assertSame('rejected', $card->fresh()->status);

        $this->getJson('/api/membership-card?token=' . $this->token($user))
            ->assertJsonPath('card.status', 'rejected')
            ->assertJsonPath('card.rejection_reason', 'نام خانوادگی عضو را کامل وارد کنید.')
            ->assertJsonPath('card.members', ['علی']);

        $this->submit($user, ['علی رضایی', 'مریم احمدی'])
            ->assertJsonPath('status', 201)
            ->assertJsonPath('card.status', 'pending')
            ->assertJsonPath('card.serial_number', '1394 0010 0363 1901')
            ->assertJsonPath('card.rejection_reason', null);

        $card->refresh();
        $this->assertSame(2, MembershipCard::count());
        $this->assertSame(['علی رضایی', 'مریم احمدی'], $card->members);
        // The admin still sees what was asked for when reviewing the corrected request.
        $this->assertSame('نام خانوادگی عضو را کامل وارد کنید.', $card->rejection_reason);
        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardInAdminPanel', $card->id))
            ->assertSee('توضیحی که پیش از این برای کاربر فرستاده شد');
    }

    public function test_a_reviewed_request_cannot_be_reviewed_again(): void
    {
        $user = $this->makeUser();
        $this->submit($user, ['علی رضایی']);
        $card = MembershipCard::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')->put(route('rejectMembershipCard', $card->id), ['rejection_reason' => 'اطلاعات ناقص است.']);
        $this->actingAs($admin, 'admin')->put(route('approveMembershipCard', $card->id))
            ->assertSessionHas('error_msg');

        $this->assertSame('rejected', $card->fresh()->status);
    }

    public function test_card_expires_after_one_year(): void
    {
        \Carbon\Carbon::setTestNow(\Carbon\Carbon::parse('2026-09-27 09:00:00', 'Asia/Tehran'));
        $user = $this->makeUser();
        $this->submit($user, ['علی رضایی']);
        $card = MembershipCard::sole();
        $this->assertSame('1405/07/05', $card->membership_date);
        $this->assertSame('1406/07/05', $card->expiry_date);
        $this->assertFalse($card->isExpired());

        \Carbon\Carbon::setTestNow(\Carbon\Carbon::parse('2027-09-26 23:00:00', 'Asia/Tehran'));
        $this->assertFalse($card->fresh()->isExpired());
        \Carbon\Carbon::setTestNow(\Carbon\Carbon::parse('2027-09-27 00:30:00', 'Asia/Tehran'));
        $this->assertTrue($card->fresh()->isExpired());
        \Carbon\Carbon::setTestNow();
    }

    public function test_admin_pages_render_and_search(): void
    {
        $user = $this->makeUser();
        $this->submit($user, ['علی <b>رضایی</b>', 'مریم احمدی']);
        $card = MembershipCard::sole();
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardsInAdminPanel'))
            ->assertOk()
            ->assertSee('کارت عضویت')
            ->assertSee('1394 0010 0363 1900')
            ->assertSee('بررسی درخواست');

        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardsInAdminPanel', ['status' => 'all', 'q' => 'مریم']))
            ->assertSee('1394 0010 0363 1900');
        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardsInAdminPanel', ['status' => 'all', 'q' => '1900']))
            ->assertSee('1394 0010 0363 1900');
        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardsInAdminPanel', ['status' => 'all', 'q' => 'ناموجود']))
            ->assertDontSee('1394 0010 0363 1900');

        $this->actingAs($admin, 'admin')
            ->get(route('showMembershipCardInAdminPanel', $card->id))
            ->assertOk()
            ->assertSee('کارت هدیه معرفی به مراکز طرف قرارداد')
            ->assertSee('تایید کارت عضویت')
            ->assertSee('رد درخواست و ارسال توضیح به کاربر')
            ->assertSee('علی &lt;b&gt;رضایی&lt;/b&gt;', false)
            ->assertDontSee('علی <b>رضایی</b>', false);
    }

    public function test_admin_pages_are_closed_to_guests(): void
    {
        $this->get(route('showMembershipCardsInAdminPanel'))->assertRedirect(route('showAdminLoginForm'));
    }
}
