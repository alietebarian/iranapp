<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;

/**
 * The app's vehicle / estate / employ lists call getElapsedTime() from app/helpers.php for every
 * row. That file was not autoloaded, so any list with at least one ad crashed with a 500.
 */
class ElapsedTimeApiTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Only the shape of passed_time is asserted: jdf::jdate(), called just before it in the
     * controller, switches PHP's default timezone to Asia/Tehran as a side effect, so the exact
     * value depends on that (see HelpersTest for exact values).
     */
    public function test_vehicle_ads_list_reports_how_long_ago_each_ad_was_posted(): void
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $regionId = DB::table('region')->insertGetId(['city_id' => $cityId, 'name' => 'مرکز']);
        $userId = DB::table('users')->insertGetId([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111',
            'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
        ]);
        DB::table('vehicles_ads')->insert([
            'region_id' => $regionId,
            'user_id' => $userId,
            'valid_since' => now()->subDay()->toDateString(),
            'valid_until' => now()->addMonth()->toDateString(),
            'status' => 'approved',
            'ads_title' => 'پژو ۲۰۶',
            'type' => 'khodro',
            'person_or_company' => 'person',
            'created_at_2' => now()->subHours(3)->toDateTimeString(),
            'updated_at' => now(),
        ]);

        $response = $this->getJson('/api/vehicles/ads?limit=10')
            ->assertOk()
            ->assertJsonPath('list.0.ads_title', 'پژو ۲۰۶');

        $this->assertMatchesRegularExpression(
            '/^(\d+ (ثانیه|دقیقه|ساعت|روز|ماه|سال) قبل|لحظاتی قبل)$/u',
            $response->json('list.0.passed_time')
        );
    }
}
