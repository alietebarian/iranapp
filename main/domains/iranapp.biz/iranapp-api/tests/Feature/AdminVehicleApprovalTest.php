<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Admins approve an app-submitted vehicle ad from its edit page. For every ad without a brand
 * (anything but a car) that page 500'd, and saving any vehicle ad 500'd right after the save.
 */
class AdminVehicleApprovalTest extends TestCase
{
    use RefreshDatabase;

    private int $regionId;
    private int $userId;
    private int $adId;

    protected function setUp(): void
    {
        parent::setUp();

        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $this->regionId = DB::table('region')->insertGetId(['city_id' => $cityId, 'name' => 'مرکز']);
        $this->userId = DB::table('users')->insertGetId([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111',
            'password' => 'x', 'created_at' => now(), 'updated_at' => now(),
        ]);
        // As submitted from the app: a heavy vehicle has no brand, and waits for approval.
        $this->adId = DB::table('vehicles_ads')->insertGetId([
            'region_id' => $this->regionId, 'user_id' => $this->userId, 'status' => 'pending',
            'ads_title' => 'کامیون بنز', 'type' => 'khordrosorn', 'person_or_company' => 'person',
            'neworold' => 'old', 'address' => 'اصفهان',
            'valid_since' => now()->toDateString(), 'valid_until' => now()->addYear()->toDateString(),
            'created_at_2' => now()->toDateTimeString(), 'created_at' => now(), 'updated_at' => now(),
        ]);

        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();
        $this->actingAs($admin, 'admin');
    }

    public function test_the_edit_page_of_a_vehicle_ad_without_a_brand_opens(): void
    {
        $this->get(route('vehicleAds.update.show', $this->adId))
            ->assertOk()
            ->assertSee('کامیون بنز');
    }

    public function test_approving_a_vehicle_ad_publishes_it_in_the_app(): void
    {
        $this->put(route('vehicleAds.update', $this->adId), [
            'ads_title' => 'کامیون بنز', 'region_id' => $this->regionId, 'user_id' => $this->userId,
            'address' => 'اصفهان', 'status' => 'approved', 'type' => 'khordrosorn',
            'neworold' => 'old', 'person_or_company' => 'person', 'brand' => 'all', 'model' => 'all',
            'publish_duration' => 30,
        ])->assertRedirect(route('vehicleAds.photos.show', $this->adId));

        $ad = DB::table('vehicles_ads')->find($this->adId);
        $this->assertSame('approved', $ad->status);
        $this->assertSame(now()->addDays(30)->toDateString(), $ad->valid_until);

        $this->getJson('/api/vehicles/ads?limit=10')
            ->assertOk()
            ->assertJsonPath('list.0.ads_title', 'کامیون بنز');
    }
}
