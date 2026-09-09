<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\AdsLikes;
use App\Models\User;
use Illuminate\Http\Request;

class AdsLikesController extends Controller
{
    public function toggleLike(Request $request , Ads $ads){
        try{
            $user = auth('sanctum')->user();
        } catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
        $like = AdsLikes::where('user_id' , $user->id)->where('ads_id' , $ads->id)->first();
        if(!$like){
            $like = new AdsLikes();
            $like->user_id = $user->id;
            $like->ads_id = $ads->id;
            $like->like_type = $request->like_type;
        }else{
            if($like->like_type == 'like'){
                $like->like_type = 'dislike';
            }else{
                $like->like_type = 'like';
            }
        }
        $like->save();
        $likesCount = AdsLikes::where('ads_id' , $ads->id)->where('like_type' , 'like')->count();
        $dislikesCount = AdsLikes::where('ads_id' , $ads->id)->where('like_type' , 'dislike')->count();

        return response()->json(['status' => 200 , 'like_type' => $like->like_type , 'likes_count' => $likesCount , 'dislikes_count' => $dislikesCount]);
    }

    public function getLikesAndDislikesCount(Request $request , Ads $ads){
        $likes = AdsLikes::where('ads_id' , $ads->id)->where('like_type' , 'like')->count();
        $dislikes = AdsLikes::where('ads_id' , $ads->id)->where('like_type' , 'dislike')->count();

        return response()->json(['status' => 200 , 'likes' => $likes , 'dislike' => $dislikes]);
    }

    public function LikeOff(Request $request , Ads $ads){
        try{
            $user = auth('sanctum')->user();
        } catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
        $like = AdsLikes::where('like_type' , $request->like_type)->where('ads_id' , $ads->id)
            ->where('user_id' , $user->id)->first();
        $like->delete();

        return response()->json(['status' => 204]);
    }

    public function xyz(Request $request){
        $user = auth('sanctum')->user();
        $like = new AdsLikes();
        $like->user_id = $user->id;
        $like->post_id = 12;
        $like->save();

        return response()->json(['status' => 200 , 'like' => 1]);
    }
}
