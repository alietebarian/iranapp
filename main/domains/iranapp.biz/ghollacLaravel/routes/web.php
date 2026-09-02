<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get( 'chack' , function ( Request $request ) {
//	$pay                  = \App\Webazin\Pay\Pay::whereCode( 'efe340' )->first();
//	$payPing              = new App\Webazin\Payping\Payping();
//	$toman                = $pay->price / 10;
//	$percent              = 100 - $pay->ad->discount;
//	$priceWithOutDiscount = ( $toman * 100 ) / $percent; //get price discount //TODO
//	$priceDiscount        = $priceWithOutDiscount - $toman;
////		$discountPrice = ( $pay->ad->discount * 100 ) / 10; //get price discount //TODO
//	$price = ( 20 * $priceDiscount ) / 100; //get 90 percent off discount  TODO
////	send_sms( 'payed' , $pay->ad->mobile , $toman . 'تومان' );
//	// TODO calculate shaba amount and send sms
//	$payPing->setShaba( $pay->ad->shaba );
//	$payPing->setShabaAmount( (integer) ($toman - $price) );
//	$shaba = $payPing->shaba();
//	$shaba = json_decode( $shaba );
//	if ( isset( $shaba->code ) ) {
//		$pay->shaba_code = $shaba->code;
//	} else {
//		$error = 'تراکنش شبا برای پذیرنده انجام نشد '; //TODO set error
//	}
//
//	$pay->save();
//	$ad    = \App\Ads::find( 1595 );
//	$toman = 1000;
//	send_sms( 'payed' , $ad->mobile , str_replace(' ','_',$ad->title) , $toman . 'تومان' , 'امروز' , 'تخفیف' );
//
//	return 1;

	$toman                = $request->toman;
	$percent              = 100 - $request->discount;
	$priceWithOutDiscount = ( $toman * 100 ) / $percent; //get price discount //TODO
	$priceDiscount        = $priceWithOutDiscount - $toman;
//		$discountPrice = ( $pay->ad->discount * 100 ) / 10; //get price discount //TODO
	$price      = ( 20 * $priceDiscount ) / 100; //get 90 percent off discount  TODO
	$shabaPrice = $toman - $price;

	return view( 'webazin.chackDiscount' , compact( 'price' , 'shabaPrice' ) );
} )->name( 'webazin.discount.check' );
Route::group( [ 'prefix' => 'api' ] , function () {
	Route::group( [] , function () {
		Route::get( '/app-version' , 'CommonController@appVersionShow' );
		Route::post( '/register' , 'UserController@register' );
		Route::put( '/users/verify-token/regenerate' , 'UserController@reGenerateVerifyToken' );
		Route::post( '/verify-and-login' , 'UserController@verifyAndLogin' );
		Route::post( '/login' , 'UserController@doLogin' );
		Route::put( '/users/mobile/change' , 'UserController@updateMobileNumber' );
		Route::put( '/users/password/reset-password/token/send' , 'UserController@sendResetPasswordToken' );
		Route::get( '/reset-password/token/verify' , 'UserController@verifyResetPasswordToken' );
		Route::put( '/reset-password' , 'UserController@resetPassword' );
		Route::get( '/cylinder-volumes' , 'CylinderVolumesController@showJson' );
//        Route::get('/categories' , 'CategoryController@showAllWithRandomMainPageVipAd');
		Route::get( 'categories/{categoryId}/subcategories' , 'SubCategoryController@getByCategoryId' );
		Route::get( '/provinces' , 'ProvinceController@showAll' );
		Route::get( '/provinces/{provinceId}/cities' , 'CityController@showByProvinceId' );
		Route::get( '/provinces/cities/{city}/regions' , 'RegionController@getByCityId' );
		Route::get( '/cities/{cityId}/subCategories/{subCategoryId}/ads' , 'AdsController@getBySubCategoryId' );
		Route::get( '/news' , 'NewsController@showAll' );
		Route::get( '/home-page/ads/{cityId}/vip' , 'VipAdsController@getForMainPage' );
		Route::get( '/ads/latest' , 'AdsController@getLatest' );
		Route::get( '/vip-ads/subcategories/{subcategoryId}/cities/{cityId}' , 'VipAdsController@getForSubcategoryPage' );
		Route::get( '/ads/discounts' , 'AdsController@getDiscount' );
		Route::get( '/ads/needs' , 'AdsController@needs' );
		Route::post( '/ads/search' , 'AdsController@search' );
		Route::get( '/categories' , 'CategoryController@getAll' );
		Route::get( '/categories/{categoryId}/subcategories/all' , 'SubCategoryController@getByCategoryId2' );
		Route::get( '/city/{cityId}/ads/category' , 'CategoryController@getCategoryWithAdsInCity' );
		Route::get( '/ads/plans' , 'AdsPlanController@getAll' );
		Route::get( '/nearest-ads' , 'AdsController@nearBy' );
		Route::get( '/ads/id/{ads}' , 'AdsController@getById' );
		Route::get( '/news/{news}' , 'NewsController@getById' );
		Route::post( '/notification-settings/create' , 'NotificationSettingController@save' )->name( 'saveFcmTokenOfUser' );
		Route::put( '/notification-settings/update' , 'NotificationSettingController@update' );
		Route::get( '/ads/{ads}/likes-and-dislikes' , 'AdsLikesController@getLikesAndDislikesCount' );
		Route::group( [ 'prefix' => 'vehicles' ] , function () {
			Route::get( '/brands' , 'BrandController@showAllJson' );
			Route::get( '/ads' , 'VehicleAdsController@getJson' );
			Route::get( '/brands/{brand}/models' , 'VehicleModelController@getByBrandId' );
			Route::post( '/search' , 'VehicleAdsController@searchJson' );
		} );
		Route::group( [ 'prefix' => 'estates' ] , function () {
			Route::get( '/city/{city}/ads' , 'EstateAdsController@getJson' );
			Route::post( '/search' , 'EstateAdsController@searchJson' );
		} );
		Route::group( [ 'prefix' => 'employs' ] , function () {
			Route::get( '/cities/{city}/ads' , 'EmploysAdsController@getJsonByCityId' );
			Route::get( '/specialities' , 'EmploysAdsSpecialityController@showJson' );
			Route::post( '/search' , 'EmploysAdsController@searchJson' );
		} );
		Route::any( 'webazin/discount/pay/callback/{uuid}' , 'webazin\api\DiscountController@callback' )->name( 'webazin.discount.callback' );
	} );
	Route::group( [ 'middleware' => [ 'jwt.auth' ] ] , function () {
		Route::post( '/distance' , 'UserAdsNotificationController@add' );
		Route::post( '/ads' , 'AdsController@saveAd' );
		Route::post( '/account-info/change' , 'UserController@changeAccountInfo' );
		Route::post( '/password/change' , 'UserController@changePassword' );
		Route::post( '/ads/{adId}/fav' , 'FavoriteAdsController@makeAsFavorite' );
		Route::post( '/ads/{adId}/warning' , 'FavoriteAdsController@makewarning' );
		Route::get( '/users/ads/favorites' , 'FavoriteAdsController@gettingListByUserId' );
		Route::get( '/users/ads/{adsId}' , 'AdsController@getFavoritesAndLikes' );
		Route::get( '/current-user/ads' , 'AdsController@getCurrentUserAds' );
		Route::post( '/ads/{ads}/likes' , 'AdsLikesController@toggleLike' );
		Route::get( '/ads/{ads}/delete' , 'AdsController@delete' );
		Route::post( '/ads/{ads}/update' , 'AdsController@updateInApi' );
		Route::put( '/users/notifications/setting' , 'UserController@updateNotificationSetting' )->name( 'updateUserNotificationSetting' );
		Route::get( '/ads/{ads}/like/off' , 'AdsLikesController@LikeOff' )->name( 'LikeAdsOff' );
		Route::post( '/vip-ads/favorite' , 'VipAdsFavoriteController@saveJson' );
		Route::get( '/vip-ads/favorite/check' , 'VipAdsFavoriteController@isFavorite' );
		Route::group( [ 'prefix' => 'vehicles' ] , function () {
			Route::post( '/' , 'VehicleAdsController@saveJson' );
			Route::post( '/{ads}/update' , 'VehicleAdsController@updateJson' );
			Route::get( '/users/ads' , 'VehicleAdsController@getJsonByUser' );
			Route::delete( '{ads}' , 'VehicleAdsController@deleteJsonById' );
		} );
		Route::group( [ 'prefix' => 'estates' ] , function () {
			Route::get( '/ads' , 'EstateAdsController@getUserAds' );
			Route::post( '/save' , 'EstateAdsController@saveJson' );
			Route::delete( '/ads/{ads}' , 'EstateAdsController@delete' );
			Route::post( '/ads/{ads}/update' , 'EstateAdsController@updateJson' );
			Route::post( '/search' , 'EstateAdsController@searchJson' );
		} );
		Route::group( [ 'prefix' => 'employs' ] , function () {
			Route::post( '/' , 'EmploysAdsController@saveJson' );
			Route::get( '/users/ads' , 'EmploysAdsController@getJsonByUser' );
			Route::delete( '/{ads}' , 'EmploysAdsController@deleteJson' );
			Route::post( '/{ads}/update' , 'EmploysAdsController@updateJson' );
		} );
		Route::group( [ 'prefix' => 'webazin' , 'namespace' => 'webazin\api' ] , function () {
			Route::get( 'discount/get/{ad_id}' , 'DiscountController@show' );
			Route::post( 'discount/pay/{ad_id}' , 'DiscountController@pay' )->name( 'webazin.discount.pay' );

			Route::get( 'awards' , 'AwardController@index' );
			Route::get( 'awards/get/{id}' , 'AwardController@get' );

			Route::get( 'wallet/count' , 'AwardController@userCount' );
		} );
		Route::get( '/users/vehicle-ads/favorites' , 'VipAdsFavoriteController@getUserVehicleAds' );
		Route::get( '/users/estate-ads/favorites' , 'VipAdsFavoriteController@getUserEstateAds' );
		Route::get( '/users/employs-ads/favorites' , 'VipAdsFavoriteController@getUserEmployAds' );

	} );
	/*-------------------------------------------API visitor-----------------------------------------------------*/
	Route::post( 'visitor/login' , 'VisitorController@login' );

	Route::group( [ 'middleware' => [ 'auth:visitor_api' ] ] , function () {
		Route::post( 'visitor/store_ads' , 'VisitorController@storeAds' );
		Route::get( 'visitor/show_ads_count' , 'VisitorController@showAdsCount' );
		Route::get( 'visitor/show-users-ads' , 'VisitorController@showAds' );
	} );
} );

Route::group( [ 'prefix' => 'admin' ] , function () {
	Route::group( [ 'middleware' => 'guest:admin' ] , function () {
		Route::get( '/login' , 'Auth\LoginController@showLoginForm' )->name( 'showAdminLoginForm' );
		Route::post( '/login' , 'Auth\LoginController@login' )->name( 'doLogin' );
	} );
	Route::group( [ 'middleware' => 'auth:admin' ] , function () {

		Route::get( '/visitor/show-create-page' , 'VisitorController@showCreatePage' )->name( 'showVisitorCreatePage' );
		Route::post( '/visitor/create' , 'VisitorController@create' )->name( 'createVisitor' );
		Route::get( '/visitor/show-list' , 'VisitorController@showList' )->name( 'showListOfVisitorPage' );
		Route::get( '/visitor/{visitor}/update' , 'VisitorController@updatePage' )->name( 'showVisitorUpdatePage' );
		Route::put( '/visitor/{visitor}/update' , 'VisitorController@update' )->name( 'updateVisitor' );
		Route::get( '/visitor/{visitor}/list' , 'VisitorController@showAdsVisitor' )->name( 'showAdsVisitor' );
		Route::get( '/visitor/{visitor}/change-status' , 'VisitorController@changeStatus' )->name( 'changeStatusOfVisitor' );

		Route::get( '/dashboard' , 'AdminController@showDashboard' )->name( 'showAdminDashboard' );
		Route::get( '/categories' , 'CategoryController@showAllInAdmin' )->name( 'showAllCategoriesInAdminPanel' );
		Route::post( '/categories' , 'CategoryController@saveNewInAdmin' )->name( 'saveNewCategoryInAdminPanel' );
		Route::get( '/categories/{category}/delete' , 'CategoryController@delete' )->name( 'deleteCategory' );
		Route::put( '/categories/{category}' , 'CategoryController@updateByIdInAdminPanel' )->name( 'UpdateCategoryInAdminPanel' );
		Route::get( '/categories/{category}/sub-categories' , 'SubCategoryController@showAllInAdminPanel' )->name( 'showSubCategoriesInAdminPanel' );
		Route::post( '/categories/{category}/sub-categories' , 'SubCategoryController@save' )->name( 'saveSubCategoryInAdminPanel' );
		Route::put( '/sub-categories/{subCategory}' , 'SubCategoryController@update' )->name( 'updateSubCategoryById' );
		Route::get( '/sub-categories/{subCategory}/delete' , 'SubCategoryController@delete' )->name( 'deleteSubCategoryInAdminPanel' );
		Route::get( '/provinces' , 'ProvinceController@showAllInAdminPanel' )->name( 'showAllProvincesInAdminPanel' );
		Route::put( '/provinces/{province}' , 'ProvinceController@update' )->name( 'updateProvinceInAdminPanel' );
		Route::get( '/province/{province}/delete' , 'ProvinceController@delete' )->name( 'deleteProvinceInAdminPanel' );
		Route::post( '/provinces' , 'ProvinceController@save' )->name( 'saveProvinceInAdminPanel' );
		Route::get( '/provinces/{province}/cities' , 'CityController@showAllInAdminPanel' )->name( 'showAllCitiesInAdminPanel' );
		Route::get( '/provinces/cities/{city}/regions' , 'RegionController@showInAdminPanel' )->name( 'showRegionsInAdminPanel' );
		Route::post( '/provinces/cities/{city}/regions' , 'RegionController@saveInAdminPanel' )->name( 'saveRegionInAdminPanel' );
		Route::get( '/provinces/cities/regions/{region}/delete' , 'RegionController@deleteRegion' )->name( 'deleteRegionInAdminPanel' );
		Route::put( '/provinces/cities/regions/{region}' , 'RegionController@updateInAdminPanel' )->name( 'updateRegionInAdminPanel' );
		Route::post( '/provinces/{province}/cities' , 'CityController@save' )->name( 'saveCityInAdminPanel' );
		Route::put( '/cities/{city}' , 'CityController@update' )->name( 'updateCityInAdminPanel' );
		Route::get( '/cities/{city}/delete' , 'CityController@delete' )->name( 'deleteCityInAdminPanel' );

		Route::get( '/cylinder-volumes' , 'CylinderVolumesController@showAllInAdminPanel' )->name( 'ShowAllCylinderVolumesInAdminPanel' );
		Route::post( '/cylinder-volumes' , 'CylinderVolumesController@saveInAdminPanel' )->name( 'saveCylinderVolumeInAdminPanel' );
		Route::put( '/cylinder-volumes/{volume}' , 'CylinderVolumesController@update' )->name( 'updateCylinderVolumeInAdminPanel' );
		Route::get( '/cylinder-volumes/{volume}/delete' , 'CylinderVolumesController@delete' )->name( 'deleteCylinderVolumeInAdminPanel' );

		Route::get( '/ads-plans' , 'AdsPlanController@showAllInAdminPanel' )->name( 'showAllPlansListInAdminPanel' );
		Route::get( '/news' , 'NewsController@showAllInAdminPanel' )->name( 'showNewsListInAdminPanel' );
		Route::get( '/news/{news}/delete' , 'NewsController@delete' )->name( 'deleteNewsInAdminPanel' );
		Route::get( '/ads-plans/ordering-factors/update' , 'AdsPlanController@updatePlansOrder' )->name( 'updateAdsPlansOrderingFactor' );
		Route::post( '/ads-plans' , 'AdsPlanController@save' )->name( 'saveAdPlan' );
		Route::put( '/ads-photo/{adsPlan}' , 'AdsPlanController@update' )->name( 'updateAdsPlanInAdminPanel' );
		Route::get( 'ads-plans/{adsPlan}/delete' , 'AdsPlanController@deleteById' )->name( 'deleteAdsPlan' );
		Route::get( '/ads/create' , 'AdsController@showInsertPageInAdminPanel' )->name( 'showAdsCreatePage' );
		Route::post( '/ads' , 'AdsController@saveAdsInAdminPanel' )->name( 'saveAdsInAdminPanel' );
		Route::get( '/ads/list' , 'AdsController@showListOfAdsInAdminPanel' )->name( 'showAdsListInAdminPanel' );
		Route::get( '/ads/discount/list' , 'AdsController@showListOfAdsDiscountInAdminPanel' )->name( 'showDiscountAdsListInAdminPanel' );
		Route::get( '/ads/{ads}/edit' , 'AdsController@showAdsUpdatePage' )->name( 'showAdsUpdatePageInAdminPanel' );
		Route::put( '/ads/{ads}' , 'AdsController@updateAdsInAdminPanel' )->name( 'updateAdInAdminPanel' );
		Route::get( '/ads/{ads}/delete' , 'AdsController@deleteById' )->name( 'deleteAdsByIdInAdminPanel' );
		Route::put( '/ads/{ads}/re-validate' , 'AdsController@reValidateAd' )->name( 'reValidateAdsInAdminPanel' );
		Route::post( '/ads/actions/do' , 'AdsController@doSomeActionsOnAds' )->name( 'doSomeActionsOnPost' );

		Route::get( '/ads-photos/{photo}/delete' , 'AdsPhotoController@deleteInAdminPanel' )->name( 'deleteAdsPhotoInAdminPanel' );

		Route::get( '/ads/{ads}/photos' , 'AdsPhotoController@showById' )->name( 'showAdsPhotoById' );
		Route::post( '/ads/{ads}/photos' , 'AdsPhotoController@uploadInAdminPanel' )->name( 'uploadPhotoInAdminPanel' );
		Route::get( '/ads/{ads}/vip/create' , 'VipAdsController@showCreatePage' )->name( 'showVipAdsCreatePage' );
		Route::post( '/ads/{ads}/vip' , 'VipAdsController@save' )->name( 'saveVipAds' );
		Route::get( '/ads/{vipAds}/vip/update' , 'VipAdsController@showUpdatePage' )->name( 'showVipAdsUpdatePage' );
		Route::put( '/vip-ads/{vipAds}' , 'VipAdsController@updateVipAdInAdminPanel' )->name( 'updateVipAdsInAdminPanel' );
		Route::get( '/vip-ads/list' , 'VipAdsController@showAllInAdminPanel' )->name( 'showAllVipAdsInAdminPanel' );
		Route::get( '/vip-ads/{vipAds}/delete' , 'VipAdsController@deleteById' )->name( 'deleteVipAdsInAdminPanel' );
		Route::get( '/my-account' , 'AdminController@showUpdatePage' )->name( 'showMyAccountInAdminPanel' );
		Route::get( '/sms-panel/information/change' , 'AdminController@smsPanelInformationShow' )->name( 'smsPanelInformationShow' );
		Route::put( '/sms-panel/information/change' , 'AdminController@smsPanelInformationUpdate' )->name( 'smsPanelInformationUpdate' );
		Route::put( '/my-account' , 'AdminController@update' )->name( 'updateAdminMyAccount' );
		Route::get( '/admin/password/change' , 'AdminController@showChangePasswordForm' )->name( 'showChangePasswordForm' );
		Route::put( '/admin/password' , 'AdminController@updatePassword' )->name( 'changeAdminPassword' );
		Route::get( '/news/create' , 'NewsController@showInsertFormInAdminPanel' )->name( 'showNewsInsertForm' );
//        Route::get('/news/{news}/update' , 'NewsController@showUpdatePage')->name('showNewsUpdatePageInAdminPanel');
		Route::post( '/news/create' , 'NewsController@saveNews' )->name( 'saveNewsInAdminPanel' );
		Route::get( '/news/{news}/update' , 'NewsController@showNewsUpdatePage' )->name( 'showNewsUpdatePage' );
		Route::put( '/news/{news}/update' , 'NewsController@updateNewsInAdminPanel' )->name( 'updateNewsInAdminPanel' );
		Route::get( '/logout' , 'AdminController@logout' )->name( 'logoutAdmin' );
		Route::get( '/categories/ordering-factors/update' , 'CategoryController@updateOrders' )->name( 'updateCategoryOrderingFactor' );
		Route::get( '/subcategories/ordering-factors/update' , 'SubCategoryController@updateOrders' )->name( 'updateSubCategoriesOrderingFactors' );
		Route::get( 'news/{news}/photos' , 'NewsPhotoController@showByNewsId' )->name( 'showPhotosOfNewsById' );
		Route::post( '/news/{news}/photos' , 'NewsPhotoController@upload' )->name( 'uploadNewsPhotoInAdminPanel' );
		Route::get( '/news-photo/{newsPhoto}/delete' , 'NewsPhotoController@deleteById' )->name( 'deleteNewsPhotoById' );

		Route::get( '/users' , 'UserController@showListInAdminPanel' )->name( 'showUsersListInAdminPanel' );
		Route::get( 'users/{user}/update' , 'UserController@showUpdatePageInAdminPanel' )->name( 'showUserUpdatePage' );
		Route::put( '/users/{user}' , 'UserController@updateUserInAdminPanel' )->name( 'updateUserInAdminPanel' );
		Route::get( '/users/create' , 'UserController@showUserCreatePage' )->name( 'showUserCreatePage' );
		Route::post( '/users' , 'UserController@saveNewUserInAdminPanel' )->name( 'saveNewUserInAdminPanel' );
		Route::get( '/users/{user}/delete' , 'UserController@delete' )->name( 'deleteUserByIdInAdminPanel' );
		Route::get( '/users/mobile-banks' , 'UserController@showUserMobileBanks' )->name( 'showUserMobileNumbersBank' );
		Route::get( '/users/mobiles/excel/export' , 'UserController@exportUserMobilesInExcelFormat' )->name( 'ExportUserMobilesInExcelFormat' );
		Route::get( '/ads/expiring' , 'AdsController@showExpiringAds' )->name( 'showExpiringAds' );


		Route::get( '/estates/ads' , 'EstateAdsController@showAllInAdmin' )->name( 'showAllEstateAdsInAdminPanel' );
		Route::get( '/estates/ads/{ads}/delete' , 'EstateAdsController@deleteById' )->name( 'estate.ads.delete' );
		Route::get( '/estates/ads/create' , 'EstateAdsController@showCreatePage' )->name( 'estate.ads.createPage.show' );
		Route::get( '/estates/ads/categories/{category}/children' , 'EstateAdsCategoriesController@getByParentId' );
		Route::post( '/estates/ads' , 'EstateAdsController@save' )->name( 'saveEstateAdsInAdminPanel' );
		Route::get( '/estates/ads/{ads}/photos' , 'EstateAdsPhotoController@showByAdsId' )->name( 'estateAds.photos.show' );
		Route::post( '/estates/ads/{ads}/photos' , 'EstateAdsPhotoController@upload' )->name( 'uploadEstateAdsPhotoInAdminPanel' );
		Route::get( '/estates/ads/{ads}/update' , 'EstateAdsController@showUpdatePage' )->name( 'estateAds.update.show' );
		Route::put( '/estates/ads/{ads}' , 'EstateAdsController@update' )->name( 'estateAds.update' );
		Route::get( '/estates-ads/photo/{photo}/delete' , 'EstateAdsPhotoController@delete' )->name( 'deleteEstateAdsPhoto' );
		Route::get( 'estates-ads/{ads}/thumbnail/delete' , 'EstateAdsController@deleteThumbnailPhoto' )->name( 'deleteEstateAdsThumbnailPhoto' );

		Route::get( '/vehicles/ads' , 'VehicleAdsController@showAllInAdmin' )->name( 'showAllVehicleAdsInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/delete' , 'VehicleAdsController@deleteById' )->name( 'vehicle.ads.delete' );
		Route::get( '/vehicles/ads/create' , 'VehicleAdsController@showCreatePage' )->name( 'vehicle.ads.createPage.show' );
		Route::post( '/vehicles/ads' , 'VehicleAdsController@save' )->name( 'saveVehicleAdsInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/photos' , 'VehicleAdsPhotoController@showByAdsId' )->name( 'vehicleAds.photos.show' );
		Route::post( '/vehicles/ads/{ads}/photos' , 'VehicleAdsPhotoController@upload' )->name( 'uploadVehicleAdsPhotoInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/update' , 'VehicleAdsController@showUpdatePage' )->name( 'vehicleAds.update.show' );
		Route::put( '/vehicles/ads/{ads}' , 'VehicleAdsController@update' )->name( 'vehicleAds.update' );
		Route::get( '/vehicles-ads/photo/{photo}/delete' , 'VehicleAdsPhotoController@delete' )->name( 'deleteVehicleAdsPhoto' );
		Route::get( 'vehicles-ads/{ads}/thumbnail/delete' , 'VehicleAdsController@deleteThumbnailInAdminPanel' )->name( 'deleteVehicleAdsThumbnailPhoto' );
		Route::get( '/vehicle-ads/brands' , 'BrandController@showAllInAdminPanel' )->name( 'showAllBrandsInAdmin' );
		Route::post( '/vehicle-ads/brands' , 'BrandController@save' )->name( 'saveVehicleAdsBrand' );
		Route::get( 'vehicle-ads/brands/{brand}/delete' , 'BrandController@delete' )->name( 'deleteBrandInAdminPanel' );
		Route::put( '/vehicle-ads/brands/{brand}' , 'BrandController@update' )->name( 'updateVehicleAdsBrandInAdmin' );

		Route::get( '/vehicle-ads/brands/{brand}/models' , 'VehicleModelController@showAllInAdmin' )->name( 'showAllVehicleAdsModelsInAdmin' );
		Route::post( '/vehicle-ads/brands/{brand}/models' , 'VehicleModelController@save' )->name( 'saveVehicleAdsModel' );
		Route::put( '/vehicle-ads/brands/models/{model}' , 'VehicleModelController@update' )->name( 'updateModelNameInAdminPanel' );
		Route::get( '/vehicle-ads/brands/models/{model}/delete' , 'VehicleModelController@delete' )->name( 'deleteVehicleAdsInAdminPanel' );


		Route::get( '/employs/ads' , 'EmploysAdsController@showAllInAdmin' )->name( 'showAllEmploysAdsInAdminPanel' );
		Route::get( '/employs/ads/{ads}/delete' , 'EmploysAdsController@deleteById' )->name( 'employs.ads.delete' );
		Route::get( '/employs/ads/create' , 'EmploysAdsController@showCreatePage' )->name( 'employs.ads.createPage.show' );
		Route::post( '/employs/ads' , 'EmploysAdsController@save' )->name( 'saveEmploysAdsInAdminPanel' );
		Route::get( '/employs/ads/{ads}/photos' , 'EmploysAdsPhotoController@showByAdsId' )->name( 'employsAds.photos.show' );
		Route::post( '/employs/ads/{ads}/photos' , 'EmploysAdsPhotoController@upload' )->name( 'uploadEmploysAdsPhotoInAdminPanel' );
		Route::get( '/employs/ads/{ads}/update' , 'EmploysAdsController@showUpdatePage' )->name( 'employsAds.update.show' );
		Route::put( '/employs/ads/{ads}' , 'EmploysAdsController@update' )->name( 'employsAds.update' );
		Route::get( '/employs-ads/photo/{photo}/delete' , 'EmploysAdsPhotoController@delete' )->name( 'deleteEmploysAdsPhoto' );
		Route::get( '/employs/ads/{ads}/thumbnail/delete' , 'EmploysAdsController@deleteThumbnailPhoto' )->name( 'deleteEmployAdsThumbnailPhoto' );

		Route::get( '/specialities' , 'EmploysAdsSpecialityController@showAllInAdminPanel' )->name( 'showAllSpecialitiesInAdminPanel' );
		Route::post( '/specialities' , 'EmploysAdsSpecialityController@saveInAdminPanel' )->name( 'saveSpecialityInAdminPanel' );
		Route::get( '/specialities/{specialty}/delete' , 'EmploysAdsSpecialityController@delete' )->name( 'deleteSpecialityInAdminPanel' );
		Route::put( '/specialities/{speciality}' , 'EmploysAdsSpecialityController@updateInAdminPanel' )->name( 'updateSpecialityInAdminPanel' );
		Route::get( '/business/refers' , 'UserAdsNotificationController@index' )->name( 'business.refers.index' );
		Route::get( '/business/refers/{item}/delete' , 'UserAdsNotificationController@destroy' )->name( 'business.refers.destroy' );

		Route::get( '/refers/users/search' , 'UserController@searchNames' )->name( 'searchNamesOfUsers' );

		Route::get( '/ajax/ads' , 'AdsController@getViaAjax' )->name( 'getAdsViaAjax' );
		Route::get( '/ajax/users' , 'UserController@getUsersListViaAjax' );
		Route::namespace( 'webazin\admin' )->group( function () {
			Route::get( 'awards' , 'Awards\AwardsController@index' )->name( 'admin.award.index' );
			Route::get( 'awards/create' , 'Awards\AwardsController@create' )->name( 'admin.award.create' );
			Route::post( 'awards/store' , 'Awards\AwardsController@store' )->name( 'admin.award.store' );
			Route::put( 'awards/update/{id}' , 'Awards\AwardsController@update' )->name( 'admin.award.update' );
			Route::get( 'awards/delete/{id}' , 'Awards\AwardsController@destroy' )->name( 'admin.award.delete' );
		} );
	} );
} );

Route::get( '/currencies' , 'CommonController@showCurrencies' )->name( 'Currencies' );
Route::get( '/football' , 'CommonController@showFootball' )->name( 'Football' );
Route::get( '/gulf' , 'CommonController@showGulf' )->name( 'Gulf' );
