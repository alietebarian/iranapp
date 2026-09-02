<?php

namespace App\Http\Controllers;

use App\Ads;
use App\AdsLikes;
use App\User;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Facades\JWTAuth;

class AdsLikesController extends Controller
{
    public function toggleLike(Request $request , Ads $ads){
        try{
            $user = JWTAuth::parseToken()->authenticate();
        }catch (TokenExpiredException $e) {

            return response()->json(['status' => $e->getStatusCode() , 'error' => 'token_expired']);

        } catch (TokenInvalidException $e) {

            return response()->json(['status' => $e->getStatusCode() , 'error' => 'token_invalid']);

        } catch (JWTException $e) {

            return response()->json(['status' => $e->getStatusCode() ,  'error' => 'token_absent']);

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
            $user = JWTAuth::parseToken()->authenticate();
        }catch (TokenExpiredException $e) {
            return response()->json(['status' => $e->getStatusCode() , 'error' => 'token_expired']);
        } catch (TokenInvalidException $e) {
            return response()->json(['status' => $e->getStatusCode() , 'error' => 'token_invalid']);
        } catch (JWTException $e) {
            return response()->json(['status' => $e->getStatusCode() ,  'error' => 'token_absent']);
        }
        $like = AdsLikes::where('like_type' , $request->like_type)->where('ads_id' , $ads->id)
            ->where('user_id' , $user->id)->first();
        $like->delete();

        return response()->json(['status' => 204]);
    }

    public function xyz(Request $request){
        $user = JWTAuth::parseToken()->authenticate();
        $like = new AdsLikes();
        $like->user_id = $user->id;
        $like->post_id = 12;
        $like->save();

        return response()->json(['status' => 200 , 'like' => 1]);
    }
}
