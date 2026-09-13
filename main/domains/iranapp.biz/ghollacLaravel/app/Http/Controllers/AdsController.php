<?php

namespace App\Http\Controllers;

use App\Ads;
use App\AdsLikes;
use App\AdsPhoto;
use App\AdsPlan;
use App\Category;
use App\City;
use App\FavoriteAds;
use App\Http\Requests\Admin\SaveAds;
use App\Http\Requests\Admin\updateAd;
use App\Libraries\jdf;
use App\Notification;
use App\Province;
use App\SubCategory;
use App\User;
use App\UserAds;
use App\VipAds;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;
use Intervention\Image\Facades\Image;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Facades\JWTAuth;

class AdsController extends Controller {
	/**
	 * Public listings accept a token but do not require one: signed-in users get their own
	 * vote back on every row, guests just get null instead of being turned away.
	 */
	private function optionalUser() {
		try {
			return JWTAuth::parseToken()->authenticate();
		} catch ( \Exception $e ) {
			return null;
		}
	}

	private function optionalUserId() {
		$user = $this->optionalUser();

		return $user ? $user->id : null;
	}

	public function getBySubCategoryId( Request $request , $cityId , $subCategoryId ) {
		$ads  = new Ads();
		$list = $ads->selectFields( Ads::FIELDS )->getApproved()->getValidAds()
		            ->withUserLikeStatus( $this->optionalUserId() )->orderByLikes()->getQuery();
		$list = $list->where( 'ads.sub_category_id' , '=' , $subCategoryId )
		             ->where( 'ads.city_id' , '=' , $cityId )
		             ->selectRaw( 'ads.* , category.name as category_name , sub_category.name as sub_category_name , city.name as city_name , province.name as province_name , ads_plan.num_of_stars , ads_plan.ordering_factor , ads_plan.plan_title' );
		if ( $request->has( 'type' ) && in_array( $request->type , [ 'discount' , 'need' ] ) ) {
			$list = $list->where( 'ads.type' , '=' , $request->type );
		}
		if ( $request->has( 'offset' ) && $request->has( 'limit' ) ) {
			$offset = $request->offset;
			$limit  = $request->limit;
		} else {
			$offset = 0;
			$limit  = 1;
		}
		$list = $list->offset( $offset )->limit( $limit )
		             ->get();
		foreach ( $list as $index => $row ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			foreach ( $photos as $pindex => $prow ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			}
			$list[ $index ]->photos = $photos;
		}
		$province = City::find( $cityId )->province;

		$adsObj = new VipAds();
		$vip    = $adsObj
			->selectFields( VipAds::FIELDS )
			->getValidAds()
			->getApproved()
			->getQuery()
			->whereRaw( '((
            (vip_ads.show_in_country = 1) or 
            (vip_ads.show_in_city = 1 and ads.city_id = ' . $cityId . ') or 
            (vip_ads.show_in_province = 1 and ads.city_id in ( select city.id from city where province_id = ' . $province->id . ' )  )
            ) and (
            (vip_ads.show_in_subcategory = 1 and ads.sub_category_id = ' . $subCategoryId . '  )
            ))' )
			->orderBy( DB::raw( 'RAND()' ) )
			->first();
		$vip    = VipAds::outputJson( $vip );

		return response()->json( [ 'status' => 200 , 'list' => $list , 'vip_ad' => $vip ] );
	}

	public function getLatest( Request $request ) {
		if ( $request->has( 'offset' ) && $request->has( 'limit' ) ) {
			$offset = $request->offset;
			$limit  = $request->limit;
		} else {
			$offset = 0;
			$limit  = 1;
		}
		$ads    = new Ads();
		$latest = $ads
			->selectFields( Ads::FIELDS )
			->getValidAds()
			->getApproved()
			->withUserLikeStatus( $this->optionalUserId() )
			->getQuery()
			->orderBy( 'ads.created_at' , 'desc' )
			->where( 'ads.city_id' , '=' , $request->city_id )
			->offset( $offset )->limit( $limit )
			->get();
		foreach ( $latest as $index => $row ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			foreach ( $photos as $pindex => $prow ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			}
			$latest[ $index ]->photos = $photos;
		}

		return response()->json( [ 'status' => 200 , 'list' => $latest ] );
	}

	public function getDiscount( Request $request ) {
		$adsObj   = new Ads();
		$discount = $adsObj->selectFields( Ads::FIELDS )
		                   ->getValidAds()
		                   ->getApproved()
		                   ->withUserLikeStatus( $this->optionalUserId() )
		                   ->orderByLikes()
		                   ->getQuery()
		                   ->where( 'ads.type' , '=' , 'discount' )
		                   ->get();
		foreach ( $discount as $index => $row ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			foreach ( $photos as $pindex => $prow ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			}
			$discount[ $index ]->photos = $photos;
		}

		return response()->json( [ 'status' => 200 , 'list' => $discount ] );
	}

	public function needs( Request $request ) {
		$adsObj = new Ads();
		$needs  = $adsObj->selectFields( Ads::FIELDS )
		                 ->getValidAds()
		                 ->getApproved()
		                 ->withUserLikeStatus( $this->optionalUserId() )
		                 ->orderByLikes()
		                 ->getQuery()
		                 ->where( 'ads.type' , '=' , 'need' )
		                 ->get();
		foreach ( $needs as $index => $row ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			foreach ( $photos as $pindex => $prow ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			}
			$needs[ $index ]->photos = $photos;
		}

		return response()->json( [ 'status' => 200 , 'list' => $needs ] );
	}

	public function getFavoritesAndLikes( Request $request , $adsId ) {
		try {
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

		}
		$fav = FavoriteAds::where( 'user_id' , $user->id )->where( 'ads_id' , $adsId )->first();
		if ( $fav ) {
			$isFav = 1;
		} else {
			$isFav = 0;
		}
		$like             = AdsLikes::where( 'user_id' , $user->id )->where( 'ads_id' , $adsId )->first();
		$adsLikesCount    = AdsLikes::where( 'ads_id' , $adsId )->where( 'like_type' , 'like' )->count();
		$adsdisLikesCount = AdsLikes::where( 'ads_id' , $adsId )->where( 'like_type' , 'dislike' )->count();
		if ( $like ) {
			$likee = $like->like_type;
		} else {
			$likee = null;
		}

		return response()->json( [
			'status'         => 200 ,
			'like'           => $likee ,
			'fav'            => $isFav ,
			'likes_count'    => $adsLikesCount ,
			'dislikes_count' => $adsdisLikesCount
		] );
	}

	public function search( Request $request ) {
		$ads  = new Ads();
		$list = $ads->selectFields( Ads::FIELDS )
		            ->getValidAds()
		            ->getApproved()
		            ->withUserLikeStatus( $this->optionalUserId() )
		            ->orderByLikes()
		            ->getQuery();
		if ( $request->has( 'title' ) ) {
			$list = $list->whereRaw( ' MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->title . '" IN NATURAL LANGUAGE MODE) ' );
		}
		if ( $request->has( 'subcategory_id' ) ) {
			$list = $list->where( 'ads.sub_category_id' , '=' , $request->subcategory_id );
		} elseif ( $request->has( 'category_id' ) ) {
			$list = $list->whereRaw( 'ads.sub_category_id in (select sub_category.id from sub_category where category_id = ' . $request->category_id . ')' );
		}
		if ( $request->has( 'city_id' ) ) {
			$list = $list->where( 'ads.city_id' , '=' , $request->city_id );
		} elseif ( $request->has( 'province_id' ) ) {
			$list = $list->whereRaw( 'ads.city_id in (select city.id from city where province_id = ' . $request->province_id . ')' );
		}
		if ( $request->has( 'address' ) ) {
			$list = $list->whereRaw( 'ads.address like "%' . $request->address . '%"' );
		}
		if ( $request->has( 'type' ) && in_array( $request->type , [ 'need' , 'discount' ] ) ) {
			$list = $list->where( 'ads.type' , '=' , $request->type );
		}
		if ( $request->has( 'offset' ) && $request->has( 'limit' ) ) {
			$offset = $request->offset;
			$limit  = $request->limit;
		} else {
			$offset = 0;
			$limit  = 1;
		}
		$list          = $list->offset( $offset )->limit( $limit )->get();
		$adsCollection = collect( $list );
		$adsCollection->each( function ( $row , $index ) use ( $adsCollection ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			foreach ( $photos as $pindex => $prow ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			}
			$adsCollection[ $index ]->photos = $photos;
		} );

		return response()->json( [ 'status' => 200 , 'list' => $adsCollection ] );
	}

	public function saveAd( Request $request ) {
		try {
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

		}
		$ads        = new Ads();
		$ads->title = $request->title;
		if ( $request->has( 'latitude' ) ) {
			$ads->latitude = $request->latitude;
		}
		if ( $request->has( 'longitude' ) ) {
			$ads->longitude = $request->longitude;
		}
		$ads->city_id         = $request->city_id;
		$ads->address         = $request->address;
		$ads->type            = $request->type;
		$ads->sub_category_id = $request->sub_category_id;
		$ads->email           = $request->email;
		$ads->mobile          = $request->mobile;
		$ads->tel1            = $request->tel1;
		$ads->tel2            = $request->tel2;
		$ads->link            = $request->link;
		if ( $request->type == 'discount' ) {
			$ads->discount = $request->discount;
		}
		$ads->ads_owner_name = $request->ads_owner_name;
		$ads->working_time   = $request->working_time;
		$ads->telegram       = $request->telegram;
		$ads->instagram      = $request->instagram;
		$ads->notes          = $request->notes;
		$ads->ads_plan_id    = $request->ads_plan_id;
		$ads->shaba          = "IR" . $request->shaba;
		$ads->user_id        = $user->id;
		$plan                = AdsPlan::find( $request->ads_plan_id );
		$ads->valid_since    = Carbon::now()->toDateTimeString();
		$ads->valid_until    = Carbon::now()->addDays( $plan->interval_days )->toDateTimeString();
		$ads->status         = 'pending';
		$success             = $ads->save();
		$ads->userAds()->sync( $user->id );

		if ( $request->hasFile( 'photos' ) ) {
			$photos = $request->photos;
			for ( $i = 0 ; $i < $plan->max_number_of_photos ; $i ++ ) {
				if ( isset( $photos[ $i ] ) ) {
					$photoItem = $photos[ $i ];
					$imgName   = uniqid() . '.' . $photoItem->getClientOriginalExtension();
					$photoItem->move( public_path( 'ads_photo' ) , $imgName );

					$imageToAddWatermark = Image::make( public_path( '/ads_photo/' ) . '/' . $imgName );
					$imageToAddWatermark->insert( public_path( '/ads_photo/watermark2.png' ) , 'bottom-left' , 0 , 25 );
					$imageToAddWatermark->save();

					$photoRow            = new AdsPhoto();
					$photoRow->ads_id    = $ads->id;
					$photoRow->file_name = $imgName;
					$photoRow->save();
				}
			}
		}
		$adsPhotos = AdsPhoto::where( 'ads_id' , $ads->id )->get();
		$adsPhotos->each( function ( $row , $index ) use ( $adsPhotos ) {
			$adsPhotos[ $index ]->file_name = URL::to( '/ads_photo' ) . '/' . $row->file_name;
		} );
		$ads->photos = $adsPhotos;

		if ( $success ) {
			return response()->json( [ 'status' => 200 , 'ads' => $ads ] );
		} else {
			return response()->json( [ 'status' => 501 , 'error' => 'server_error' ] );
		}
	}

	public function getCurrentUserAds( Request $request ) {
		try {
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

		}
		if ( $request->has( 'offset' ) && $request->has( 'limit' ) ) {
			$offset = $request->offset;
			$limit  = $request->limit;
		} else {
			$offset = 0;
			$limit  = 1;
		}
		$adsObj = new UserAds();
		$ads    = $adsObj->selectFields( UserAds::FIELDS )
		                 ->getQuery()
		                 ->addSelect( DB::raw( '( select like_type from ads_like
                where ads_like.ads_id = ads.id and ads_like.user_id = ' . intval( $user->id ) . ' limit 1 ) as user_like_type' ) )
		                 ->where( 'user_ads.user_id' , '=' , $user->id )
		                 ->offset( $offset )->limit( $limit )
		                 ->get();
		$ads    = collect( $ads );
		$ads->each( function ( $item , $index ) use ( $ads ) {
			$photos = AdsPhoto::where( 'ads_id' , $item->id )->get();
			$photos->each( function ( $pitem , $pindex ) use ( $photos ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $pitem->file_name;
			} );
			$user                      = JWTAuth::parseToken()->authenticate();
			$ads[ $index ]->user_id    = $user->id;
			$ads[ $index ]->photos     = $photos;
			$ads[ $index ]->created_at = jdf::jdate( 'j F Y' , Carbon::createFromFormat( 'Y-m-d H:i:s' , $item->created_at )->getTimestamp() );
			$ads[ $index ]->updated_at = jdf::jdate( 'j F Y' , Carbon::createFromFormat( 'Y-m-d H:i:s' , $item->updated_at )->getTimestamp() );
		} );


		return response()->json( [ 'status' => 200 , 'list' => $ads ] );
	}

	public function nearBy( Request $request ) {
		$latitude  = $request->latitude;
		$longitude = $request->longitude;
		$radius    = 2;
		$adsObj    = new Ads();
		$ads       = $adsObj->selectFields( Ads::FIELDS )
		                    ->getApproved()
		                    ->getValidAds()
		                    ->withUserLikeStatus( $this->optionalUserId() )
		                    ->orderByLikes()
		                    ->getQuery()
		                    ->addSelect( DB::raw( "( 6371 * acos( cos( radians(" . $latitude . ") ) * cos( radians( ads.latitude ) ) * cos( radians( ads.longitude ) - radians(" . $longitude . ") ) + sin( radians(" . $latitude . ") ) * sin( radians( ads.latitude ) ) ) ) AS distance" ) )
		                    ->whereRaw( "( 6371 * acos( cos( radians(" . $latitude . ") ) * cos( radians( ads.latitude ) ) * cos( radians( ads.longitude ) - radians(" . $longitude . ") ) + sin( radians(" . $latitude . ") ) * sin( radians( ads.latitude ) ) ) ) < " . $radius );

		if ( $request->has( 'sub_category_id' ) ) {
			$ads = $ads->where( 'ads.sub_category_id' , '=' , $request->sub_category_id );
		} elseif ( $request->has( 'category_id' ) ) {
			$ads = $ads->where( 'category.id' , '=' , $request->category_id );
		}
		$ads = $ads->get();
		$ads = collect( $ads );
		$ads->each( function ( $row , $index ) use ( $ads ) {
			$photos = AdsPhoto::where( 'ads_id' , $row->id )->get();
			$photos->each( function ( $prow , $pindex ) use ( $photos ) {
				$photos[ $pindex ]->file_name = URL::to( '/ads_photo' ) . '/' . $prow->file_name;
			} );
			$ads[ $index ]->photos     = $photos;
			$ads[ $index ]->created_at = jdf::jdate( 'j F Y' , Carbon::createFromFormat( 'Y-m-d H:i:s' , $row->created_at )->getTimestamp() );
			$ads[ $index ]->updated_at = jdf::jdate( 'j F Y' , Carbon::createFromFormat( 'Y-m-d H:i:s' , $row->updated_at )->getTimestamp() );
		} );

		return response()->json( [ 'status' => 200 , 'list' => $ads ] );
	}

	public function getById( Request $request , $adsId ) {
		$adsObj  = new Ads();
		$adsItem = $adsObj->selectFields( Ads::FIELDS )
		                  ->withUserLikeStatus( $this->optionalUserId() )
		                  ->getQuery()
		                  ->where( 'ads.id' , '=' , $adsId )
		                  ->first();
		$photos  = AdsPhoto::where( 'ads_id' , $adsId )->get();
		$photos->each( function ( $item , $index ) use ( $photos ) {
			$photos[ $index ]->file_name = URL::to( '/ads_photo' ) . '/' . $item->file_name;
		} );
		$adsItem->photos = $photos;

		return response()->json( [ 'status' => 200 , 'ad' => $adsItem ] );
	}

	public function delete( Request $request , Ads $ads ) {
		$ads->delete();

		return response()->json( [ 'status' => 204 ] );
	}

	public function updateInApi( Request $request , Ads $ads ) {
		try {
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

		}
		$plan = AdsPlan::find( $ads->ads_plan_id );
		if ( $ads->updates_count >= $plan->num_of_updates ) {
			return response()->json( [ 'status' => 401 , 'error' => 'maximum_limit_reached' ] );
		}
		$ads->title           = $request->title;
		$ads->latitude        = $request->latitude;
		$ads->longitude       = $request->longitude;
		$ads->city_id         = $request->city_id;
		$ads->address         = $request->address;
		$ads->type            = $request->type;
		$ads->sub_category_id = $request->sub_category_id;
		$ads->email           = $request->email;
		$ads->mobile          = $request->mobile;
		$ads->tel1            = $request->tel1;
		$ads->tel2            = $request->tel2;
		$ads->link            = $request->link;
		$ads->ads_owner_name  = $request->ads_owner_name;
		if ( $request->type == 'discount' ) {
			$ads->discount = $request->discount;
		}
		$ads->working_time  = $request->working_time;
		$ads->telegram      = $request->telegram;
		$ads->instagram     = $request->instagram;
		$ads->notes         = $request->notes;
		$ads->updates_count += 1;
		$ads->status        = 'pending';
		$success            = $ads->save();
		if ( $request->hasFile( 'new_photos' ) ) {
			foreach ( $request->new_photos as $photo ) {
				$imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
				$photo->move( public_path( 'ads_photo' ) , $imgName );

				$imageToAddWatermark = Image::make( public_path( '/ads_photo/' ) . '/' . $imgName );
				$imageToAddWatermark->insert( public_path( '/ads_photo/watermark2.png' ) , 'bottom-left' , 0 , 25 );
				$imageToAddWatermark->save();

				$photoRow            = new AdsPhoto();
				$photoRow->ads_id    = $ads->id;
				$photoRow->file_name = $imgName;
				$photoRow->save();
			}
		}

		if ( $request->has( 'photos_to_edit_id' ) && $request->hasFile( 'photo_to_edit' ) ) {
			$idToEdit    = $request->photos_to_edit_id;
			$photoToEdit = $request->photo_to_edit;
			foreach ( $idToEdit as $index => $id ) {
				$imgName = uniqid() . '.' . $photoToEdit[ $index ]->getClientOriginalExtension();
				$photoToEdit[ $index ]->move( public_path( 'ads_photo' ) , $imgName );

				$imageToAddWatermark = Image::make( public_path( '/ads_photo/' ) . '/' . $imgName );
				$imageToAddWatermark->insert( public_path( '/ads_photo/watermark2.png' ) , 'bottom-left' , 0 , 25 );
				$imageToAddWatermark->save();

				$photoRow            = AdsPhoto::find( $id );
				$photoRow->file_name = $imgName;
				$photoRow->save();
			}
		}
		if ( $request->has( 'ids_to_delete' ) ) {
			$oldPhotos = AdsPhoto::find( $request->ids_to_delete );
			foreach ( $oldPhotos as $oldPhoto ) {
				$oldPhoto->delete();
			}
		}
		if ( $success ) {
			return response()->json( [ 'status' => 200 , 'ads' => $ads ] );
		} else {
			return response()->json( [ 'status' => 501 , 'error' => 'server_error' ] );
		}
	}

	public function showInsertPageInAdminPanel( Request $request ) {
		$plans                = AdsPlan::where( 'deleted' , '!=' , 1 )->get();
		$data[ 'plans' ]      = $plans;
		$data[ 'provinces' ]  = Province::all();
		$data[ 'categories' ] = Category::all();
		$data[ 'users' ]      = User::all();

		return view( 'admin.ads_insert_page' )->with( $data );
	}

	public function saveAdsInAdminPanel( SaveAds $request ) {
		$ads        = new Ads();
		$ads->title = $request->title;
		if ( $request->has( 'latitude' ) ) {
			$ads->latitude = $request->latitude;
		} else {
			$ads->latitude = null;
		}

		if ( $request->has( 'longitude' ) ) {
			$ads->longitude = $request->longitude;
		} else {
			$ads->longitude = null;
		}

		$ads->city_id = $request->city_id;
//        $ads->user_id = $request->user_id;
		$ads->address         = $request->address;
		$ads->type            = $request->type;
		$ads->sub_category_id = $request->sub_category_id;
		$ads->email           = $request->email;
		$ads->mobile          = $request->mobile;
		$ads->tel1            = $request->tel1;
		$ads->tel2            = $request->tel2;
		$ads->link            = $request->link;
		$ads->discount        = $request->discount;
		$ads->working_time    = $request->working_time;
		$ads->telegram        = $request->telegram;
		$ads->instagram       = $request->instagram;
		$ads->notes           = $request->notes;
		$ads->status          = $request->status;
		$ads->ads_plan_id     = $request->ads_plan_id;
		$ads->ads_owner_name  = $request->ads_owner_name;

		$plan             = AdsPlan::find( $request->ads_plan_id );
		$ads->valid_since = Carbon::now()->toDateString();
		$ads->valid_until = Carbon::now()->addDays( $plan->interval_days )->toDateString();
		$ads->save();
		$ads->userAds()->attach( $request->user_id );

		if ( $request->has( 'send_notification' ) && ( $ads->status == 'approved' ) ) {
//            $fcm_tokens = DB::table('users')->where('users.send_ads_notifications' , '=' , 1)->pluck('fcm_token')->toArray();
			$fcm_tokens = DB::table( 'notification_setting' )->where( 'send_ads_notifications' , '=' , 1 )
			                ->where( 'notification_setting.city_id' , '=' , $ads->city_id )
			                ->pluck( 'fcm_token' )->toArray();
			if ( count( $fcm_tokens ) > 0 ) {
				$optionBuilder = new OptionsBuilder();
				$optionBuilder->setTimeToLive( 60 * 20 );

				$notificationBuilder = new PayloadNotificationBuilder( 'ایران اپ' );
				$notificationBuilder->setBody( $ads->title )
				                    ->setSound( 'default' );

				$dataBuilder = new PayloadDataBuilder();
				$dataBuilder->addData( [ 'status' => '0' , 'content_id' => $ads->id ] );
				$data         = $dataBuilder->build();
				$option       = $optionBuilder->build();
				$notification = $notificationBuilder->build();

				$downstreamResponse = FCM::sendTo( $fcm_tokens , $option , $notification , $data );

				$downstreamResponse->numberFailure();
				$downstreamResponse->numberModification();
				$downstreamResponse->numberSuccess();

				$subCategory = SubCategory::find( $ads->sub_category_id );
				$category    = $subCategory->category;

				$notification                    = new Notification();
				$notification->category_id       = $category->id;
				$notification->msg_text          = $ads->title;
				$notification->successfully_sent = $downstreamResponse->numberSuccess();
				$notification->failures_on_send  = $downstreamResponse->numberFailure();
				$notification->save();
			}

		}

		$msg        = new \stdClass();
		$msg->title = 'ثبت موفقیت آمیز';
		$msg->msg   = 'آگهی مورد نظر با موفقیت ثبت شد.';

		return redirect()->route( 'showAdsPhotoById' , $ads->id )->with( 'success_msg' , $msg );
	}

	public function showListOfAdsInAdminPanel( Request $request ) {
		$adsObj = new Ads();
		$ads    = $adsObj->selectFields( Ads::FIELDS )
		                 ->getQuery();
		if ( $request->has( 'plan_id' ) && $request->plan_id != 'all' ) {
			$ads = $ads->where( 'ads.ads_plan_id' , '=' , $request->plan_id );
		}
		if ( $request->has( 'city_id' ) && $request->city_id != 'all' ) {
			$ads = $ads->where( 'ads.city_id' , '=' , $request->city_id );
		}
		if ( $request->has( 'sub_category_id' ) && $request->sub_category_id != 'all' ) {
			$ads = $ads->where( 'ads.sub_category_id' , '=' , $request->sub_category_id );
		}
		if ( $request->has( 'status' ) && $request->status != 'all' ) {
			$ads = $ads->where( 'ads.status' , '=' , $request->status );
		}
		if ( $request->has( 'keyword' ) ) {
			$ads = $ads->whereRaw( ' MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ' )
			           ->addSelect( DB::raw( '( MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ) as relevance' ) )
			           ->orderBy( 'relevance' , 'desc' );
		} else {
			$ads = $ads->orderBy( 'ads.created_at' , 'desc' );
		}
		$ads                  = $ads->where( 'type' , 'need' );
		$ads                  = $ads->paginate( 15 );
		$data[ 'ads' ]        = $ads;
		$data[ 'plans' ]      = AdsPlan::where( 'deleted' , 0 )->get();
		$data[ 'provinces' ]  = Province::all();
		$data[ 'categories' ] = Category::all();

		return view( 'admin.ads_list' )->with( $data );
	}

	public function showListOfAdsDiscountInAdminPanel( Request $request ) {
		$adsObj = new Ads();
		$ads    = $adsObj->selectFields( Ads::FIELDS )
		                 ->getQuery();
		if ( $request->has( 'plan_id' ) && $request->plan_id != 'all' ) {
			$ads = $ads->where( 'ads.ads_plan_id' , '=' , $request->plan_id );
		}
		if ( $request->has( 'city_id' ) && $request->city_id != 'all' ) {
			$ads = $ads->where( 'ads.city_id' , '=' , $request->city_id );
		}
		if ( $request->has( 'sub_category_id' ) && $request->sub_category_id != 'all' ) {
			$ads = $ads->where( 'ads.sub_category_id' , '=' , $request->sub_category_id );
		}
		if ( $request->has( 'status' ) && $request->status != 'all' ) {
			$ads = $ads->where( 'ads.status' , '=' , $request->status );
		}
		if ( $request->has( 'keyword' ) ) {
			$ads = $ads->whereRaw( ' MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ' )
			           ->addSelect( DB::raw( '( MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ) as relevance' ) )
			           ->orderBy( 'relevance' , 'desc' );
		} else {
			$ads = $ads->orderBy( 'ads.created_at' , 'desc' );
		}
		$ads                  = $ads->where( 'type' , 'discount' );
		$ads                  = $ads->paginate( 15 );
		$data[ 'ads' ]        = $ads;
		$data[ 'plans' ]      = AdsPlan::where( 'deleted' , 0 )->get();
		$data[ 'provinces' ]  = Province::all();
		$data[ 'categories' ] = Category::all();

		return view( 'admin.ads_list' )->with( $data );
	}

	public function showAdsUpdatePage( Request $request , Ads $ads ) {
		$plans                      = AdsPlan::where( 'deleted' , '!=' , 1 )->get();
		$data[ 'plans' ]            = $plans;
		$data[ 'provinces' ]        = Province::all();
		$data[ 'categories' ]       = Category::all();
		$currentProvince            = City::find( $ads->city_id )->province;
		$data[ 'cities' ]           = $currentProvince->city;
		$data[ 'current_province' ] = $currentProvince;
		$currentCategory            = SubCategory::find( $ads->sub_category_id )->category;
		$data[ 'current_category' ] = $currentCategory;
		$data[ 'sub_categories' ]   = $currentCategory->subCategories;
		$data[ 'users' ]            = User::all();
		$data[ 'ads' ]              = $ads;
		$data[ 'ad_plan' ]          = $ads->plan;
		$data[ 'photos' ]           = AdsPhoto::where( 'ads_id' , $ads->id )->get();

		$data[ 'user_ads' ] = UserAds::where( 'ads_id' , $ads->id )
		                             ->join( 'users' , 'users.id' , '=' , 'user_ads.user_id' )
		                             ->pluck( 'user_ads.user_id' )
		                             ->toArray();

		return view( 'admin.ads_update_page' )->with( $data );
	}

	public function updateAdsInAdminPanel( updateAd $request , Ads $ads ) {
		$plan = AdsPlan::find( $request->ads_plan_id );
		if ( $request->has( 'add_update_counter' ) ) {
			if ( $ads->updates_count >= $plan->num_of_updates ) {
				$msg        = new \stdClass();
				$msg->title = 'خطا';
				$msg->msg   = 'تعداد به روز رسانی های آگهی بیشتر از حد مجاز است.';

				return redirect()->back()->with( 'error_msg' , $msg );
			} else {
				$ads->updates_count += 1;
			}
		}
		$ads->title = $request->title;
		if ( $request->has( 'latitude' ) ) {
			$ads->latitude = $request->latitude;
		} else {
			$ads->latitude = null;
		}

		if ( $request->has( 'longitude' ) ) {
			$ads->longitude = $request->longitude;
		} else {
			$ads->longitude = null;
		}

		$ads->city_id         = $request->city_id;
		$ads->address         = $request->address;
		$ads->type            = $request->type;
		$ads->sub_category_id = $request->sub_category_id;
		$ads->email           = $request->email;
		$ads->mobile          = $request->mobile;
		$ads->tel1            = $request->tel1;
		$ads->tel2            = $request->tel2;
		$ads->link            = $request->link;
		$ads->discount        = $request->discount;
		$ads->working_time    = $request->working_time;
		$ads->telegram        = $request->telegram;
		$ads->instagram       = $request->instagram;
		$ads->notes           = $request->notes;
		$ads->status          = $request->status;
		$ads->ads_owner_name  = $request->ads_owner_name;
		$ads->ads_plan_id     = $request->ads_plan_id;
		$ads->shaba           = $request->shaba;
//        $ads->user_id = $request->user_id;
		$ads->save();
		$ads->userAds()->sync( $request->user_id );

		if ( $request->has( 'send_notification' ) && ( $ads->status == 'approved' ) ) {
//            $fcm_tokens = DB::table('users')->where('users.send_ads_notifications' , '=' , 1)->pluck('fcm_token')->toArray();
			$fcm_tokens = DB::table( 'notification_setting' )
			                ->where( 'send_ads_notifications' , '=' , 1 )
			                ->where( 'notification_setting.city_id' , '=' , $ads->city_id )
			                ->pluck( 'fcm_token' )->toArray();
			if ( count( $fcm_tokens ) > 0 ) {
				$optionBuilder = new OptionsBuilder();
				$optionBuilder->setTimeToLive( 60 * 20 );

				$notificationBuilder = new PayloadNotificationBuilder( 'ایران اپ' );
				$notificationBuilder->setBody( $ads->title )
				                    ->setSound( 'default' );

				$dataBuilder = new PayloadDataBuilder();
				$dataBuilder->addData( [ 'status' => '0' , 'content_id' => $ads->id ] );
				$data         = $dataBuilder->build();
				$option       = $optionBuilder->build();
				$notification = $notificationBuilder->build();

				$downstreamResponse = FCM::sendTo( $fcm_tokens , $option , $notification , $data );

				$downstreamResponse->numberFailure();
				$downstreamResponse->numberModification();
				$downstreamResponse->numberSuccess();

				$subCategory = SubCategory::find( $ads->sub_category_id );
				$category    = $subCategory->category;

				$notification                    = new Notification();
				$notification->category_id       = $category->id;
				$notification->msg_text          = $ads->title;
				$notification->successfully_sent = $downstreamResponse->numberSuccess();
				$notification->failures_on_send  = $downstreamResponse->numberFailure();
				$notification->save();
			}

		}

		$msg        = new \stdClass();
		$msg->title = 'ویرایش موفقیت آمیز';
		$msg->msg   = 'آگهی مورد نظر با موفقیت ویرایش شد.';
		$plan       = AdsPlan::find( $ads->ads_plan_id );
		if ( $plan->max_number_of_photos == $ads->photo->count() ) {
			return redirect()->route( 'showAdsListInAdminPanel' )->with( 'success_msg' , $msg );
		}

		return redirect()->route( 'showAdsPhotoById' , $ads->id )->with( 'success_msg' , $msg );


	}

	public function reValidateAd( Request $request , Ads $ads ) {
		$oldDate          = Carbon::createFromFormat( 'Y-m-d' , $ads->valid_until );
		$ads->valid_until = $oldDate->addDays( $request->days )->toDateString();
		$ads->save();

		$msg        = new \stdClass();
		$msg->title = 'به روز رسانی موفقیت آمیز';
		$msg->msg   = 'آگهی به مدت ' . $request->days . ' روز تمدید شد.';

		return redirect()->back()->with( 'success_msg' , $msg );
	}

	public function deleteById( Request $request , Ads $ads ) {
		$ads->delete();
		$msg        = new \stdClass();
		$msg->title = 'حذف موفقیت آمیز';
		$msg->msg   = 'آگهی مورد نظر با موفقیت حذف شد.';

		return redirect()->back()->with( 'success_msg' , $msg );
	}

	public function doSomeActionsOnAds( Request $request ) {
		$allAds = $request->ads_id;
		$action = $request->action;
		if ( $action != 'delete' ) {
			return redirect()->back();
		}
		foreach ( $allAds as $adsId ) {
			$ads = Ads::find( $adsId );
			$ads->delete();
		}
		if ( count( $allAds ) > 0 ) {
			$msg        = new \stdClass();
			$msg->title = 'حذف موفقیت آمیز';
			$msg->msg   = 'آگهی های مورد نظر حذف شد.';

			return redirect()->back()->with( 'success_msg' , $msg );
		} else {
			return redirect()->back();
		}

	}

	public function showExpiringAds( Request $request ) {
		$adsObj = new Ads();
		if ( ! $request->has( 'filter' ) || $request->filter != 'doFilter' ) {
			$data[ 'ads' ]        = null;
			$data[ 'filter_set' ] = 0;
		} else {
			if ( $request->has( 'interval_days' ) && $request->interval_days != 'without_selection' ) {
				$data[ 'filter_set' ] = 1;
				$ads                  = $adsObj->selectFields( Ads::FIELDS )
				                               ->getQuery();
				if ( $request->interval_days == 'expired' ) {
					$ads = $ads->where( 'ads.valid_until' , '<' , Carbon::now()->toDateString() );
				} else {
					$intervalDays = (int) $request->interval_days;
					$ads          = $ads->where( 'ads.valid_until' , '>' , Carbon::now()->toDateString() )
					                    ->where( 'ads.valid_until' , '<' , Carbon::now()->addDays( $intervalDays )->toDateString() );

				}

				$ads           = $ads->paginate( 15 );
				$data[ 'ads' ] = $ads;
			} else {
				$data[ 'filter_set' ] = 0;
				$data[ 'ads' ]        = null;
			}

		}

		return view( 'admin.expiring_ads' )->with( $data );
	}

	public function getViaAjax( Request $request ) {
		if ( $request->has( 'q' ) ) {
			$ads = Ads::where( 'title' , 'like' , "%" . $request->q . "%" )->get();
		}

		return response()->json( $ads );
	}
}
