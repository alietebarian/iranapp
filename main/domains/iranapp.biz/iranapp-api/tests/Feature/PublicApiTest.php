<?php

namespace Tests\Feature;

use App\Models\Category;
use App\Models\Province;
use Illuminate\Foundation\Testing\RefreshDatabase;
use PHPUnit\Framework\Attributes\DataProvider;
use Tests\TestCase;

/**
 * The open (unauthenticated) endpoints the app hits on launch. These are the routes that
 * decide whether the app shows content or its "no connection" screen, so they are worth
 * pinning after the port from Laravel 5.4.
 */
class PublicApiTest extends TestCase
{
    use RefreshDatabase;

    public static function publicEndpoints(): array
    {
        return [
            'provinces' => ['/api/provinces'],
            'categories' => ['/api/categories'],
            'app version' => ['/api/app-version'],
            'news' => ['/api/news'],
            'ad plans' => ['/api/ads/plans'],
            'cylinder volumes' => ['/api/cylinder-volumes'],
        ];
    }

    #[DataProvider('publicEndpoints')]
    public function test_public_endpoint_responds(string $uri): void
    {
        $this->getJson($uri)->assertOk();
    }

    public function test_provinces_returns_rows_that_exist(): void
    {
        // The ported models declare no $fillable, so attributes are set directly.
        $province = new Province();
        $province->name = 'اصفهان';
        $province->save();

        $response = $this->getJson('/api/provinces')->assertOk();

        $this->assertSame(200, $response->json('status'));
        $this->assertNotEmpty($response->json('list'));
    }

    public function test_categories_returns_rows_that_exist(): void
    {
        $category = new Category();
        $category->name = 'خودرو';
        $category->ordering_factor = 1;
        $category->save();

        $response = $this->getJson('/api/categories')->assertOk();

        $this->assertNotEmpty($response->json());
    }

    public function test_unknown_api_route_is_a_404_not_a_crash(): void
    {
        $this->getJson('/api/this-route-does-not-exist')->assertStatus(404);
    }
}
