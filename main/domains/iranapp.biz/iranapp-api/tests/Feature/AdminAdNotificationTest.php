<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Kreait\Firebase\Contract\Messaging;
use Kreait\Firebase\Exception\Messaging\InvalidMessage;
use Kreait\Firebase\Exception\Messaging\NotFound;
use Kreait\Firebase\Messaging\MessageTarget;
use Kreait\Firebase\Messaging\MulticastSendReport;
use Kreait\Firebase\Messaging\SendReport;
use Tests\TestCase;

/**
 * The "send notification" checkbox of the admin "new ad" form. It used to break the admin panel;
 * a push that cannot be delivered must never stop the ad from being saved. The push runs after
 * the response (SendAdPushNotification); the test client terminates the kernel, so it still runs
 * before the assertions.
 */
class AdminAdNotificationTest extends TestCase
{
    use RefreshDatabase;

    private int $cityId;

    private function actingAsAdmin(): self
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        return $this->actingAs($admin, 'admin');
    }

    private function approvedAdWithNotification(array $overrides = []): array
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $this->cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $categoryId = DB::table('category')->insertGetId(['name' => 'خدمات']);

        return array_merge([
            'title' => 'آگهی تست',
            'city_id' => $this->cityId,
            'user_id' => [DB::table('users')->insertGetId([
                'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111',
                'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
            ])],
            'type' => 'need',
            'sub_category_id' => DB::table('sub_category')->insertGetId(['category_id' => $categoryId, 'name' => 'تعمیرات']),
            'status' => 'approved',
            'ads_plan_id' => DB::table('ads_plan')->insertGetId([
                'ordering_factor' => 1,
                'max_number_of_photos' => 3,
                'price' => 0,
                'plan_title' => 'پایه',
                'num_of_updates' => 1,
                'interval_days' => 30,
            ]),
            'send_notification' => 'on',
        ], $overrides);
    }

    private function subscribe(string ...$tokens): void
    {
        foreach ($tokens as $token) {
            DB::table('notification_setting')->insert([
                'fcm_token' => $token,
                'city_id' => $this->cityId,
                'created_at' => now(),
                'updated_at' => now(),
            ]);
        }
    }

    private function userWithToken(string $mobile, string $token): int
    {
        return DB::table('users')->insertGetId([
            'first_name' => 'کاربر', 'last_name' => 'تست', 'mobile' => $mobile, 'fcm_token' => $token,
            'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
        ]);
    }

    /** No service-account.json (as on a fresh server): the ad is still saved and the failure recorded. */
    public function test_ad_is_saved_even_when_firebase_is_not_configured(): void
    {
        $payload = $this->approvedAdWithNotification();
        $this->subscribe('token-a', 'token-b');

        $response = $this->actingAsAdmin()->post(route('saveAdsInAdminPanel'), $payload);

        $adId = DB::table('ads')->where('title', 'آگهی تست')->value('id');
        $this->assertNotNull($adId);
        $response->assertSessionHasNoErrors()->assertRedirect(route('showAdsPhotoById', $adId));

        $this->assertDatabaseHas('notification', [
            'msg_text' => 'آگهی تست',
            'successfully_sent' => 0,
            'failures_on_send' => 2,
        ]);
    }

    public function test_delivery_is_recorded_and_dead_tokens_are_forgotten(): void
    {
        $payload = $this->approvedAdWithNotification();
        $this->subscribe('token-ok', 'token-gone', 'token-bad');
        $goneUser = $this->userWithToken('09120000001', 'token-gone');
        $badUser = $this->userWithToken('09120000002', 'token-bad');

        $report = MulticastSendReport::withItems([
            SendReport::success(MessageTarget::with(MessageTarget::TOKEN, 'token-ok'), []),
            SendReport::failure(MessageTarget::with(MessageTarget::TOKEN, 'token-gone'), new NotFound('Requested entity was not found.')),
            SendReport::failure(MessageTarget::with(MessageTarget::TOKEN, 'token-bad'), new InvalidMessage('The registration token is not a valid FCM registration token')),
        ]);
        $this->mock(Messaging::class, fn ($mock) => $mock->shouldReceive('sendMulticast')->once()->andReturn($report));

        $this->actingAsAdmin()->post(route('saveAdsInAdminPanel'), $payload)->assertSessionHasNoErrors();

        $this->assertDatabaseHas('notification', [
            'msg_text' => 'آگهی تست',
            'category_id' => DB::table('category')->value('id'),
            'successfully_sent' => 1,
            'failures_on_send' => 2,
        ]);
        // Both kinds of dead token are cleared — an unknown one and an invalid one.
        $this->assertNull(DB::table('users')->where('id', $goneUser)->value('fcm_token'));
        $this->assertNull(DB::table('users')->where('id', $badUser)->value('fcm_token'));
    }

    public function test_nothing_is_sent_when_the_checkbox_is_off(): void
    {
        $payload = $this->approvedAdWithNotification(['send_notification' => null]);
        unset($payload['send_notification']);
        $this->subscribe('token-a');
        $this->mock(Messaging::class, fn ($mock) => $mock->shouldNotReceive('sendMulticast'));

        $this->actingAsAdmin()->post(route('saveAdsInAdminPanel'), $payload)->assertSessionHasNoErrors();

        $this->assertDatabaseCount('notification', 0);
    }

    public function test_nothing_is_sent_for_an_ad_that_is_not_approved(): void
    {
        $payload = $this->approvedAdWithNotification(['status' => 'pending']);
        $this->subscribe('token-a');
        $this->mock(Messaging::class, fn ($mock) => $mock->shouldNotReceive('sendMulticast'));

        $this->actingAsAdmin()->post(route('saveAdsInAdminPanel'), $payload)->assertSessionHasNoErrors();

        $this->assertDatabaseCount('notification', 0);
    }

    public function test_firebase_requests_have_a_default_timeout(): void
    {
        $this->assertSame(10, config('firebase.projects.' . config('firebase.default') . '.http_client_options.timeout'));
    }
}
