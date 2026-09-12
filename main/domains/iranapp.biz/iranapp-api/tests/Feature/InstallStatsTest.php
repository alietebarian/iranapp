<?php

namespace Tests\Feature;

use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;

/**
 * The install statistics shown on the app's landing page. Each installed device registers its
 * FCM token once, so every notification_setting row counts as one install.
 */
class InstallStatsTest extends TestCase
{
    use RefreshDatabase;

    private function install(string $token, Carbon $at): void
    {
        DB::table('notification_setting')->insert([
            'fcm_token' => $token,
            'created_at' => $at,
            'updated_at' => $at,
        ]);
    }

    public function test_installs_are_counted_in_total_last_30_days_and_today(): void
    {
        $this->travelTo(Carbon::parse('2026-09-12 15:00:00'));
        $this->install('three-months-ago', now()->subMonths(3));
        $this->install('last-week', now()->subWeek());
        $this->install('yesterday-night', now()->startOfDay()->subMinute());
        $this->install('this-morning', now()->startOfDay()->addHours(8));

        $this->getJson('/api/app-stats/installs')
            ->assertOk()
            ->assertExactJson(['status' => 200, 'total' => 4, 'month' => 3, 'today' => 1]);
    }

    public function test_no_installs_yet(): void
    {
        $this->getJson('/api/app-stats/installs')
            ->assertOk()
            ->assertExactJson(['status' => 200, 'total' => 0, 'month' => 0, 'today' => 0]);
    }
}
