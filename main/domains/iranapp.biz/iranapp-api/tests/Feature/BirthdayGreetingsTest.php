<?php

namespace Tests\Feature;

use App\Jobs\SendBirthdayPushNotifications;
use App\Models\Admin;
use App\Models\User;
use App\Support\Birthdays;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Bus;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Http;
use Kreait\Firebase\Contract\Messaging;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\MessageTarget;
use Kreait\Firebase\Messaging\MulticastSendReport;
use Kreait\Firebase\Messaging\SendReport;
use Tests\TestCase;

/**
 * New users give their Jalali birth date at sign-up, and every day those whose Jalali birthday it
 * is get a push wishing them a happy birthday. Users who signed up before have no birth date.
 */
class BirthdayGreetingsTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();

        // 5 Mehr 1405, 10:00 in Tehran.
        $this->travelTo(Carbon::create(2026, 9, 27, 10, 0, 0, 'Asia/Tehran'));
    }

    private function register(array $fields = [])
    {
        Http::fake(['api.kavenegar.com/*' => Http::response(['return' => ['status' => 200]])]);

        return $this->postJson('/api/register', $fields + [
            'first_name' => 'علی',
            'last_name' => 'رضایی',
            'mobile' => '09121111111',
            'password' => 'secret123',
            'birth_date' => '1380/05/12',
        ]);
    }

    private function user(string $mobile, ?string $birthDate, array $fields = []): User
    {
        return User::create($fields + [
            'first_name' => 'کاربر',
            'last_name' => 'تست',
            'mobile' => $mobile,
            'password' => Hash::make('secret123'),
            'is_mobile_verified' => 1,
            'fcm_token' => 'token-' . $mobile,
            'birth_date' => $birthDate,
        ]);
    }

    private function celebratingMobiles(string $date): array
    {
        return Birthdays::celebrating($date)->pluck('mobile')->sort()->values()->all();
    }

    public function test_registration_stores_the_jalali_birth_date_in_gregorian(): void
    {
        $this->register()->assertJsonPath('status', 204);

        $this->assertSame('2001-08-03', User::where('mobile', '09121111111')->first()->birth_date->format('Y-m-d'));
    }

    public function test_registration_accepts_persian_digits(): void
    {
        $this->register(['birth_date' => '۱۳۸۰/۰۵/۱۲'])->assertJsonPath('status', 204);

        $this->assertSame('2001-08-03', User::where('mobile', '09121111111')->first()->birth_date->format('Y-m-d'));
    }

    public function test_registration_requires_a_real_past_birth_date(): void
    {
        foreach ([null, '', 'دیروز', '1380/13/01', '1380/12/30', '1405/07/05', '1410/01/01', '1250/01/01'] as $birthDate) {
            $this->register(['birth_date' => $birthDate])
                ->assertJsonPath('status', 400)
                ->assertJsonStructure(['errors' => ['birth_date']]);
        }

        $this->assertSame(0, User::count());
    }

    public function test_users_are_greeted_on_their_jalali_birthday_not_the_gregorian_one(): void
    {
        $this->user('09120000001', '1996-09-26');   // 1375/07/05
        $this->user('09120000002', '2001-09-27');   // 1380/07/05
        $this->user('09120000003', '1996-09-27');   // 1375/07/06 — Gregorian date matches, Jalali does not
        $this->user('09120000004', '2001-09-26');   // 1380/07/04
        $this->user('09120000005', null);           // signed up before birth dates were asked

        $this->assertSame(['09120000001', '09120000002'], $this->celebratingMobiles('2026-09-27'));
    }

    public function test_only_verified_users_with_a_device_are_greeted(): void
    {
        $this->user('09120000001', '2001-09-27');
        $this->user('09120000002', '2001-09-27', ['fcm_token' => null]);
        $this->user('09120000003', '2001-09-27', ['fcm_token' => '']);
        $this->user('09120000004', '2001-09-27', ['is_mobile_verified' => 0]);

        $this->assertSame(['09120000001'], $this->celebratingMobiles('2026-09-27'));
    }

    public function test_those_born_on_30_esfand_are_greeted_on_29_esfand_in_a_common_year(): void
    {
        $this->user('09120000001', '2021-03-20');   // 1399/12/30
        $this->user('09120000002', '2021-03-19');   // 1399/12/29
        $this->user('09120000003', '2021-03-18');   // 1399/12/28

        // 1405 has no 30 Esfand; its 29 Esfand is 2027-03-20.
        $this->assertSame(['09120000001', '09120000002'], $this->celebratingMobiles('2027-03-20'));
    }

    public function test_greetings_go_out_once_a_day_and_not_before_nine(): void
    {
        Bus::fake();

        $this->travelTo(Carbon::create(2026, 9, 27, 8, 59, 0, 'Asia/Tehran'));
        Birthdays::sendDueGreetings();
        Bus::assertNotDispatchedAfterResponse(SendBirthdayPushNotifications::class);

        $this->travelTo(Carbon::create(2026, 9, 27, 9, 0, 0, 'Asia/Tehran'));
        Birthdays::sendDueGreetings();
        Birthdays::sendDueGreetings();
        Bus::assertDispatchedAfterResponseTimes(SendBirthdayPushNotifications::class, 1);

        $this->travelTo(Carbon::create(2026, 9, 28, 12, 0, 0, 'Asia/Tehran'));
        Birthdays::sendDueGreetings();
        Bus::assertDispatchedAfterResponseTimes(SendBirthdayPushNotifications::class, 2);
        $this->assertSame('2026-09-28', DB::table('setting')->where('setting_key', Birthdays::LAST_SENT_SETTING)->value('setting_value'));
    }

    public function test_an_app_request_triggers_the_days_greetings(): void
    {
        Bus::fake();

        $this->getJson('/api/app-version');
        $this->getJson('/api/app-version');

        Bus::assertDispatchedAfterResponseTimes(SendBirthdayPushNotifications::class, 1);
    }

    public function test_each_birthday_user_gets_a_personal_greeting_and_the_send_is_recorded(): void
    {
        $user = $this->user('09120000001', '2001-09-27', ['first_name' => 'مریم']);
        $this->user('09120000002', '2001-09-20');

        $this->mock(Messaging::class, function ($mock) use ($user) {
            $mock->shouldReceive('sendMulticast')->once()
                ->withArgs(function (CloudMessage $message, array $tokens) use ($user) {
                    $payload = $message->jsonSerialize();

                    return $tokens === ['token-09120000001']
                        && $payload['notification']['title'] === '🎂 مریم عزیز، تولدت مبارک!'
                        && $payload['data']['status'] === SendBirthdayPushNotifications::PUSH_STATUS
                        && $payload['data']['content_id'] === (string) $user->id;
                })
                ->andReturn(MulticastSendReport::withItems([
                    SendReport::success(MessageTarget::with(MessageTarget::TOKEN, 'token-09120000001'), []),
                ]));
        });

        (new SendBirthdayPushNotifications('2026-09-27'))->handle();

        $this->assertDatabaseHas('notification', [
            'msg_text' => 'تبریک تولد به 1 کاربر',
            'successfully_sent' => 1,
            'failures_on_send' => 0,
        ]);
    }

    public function test_the_admin_sees_the_birth_date_in_jalali(): void
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        $withBirthDate = $this->user('09120000001', '2001-08-03');
        $withoutBirthDate = $this->user('09120000002', null);

        $this->actingAs($admin, 'admin')->get(route('showUserUpdatePage', $withBirthDate))
            ->assertOk()->assertSee('1380/05/12');
        $this->actingAs($admin, 'admin')->get(route('showUserUpdatePage', $withoutBirthDate))
            ->assertOk()->assertSee('ثبت نشده');
    }

    public function test_nothing_is_sent_on_a_day_without_birthdays(): void
    {
        $this->user('09120000001', '2001-09-20');
        $this->mock(Messaging::class, fn ($mock) => $mock->shouldNotReceive('sendMulticast'));

        (new SendBirthdayPushNotifications('2026-09-27'))->handle();

        $this->assertDatabaseCount('notification', 0);
    }
}
