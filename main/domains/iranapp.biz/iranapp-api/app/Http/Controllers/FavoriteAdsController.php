<?php

namespace App\Http\Controllers;

use App\Models\AdsPhoto;
use App\Models\FavoriteAds;
use App\Libraries\jdf;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\URL;

class FavoriteAdsController extends Controller
{
    public function makeAsFavorite(Request $request , $adsId){
        try{
            $user = auth('sanctum')->user();
        } catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
        $favExist = FavoriteAds::where('ads_id' , $adsId)->where('user_id' , $user->id)->count();
        if($favExist > 0 ){
            $fav = FavoriteAds::where('ads_id' , $adsId)->where('user_id' , $user->id)->first();
            $fav->delete();
            $favStatus = 'removed';
        }else{
            $fav = new FavoriteAds();
            $fav->user_id = $user->id;
            $fav->ads_id = $adsId;
            $fav->save();
            $favStatus = 'saved';
        }

        if($fav){
            return response()->json(['status' => 200 , 'fav_status' => $favStatus]);
        }else{
            return response()->json(['status' => 501 , 'error' => 'server_side_error']);
        }
    }

    public function gettingListByUserId(Request $request){
        try{
            $user = auth('sanctum')->user();
        } catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
        if($request->has('offset') && $request->has('limit')){
            $offset = $request->offset;
            $limit = $request->limit;
        }else{
            $offset = 0;
            $limit = 1;
        }
        $favoriteAdsObj = new FavoriteAds();
        $favAds = $favoriteAdsObj->selectFields(FavoriteAds::FIELDS)
            ->where('users.id' , '=' , $user->id)
            ->where('ads.status' , '=' , 'approved')
            ->where('ads.valid_since' , '<=' , date('Y-m-d H:i:s' , time()))
            ->where('ads.valid_until' , '>=' , date('Y-m-d H:i:s' , time()))
            ->offset($offset)->limit($limit)
            ->get();
        $favAdsCollection = collect($favAds);
        $favAdsCollection->each(function ($ad , $adIndex)use($favAdsCollection){
            $photos = AdsPhoto::where('ads_id' , $ad->ads_id)->get();
            $photos->each(function($photo , $index) use($photos){
                $photos[$index]->file_name = URL::to('/ads_photo') . '/' . $photo->file_name;
            });
            $favAdsCollection[$adIndex]->photos = $photos;
            $favAdsCollection[$adIndex]->created_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $ad->created_at)->getTimestamp());
            $favAdsCollection[$adIndex]->updated_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $ad->updated_at)->getTimestamp());
            $favAdsCollection[$adIndex]->valid_since = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d' , $ad->valid_since)->getTimestamp());
            $favAdsCollection[$adIndex]->valid_until = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp());
        });
        return response()->json(['status' => 200 , 'list' => $favAds]);
    }

	public function makewarning(Request $request,$adsId) {
		return response()->json(['status' => 200 , 'fav_status' => 1]);
	}
}
