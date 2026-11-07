<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;

/**
 * App builds before 202 are refused by the API once BLOCK_OUTDATED_APPS is on, except for the
 * version check that shows them the update dialog. See App\Http\Middleware\BlockOutdatedApps.
 */
class BlockOutdatedAppsTest extends TestCase
{
    use RefreshDatabase;

    private const OLD_VOLLEY = 'Dalvik/2.1.0 (Linux; U; Android 13; SM-A346E Build/TP1A.220624.014)';
    private const OLD_OKHTTP = 'okhttp/3.12.1';
    private const NEW_APP = 'IranApp/202 Dalvik/2.1.0 (Linux; U; Android 13; SM-A346E Build/TP1A.220624.014)';
    private const BROWSER = 'Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36';

    protected function setUp(): void
    {
        parent::setUp();
        config(['app.block_outdated_apps' => true]);
    }

    private function getAs(string $agent, string $uri)
    {
        return $this->withHeaders(['User-Agent' => $agent])->get($uri);
    }

    public function test_old_app_is_refused(): void
    {
        $this->getAs(self::OLD_VOLLEY, '/api/app-stats/installs')
            ->assertStatus(426)
            ->assertJson(['status' => 426, 'error' => 'app_outdated']);

        $this->withHeaders(['User-Agent' => self::OLD_OKHTTP])
            ->post('/api/login', ['mobile' => '09120000000', 'password' => 'x'])
            ->assertStatus(426);
    }

    public function test_old_app_still_gets_the_version_so_it_shows_the_update_dialog(): void
    {
        DB::table('setting')->insert(['setting_key' => 'app_version', 'setting_value' => '202']);

        $this->getAs(self::OLD_VOLLEY, '/api/app-version')
            ->assertOk()
            ->assertJson(['status' => 200, 'app_version' => 202]);
    }

    public function test_new_app_and_browsers_pass(): void
    {
        $this->getAs(self::NEW_APP, '/api/app-stats/installs')->assertOk();
        $this->getAs(self::BROWSER, '/api/app-stats/installs')->assertOk();
    }

    public function test_nothing_is_refused_while_switched_off(): void
    {
        config(['app.block_outdated_apps' => false]);

        $this->getAs(self::OLD_VOLLEY, '/api/app-stats/installs')->assertOk();
    }
}
