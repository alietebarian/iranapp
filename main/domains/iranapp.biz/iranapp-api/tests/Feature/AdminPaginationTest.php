<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Every list in the admin panel paginates through resources/views/admin/partials/pagination
 * (the default view for ->links()). Laravel's own default is Tailwind markup the panel's
 * Bootstrap 3 theme does not style.
 */
class AdminPaginationTest extends TestCase
{
    use RefreshDatabase;

    private function admin(): Admin
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        return $admin;
    }

    private function phoneBook(int $count, string $guild, string $prefix = '0912'): void
    {
        $rows = [];
        for ($i = 0; $i < $count; $i++) {
            $rows[] = ['full_name' => 'مخاطب ' . $i, 'phone' => $prefix . str_pad((string) $i, 7, '0', STR_PAD_LEFT),
                'guild' => $guild, 'created_at' => now(), 'updated_at' => now()];
        }
        DB::table('phone_book_entries')->insert($rows);
    }

    public function test_a_long_list_shows_the_summary_page_window_and_jump_box(): void
    {
        // 250 entries in the filtered guild: 13 pages of 20.
        $this->phoneBook(250, 'پوشاک');
        $this->phoneBook(5, 'خودرو', '0935');

        $html = $this->actingAs($this->admin(), 'admin')
            ->get(route('showPhoneBookInAdminPanel', ['guild' => 'پوشاک', 'page' => 3]))
            ->assertOk()
            ->assertSee('class="admin-pagination"', false)
            ->assertSee('نمایش <b>41</b> تا <b>60</b>', false)
            ->assertSee('از <b>250</b> مورد', false)
            ->assertSee('صفحه 3 از 13')
            ->assertSee('برو به صفحه')
            // Not Laravel's Tailwind markup.
            ->assertDontSee('relative inline-flex', false)
            ->getContent();

        // Page links keep the filter, and the current page is not a link.
        $this->assertStringContainsString('guild=' . urlencode('پوشاک') . '&amp;page=4', $html);
        $this->assertStringContainsString('<li class="active" aria-current="page"><span>3</span></li>', $html);
        // The jump-to-page form carries the filter as a hidden field.
        $this->assertMatchesRegularExpression('/<input type="hidden" name="guild" value="پوشاک">/u', $html);
    }

    public function test_a_single_page_shows_only_the_summary(): void
    {
        $this->phoneBook(7, 'پوشاک');

        $this->actingAs($this->admin(), 'admin')
            ->get(route('showPhoneBookInAdminPanel'))
            ->assertOk()
            ->assertSee('از <b>7</b> مورد', false)
            ->assertDontSee('class="ap-pages"', false);
    }

    public function test_pages_that_had_their_own_pagination_render(): void
    {
        $admin = $this->admin();
        foreach (['showAdsListInAdminPanel', 'showAllCategoriesInAdminPanel', 'showAllProvincesInAdminPanel',
                     'showUserMobileNumbersBank', 'showListOfVisitorPage', 'showAllVipAdsInAdminPanel',
                     'showAllEstateAdsInAdminPanel', 'showAllVehicleAdsInAdminPanel', 'showAllEmploysAdsInAdminPanel',
                     'showAllBrandsInAdmin', 'showAllSpecialitiesInAdminPanel', 'ShowAllCylinderVolumesInAdminPanel',
                     'admin.award.index'] as $route) {
            $this->actingAs($admin, 'admin')->get(route($route))->assertOk();
        }
    }
}
