<?php

namespace Tests\Feature;

use App\Models\Admin;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * The bell in the admin panel's top bar: every approved ad 15 days or less from expiry is announced
 * once, until an admin opens it or the ad is renewed.
 */
class ExpiringAdsAdminNotificationTest extends TestCase
{
    use RefreshDatabase;

    private int $cityId;
    private int $subCategoryId;
    private int $planId;

    protected function setUp(): void
    {
        parent::setUp();

        // Mid-morning in Tehran, so "today" is the same date in Tehran and in UTC.
        $this->travelTo(Carbon::create(2026, 9, 13, 10, 0, 0, 'Asia/Tehran'));

        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $this->cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $categoryId = DB::table('category')->insertGetId(['name' => 'خدمات']);
        $this->subCategoryId = DB::table('sub_category')->insertGetId(['category_id' => $categoryId, 'name' => 'تعمیرات']);
        $this->planId = DB::table('ads_plan')->insertGetId([
            'ordering_factor' => 1,
            'max_number_of_photos' => 3,
            'price' => 0,
            'plan_title' => 'پایه',
            'num_of_updates' => 1,
            'interval_days' => 30,
        ]);
    }

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

    private function tehranToday(): Carbon
    {
        return Carbon::now('Asia/Tehran')->startOfDay();
    }

    private function adExpiringIn(int $days, string $title, string $status = 'approved'): int
    {
        return DB::table('ads')->insertGetId([
            'title' => $title,
            'city_id' => $this->cityId,
            'type' => 'need',
            'sub_category_id' => $this->subCategoryId,
            'status' => $status,
            'ads_plan_id' => $this->planId,
            'valid_since' => $this->tehranToday()->subDays(30)->toDateString(),
            'valid_until' => $this->tehranToday()->addDays($days)->toDateString(),
            'created_at' => now(),
            'updated_at' => now(),
        ]);
    }

    /** A page that renders the top bar but lists no ad titles of its own. */
    private function anyAdminPage()
    {
        return $this->get(route('showExpiringAds'))->assertOk();
    }

    public function test_approved_ads_up_to_fifteen_days_from_expiry_are_announced(): void
    {
        $this->adExpiringIn(0, 'تعمیر یخچال');
        $this->adExpiringIn(15, 'فروش فرش');
        $this->adExpiringIn(16, 'کلاس زبان');
        $this->adExpiringIn(-1, 'لوله کشی');
        $this->adExpiringIn(5, 'باغبانی', 'pending');

        $this->actingAsAdmin()->anyAdminPage()
            ->assertSee('تعمیر یخچال')
            ->assertSee('فروش فرش')
            ->assertDontSee('کلاس زبان')
            ->assertDontSee('لوله کشی')
            ->assertDontSee('باغبانی');

        $this->assertDatabaseCount('admin_notifications', 2);
    }

    public function test_each_ad_is_announced_only_once(): void
    {
        $this->adExpiringIn(10, 'تعمیر یخچال');

        $this->actingAsAdmin()->anyAdminPage();
        $this->anyAdminPage();

        $this->assertDatabaseCount('admin_notifications', 1);
    }

    public function test_opening_a_notification_marks_it_read_and_shows_the_expiring_list(): void
    {
        $this->adExpiringIn(10, 'تعمیر یخچال');
        $this->actingAsAdmin()->anyAdminPage();
        $notificationId = DB::table('admin_notifications')->value('id');

        $this->get(route('openAdminNotification', $notificationId))
            ->assertRedirect(route('showExpiringAds', ['filter' => 'doFilter', 'interval_days' => 15]));

        $this->assertNotNull(DB::table('admin_notifications')->where('id', $notificationId)->value('read_at'));
        $this->anyAdminPage()->assertDontSee('تعمیر یخچال');
    }

    public function test_all_notifications_can_be_marked_read(): void
    {
        $this->adExpiringIn(3, 'تعمیر یخچال');
        $this->adExpiringIn(12, 'فروش فرش');
        $this->actingAsAdmin()->anyAdminPage();

        $this->post(route('readAllAdminNotifications'))->assertRedirect();

        $this->assertSame(0, DB::table('admin_notifications')->whereNull('read_at')->count());
        $this->anyAdminPage()->assertDontSee('تعمیر یخچال')->assertDontSee('فروش فرش');
    }

    public function test_renewing_an_ad_retires_its_notification_and_the_new_date_is_announced_later(): void
    {
        $adId = $this->adExpiringIn(10, 'تعمیر یخچال');
        $this->actingAsAdmin()->anyAdminPage()->assertSee('تعمیر یخچال');

        DB::table('ads')->where('id', $adId)->update(['valid_until' => $this->tehranToday()->addDays(40)->toDateString()]);
        $this->anyAdminPage()->assertDontSee('تعمیر یخچال');

        $this->travel(30)->days();
        $this->anyAdminPage()->assertSee('تعمیر یخچال');
        $this->assertDatabaseCount('admin_notifications', 2);
    }

    public function test_days_are_counted_on_the_tehran_calendar(): void
    {
        // 01:00 in Tehran is still the previous day in UTC; the ad is 15 Tehran days away (16 in UTC).
        $this->travelTo(Carbon::create(2026, 9, 14, 1, 0, 0, 'Asia/Tehran'));
        $this->adExpiringIn(15, 'تعمیر یخچال');

        $this->actingAsAdmin()->anyAdminPage()->assertSee('تعمیر یخچال');
    }

    public function test_expiring_list_includes_the_last_day_and_the_day_the_range_ends(): void
    {
        // Pending ads are never in the bell, so any title seen here comes from the list itself.
        $this->adExpiringIn(0, 'تعمیر یخچال', 'pending');
        $this->adExpiringIn(15, 'فروش فرش', 'pending');
        $this->adExpiringIn(16, 'کلاس زبان', 'pending');
        $this->adExpiringIn(-1, 'لوله کشی', 'pending');

        $this->actingAsAdmin()->get(route('showExpiringAds', ['filter' => 'doFilter', 'interval_days' => 15]))
            ->assertOk()
            ->assertSee('تعمیر یخچال')
            ->assertSee('فروش فرش')
            ->assertDontSee('کلاس زبان')
            ->assertDontSee('لوله کشی');

        $this->get(route('showExpiringAds', ['filter' => 'doFilter', 'interval_days' => 'expired']))
            ->assertOk()
            ->assertSee('لوله کشی')
            ->assertDontSee('تعمیر یخچال');
    }
}
