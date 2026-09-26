<?php

namespace Tests\Feature;

use App\Models\Admin;
use App\Models\AppInstall;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * The install statistics shown on the app's landing page and in the admin panel's install report.
 *
 * New app versions report each install themselves (app_installs) on first launch; before that,
 * installs were counted from Firebase token registrations (notification_setting). Both are
 * combined without counting a device twice. See App\Support\InstallStats.
 */
class InstallStatsTest extends TestCase
{
    use RefreshDatabase;

    /** An install counted the old way: a Firebase token registration. */
    private function legacyInstall(string $token, Carbon $at): void
    {
        DB::table('notification_setting')->insert([
            'fcm_token' => $token,
            'created_at' => $at->copy()->utc(),
            'updated_at' => $at->copy()->utc(),
        ]);
    }

    /** An install reported by the app itself. */
    private function newInstall(string $id, Carbon $at, string $source = AppInstall::SOURCE_BAZAAR, bool $upgrade = false): void
    {
        DB::table('app_installs')->insert([
            'install_id' => $id,
            'source' => $source,
            'is_upgrade' => $upgrade,
            'app_version' => '1.1.105',
            'created_at' => $at->copy()->utc(),
            'updated_at' => $at->copy()->utc(),
        ]);
    }

    private function stats(): array
    {
        return $this->getJson('/api/app-stats/installs')->assertOk()->json();
    }

    public function test_no_installs_yet(): void
    {
        $this->getJson('/api/app-stats/installs')
            ->assertOk()
            ->assertExactJson(['status' => 200, 'total' => 0, 'month' => 0, 'today' => 0]);
    }

    public function test_before_any_app_reports_installs_the_old_token_count_is_used(): void
    {
        $this->travelTo(Carbon::parse('2026-09-12 15:00:00', 'Asia/Tehran'));
        $this->legacyInstall('three-months-ago', now()->subMonths(3));
        $this->legacyInstall('last-week', now()->subWeek());
        $this->legacyInstall('this-morning', Carbon::parse('2026-09-12 08:00:00', 'Asia/Tehran'));

        $this->assertSame(['status' => 200, 'total' => 3, 'month' => 2, 'today' => 1], $this->stats());
    }

    /** "Today" used to start at midnight UTC, i.e. 03:30 in Tehran. */
    public function test_today_starts_at_midnight_tehran_time(): void
    {
        $this->travelTo(Carbon::parse('2026-09-12 04:00:00', 'Asia/Tehran'));
        $this->newInstall('late-last-night-in-tehran', Carbon::parse('2026-09-11 23:50:00', 'Asia/Tehran'));
        $this->newInstall('just-after-midnight-in-tehran', Carbon::parse('2026-09-12 00:10:00', 'Asia/Tehran'));
        $this->newInstall('early-morning-in-tehran', Carbon::parse('2026-09-12 02:00:00', 'Asia/Tehran'));

        $this->assertSame(2, $this->stats()['today']);
    }

    public function test_old_and_new_installs_are_combined_without_counting_anyone_twice(): void
    {
        $this->travelTo(Carbon::parse('2026-09-20 12:00:00', 'Asia/Tehran'));
        $cutover = Carbon::parse('2026-09-15 10:00:00', 'Asia/Tehran');

        // Before the new version: counted from token registrations.
        $this->legacyInstall('old-1', now()->subMonths(2));
        $this->legacyInstall('old-2', now()->subDays(10));

        // The first report from the new version marks the cutover.
        $this->newInstall('first-new-install', $cutover, AppInstall::SOURCE_DIRECT);
        // A device that had the old version and updated: already counted as old-1 or old-2.
        $this->newInstall('updated-old-device', $cutover->copy()->addHour(), AppInstall::SOURCE_BAZAAR, true);
        // After the cutover, new installs also register a token; that row must not count again.
        $this->legacyInstall('token-of-first-new-install', $cutover->copy()->addMinute());
        $this->newInstall('installed-today', Carbon::parse('2026-09-20 09:00:00', 'Asia/Tehran'), AppInstall::SOURCE_GOOGLE_PLAY);

        $this->assertSame(['status' => 200, 'total' => 4, 'month' => 3, 'today' => 1], $this->stats());
    }

    public function test_the_app_registers_an_install_once_and_later_only_updates_its_version(): void
    {
        $payload = [
            'install_id' => '9b2f7c1e-5a4d-4c3b-8e2f-1a2b3c4d5e6f',
            'installer_package' => 'com.farsitel.bazaar',
            'is_upgrade' => '0',
            'app_version' => '1.1.105',
            'android_version' => '11',
            'device_model' => 'Samsung SM-A515F',
        ];

        $this->postJson('/api/app-stats/installs', $payload)->assertJsonPath('status', 201);
        $this->postJson('/api/app-stats/installs', ['app_version' => '1.1.106'] + $payload)->assertJsonPath('status', 200);

        $install = AppInstall::sole();
        $this->assertSame(AppInstall::SOURCE_BAZAAR, $install->source);
        $this->assertSame('1.1.106', $install->app_version);
        $this->assertFalse($install->is_upgrade);
    }

    public function test_invalid_install_ids_are_refused(): void
    {
        $this->postJson('/api/app-stats/installs', ['install_id' => 'short'])->assertJsonPath('status', 422);
        $this->postJson('/api/app-stats/installs', ['install_id' => "abc'; drop table users; --xxxxxxxx"])->assertJsonPath('status', 422);
        $this->assertSame(0, AppInstall::count());
    }

    public function test_installer_packages_map_to_sources(): void
    {
        $this->assertSame(AppInstall::SOURCE_BAZAAR, AppInstall::sourceFromInstaller('com.farsitel.bazaar'));
        $this->assertSame(AppInstall::SOURCE_GOOGLE_PLAY, AppInstall::sourceFromInstaller('com.android.vending'));
        $this->assertSame(AppInstall::SOURCE_MYKET, AppInstall::sourceFromInstaller('ir.mservices.market'));
        $this->assertSame(AppInstall::SOURCE_DIRECT, AppInstall::sourceFromInstaller(null));
        $this->assertSame(AppInstall::SOURCE_DIRECT, AppInstall::sourceFromInstaller('com.google.android.packageinstaller'));
        $this->assertSame(AppInstall::SOURCE_OTHER, AppInstall::sourceFromInstaller('com.huawei.appmarket'));
    }

    public function test_admin_install_report_renders(): void
    {
        $this->travelTo(Carbon::parse('2026-09-20 12:00:00', 'Asia/Tehran'));
        $this->legacyInstall('old', now()->subDays(40));
        $this->newInstall('a', now()->subDays(2), AppInstall::SOURCE_BAZAAR);
        $this->newInstall('b', now()->subHour(), AppInstall::SOURCE_DIRECT);

        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        $this->actingAs($admin, 'admin')
            ->get(route('showInstallReportInAdminPanel'))
            ->assertOk()
            ->assertSee('گزارش نصب ها')
            ->assertSee('کافه بازار')
            ->assertSee('فایل APK (نصب مستقیم)')
            ->assertSee('نصب های قبلی (پیش از ثبت منبع نصب)')
            ->assertSee('1405/06/29'); // today, 2026-09-20
    }

    public function test_admin_install_report_is_closed_to_guests(): void
    {
        $this->get(route('showInstallReportInAdminPanel'))->assertRedirect(route('showAdminLoginForm'));
    }
}
