<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

/**
 * The live ad tables are legacy utf8mb3, so an ad typed with an emoji failed with MySQL error
 * 3988 (see the 2026_09_27_140000 migration). The test tables are created as utf8mb4 by the
 * migrations, so each test first puts the table back into the legacy state.
 */
class AdEmojiTextTest extends TestCase
{
    use RefreshDatabase;

    private const MIGRATION = 'database/migrations/2026_09_27_140000_convert_ad_tables_to_utf8mb4.php';

    public function test_ad_tables_are_converted_to_utf8mb4_keeping_persian_collation(): void
    {
        DB::statement('ALTER TABLE vehicles_ads CONVERT TO CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci');
        DB::statement('ALTER TABLE employs_ads CONVERT TO CHARACTER SET utf8mb3 COLLATE utf8mb3_persian_ci');

        (require base_path(self::MIGRATION))->up();

        $this->assertSame('utf8mb4_unicode_ci', $this->collation('vehicles_ads'));
        $this->assertSame('utf8mb4_persian_ci', $this->collation('employs_ads'));
    }

    public function test_a_vehicle_ad_with_an_emoji_is_saved_after_the_conversion(): void
    {
        DB::statement('ALTER TABLE vehicles_ads CONVERT TO CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci');
        (require base_path(self::MIGRATION))->up();

        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $regionId = DB::table('region')->insertGetId(['city_id' => $cityId, 'name' => 'مرکز']);
        $user = User::forceCreate([
            'first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111', 'password' => 'x',
        ]);
        Sanctum::actingAs($user);

        $this->post('/api/vehicles', [
            'ads_title' => 'کامیون بنز 🚚',
            'description' => 'ممنون از دعوت 😊',
            'region_id' => $regionId,
            'address' => 'اصفهان',
            'type' => 'khordrosorn',
            'neworold' => 'old',
            'person_or_company' => 'person',
            'price' => 0,
            'terms_accepted' => 1,
        ])->assertOk()->assertJsonPath('status', 200);

        $this->assertSame('ممنون از دعوت 😊', DB::table('vehicles_ads')->value('description'));
    }

    private function collation(string $table): string
    {
        return DB::selectOne(
            'select TABLE_COLLATION as collation from information_schema.TABLES where TABLE_SCHEMA = ? and TABLE_NAME = ?',
            [DB::getDatabaseName(), $table]
        )->collation;
    }
}
