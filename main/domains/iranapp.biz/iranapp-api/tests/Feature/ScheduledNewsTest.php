<?php

namespace Tests\Feature;

use App\Jobs\SendNewsPushNotification;
use App\Models\Admin;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Bus;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * An admin can schedule a news item for a Jalali date and a Tehran time; until then the app does
 * not see it, and its push notification waits for the same moment.
 */
class ScheduledNewsTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();

        // 1 Mehr 1405, 10:00 in Tehran (06:30 UTC).
        $this->travelTo(Carbon::create(2026, 9, 23, 10, 0, 0, 'Asia/Tehran'));
        Bus::fake();
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

    private function saveNews(array $fields)
    {
        return $this->post(route('saveNewsInAdminPanel'), $fields + [
            'title' => 'خبر تست',
            'news_text' => 'متن خبر تست',
        ]);
    }

    private function scheduledFor(int $year, int $month, int $day, int $hour, int $minute): array
    {
        return [
            'publish_mode' => 'scheduled',
            'publish_year' => $year,
            'publish_month' => $month,
            'publish_day' => $day,
            'publish_hour' => $hour,
            'publish_minute' => $minute,
        ];
    }

    private function appNewsTitles(): array
    {
        return collect($this->getJson('/api/news?offset=0&limit=10')->assertOk()->json('list'))
            ->pluck('title')->all();
    }

    public function test_immediate_news_is_published_and_notified_at_once(): void
    {
        $this->actingAsAdmin()->saveNews(['publish_mode' => 'now', 'send_notification' => 'on'])
            ->assertRedirect(route('showNewsListInAdminPanel'));

        $this->assertSame(['خبر تست'], $this->appNewsTitles());
        Bus::assertDispatchedAfterResponse(SendNewsPushNotification::class);
    }

    public function test_form_without_a_publish_mode_still_publishes_immediately(): void
    {
        $this->actingAsAdmin()->saveNews([])->assertRedirect(route('showNewsListInAdminPanel'));

        $this->assertSame(['خبر تست'], $this->appNewsTitles());
    }

    public function test_the_chosen_moment_is_read_as_tehran_time_on_the_jalali_calendar(): void
    {
        $this->actingAsAdmin()->saveNews($this->scheduledFor(1405, 7, 5, 14, 30));

        // 5 Mehr 1405 is 27 September 2026; 14:30 in Tehran (UTC+3:30) is 11:00 UTC.
        $this->assertSame('2026-09-27 11:00:00', DB::table('news')->value('publish_at'));
    }

    public function test_scheduled_news_stays_hidden_until_its_time_then_notifies_once(): void
    {
        $this->actingAsAdmin()->saveNews($this->scheduledFor(1405, 7, 1, 12, 0) + ['send_notification' => 'on'])
            ->assertRedirect(route('showNewsListInAdminPanel'));
        $newsId = DB::table('news')->value('id');

        $this->assertSame([], $this->appNewsTitles());
        $this->getJson("/api/news/{$newsId}")->assertNotFound();
        Bus::assertNotDispatchedAfterResponse(SendNewsPushNotification::class);

        $this->travelTo(Carbon::create(2026, 9, 23, 12, 0, 0, 'Asia/Tehran'));
        $this->assertSame(['خبر تست'], $this->appNewsTitles());
        $this->getJson("/api/news/{$newsId}")->assertOk();
        $this->getJson('/api/news?offset=0&limit=10');

        Bus::assertDispatchedAfterResponseTimes(SendNewsPushNotification::class, 1);
    }

    public function test_scheduled_news_without_notification_never_notifies(): void
    {
        $this->actingAsAdmin()->saveNews($this->scheduledFor(1405, 7, 1, 12, 0));

        $this->travelTo(Carbon::create(2026, 9, 23, 13, 0, 0, 'Asia/Tehran'));
        $this->assertSame(['خبر تست'], $this->appNewsTitles());
        Bus::assertNotDispatchedAfterResponse(SendNewsPushNotification::class);
    }

    public function test_a_moment_in_the_past_is_rejected(): void
    {
        $this->actingAsAdmin()->saveNews($this->scheduledFor(1405, 7, 1, 9, 0))
            ->assertSessionHasErrors('publish_day');

        $this->assertDatabaseCount('news', 0);
    }

    public function test_a_day_missing_from_the_month_is_rejected(): void
    {
        // Mehr has 30 days; 1405 is not a leap year, so Esfand has 29.
        $this->actingAsAdmin()->saveNews($this->scheduledFor(1405, 7, 31, 12, 0))
            ->assertSessionHasErrors('publish_day');
        $this->saveNews($this->scheduledFor(1405, 12, 30, 12, 0))
            ->assertSessionHasErrors('publish_day');

        $this->assertDatabaseCount('news', 0);
    }

    public function test_scheduling_requires_every_part_of_the_moment(): void
    {
        $this->actingAsAdmin()->saveNews(['publish_mode' => 'scheduled', 'publish_year' => 1405])
            ->assertSessionHasErrors(['publish_month', 'publish_day', 'publish_hour', 'publish_minute']);
    }

    public function test_admin_pages_show_the_publish_time(): void
    {
        $this->actingAsAdmin()->get(route('showNewsInsertForm'))
            ->assertOk()
            ->assertSee('انتشار در تاریخ و ساعت مشخص');

        $this->saveNews($this->scheduledFor(1405, 7, 5, 14, 30));
        $this->get(route('showNewsListInAdminPanel'))
            ->assertOk()
            ->assertSee('۵ مهر ۱۴۰۵ ساعت ۱۴:۳۰')
            ->assertSee('در انتظار انتشار');
    }
}
