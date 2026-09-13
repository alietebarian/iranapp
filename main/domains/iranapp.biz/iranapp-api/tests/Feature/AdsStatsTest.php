<?php

namespace Tests\Feature;

use App\Models\Ads;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * The ad performance page: views recorded when the app opens an ad, plus the saves and
 * likes that already exist, shown only to the ad's owner.
 */
class AdsStatsTest extends TestCase
{
    use RefreshDatabase;

    private function makeUser(string $mobile): User
    {
        return User::create([
            'first_name' => 'تست',
            'last_name' => 'کاربر',
            'mobile' => $mobile,
            'password' => Hash::make('secret123'),
            'is_mobile_verified' => 1,
            'type' => 'user',
        ]);
    }

    private function tokenFor(User $user): string
    {
        return $user->createToken('mobile')->plainTextToken;
    }

    /**
     * Within one test the auth guard keeps the first user it resolved for every later
     * request; a real request always starts clean, so reset it between calls.
     */
    private function freshRequest(): self
    {
        $this->app['auth']->forgetGuards();

        return $this;
    }

    private function makeAd(?User $owner): Ads
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $categoryId = DB::table('category')->insertGetId(['name' => 'خدمات']);
        $subCategoryId = DB::table('sub_category')->insertGetId(['category_id' => $categoryId, 'name' => 'تعمیرات']);
        $planId = DB::table('ads_plan')->insertGetId([
            'ordering_factor' => 1,
            'max_number_of_photos' => 3,
            'price' => 0,
            'plan_title' => 'پایه',
            'num_of_updates' => 1,
            'interval_days' => 30,
        ]);

        $ad = new Ads();
        $ad->title = 'آگهی تست';
        $ad->city_id = $cityId;
        $ad->user_id = $owner?->id;
        $ad->type = 'need';
        $ad->sub_category_id = $subCategoryId;
        $ad->status = 'approved';
        $ad->ads_plan_id = $planId;
        $ad->valid_since = Carbon::now()->subDay()->toDateString();
        $ad->valid_until = Carbon::now()->addDays(30)->toDateString();
        $ad->save();

        return $ad;
    }

    private function favorite(Ads $ad, User $user, Carbon $at): void
    {
        DB::table('favorite_ads')->insert([
            'ads_id' => $ad->id, 'user_id' => $user->id, 'created_at' => $at, 'updated_at' => $at,
        ]);
    }

    private function vote(Ads $ad, User $user, string $type, Carbon $at): void
    {
        DB::table('ads_like')->insert([
            'ads_id' => $ad->id, 'user_id' => $user->id, 'like_type' => $type, 'created_at' => $at, 'updated_at' => $at,
        ]);
    }

    private function addView(Ads $ad, Carbon $at): void
    {
        DB::table('ads_view')->insert(['ads_id' => $ad->id, 'user_id' => null, 'created_at' => $at]);
    }

    public function test_guest_and_other_users_views_count_but_the_owners_own_views_do_not(): void
    {
        $owner = $this->makeUser('09120000001');
        $visitor = $this->makeUser('09120000002');
        $ad = $this->makeAd($owner);

        $this->freshRequest()->postJson("/api/ads/{$ad->id}/views")->assertOk()->assertJsonPath('counted', true);
        $this->freshRequest()->postJson("/api/ads/{$ad->id}/views?token=" . $this->tokenFor($visitor))->assertOk()->assertJsonPath('counted', true);
        $this->freshRequest()->postJson("/api/ads/{$ad->id}/views?token=" . $this->tokenFor($owner))->assertOk()->assertJsonPath('counted', false);

        $this->assertSame(2, DB::table('ads_view')->where('ads_id', $ad->id)->count());
        $this->assertSame(1, DB::table('ads_view')->where('user_id', $visitor->id)->count());
    }

    public function test_viewing_an_unknown_ad_is_not_found(): void
    {
        $this->postJson('/api/ads/999999/views')->assertNotFound();
        $this->assertSame(0, DB::table('ads_view')->count());
    }

    public function test_owner_sees_totals_and_the_last_7_days(): void
    {
        $this->travelTo(Carbon::parse('2026-09-13 12:00:00'));
        $owner = $this->makeUser('09120000001');
        $a = $this->makeUser('09120000002');
        $b = $this->makeUser('09120000003');
        $c = $this->makeUser('09120000004');
        $ad = $this->makeAd($owner);
        $old = now()->subDays(10);
        $recent = now()->subDays(2);

        $this->addView($ad, $old);
        $this->addView($ad, $recent);
        $this->addView($ad, $recent);
        $this->favorite($ad, $a, $old);
        $this->favorite($ad, $b, $recent);
        $this->vote($ad, $a, 'like', $old);
        $this->vote($ad, $b, 'like', $recent);
        $this->vote($ad, $c, 'dislike', $recent);
        // Another ad's activity must not leak into this one.
        $other = $this->makeAd($a);
        $this->addView($other, $recent);
        $this->favorite($other, $c, $recent);
        $this->vote($other, $c, 'like', $recent);

        $this->getJson("/api/ads/{$ad->id}/stats?token=" . $this->tokenFor($owner))
            ->assertOk()
            ->assertExactJson([
                'status' => 200,
                'views' => 3,
                'views_week' => 2,
                'favorites' => 2,
                'favorites_week' => 1,
                'likes' => 2,
                'likes_week' => 1,
                'dislikes' => 1,
            ]);
    }

    public function test_a_new_ad_has_all_zero_stats(): void
    {
        $owner = $this->makeUser('09120000001');
        $ad = $this->makeAd($owner);

        $this->getJson("/api/ads/{$ad->id}/stats?token=" . $this->tokenFor($owner))
            ->assertOk()
            ->assertJson(['views' => 0, 'favorites' => 0, 'likes' => 0, 'dislikes' => 0]);
    }

    public function test_owner_linked_only_through_user_ads_sees_stats(): void
    {
        // Ads a visitor registers for a user have no ads.user_id, only a user_ads link.
        $owner = $this->makeUser('09120000001');
        $ad = $this->makeAd(null);
        $ad->userAds()->attach($owner->id);

        $this->getJson("/api/ads/{$ad->id}/stats?token=" . $this->tokenFor($owner))->assertOk();
        $this->postJson("/api/ads/{$ad->id}/views?token=" . $this->tokenFor($owner))->assertJsonPath('counted', false);
    }

    public function test_other_users_and_guests_cannot_see_stats(): void
    {
        $owner = $this->makeUser('09120000001');
        $stranger = $this->makeUser('09120000002');
        $ad = $this->makeAd($owner);

        $this->getJson("/api/ads/{$ad->id}/stats?token=" . $this->tokenFor($stranger))
            ->assertForbidden()
            ->assertJsonPath('error', 'not_ad_owner');
        $this->freshRequest()->getJson("/api/ads/{$ad->id}/stats")->assertUnauthorized();
    }
}
