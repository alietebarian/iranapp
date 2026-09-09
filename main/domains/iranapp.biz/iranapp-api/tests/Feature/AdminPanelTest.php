<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use PHPUnit\Framework\Attributes\DataProvider;
use Tests\TestCase;

/**
 * The server-rendered admin panel: session login plus the pages that actually render
 * Blade templates and run dashboard aggregates. The dashboard in particular divided by a
 * count that is zero on an empty database — harmless in PHP 7, fatal in PHP 8 — so it is
 * covered explicitly.
 */
class AdminPanelTest extends TestCase
{
    use RefreshDatabase;

    private function makeAdmin(string $mobile = '09121234567', string $password = 'admin12345'): Admin
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = $mobile;
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make($password);
        $admin->save();

        return $admin;
    }

    public function test_login_page_renders(): void
    {
        $this->get('/admin/login')
            ->assertOk()
            ->assertSee('name="login"', false);
    }

    public function test_valid_credentials_reach_the_dashboard(): void
    {
        $this->makeAdmin();

        $this->post('/admin/login', [
            'login' => '09121234567',
            'password' => 'admin12345',
        ])->assertRedirect(route('showAdminDashboard'));

        $this->assertAuthenticatedAs(Admin::first(), 'admin');
    }

    public function test_wrong_password_does_not_authenticate(): void
    {
        $this->makeAdmin();

        $this->post('/admin/login', [
            'login' => '09121234567',
            'password' => 'wrong-password',
        ]);

        $this->assertGuest('admin');
    }

    public function test_dashboard_is_closed_to_guests(): void
    {
        $this->get('/admin/dashboard')->assertRedirect();
    }

    /** An empty database must not trigger a division by zero in the dashboard aggregates. */
    public function test_dashboard_renders_with_no_ads_at_all(): void
    {
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')
            ->get('/admin/dashboard')
            ->assertOk();
    }

    public static function adminPages(): array
    {
        return [
            'ads list' => ['/admin/ads/list'],
            'news' => ['/admin/news'],
            'categories' => ['/admin/categories'],
            'provinces' => ['/admin/provinces'],
            'ad plans' => ['/admin/ads-plans'],
            'users' => ['/admin/users'],
            'estate ads' => ['/admin/estates/ads'],
            'vehicle ads' => ['/admin/vehicles/ads'],
            'employ ads' => ['/admin/employs/ads'],
        ];
    }

    #[DataProvider('adminPages')]
    public function test_admin_page_renders(string $uri): void
    {
        $admin = $this->makeAdmin();

        $this->actingAs($admin, 'admin')->get($uri)->assertOk();
    }
}
