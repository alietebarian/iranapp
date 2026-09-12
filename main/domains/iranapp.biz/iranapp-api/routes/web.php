<?php

use App\Http\Controllers\AdminController;
use App\Http\Controllers\AdsController;
use App\Http\Controllers\AdsLikesController;
use App\Http\Controllers\AdsPhotoController;
use App\Http\Controllers\AdsPlanController;
use App\Http\Controllers\AdsVideoController;
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
| Admin panel routes, ported from the legacy routes/web.php.
*/

Route::group( [ 'prefix' => 'admin' ] , function () {
	Route::group( [ 'middleware' => 'guest:admin' ] , function () {
		Route::get( '/login' , [AuthLoginController::class, 'showLoginForm'] )->name( 'showAdminLoginForm' );
		Route::post( '/login' , [AuthLoginController::class, 'login'] )->name( 'doLogin' );
	} );
	Route::group( [ 'middleware' => 'auth:admin' ] , function () {

		Route::get( '/visitor/show-create-page' , [VisitorController::class, 'showCreatePage'] )->name( 'showVisitorCreatePage' );
		Route::post( '/visitor/create' , [VisitorController::class, 'create'] )->name( 'createVisitor' );
		Route::get( '/visitor/show-list' , [VisitorController::class, 'showList'] )->name( 'showListOfVisitorPage' );
		Route::get( '/visitor/{visitor}/update' , [VisitorController::class, 'updatePage'] )->name( 'showVisitorUpdatePage' );
		Route::put( '/visitor/{visitor}/update' , [VisitorController::class, 'update'] )->name( 'updateVisitor' );
		Route::get( '/visitor/{visitor}/list' , [VisitorController::class, 'showAdsVisitor'] )->name( 'showAdsVisitor' );
		Route::get( '/visitor/{visitor}/change-status' , [VisitorController::class, 'changeStatus'] )->name( 'changeStatusOfVisitor' );

		Route::get( '/dashboard' , [AdminController::class, 'showDashboard'] )->name( 'showAdminDashboard' );
		Route::get( '/categories' , [CategoryController::class, 'showAllInAdmin'] )->name( 'showAllCategoriesInAdminPanel' );
		Route::post( '/categories' , [CategoryController::class, 'saveNewInAdmin'] )->name( 'saveNewCategoryInAdminPanel' );
		Route::get( '/categories/{category}/delete' , [CategoryController::class, 'delete'] )->name( 'deleteCategory' );
		Route::put( '/categories/{category}' , [CategoryController::class, 'updateByIdInAdminPanel'] )->name( 'UpdateCategoryInAdminPanel' );
		Route::get( '/categories/{category}/sub-categories' , [SubCategoryController::class, 'showAllInAdminPanel'] )->name( 'showSubCategoriesInAdminPanel' );
		Route::post( '/categories/{category}/sub-categories' , [SubCategoryController::class, 'save'] )->name( 'saveSubCategoryInAdminPanel' );
		Route::put( '/sub-categories/{subCategory}' , [SubCategoryController::class, 'update'] )->name( 'updateSubCategoryById' );
		Route::get( '/sub-categories/{subCategory}/delete' , [SubCategoryController::class, 'delete'] )->name( 'deleteSubCategoryInAdminPanel' );
		Route::get( '/provinces' , [ProvinceController::class, 'showAllInAdminPanel'] )->name( 'showAllProvincesInAdminPanel' );
		Route::put( '/provinces/{province}' , [ProvinceController::class, 'update'] )->name( 'updateProvinceInAdminPanel' );
		Route::get( '/province/{province}/delete' , [ProvinceController::class, 'delete'] )->name( 'deleteProvinceInAdminPanel' );
		Route::post( '/provinces' , [ProvinceController::class, 'save'] )->name( 'saveProvinceInAdminPanel' );
		Route::get( '/provinces/{province}/cities' , [CityController::class, 'showAllInAdminPanel'] )->name( 'showAllCitiesInAdminPanel' );
		Route::get( '/provinces/cities/{city}/regions' , [RegionController::class, 'showInAdminPanel'] )->name( 'showRegionsInAdminPanel' );
		Route::post( '/provinces/cities/{city}/regions' , [RegionController::class, 'saveInAdminPanel'] )->name( 'saveRegionInAdminPanel' );
		Route::get( '/provinces/cities/regions/{region}/delete' , [RegionController::class, 'deleteRegion'] )->name( 'deleteRegionInAdminPanel' );
		Route::put( '/provinces/cities/regions/{region}' , [RegionController::class, 'updateInAdminPanel'] )->name( 'updateRegionInAdminPanel' );
		Route::post( '/provinces/{province}/cities' , [CityController::class, 'save'] )->name( 'saveCityInAdminPanel' );
		Route::put( '/cities/{city}' , [CityController::class, 'update'] )->name( 'updateCityInAdminPanel' );
		Route::get( '/cities/{city}/delete' , [CityController::class, 'delete'] )->name( 'deleteCityInAdminPanel' );

		Route::get( '/cylinder-volumes' , [CylinderVolumesController::class, 'showAllInAdminPanel'] )->name( 'ShowAllCylinderVolumesInAdminPanel' );
		Route::post( '/cylinder-volumes' , [CylinderVolumesController::class, 'saveInAdminPanel'] )->name( 'saveCylinderVolumeInAdminPanel' );
		Route::put( '/cylinder-volumes/{volume}' , [CylinderVolumesController::class, 'update'] )->name( 'updateCylinderVolumeInAdminPanel' );
		Route::get( '/cylinder-volumes/{volume}/delete' , [CylinderVolumesController::class, 'delete'] )->name( 'deleteCylinderVolumeInAdminPanel' );

		Route::get( '/ads-plans' , [AdsPlanController::class, 'showAllInAdminPanel'] )->name( 'showAllPlansListInAdminPanel' );
		Route::get( '/news' , [NewsController::class, 'showAllInAdminPanel'] )->name( 'showNewsListInAdminPanel' );
		Route::get( '/news/{news}/delete' , [NewsController::class, 'delete'] )->name( 'deleteNewsInAdminPanel' );
		Route::get( '/ads-plans/ordering-factors/update' , [AdsPlanController::class, 'updatePlansOrder'] )->name( 'updateAdsPlansOrderingFactor' );
		Route::post( '/ads-plans' , [AdsPlanController::class, 'save'] )->name( 'saveAdPlan' );
		Route::put( '/ads-photo/{adsPlan}' , [AdsPlanController::class, 'update'] )->name( 'updateAdsPlanInAdminPanel' );
		Route::get( 'ads-plans/{adsPlan}/delete' , [AdsPlanController::class, 'deleteById'] )->name( 'deleteAdsPlan' );
		Route::get( '/ads/create' , [AdsController::class, 'showInsertPageInAdminPanel'] )->name( 'showAdsCreatePage' );
		Route::post( '/ads' , [AdsController::class, 'saveAdsInAdminPanel'] )->name( 'saveAdsInAdminPanel' );
		Route::get( '/ads/list' , [AdsController::class, 'showListOfAdsInAdminPanel'] )->name( 'showAdsListInAdminPanel' );
		Route::get( '/ads/discount/list' , [AdsController::class, 'showListOfAdsDiscountInAdminPanel'] )->name( 'showDiscountAdsListInAdminPanel' );
		Route::get( '/ads/{ads}/edit' , [AdsController::class, 'showAdsUpdatePage'] )->name( 'showAdsUpdatePageInAdminPanel' );
		Route::put( '/ads/{ads}' , [AdsController::class, 'updateAdsInAdminPanel'] )->name( 'updateAdInAdminPanel' );
		Route::get( '/ads/{ads}/delete' , [AdsController::class, 'deleteById'] )->name( 'deleteAdsByIdInAdminPanel' );
		Route::put( '/ads/{ads}/re-validate' , [AdsController::class, 'reValidateAd'] )->name( 'reValidateAdsInAdminPanel' );
		Route::post( '/ads/actions/do' , [AdsController::class, 'doSomeActionsOnAds'] )->name( 'doSomeActionsOnPost' );

		Route::get( '/ads-photos/{photo}/delete' , [AdsPhotoController::class, 'deleteInAdminPanel'] )->name( 'deleteAdsPhotoInAdminPanel' );

		Route::get( '/ads/{ads}/photos' , [AdsPhotoController::class, 'showById'] )->name( 'showAdsPhotoById' );
		Route::post( '/ads/{ads}/photos' , [AdsPhotoController::class, 'uploadInAdminPanel'] )->name( 'uploadPhotoInAdminPanel' );
		Route::post( '/ads/{ads}/video' , [AdsVideoController::class, 'uploadInAdminPanel'] )->name( 'uploadAdsVideoInAdminPanel' );
		Route::get( '/ads/{ads}/video/delete' , [AdsVideoController::class, 'deleteInAdminPanel'] )->name( 'deleteAdsVideoInAdminPanel' );
		Route::get( '/ads/{ads}/vip/create' , [VipAdsController::class, 'showCreatePage'] )->name( 'showVipAdsCreatePage' );
		Route::post( '/ads/{ads}/vip' , [VipAdsController::class, 'save'] )->name( 'saveVipAds' );
		Route::get( '/ads/{vipAds}/vip/update' , [VipAdsController::class, 'showUpdatePage'] )->name( 'showVipAdsUpdatePage' );
		Route::put( '/vip-ads/{vipAds}' , [VipAdsController::class, 'updateVipAdInAdminPanel'] )->name( 'updateVipAdsInAdminPanel' );
		Route::get( '/vip-ads/list' , [VipAdsController::class, 'showAllInAdminPanel'] )->name( 'showAllVipAdsInAdminPanel' );
		Route::get( '/vip-ads/{vipAds}/delete' , [VipAdsController::class, 'deleteById'] )->name( 'deleteVipAdsInAdminPanel' );
		Route::get( '/my-account' , [AdminController::class, 'showUpdatePage'] )->name( 'showMyAccountInAdminPanel' );
		Route::get( '/sms-panel/information/change' , [AdminController::class, 'smsPanelInformationShow'] )->name( 'smsPanelInformationShow' );
		Route::put( '/sms-panel/information/change' , [AdminController::class, 'smsPanelInformationUpdate'] )->name( 'smsPanelInformationUpdate' );
		Route::put( '/my-account' , [AdminController::class, 'update'] )->name( 'updateAdminMyAccount' );
		Route::get( '/admin/password/change' , [AdminController::class, 'showChangePasswordForm'] )->name( 'showChangePasswordForm' );
		Route::put( '/admin/password' , [AdminController::class, 'updatePassword'] )->name( 'changeAdminPassword' );
		Route::get( '/news/create' , [NewsController::class, 'showInsertFormInAdminPanel'] )->name( 'showNewsInsertForm' );
//        Route::get('/news/{news}/update' , [NewsController::class, 'showUpdatePage'])->name('showNewsUpdatePageInAdminPanel');
		Route::post( '/news/create' , [NewsController::class, 'saveNews'] )->name( 'saveNewsInAdminPanel' );
		Route::get( '/news/{news}/update' , [NewsController::class, 'showNewsUpdatePage'] )->name( 'showNewsUpdatePage' );
		Route::put( '/news/{news}/update' , [NewsController::class, 'updateNewsInAdminPanel'] )->name( 'updateNewsInAdminPanel' );
		Route::get( '/logout' , [AdminController::class, 'logout'] )->name( 'logoutAdmin' );
		Route::get( '/categories/ordering-factors/update' , [CategoryController::class, 'updateOrders'] )->name( 'updateCategoryOrderingFactor' );
		Route::get( '/subcategories/ordering-factors/update' , [SubCategoryController::class, 'updateOrders'] )->name( 'updateSubCategoriesOrderingFactors' );
		Route::get( 'news/{news}/photos' , [NewsPhotoController::class, 'showByNewsId'] )->name( 'showPhotosOfNewsById' );
		Route::post( '/news/{news}/photos' , [NewsPhotoController::class, 'upload'] )->name( 'uploadNewsPhotoInAdminPanel' );
		Route::get( '/news-photo/{newsPhoto}/delete' , [NewsPhotoController::class, 'deleteById'] )->name( 'deleteNewsPhotoById' );

		Route::get( '/users' , [UserController::class, 'showListInAdminPanel'] )->name( 'showUsersListInAdminPanel' );
		Route::get( 'users/{user}/update' , [UserController::class, 'showUpdatePageInAdminPanel'] )->name( 'showUserUpdatePage' );
		Route::put( '/users/{user}' , [UserController::class, 'updateUserInAdminPanel'] )->name( 'updateUserInAdminPanel' );
		Route::get( '/users/create' , [UserController::class, 'showUserCreatePage'] )->name( 'showUserCreatePage' );
		Route::post( '/users' , [UserController::class, 'saveNewUserInAdminPanel'] )->name( 'saveNewUserInAdminPanel' );
		Route::get( '/users/{user}/delete' , [UserController::class, 'delete'] )->name( 'deleteUserByIdInAdminPanel' );
		Route::get( '/users/mobile-banks' , [UserController::class, 'showUserMobileBanks'] )->name( 'showUserMobileNumbersBank' );
		Route::get( '/users/mobiles/excel/export' , [UserController::class, 'exportUserMobilesInExcelFormat'] )->name( 'ExportUserMobilesInExcelFormat' );
		Route::get( '/ads/expiring' , [AdsController::class, 'showExpiringAds'] )->name( 'showExpiringAds' );


		Route::get( '/estates/ads' , [EstateAdsController::class, 'showAllInAdmin'] )->name( 'showAllEstateAdsInAdminPanel' );
		Route::get( '/estates/ads/{ads}/delete' , [EstateAdsController::class, 'deleteById'] )->name( 'estate.ads.delete' );
		Route::get( '/estates/ads/create' , [EstateAdsController::class, 'showCreatePage'] )->name( 'estate.ads.createPage.show' );
		Route::get( '/estates/ads/categories/{category}/children' , [EstateAdsCategoriesController::class, 'getByParentId'] );
		Route::post( '/estates/ads' , [EstateAdsController::class, 'save'] )->name( 'saveEstateAdsInAdminPanel' );
		Route::get( '/estates/ads/{ads}/photos' , [EstateAdsPhotoController::class, 'showByAdsId'] )->name( 'estateAds.photos.show' );
		Route::post( '/estates/ads/{ads}/photos' , [EstateAdsPhotoController::class, 'upload'] )->name( 'uploadEstateAdsPhotoInAdminPanel' );
		Route::get( '/estates/ads/{ads}/update' , [EstateAdsController::class, 'showUpdatePage'] )->name( 'estateAds.update.show' );
		Route::put( '/estates/ads/{ads}' , [EstateAdsController::class, 'update'] )->name( 'estateAds.update' );
		Route::get( '/estates-ads/photo/{photo}/delete' , [EstateAdsPhotoController::class, 'delete'] )->name( 'deleteEstateAdsPhoto' );
		Route::get( 'estates-ads/{ads}/thumbnail/delete' , [EstateAdsController::class, 'deleteThumbnailPhoto'] )->name( 'deleteEstateAdsThumbnailPhoto' );

		Route::get( '/vehicles/ads' , [VehicleAdsController::class, 'showAllInAdmin'] )->name( 'showAllVehicleAdsInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/delete' , [VehicleAdsController::class, 'deleteById'] )->name( 'vehicle.ads.delete' );
		Route::get( '/vehicles/ads/create' , [VehicleAdsController::class, 'showCreatePage'] )->name( 'vehicle.ads.createPage.show' );
		Route::post( '/vehicles/ads' , [VehicleAdsController::class, 'save'] )->name( 'saveVehicleAdsInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/photos' , [VehicleAdsPhotoController::class, 'showByAdsId'] )->name( 'vehicleAds.photos.show' );
		Route::post( '/vehicles/ads/{ads}/photos' , [VehicleAdsPhotoController::class, 'upload'] )->name( 'uploadVehicleAdsPhotoInAdminPanel' );
		Route::get( '/vehicles/ads/{ads}/update' , [VehicleAdsController::class, 'showUpdatePage'] )->name( 'vehicleAds.update.show' );
		Route::put( '/vehicles/ads/{ads}' , [VehicleAdsController::class, 'update'] )->name( 'vehicleAds.update' );
		Route::get( '/vehicles-ads/photo/{photo}/delete' , [VehicleAdsPhotoController::class, 'delete'] )->name( 'deleteVehicleAdsPhoto' );
		Route::get( 'vehicles-ads/{ads}/thumbnail/delete' , [VehicleAdsController::class, 'deleteThumbnailInAdminPanel'] )->name( 'deleteVehicleAdsThumbnailPhoto' );
		Route::get( '/vehicle-ads/brands' , [BrandController::class, 'showAllInAdminPanel'] )->name( 'showAllBrandsInAdmin' );
		Route::post( '/vehicle-ads/brands' , [BrandController::class, 'save'] )->name( 'saveVehicleAdsBrand' );
		Route::get( 'vehicle-ads/brands/{brand}/delete' , [BrandController::class, 'delete'] )->name( 'deleteBrandInAdminPanel' );
		Route::put( '/vehicle-ads/brands/{brand}' , [BrandController::class, 'update'] )->name( 'updateVehicleAdsBrandInAdmin' );

		Route::get( '/vehicle-ads/brands/{brand}/models' , [VehicleModelController::class, 'showAllInAdmin'] )->name( 'showAllVehicleAdsModelsInAdmin' );
		Route::post( '/vehicle-ads/brands/{brand}/models' , [VehicleModelController::class, 'save'] )->name( 'saveVehicleAdsModel' );
		Route::put( '/vehicle-ads/brands/models/{model}' , [VehicleModelController::class, 'update'] )->name( 'updateModelNameInAdminPanel' );
		Route::get( '/vehicle-ads/brands/models/{model}/delete' , [VehicleModelController::class, 'delete'] )->name( 'deleteVehicleAdsInAdminPanel' );


		Route::get( '/employs/ads' , [EmploysAdsController::class, 'showAllInAdmin'] )->name( 'showAllEmploysAdsInAdminPanel' );
		Route::get( '/employs/ads/{ads}/delete' , [EmploysAdsController::class, 'deleteById'] )->name( 'employs.ads.delete' );
		Route::get( '/employs/ads/create' , [EmploysAdsController::class, 'showCreatePage'] )->name( 'employs.ads.createPage.show' );
		Route::post( '/employs/ads' , [EmploysAdsController::class, 'save'] )->name( 'saveEmploysAdsInAdminPanel' );
		Route::get( '/employs/ads/{ads}/photos' , [EmploysAdsPhotoController::class, 'showByAdsId'] )->name( 'employsAds.photos.show' );
		Route::post( '/employs/ads/{ads}/photos' , [EmploysAdsPhotoController::class, 'upload'] )->name( 'uploadEmploysAdsPhotoInAdminPanel' );
		Route::get( '/employs/ads/{ads}/update' , [EmploysAdsController::class, 'showUpdatePage'] )->name( 'employsAds.update.show' );
		Route::put( '/employs/ads/{ads}' , [EmploysAdsController::class, 'update'] )->name( 'employsAds.update' );
		Route::get( '/employs-ads/photo/{photo}/delete' , [EmploysAdsPhotoController::class, 'delete'] )->name( 'deleteEmploysAdsPhoto' );
		Route::get( '/employs/ads/{ads}/thumbnail/delete' , [EmploysAdsController::class, 'deleteThumbnailPhoto'] )->name( 'deleteEmployAdsThumbnailPhoto' );

		Route::get( '/specialities' , [EmploysAdsSpecialityController::class, 'showAllInAdminPanel'] )->name( 'showAllSpecialitiesInAdminPanel' );
		Route::post( '/specialities' , [EmploysAdsSpecialityController::class, 'saveInAdminPanel'] )->name( 'saveSpecialityInAdminPanel' );
		Route::get( '/specialities/{specialty}/delete' , [EmploysAdsSpecialityController::class, 'delete'] )->name( 'deleteSpecialityInAdminPanel' );
		Route::put( '/specialities/{speciality}' , [EmploysAdsSpecialityController::class, 'updateInAdminPanel'] )->name( 'updateSpecialityInAdminPanel' );
		Route::get( '/business/refers' , [UserAdsNotificationController::class, 'index'] )->name( 'business.refers.index' );
		Route::get( '/business/refers/{item}/delete' , [UserAdsNotificationController::class, 'destroy'] )->name( 'business.refers.destroy' );

		Route::get( '/refers/users/search' , [UserController::class, 'searchNames'] )->name( 'searchNamesOfUsers' );

		Route::get( '/ajax/ads' , [AdsController::class, 'getViaAjax'] )->name( 'getAdsViaAjax' );
		Route::get( '/ajax/users' , [UserController::class, 'getUsersListViaAjax'] );
		Route::group( [] ,  function () {
			Route::get( 'awards' , [AwardsAwardsController::class, 'index'] )->name( 'admin.award.index' );
			Route::get( 'awards/create' , [AwardsAwardsController::class, 'create'] )->name( 'admin.award.create' );
			Route::post( 'awards/store' , [AwardsAwardsController::class, 'store'] )->name( 'admin.award.store' );
			Route::put( 'awards/update/{id}' , [AwardsAwardsController::class, 'update'] )->name( 'admin.award.update' );
			Route::get( 'awards/delete/{id}' , [AwardsAwardsController::class, 'destroy'] )->name( 'admin.award.delete' );
		} );
	} );
} );

Route::get( '/currencies' , [CommonController::class, 'showCurrencies'] )->name( 'Currencies' );
Route::get( '/football' , [CommonController::class, 'showFootball'] )->name( 'Football' );
Route::get( '/gulf' , [CommonController::class, 'showGulf'] )->name( 'Gulf' );
