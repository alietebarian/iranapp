<?php

use App\Http\Controllers\AdminController;
use App\Http\Controllers\AdsController;
use App\Http\Controllers\AdsLikesController;
use App\Http\Controllers\AdsPhotoController;
use App\Http\Controllers\AdsPlanController;
use App\Http\Controllers\Auth\LoginController as AuthLoginController;
use App\Http\Controllers\BrandController;
use App\Http\Controllers\CategoryController;
use App\Http\Controllers\CityController;
use App\Http\Controllers\CommonController;
use App\Http\Controllers\CylinderVolumesController;
use App\Http\Controllers\EmploysAdsController;
use App\Http\Controllers\EmploysAdsPhotoController;
use App\Http\Controllers\EmploysAdsSpecialityController;
use App\Http\Controllers\EstateAdsCategoriesController;
use App\Http\Controllers\EstateAdsController;
use App\Http\Controllers\EstateAdsPhotoController;
use App\Http\Controllers\FavoriteAdsController;
use App\Http\Controllers\NewsController;
use App\Http\Controllers\NewsPhotoController;
use App\Http\Controllers\NotificationSettingController;
use App\Http\Controllers\ProvinceController;
use App\Http\Controllers\RegionController;
use App\Http\Controllers\SubCategoryController;
use App\Http\Controllers\UserAdsNotificationController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\VehicleAdsController;
use App\Http\Controllers\VehicleAdsPhotoController;
use App\Http\Controllers\VehicleModelController;
use App\Http\Controllers\VipAdsController;
use App\Http\Controllers\VipAdsFavoriteController;
use App\Http\Controllers\VisitorController;
use App\Http\Controllers\webazin\admin\Awards\AwardsController as AwardsAwardsController;
use App\Http\Controllers\webazin\api\AwardController as ApiAwardController;
use App\Http\Controllers\webazin\api\DiscountController as ApiDiscountController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
| Mobile JSON API, ported from the legacy routes/web.php `prefix => api` group.
| The /api prefix is applied by the framework, so it is not repeated here.
*/

Route::group( [] , function () {
		Route::get( '/app-version' , [CommonController::class, 'appVersionShow'] );
		Route::post( '/register' , [UserController::class, 'register'] );
		Route::put( '/users/verify-token/regenerate' , [UserController::class, 'reGenerateVerifyToken'] );
		Route::post( '/verify-and-login' , [UserController::class, 'verifyAndLogin'] );
		Route::post( '/login' , [UserController::class, 'doLogin'] );
		Route::put( '/users/mobile/change' , [UserController::class, 'updateMobileNumber'] );
		Route::put( '/users/password/reset-password/token/send' , [UserController::class, 'sendResetPasswordToken'] );
		Route::get( '/reset-password/token/verify' , [UserController::class, 'verifyResetPasswordToken'] );
		Route::put( '/reset-password' , [UserController::class, 'resetPassword'] );
		Route::get( '/cylinder-volumes' , [CylinderVolumesController::class, 'showJson'] );
//        Route::get('/categories' , [CategoryController::class, 'showAllWithRandomMainPageVipAd']);
		Route::get( 'categories/{categoryId}/subcategories' , [SubCategoryController::class, 'getByCategoryId'] );
		Route::get( '/provinces' , [ProvinceController::class, 'showAll'] );
		Route::get( '/provinces/{provinceId}/cities' , [CityController::class, 'showByProvinceId'] );
		Route::get( '/provinces/cities/{city}/regions' , [RegionController::class, 'getByCityId'] );
		Route::get( '/cities/{cityId}/subCategories/{subCategoryId}/ads' , [AdsController::class, 'getBySubCategoryId'] );
		Route::get( '/news' , [NewsController::class, 'showAll'] );
		Route::get( '/home-page/ads/{cityId}/vip' , [VipAdsController::class, 'getForMainPage'] );
		Route::get( '/ads/latest' , [AdsController::class, 'getLatest'] );
		Route::get( '/vip-ads/subcategories/{subcategoryId}/cities/{cityId}' , [VipAdsController::class, 'getForSubcategoryPage'] );
		Route::get( '/ads/discounts' , [AdsController::class, 'getDiscount'] );
		Route::get( '/ads/needs' , [AdsController::class, 'needs'] );
		Route::post( '/ads/search' , [AdsController::class, 'search'] );
		Route::get( '/categories' , [CategoryController::class, 'getAll'] );
		Route::get( '/categories/{categoryId}/subcategories/all' , [SubCategoryController::class, 'getByCategoryId2'] );
		Route::get( '/city/{cityId}/ads/category' , [CategoryController::class, 'getCategoryWithAdsInCity'] );
		Route::get( '/ads/plans' , [AdsPlanController::class, 'getAll'] );
		Route::get( '/nearest-ads' , [AdsController::class, 'nearBy'] );
		Route::get( '/ads/id/{ads}' , [AdsController::class, 'getById'] );
		Route::get( '/news/{news}' , [NewsController::class, 'getById'] );
		Route::post( '/notification-settings/create' , [NotificationSettingController::class, 'save'] )->name( 'saveFcmTokenOfUser' );
		Route::put( '/notification-settings/update' , [NotificationSettingController::class, 'update'] );
		Route::get( '/ads/{ads}/likes-and-dislikes' , [AdsLikesController::class, 'getLikesAndDislikesCount'] );
		Route::group( [ 'prefix' => 'vehicles' ] , function () {
			Route::get( '/brands' , [BrandController::class, 'showAllJson'] );
			Route::get( '/ads' , [VehicleAdsController::class, 'getJson'] );
			Route::get( '/brands/{brand}/models' , [VehicleModelController::class, 'getByBrandId'] );
			Route::post( '/search' , [VehicleAdsController::class, 'searchJson'] );
		} );
		Route::group( [ 'prefix' => 'estates' ] , function () {
			Route::get( '/city/{city}/ads' , [EstateAdsController::class, 'getJson'] );
			Route::post( '/search' , [EstateAdsController::class, 'searchJson'] );
		} );
		Route::group( [ 'prefix' => 'employs' ] , function () {
			Route::get( '/cities/{city}/ads' , [EmploysAdsController::class, 'getJsonByCityId'] );
			Route::get( '/specialities' , [EmploysAdsSpecialityController::class, 'showJson'] );
			Route::post( '/search' , [EmploysAdsController::class, 'searchJson'] );
		} );
		Route::any( 'webazin/discount/pay/callback/{uuid}' , [ApiDiscountController::class, 'callback'] )->name( 'webazin.discount.callback' );
	} );
	Route::group( [ 'middleware' => [ 'auth:sanctum' ] ] , function () {
		Route::post( '/distance' , [UserAdsNotificationController::class, 'add'] );
		Route::post( '/ads' , [AdsController::class, 'saveAd'] );
		Route::post( '/account-info/change' , [UserController::class, 'changeAccountInfo'] );
		Route::post( '/password/change' , [UserController::class, 'changePassword'] );
		Route::post( '/ads/{adId}/fav' , [FavoriteAdsController::class, 'makeAsFavorite'] );
		Route::post( '/ads/{adId}/warning' , [FavoriteAdsController::class, 'makewarning'] );
		Route::get( '/users/ads/favorites' , [FavoriteAdsController::class, 'gettingListByUserId'] );
		Route::get( '/users/ads/{adsId}' , [AdsController::class, 'getFavoritesAndLikes'] );
		Route::get( '/current-user/ads' , [AdsController::class, 'getCurrentUserAds'] );
		Route::post( '/ads/{ads}/likes' , [AdsLikesController::class, 'toggleLike'] );
		Route::get( '/ads/{ads}/delete' , [AdsController::class, 'delete'] );
		Route::post( '/ads/{ads}/update' , [AdsController::class, 'updateInApi'] );
		Route::put( '/users/notifications/setting' , [UserController::class, 'updateNotificationSetting'] )->name( 'updateUserNotificationSetting' );
		Route::get( '/ads/{ads}/like/off' , [AdsLikesController::class, 'LikeOff'] )->name( 'LikeAdsOff' );
		Route::post( '/vip-ads/favorite' , [VipAdsFavoriteController::class, 'saveJson'] );
		Route::get( '/vip-ads/favorite/check' , [VipAdsFavoriteController::class, 'isFavorite'] );
		Route::group( [ 'prefix' => 'vehicles' ] , function () {
			Route::post( '/' , [VehicleAdsController::class, 'saveJson'] );
			Route::post( '/{ads}/update' , [VehicleAdsController::class, 'updateJson'] );
			Route::get( '/users/ads' , [VehicleAdsController::class, 'getJsonByUser'] );
			Route::delete( '{ads}' , [VehicleAdsController::class, 'deleteJsonById'] );
		} );
		Route::group( [ 'prefix' => 'estates' ] , function () {
			Route::get( '/ads' , [EstateAdsController::class, 'getUserAds'] );
			Route::post( '/save' , [EstateAdsController::class, 'saveJson'] );
			Route::delete( '/ads/{ads}' , [EstateAdsController::class, 'delete'] );
			Route::post( '/ads/{ads}/update' , [EstateAdsController::class, 'updateJson'] );
			Route::post( '/search' , [EstateAdsController::class, 'searchJson'] );
		} );
		Route::group( [ 'prefix' => 'employs' ] , function () {
			Route::post( '/' , [EmploysAdsController::class, 'saveJson'] );
			Route::get( '/users/ads' , [EmploysAdsController::class, 'getJsonByUser'] );
			Route::delete( '/{ads}' , [EmploysAdsController::class, 'deleteJson'] );
			Route::post( '/{ads}/update' , [EmploysAdsController::class, 'updateJson'] );
		} );
		Route::group( [ 'prefix' => 'webazin' ] , function () {
			Route::get( 'discount/get/{ad_id}' , [ApiDiscountController::class, 'show'] );
			Route::post( 'discount/pay/{ad_id}' , [ApiDiscountController::class, 'pay'] )->name( 'webazin.discount.pay' );

			Route::get( 'awards' , [ApiAwardController::class, 'index'] );
			Route::get( 'awards/get/{id}' , [ApiAwardController::class, 'get'] );

			Route::get( 'wallet/count' , [ApiAwardController::class, 'userCount'] );
		} );
		Route::get( '/users/vehicle-ads/favorites' , [VipAdsFavoriteController::class, 'getUserVehicleAds'] );
		Route::get( '/users/estate-ads/favorites' , [VipAdsFavoriteController::class, 'getUserEstateAds'] );
		Route::get( '/users/employs-ads/favorites' , [VipAdsFavoriteController::class, 'getUserEmployAds'] );

	} );
	/*-------------------------------------------API visitor-----------------------------------------------------*/
	Route::post( 'visitor/login' , [VisitorController::class, 'login'] );

	Route::group( [ 'middleware' => [ 'auth:visitor' ] ] , function () {
		Route::post( 'visitor/store_ads' , [VisitorController::class, 'storeAds'] );
		Route::get( 'visitor/show_ads_count' , [VisitorController::class, 'showAdsCount'] );
		Route::get( 'visitor/show-users-ads' , [VisitorController::class, 'showAds'] );
	} );
