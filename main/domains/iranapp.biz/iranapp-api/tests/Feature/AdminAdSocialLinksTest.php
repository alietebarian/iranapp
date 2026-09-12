<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * The Telegram / Instagram fields of the admin "new ad" form: a bare username is stored as the
 * full link the Android app opens, and an unusable value is reported instead of being saved.
 */
class AdminAdSocialLinksTest extends TestCase
{
    use RefreshDatabase;

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

    private function validAd(array $overrides = []): array
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $categoryId = DB::table('category')->insertGetId(['name' => 'خدمات']);

        return array_merge([
            'title' => 'آگهی تست',
            'city_id' => DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']),
            'user_id' => [DB::table('users')->insertGetId([
                'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111',
                'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
            ])],
            'type' => 'need',
            'sub_category_id' => DB::table('sub_category')->insertGetId(['category_id' => $categoryId, 'name' => 'تعمیرات']),
            'status' => 'pending',
            'ads_plan_id' => DB::table('ads_plan')->insertGetId([
                'ordering_factor' => 1,
                'max_number_of_photos' => 3,
                'price' => 0,
                'plan_title' => 'پایه',
                'num_of_updates' => 1,
                'interval_days' => 30,
            ]),
        ], $overrides);
    }

    public function test_plain_usernames_are_saved_as_full_links(): void
    {
        $this->actingAsAdmin()
            ->post(route('saveAdsInAdminPanel'), $this->validAd(['telegram' => '@iranapp', 'instagram' => 'iranapp']))
            ->assertSessionHasNoErrors();

        $this->assertDatabaseHas('ads', [
            'title' => 'آگهی تست',
            'telegram' => 'https://t.me/iranapp',
            'instagram' => 'https://instagram.com/iranapp',
        ]);
    }

    public function test_empty_fields_stay_empty(): void
    {
        $this->actingAsAdmin()
            ->post(route('saveAdsInAdminPanel'), $this->validAd(['telegram' => '', 'instagram' => '']))
            ->assertSessionHasNoErrors();

        $this->assertDatabaseHas('ads', ['title' => 'آگهی تست', 'telegram' => null, 'instagram' => null]);
    }

    public function test_an_unusable_telegram_value_is_reported_and_nothing_is_saved(): void
    {
        $this->actingAsAdmin()
            ->post(route('saveAdsInAdminPanel'), $this->validAd(['telegram' => '09121234567']))
            ->assertSessionHasErrors('telegram');

        $this->assertDatabaseCount('ads', 0);
    }
}
