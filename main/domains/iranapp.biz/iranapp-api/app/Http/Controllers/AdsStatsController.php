<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\AdsLikes;
use App\Models\AdsView;
use App\Models\FavoriteAds;
use Carbon\Carbon;
use Illuminate\Http\Request;

/**
 * The ad performance page ("عملکرد آگهی"): how many times an ad was viewed, saved and liked.
 */
class AdsStatsController extends Controller
{
    /**
     * The app calls this each time an ad's page is opened. Guests count too, but the owner
     * looking at their own ad does not, so they can't inflate their own numbers.
     */
    public function recordView( Request $request , Ads $ads ) {
        $user = auth( 'sanctum' )->user();
        if ( $user && $this->isOwner( $ads , $user->id ) ) {
            return response()->json( [ 'status' => 200 , 'counted' => false ] );
        }
        $view          = new AdsView();
        $view->ads_id  = $ads->id;
        $view->user_id = $user ? $user->id : null;
        $view->save();

        return response()->json( [ 'status' => 200 , 'counted' => true ] );
    }

    /**
     * Totals plus how many of each came in the last 7 days. Only the ad's owner may see them.
     */
    public function show( Request $request , Ads $ads ) {
        $user = auth( 'sanctum' )->user();
        if ( ! $this->isOwner( $ads , $user->id ) ) {
            return response()->json( [ 'status' => 403 , 'error' => 'not_ad_owner' ] , 403 );
        }
        $weekAgo   = Carbon::now()->subDays( 7 );
        $views     = AdsView::where( 'ads_id' , $ads->id );
        $favorites = FavoriteAds::where( 'ads_id' , $ads->id );
        $likes     = AdsLikes::where( 'ads_id' , $ads->id )->where( 'like_type' , 'like' );

        return response()->json( [
            'status'         => 200 ,
            'views'          => ( clone $views )->count() ,
            'views_week'     => $views->where( 'created_at' , '>=' , $weekAgo )->count() ,
            'favorites'      => ( clone $favorites )->count() ,
            'favorites_week' => $favorites->where( 'created_at' , '>=' , $weekAgo )->count() ,
            'likes'          => ( clone $likes )->count() ,
            'likes_week'     => $likes->where( 'created_at' , '>=' , $weekAgo )->count() ,
            'dislikes'       => AdsLikes::where( 'ads_id' , $ads->id )->where( 'like_type' , 'dislike' )->count() ,
        ] );
    }

    /**
     * Same ownership as the "my ads" list: the ad's user_id, or a user_ads link (ads a
     * visitor registers for a user are linked only that way).
     */
    private function isOwner( Ads $ads , $userId ) {
        return $ads->user_id == $userId
            || $ads->userAds()->where( 'users.id' , $userId )->exists();
    }
}
