<?php

namespace Tests\Feature;

use App\Jobs\SendNewsPushNotification;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Kreait\Firebase\Contract\Messaging;
use Kreait\Firebase\Messaging\MessageTarget;
use Kreait\Firebase\Messaging\MulticastSendReport;
use Kreait\Firebase\Messaging\SendReport;
use Tests\TestCase;

/**
 * How devices get their FCM token to the server. When the app moved to a new Firebase project
 * every device got a new token, but the app only registered once per install, so the server
 * kept pushing to tokens that can never be delivered.
 */
class FcmTokenRegistrationTest extends TestCase
{
    use RefreshDatabase;

    private function cityId(): int
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);

        return DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
    }

    private function device(string $token, array $values = []): void
    {
        DB::table('notification_setting')->insert(array_merge([
            'fcm_token' => $token, 'created_at' => now(), 'updated_at' => now(),
        ], $values));
    }

    public function test_a_new_token_takes_over_the_old_tokens_settings_and_user(): void
    {
        $cityId = $this->cityId();
        $this->device('old-token', ['city_id' => $cityId, 'send_ads_notifications' => 0]);
        $userId = DB::table('users')->insertGetId([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111', 'fcm_token' => 'old-token',
            'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
        ]);

        $this->postJson('/api/notification-settings/create?token=new-token&old_token=old-token')
            ->assertJson(['status' => 204]);

        $this->assertDatabaseCount('notification_setting', 1);
        $this->assertDatabaseHas('notification_setting', [
            'fcm_token' => 'new-token', 'city_id' => $cityId, 'send_ads_notifications' => 0,
        ]);
        $this->assertSame('new-token', DB::table('users')->where('id', $userId)->value('fcm_token'));
    }

    public function test_the_old_row_is_dropped_when_the_new_token_is_already_registered(): void
    {
        $this->device('old-token');
        $this->device('new-token');

        $this->postJson('/api/notification-settings/create?token=new-token&old_token=old-token')
            ->assertJson(['status' => 204]);

        $this->assertSame(['new-token'], DB::table('notification_setting')->pluck('fcm_token')->all());
    }

    public function test_a_first_registration_still_defaults_to_the_home_city(): void
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        DB::table('city')->insert(['id' => 57, 'province_id' => $provinceId, 'name' => 'اصفهان']);

        $this->postJson('/api/notification-settings/create?token=fresh-token')->assertJson(['status' => 204]);

        $this->assertDatabaseHas('notification_setting', ['fcm_token' => 'fresh-token', 'city_id' => 57]);
    }

    public function test_updating_settings_of_an_unregistered_token_registers_it(): void
    {
        $cityId = $this->cityId();

        $this->putJson('/api/notification-settings/update?token=unknown-token&send_ads_notifications=1&send_news_notifications=0&city_id=' . $cityId)
            ->assertJson(['status' => 204]);

        $this->assertDatabaseHas('notification_setting', [
            'fcm_token' => 'unknown-token', 'city_id' => $cityId, 'send_news_notifications' => 0,
        ]);
    }

    public function test_news_reaches_devices_that_are_not_logged_in(): void
    {
        $newsId = DB::table('news')->insertGetId(['title' => 'خبر تست', 'passage' => 'متن', 'created_at' => now(), 'updated_at' => now()]);
        $this->device('guest-token');
        $this->device('muted-token', ['send_news_notifications' => 0]);
        $this->device('member-token');
        DB::table('users')->insert([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111', 'fcm_token' => 'member-token',
            'send_news_notifications' => 1, 'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
        ]);

        $this->mock(Messaging::class, fn ($mock) => $mock->shouldReceive('sendMulticast')->once()
            ->withArgs(function ($message, $tokens) {
                sort($tokens);

                return $tokens === ['guest-token', 'member-token'];
            })
            ->andReturn(MulticastSendReport::withItems([
                SendReport::success(MessageTarget::with(MessageTarget::TOKEN, 'guest-token'), []),
                SendReport::success(MessageTarget::with(MessageTarget::TOKEN, 'member-token'), []),
            ])));

        (new SendNewsPushNotification($newsId))->handle();

        $this->assertDatabaseHas('notification', ['news_id' => $newsId, 'successfully_sent' => 2, 'failures_on_send' => 0]);
    }
}
