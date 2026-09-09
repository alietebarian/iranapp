<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Authentication moved from tymon/jwt-auth 0.5 to Sanctum. The mobile client was not
 * rewritten, so these tests pin the wire contract it still depends on: the token arrives
 * as `jwt_token`, may be sent either as a `?token=` query parameter or a Bearer header,
 * and failures come back as {status, error} rather than Laravel's default shapes.
 */
class AuthenticationTest extends TestCase
{
    use RefreshDatabase;

    private function makeUser(string $mobile = '09120000000', string $password = 'secret123'): User
    {
        return User::create([
            'first_name' => 'تست',
            'last_name' => 'کاربر',
            'mobile' => $mobile,
            'password' => Hash::make($password),
            'is_mobile_verified' => 1,
            'type' => 'user',
        ]);
    }

    public function test_login_returns_a_token_under_the_legacy_jwt_token_key(): void
    {
        $this->makeUser();

        $response = $this->postJson('/api/login', [
            'mobile' => '09120000000',
            'password' => 'secret123',
        ]);

        $response->assertOk()->assertJsonPath('status', 200);
        $this->assertNotEmpty($response->json('user.jwt_token'));
    }

    public function test_login_with_wrong_password_is_rejected(): void
    {
        $this->makeUser();

        $this->postJson('/api/login', [
            'mobile' => '09120000000',
            'password' => 'wrong-password',
        ])->assertJsonPath('status', 401);
    }

    public function test_login_with_unknown_mobile_is_rejected(): void
    {
        $this->postJson('/api/login', [
            'mobile' => '09999999999',
            'password' => 'secret123',
        ])->assertJsonPath('status', 401);
    }

    public function test_protected_route_without_a_token_returns_the_legacy_error_shape(): void
    {
        $this->getJson('/api/users/ads/favorites')
            ->assertStatus(401)
            ->assertExactJson(['status' => 401, 'error' => 'token_expired']);
    }

    public function test_protected_route_accepts_a_token_in_the_query_string(): void
    {
        $user = $this->makeUser();
        $token = $user->createToken('mobile')->plainTextToken;

        $this->getJson('/api/users/ads/favorites?token=' . $token)
            ->assertOk()
            ->assertJsonPath('status', 200);
    }

    public function test_protected_route_accepts_a_bearer_token_header(): void
    {
        $user = $this->makeUser();
        $token = $user->createToken('mobile')->plainTextToken;

        $this->withHeader('Authorization', 'Bearer ' . $token)
            ->getJson('/api/users/ads/favorites')
            ->assertOk()
            ->assertJsonPath('status', 200);
    }

    public function test_a_garbage_token_is_rejected(): void
    {
        $this->getJson('/api/users/ads/favorites?token=not-a-real-token')
            ->assertStatus(401)
            ->assertJsonPath('error', 'token_expired');
    }

    public function test_registration_rejects_a_duplicate_mobile(): void
    {
        $this->makeUser('09121111111');

        $this->postJson('/api/register', [
            'first_name' => 'علی',
            'last_name' => 'رضایی',
            'mobile' => '09121111111',
            'password' => 'secret123',
        ])->assertJsonPath('status', 400);
    }
}
