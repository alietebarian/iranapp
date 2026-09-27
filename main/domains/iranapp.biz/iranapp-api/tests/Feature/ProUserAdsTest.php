<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;

/**
 * Users whose electronic contract was approved become pro. In the vehicle, estate and employ
 * categories the app shows their ads everywhere with an `is_pro` tag, and additionally in a
 * "کاربران پرو" section that asks for `pro_only=1`.
 */
class ProUserAdsTest extends TestCase
{
    use RefreshDatabase;

    private int $cityId;
    private int $regionId;
    private int $normalUserId;
    private int $proUserId;

    protected function setUp(): void
    {
        parent::setUp();

        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $this->cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $this->regionId = DB::table('region')->insertGetId(['city_id' => $this->cityId, 'name' => 'مرکز']);
        $this->normalUserId = $this->user('09121111111', User::ROLE_NORMAL);
        $this->proUserId = $this->user('09122222222', User::ROLE_PRO);
    }

    public function test_vehicle_ads_are_tagged_and_can_be_limited_to_pro_users(): void
    {
        $this->vehicleAd($this->normalUserId, 'پژو ۲۰۶');
        $this->vehicleAd($this->proUserId, 'پراید');

        $all = $this->getJson('/api/vehicles/ads?limit=10&city_id=' . $this->cityId)->assertOk();
        $this->assertSame(['پراید' => 1, 'پژو ۲۰۶' => 0], $this->proFlags($all->json('list')));

        $pro = $this->getJson('/api/vehicles/ads?limit=10&pro_only=1&city_id=' . $this->cityId)->assertOk();
        $this->assertSame(['پراید' => 1], $this->proFlags($pro->json('list')));

        $search = $this->postJson('/api/vehicles/search', ['limit' => 10, 'pro_only' => 1])->assertOk();
        $this->assertSame(['پراید' => 1], $this->proFlags($search->json('list')));
    }

    public function test_estate_ads_are_tagged_and_can_be_limited_to_pro_users(): void
    {
        $categoryId = DB::table('estate_categories')->insertGetId(['name' => 'فروش مسکونی']);
        $this->estateAd($this->normalUserId, $categoryId, 'آپارتمان ۸۰ متری');
        $this->estateAd($this->proUserId, $categoryId, 'ویلا');

        $all = $this->getJson("/api/estates/city/{$this->cityId}/ads?limit=10")->assertOk();
        $this->assertSame(['آپارتمان ۸۰ متری' => 0, 'ویلا' => 1], $this->proFlags($all->json('list')));

        $pro = $this->getJson("/api/estates/city/{$this->cityId}/ads?limit=10&pro_only=1")->assertOk();
        $this->assertSame(['ویلا' => 1], $this->proFlags($pro->json('list')));
    }

    public function test_employ_ads_are_tagged_and_can_be_limited_to_pro_users(): void
    {
        $this->employAd($this->normalUserId, 'منشی');
        $this->employAd($this->proUserId, 'حسابدار');

        $all = $this->getJson("/api/employs/cities/{$this->cityId}/ads?limit=10")->assertOk();
        $this->assertSame(['حسابدار' => 1, 'منشی' => 0], $this->proFlags($all->json('list')));

        $pro = $this->getJson("/api/employs/cities/{$this->cityId}/ads?limit=10&pro_only=1")->assertOk();
        $this->assertSame(['حسابدار' => 1], $this->proFlags($pro->json('list')));
    }

    /** Title => is_pro, sorted by title so the assertion does not depend on row order. */
    private function proFlags(array $list): array
    {
        $flags = [];
        foreach ($list as $row) {
            $flags[$row['ads_title']] = (int) $row['is_pro'];
        }
        ksort($flags);

        return $flags;
    }

    private function user(string $mobile, string $role): int
    {
        return DB::table('users')->insertGetId([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => $mobile, 'password' => 'x',
            'role' => $role, 'created_at' => now(), 'updated_at' => now(),
        ]);
    }

    private function common(int $userId, string $title): array
    {
        return [
            'region_id' => $this->regionId,
            'user_id' => $userId,
            'valid_since' => now()->subDay()->toDateString(),
            'valid_until' => now()->addMonth()->toDateString(),
            'status' => 'approved',
            'ads_title' => $title,
            'created_at_2' => now()->subHour()->toDateTimeString(),
            'created_at' => now(),
            'updated_at' => now(),
        ];
    }

    private function vehicleAd(int $userId, string $title): void
    {
        DB::table('vehicles_ads')->insert($this->common($userId, $title) + [
            'type' => 'khodro', 'person_or_company' => 'person',
        ]);
    }

    private function estateAd(int $userId, int $categoryId, string $title): void
    {
        DB::table('estates_ads')->insert($this->common($userId, $title) + [
            'category_id' => $categoryId, 'user_type' => 'person', 'sell_or_buy' => 'sell',
            'ejare_or_kharid' => 'kharid', 'meters' => '80', 'type_karbari' => 'maskooni',
        ]);
    }

    private function employAd(int $userId, string $title): void
    {
        $specialtyId = DB::table('employs_ads_specialty')->insertGetId(['name' => 'اداری']);
        DB::table('employs_ads')->insert($this->common($userId, $title) + [
            'specialty_id' => $specialtyId, 'education_level' => 'diploma',
            'agremment_type' => 'tamamvaght', 'person_or_company' => 'person',
        ]);
    }
}
