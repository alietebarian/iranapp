<?php

namespace App\Http\Controllers;

use App\EmploysAds;
use App\EmploysAdsPhoto;
use App\EstateAds;
use App\EstateAdsPhoto;
use App\Libraries\jdf;
use App\VehicleAds;
use App\VehicleAdsPhoto;
use App\VipAdsFavorites;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Tymon\JWTAuth\Facades\JWTAuth;

class VipAdsFavoriteController extends Controller
{
    public function saveJson(Request $request){
        $user = JWTAuth::parseToken()->authenticate();
        $type = $request->type;
        if(in_array($type , ['vehicles' , 'employs' , 'estates'])){
            if($type == 'vehicles'){
                $ad = VehicleAds::find($request->ads_id);
            }else if($type == 'employs'){
                $ad = EmploysAds::find($request->ads_id);
            }else{
                $ad = EstateAds::find($request->ads_id);
            }
            $exists = VipAdsFavorites::where('user_id' , $user->id)->where('ads_id' , $ad->id)->where('ads_type' , $type)->exists();
            if($exists){
                VipAdsFavorites::where('user_id' , $user->id)->where('ads_id' , $ad->id)->where('ads_type' , $type)->delete();
                $isFav = false;
            }else{
                $new = new VipAdsFavorites();
                $new->user_id = $user->id;
                $new->ads_id = $ad->id;
                $new->ads_type = $type;
                $new->save();
                $isFav = true;
            }
        }
        return response()->json(['status' => 200 , 'is_fav' => $isFav]);
    }

    public function isFavorite(Request $request){
        $user = JWTAuth::parseToken()->authenticate();
        $adType = $request->adType;
        $adsId = $request->adsId;
        $isFavorite = (VipAdsFavorites::where('ads_id' , $adsId)
            ->where('user_id' , $user->id)
            ->where('ads_type' , $adType)
            ->exists())  ? true : false;
        return response()->json(['status' => 200 , 'is_favorite' => $isFavorite]);

    }

    public function getUserVehicleAds(Request $request){
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $user = JWTAuth::parseToken()->authenticate();
        $ads = new VehicleAds();
        $list = $ads->selectFields(VehicleAds::FIELDS)
            ->where('vehicles_ads.status' , '=' , 'approved')
            ->whereRaw('( vehicles_ads.id  in (
                select vip_ads_favorite.ads_id from vip_ads_favorite where ads_type = "vehicles" and
                vip_ads_favorite.user_id = '. $user->id .'
            ) )')
            ->offset($offset)->limit($limit)
            ->get();
        foreach ($list as $index => $row) {
            $photo = VehicleAdsPhoto::where('vehicle_ads_id', '=', $row->id)->get();
            foreach ($photo as $pindex => $photoItem) {
                $photo[$pindex]->file_name = url()->to('/ads_photo') . '/' . $photoItem->file_name;
            }
            $list[$index]->photos = $photo;

            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->fa_created_at = jdf::jdate('j F Y', Carbon::createFromFormat('Y-m-d H:i:s', $row->created_at_2)->getTimestamp());

            $list[$index]->passed_time = getElapsedTime($row->created_at_2);
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }


    public function getUserEstateAds(Request $request){
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $user = JWTAuth::parseToken()->authenticate();
        $ads = new EstateAds();
        $list = $ads->selectFields(EstateAds::FIELDS)
            ->where('estates_ads.status' , '=' , 'approved')
            ->whereRaw('( estates_ads.id  in (
                select vip_ads_favorite.ads_id from vip_ads_favorite where ads_type = "estates" and
                vip_ads_favorite.user_id = '. $user->id .'
            ) )')
            ->offset($offset)->limit($limit)
            ->get();
        foreach ($list as $index => $row) {
            $photo = EstateAdsPhoto::where('estates_ads_id', '=', $row->id)->get();
            foreach ($photo as $pindex => $photoItem) {
                $photo[$pindex]->file_name = url()->to('/ads_photo') . '/' . $photoItem->file_name;
            }
            $list[$index]->photos = $photo;

            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->fa_created_at = jdf::jdate('j F Y', Carbon::createFromFormat('Y-m-d H:i:s', $row->created_at_2)->getTimestamp());

            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }

    public function getUserEmployAds(Request $request){
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $user = JWTAuth::parseToken()->authenticate();
        $ads = new EmploysAds();
        $list = $ads->selectFields(EmploysAds::FIELDS)
            ->where('employs_ads.status' , '=' , 'approved')
            ->whereRaw('( employs_ads.id  in (
                select vip_ads_favorite.ads_id from vip_ads_favorite where ads_type = "employs" and
                vip_ads_favorite.user_id = '. $user->id .'
            ) )')
            ->offset($offset)->limit($limit)
            ->get();
        foreach ($list as $index => $row) {
            $photo = EmploysAdsPhoto::where('employs_ads_id', '=', $row->id)->get();
            foreach ($photo as $pindex => $photoItem) {
                $photo[$pindex]->file_name = url()->to('/ads_photo') . '/' . $photoItem->file_name;
            }
            $list[$index]->photos = $photo;

            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->fa_created_at = jdf::jdate('j F Y', Carbon::createFromFormat('Y-m-d H:i:s', $row->created_at_2)->getTimestamp());

            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }
}
