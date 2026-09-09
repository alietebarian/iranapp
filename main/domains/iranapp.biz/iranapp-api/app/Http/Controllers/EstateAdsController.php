<?php

namespace App\Http\Controllers;

use App\Models\City;
use App\Models\EstateAds;
use App\Models\EstateAdsCategories;
use App\Models\EstateAdsPhoto;
use App\Http\Requests\Admin\SaveEstateAdsRequest;
use App\Http\Requests\Api\Estates\GetJson;
use App\Http\Requests\Api\Estates\SaveJson;
use App\Http\Requests\Api\Estates\UpdateJson;
use App\Libraries\jdf;
use App\Process\PrEstateAds;
use App\Models\Province;
use App\Models\Region;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Intervention\Image\Facades\Image;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;

class EstateAdsController extends Controller
{
    public function showAllInAdmin(Request $request)
    {
        $ads = PrEstateAds::adminPanelEstateAds()->orderBy('estates_ads.created_at', 'desc');
        if ($request->has('region_id') && $request->region_id != 'all') {
            $ads = $ads->where('estates_ads.region_id', '=', $request->region_id);
        } else {
            if ($request->has('city_id') && $request->city_id != 'all') {
                $ads = $ads->whereRaw('estates_ads.region_id in (
                    select region.id from region where region.city_id = ' . $request->city_id . '
                )');
            } else {
                if ($request->has('province_id') && $request->province_id != 'all') {
                    $ads = $ads->whereRaw('estates_ads.region_id in (
                        select region.id from region where region.city_id in (
                            select city.id from city where city.province_id = ' . $request->province_id . '
                        )
                    )');
                }
            }
        }
        if ($request->has('keyword')) {
            $ads = $ads->whereRaw('( MATCH(estates_ads.ads_title , estates_ads.description) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) )');
        }
        if ($request->has('user_type') && $request->user_type != 'all') {
            if ($request->user_type == 'moshaver_amlak') {
                $ads = $ads->where('estates_ads.user_type', '=', 'moshaver_amlak');
            } else {
                $ads = $ads->where('estates_ads.user_type', '=', 'person');
            }
        }
//        if (!$request->has('sub_category_id') or $request->sub_category_id == 'all') {
//            $subCategory = null;
//            $parentCategoryId =
//        } else {
//            $subCategory = EstateAdsCategories::find($request->sub_category_id);
//        }
        if ($request->has('sub_category_id') && $request->sub_category_id != 'all') {
            $subCategory = EstateAdsCategories::find($request->sub_category_id);
            $parentCategoryId = $subCategory->parent_id;
//            $cat1 = $request->sub_category_id;
            $ads = $ads->where('estates_ads.category_id' , '=' , $request->sub_category_id);
        } else if ($request->has('category_id') && $request->category_id != 'all') {
            $parentCategoryId = $request->category_id;
//            $parentCategoryId = null;
//            $cat1 = $request->category_id;
//            $ads = $ads->where('estates_ads.category_id' , '=' , $request->category_id);
            $ads = $ads->where(function($query)use($request){
                $query = $query->where('estates_ads.category_id' , '=' , $request->category_id)
                    ->orWhere('estate_categories.parent_id' , '=' , $request->category_id);

            });
        } else {
            $parentCategoryId = null;
            $cat1 = null;
        }
        if ($parentCategoryId == 1) {
            if ($request->has('meters_from') && $request->has('meters_to')) {
                $ads = $ads->where('estates_ads.meters', '>=', $request->meters_from)
                    ->where('estates_ads.meters', '<=', $request->meters_to);
            }
            if ($request->has('price__type') && $request->price__type != 'all') {
                if ($request->price_type == 'maghtoo') {
                    $ads = $ads->where('estates_ads.price_kharid', '>=', $request->price_kharid__from)
                        ->where('estates_ads.price_kharid', '<=', $request->price_kharid__to);
                } else if ($request->price_type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.price_kharid', '=', 0);
                }
            }
            if ($request->has('rooms_count')) {
                $ads = $ads->where('estates_ads.rooms_count', '=', $request->rooms_count);
            }
            if ($request->has('sell_or_buy') && $request->sell_or_buy != 'all') {
                $ads = $ads->where('estates_ads.sell_or_buy', '=', $request->sell_or_buy);
            }
            if ($request->has('is_in_hoome') && $request->is_in_hoome != 'all') {
                if ($request->has('is_in_hoome')) {
                    $ads = $ads->where('is_in_hoome', '=', 0);
                } else {
                    $ads = $ads->where('is_in_hoome', '=', 1);
                }
            }


        } else if ($parentCategoryId == 2) {
            if ($request->has('meters_from') && $request->has('meters_to')) {
                $ads = $ads->where('estates_ads.meters', '>=', $request->meters_from)
                    ->where('estates_ads.meters', '<=', $request->meters_to);
            }

            if ($request->has('price__type') && $request->price__type != 'all') {
                if ($request->price__type == 'maghtoo' && $request->has('pre_pay_ejare_to_from___') && $request->has('pre_pay_ejare_to_to___')) {
                    $ads = $ads->where('estates_ads.pre_pay_ejare', '>=', $request->pre_pay_ejare_to_from___)
                        ->where('estates_ads.pre_pay_ejare', '<=', $request->pre_pay_ejare_to_to___);
                } else if ($request->price__type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.pre_pay_ejare', '=', 0);
                }
            }
            if ($request->has('ejare__type') && $request->ejare__type != 'all') {
                if ($request->ejare__type == 'maghtoo' && $request->has('monthly_price_ejare_from') && $request->has('monthly_price_ejare_to')) {
                    $ads = $ads->where('estates_ads.monthly_price_ejare', '>=', $request->monthly_price_ejare_from)
                        ->where('estates_ads.monthly_price_ejare', '<=', $request->monthly_price_ejare_to);
                } else if ($request->ejare__type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.monthly_price_ejare', '=', 0);
                }
            }

            if ($request->has('rooms_count')) {
                $ads = $ads->where('estates_ads.rooms_count', '=', $request->rooms_count);
            }
            if ($request->has('sell_or_buy') && $request->sell_or_buy != 'all') {
                $ads = $ads->where('estates_ads.sell_or_buy', '=', $request->sell_or_buy);
            }
            if ($request->has('is_in_hoome') && $request->is_in_hoome != 'all') {
                if ($request->has('is_in_hoome')) {
                    $ads = $ads->where('is_in_hoome', '=', 0);
                } else {
                    $ads = $ads->where('is_in_hoome', '=', 1);
                }
            }

        } else if ($parentCategoryId == 3) {
            if ($request->has('meters_from') && $request->has('meters_to')) {
                $ads = $ads->where('estates_ads.meters', '>=', $request->meters_from)
                    ->where('estates_ads.meters', '<=', $request->meters_to);
            }
            if ($request->has('price__type') && $request->price__type != 'all') {
                if ($request->price__type == 'maghtoo' && $request->has('price_kharid_from') && $request->has('price_kharid_to')) {
                    $ads = $ads->where('estates_ads.price_kharid', '>=', $request->price_kharid_from)
                        ->where('estates_ads.price_kharid', '<=', $request->price_kharid_to);
                } else if ($request->price__type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.price_kharid', '=', 0);
                }
            }
            if ($request->has('rooms_count')) {
                $ads = $ads->where('estates_ads.rooms_count', '=', $request->rooms_count);
            }
            if ($request->has('sell_or_buy') && $request->sell_or_buy != 'all') {
                $ads = $ads->where('estates_ads.sell_or_buy', '=', $request->sell_or_buy);
            }
            if ($request->has('is_in_hoome') && $request->is_in_hoome != 'all') {
                if ($request->has('is_in_hoome')) {
                    $ads = $ads->where('is_in_hoome', '=', 0);
                } else {
                    $ads = $ads->where('is_in_hoome', '=', 1);
                }
            }
            if ($request->has('sanad_edari') && $request->sanad_edari == 'no') {
                $ads = $ads->where('sanad_edari', '=', 0);
            } else {
                $ads = $ads->where('sanad_edari', '=', 1);
            }

        } else if ($parentCategoryId == 4) {
            if ($request->has('meters_from') && $request->has('meters_to')) {
                $ads = $ads->where('estates_ads.meters', '>=', $request->meters_from)
                    ->where('estates_ads.meters', '<=', $request->meters_to);
            }

            if ($request->has('price_type') && $request->price_type != 'all') {
                if ($request->price_type == 'maghtoo' && $request->has('pre_pay_ejare_from') && $request->has('pre_pay_ejare_to')) {
                    $ads = $ads->where('estates_ads.pre_pay_ejare', '>=', $request->pre_pay_ejare_from)
                        ->where('estates_ads.pre_pay_ejare', '<=', $request->pre_pay_ejare_to);
                } else if ($request->price_type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.pre_pay_ejare', '=', 0);
                }
            }
            if ($request->has('ejare__type') && $request->ejare__type != 'all') {
                if ($request->ejare__type == 'maghtoo' && $request->has('monthly_price_ejare_from') && $request->has('monthly_price_ejare_to')) {
                    $ads = $ads->where('estates_ads.monthly_price_ejare', '>=', $request->monthly_price_ejare_from)
                        ->where('estates_ads.monthly_price_ejare', '<=', $request->monthly_price_ejare_to);
                } else if ($request->ejare__type == 'tavafoghi') {
                    $ads = $ads->where('estates_ads.monthly_price_ejare', '=', 0);
                }
            }

            if ($request->has('rooms_count')) {
                $ads = $ads->where('estates_ads.rooms_count', '=', $request->rooms_count);
            }
            if ($request->has('sell_or_buy') && $request->sell_or_buy != 'all') {
                $ads = $ads->where('estates_ads.sell_or_buy', '=', $request->sell_or_buy);
            }
            if ($request->has('is_in_hoome') && $request->is_in_hoome != 'all') {
                if ($request->has('is_in_hoome')) {
                    $ads = $ads->where('is_in_hoome', '=', 0);
                } else {
                    $ads = $ads->where('is_in_hoome', '=', 1);
                }
            }
        }
        if ($request->has('sub_category_id') && $request->sub_category_id != 'all') {
            $ads = $ads->where('category_id', '=', $request->sub_category_id);
        }
        if ($request->has('status') && $request->status != 'all') {
            $ads = $ads->where('estates_ads.status', '=', $request->status);
        }
        $ads = $ads->paginate(15);
        $data['ads'] = $ads;
        $data['categories'] = EstateAdsCategories::whereNull('parent_id')->get();
        $data['provinces'] = Province::all();
        return view('admin.estates.list')->with($data);
    }

    public function deleteById(Request $request, EstateAds $ads)
    {
        $ads->status = 'deleted';
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'آگهی مورد نظر با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg', $msg);
    }

    public function showCreatePage(Request $request)
    {
        $data['provinces'] = Province::all();
        $data['users'] = User::all();
        $data['categories'] = EstateAdsCategories::whereNull('parent_id')->get();
        return view('admin.estates.addPage')->with($data);
    }

    public function save(SaveEstateAdsRequest $request)
    {
        $ads = new EstateAds();
        $ads->ads_title = $request->ads_title;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440, null, function ($constraint) {
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 20);
            $imageToAddWatermark->save();
        }
        $ads->user_id = $request->user_id;
        $ads->address = $request->address;
        $ads->user_type = $request->user_type;
        if ($request->category_id == 1) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->price_kharid = $request->price_kharid;
            } else if ($request->price__type == 'moaveze') {
                $ads->price_kharid = -1;
            } else {
                $ads->price_kharid = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else if ($request->category_id == 2) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->pre_pay_ejare = $request->pre_pay_ejare;
            } else if ($request->price__type == 'free') {
                $ads->pre_pay_ejare = -1;
            } else {
                $ads->pre_pay_ejare = 0;
            }
            if ($request->ejare__type == 'maghtoo') {
                $ads->monthly_price_ejare = $request->monthly_price_ejare;
            } else if ($request->ejare__type == 'free') {
                $ads->monthly_price_ejare = -1;
            } else {
                $ads->monthly_price_ejare = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else if ($request->category_id == 3) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->price_kharid = $request->price_kharid;
            } else if ($request->price__type == 'moaveze') {
                $ads->price_kharid = -1;
            } else {
                $ads->price_kharid = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
            if ($request->sanad_edari == 'no') {
                $ads->sanad_edari = 0;
            } else {
                $ads->sanad_edari = 1;
            }
        } else if ($request->category_id == 4) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price_type == 'maghtoo') {
                $ads->pre_pay_ejare = $request->pre_pay_ejare;
            } else if ($request->price_type == 'free') {
                $ads->pre_pay_ejare = -1;
            } else {
                $ads->pre_pay_ejare = 0;
            }
            if ($request->ejare__type == 'maghtoo') {
                $ads->monthly_price_ejare = $request->monthly_price_ejare;
            } else if ($request->ejare__type == 'free') {
                $ads->monthly_price_ejare = -1;
            } else {
                $ads->monthly_price_ejare = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = 0;
            $ads->sell_or_buy = 'sell';
            $ads->pre_pay_ejare = 0;
            $ads->monthly_price_ejare = 0;
            $ads->is_in_hoome = 0;
            $ads->rooms_count = 0;
            $ads->price_kharid = 0;
        }
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = $request->status;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->created_at_2 = Carbon::now()->toDateTimeString();
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();

        if ($request->has('send_notification') && $request->status == 'approved') {
            $fcm_tokens = DB::table('notification_setting')->where('send_ads_notifications', '=', 1)->pluck('fcm_token')->toArray();
            if (count($fcm_tokens) > 0) {
                $optionBuilder = new OptionsBuilder();
                $optionBuilder->setTimeToLive(60 * 20);

                $notificationBuilder = new PayloadNotificationBuilder('ایران اپ');
                $notificationBuilder->setBody($ads->ads_title)
                    ->setSound('default');

                $dataBuilder = new PayloadDataBuilder();
                $dataBuilder->addData(['status' => '0', 'content_id' => $ads->id]);
                $data = $dataBuilder->build();
                $option = $optionBuilder->build();
                $notification = $notificationBuilder->build();

                $downstreamResponse = FCM::sendTo($fcm_tokens, $option, $notification, $data);

                $downstreamResponse->numberFailure();
                $downstreamResponse->numberModification();
                $downstreamResponse->numberSuccess();


            }
        }
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'آگهی با موفقیت ذخیره شد.';
        return redirect()->route('estateAds.photos.show', $ads->id)->with('success_msg', $msg);
    }

    public function showUpdatePage(Request $request, EstateAds $ads)
    {
        $data['provinces'] = Province::all();
        $data['users'] = User::all();
        $data['ads'] = $ads;
        $data['photos'] = $ads->photo;
        $data['categories'] = EstateAdsCategories::whereNull('parent_id')->get();
        $currentCity = Region::find($ads->region_id)->city;
        $currentProvince = $currentCity->province;
        $data['currentProvince'] = $currentProvince;
        $data['currentCity'] = $currentCity;
        $data['cities'] = $currentProvince->city;
        $data['regions'] = $currentCity->region;
        $parentCategory = EstateAdsCategories::find($ads->category_id)->parent_id;
        $data['photos'] = $ads->photo;
        $data['parent_category_id'] = $parentCategory;
        return view('admin.estates.update')->with($data);
    }

    public function update(Request $request, EstateAds $ads)
    {
        $ads->ads_title = $request->ads_title;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440, null, function ($constraint) {
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 20);
            $imageToAddWatermark->save();
        }
        if ($request->category_id == 1) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            if ($subCategory_id == 8) {
                $ads->rooms_count = null;
            } else {
                $ads->rooms_count = $request->rooms_count;
            }
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->price_kharid = $request->price_kharid;
            } else if ($request->price__type == 'moavaze') {
                $ads->price_kharid = -1;
            } else {
                $ads->price_kharid = 0;
            }

            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else if ($request->category_id == 2) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->pre_pay_ejare = $request->pre_pay_ejare;
            } else if ($request->price__type == 'free') {
                $ads->pre_pay_ejare = -1;
            } else {
                $ads->pre_pay_ejare = 0;
            }
            if ($request->ejare__type == 'maghtoo') {
                $ads->monthly_price_ejare = $request->monthly_price_ejare;
            } else if ($request->ejare__type == 'free') {
                $ads->monthly_price_ejare = -1;
            } else {
                $ads->monthly_price_ejare = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else if ($request->category_id == 3) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price__type == 'maghtoo') {
                $ads->price_kharid = $request->price_kharid;
            } else if ($request->price__type == 'moaveze') {
                $ads->price_kharid = -1;
            } else {
                $ads->price_kharid = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
            if ($request->sanad_edari == 'no') {
                $ads->sanad_edari = 0;
            } else {
                $ads->sanad_edari = 1;
            }
        } else if ($request->category_id == 4) {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            if ($request->price_type == 'maghtoo') {
                $ads->pre_pay_ejare = $request->pre_pay_ejare;
            } else if ($request->price_type == 'free') {
                $ads->pre_pay_ejare = -1;
            } else {
                $ads->pre_pay_ejare = 0;
            }
            if ($request->ejare__type == 'maghtoo') {
                $ads->monthly_price_ejare = $request->monthly_price_ejare;
            } else if ($request->ejare__type == 'free') {
                $ads->monthly_price_ejare = -1;
            } else {
                $ads->monthly_price_ejare = 0;
            }
            $ads->rooms_count = $request->rooms_count;
            if ($request->sell_or_buy == 'sell') {
                $ads->sell_or_buy = 'sell';
            } else {
                $ads->sell_or_buy = 'buy';
            }
            if ($request->is_in_hoome == 'no') {
                $ads->is_in_hoome = 0;
            } else {
                $ads->is_in_hoome = 1;
            }
        } else {
            $subCategory_id = $request->sub_category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = 0;
            $ads->sell_or_buy = 'sell';
            $ads->pre_pay_ejare = 0;
            $ads->monthly_price_ejare = 0;
            $ads->is_in_hoome = 0;
            $ads->rooms_count = 0;
            $ads->price_kharid = 0;
        }
        $ads->user_id = $request->user_id;
        $ads->address = $request->address;
        $ads->user_type = $request->user_type;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = $request->status;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        if($request->has('latitude')){
            $ads->latitude = $request->latitude;
        }else
            $ads->latitude = null;
        if($request->has('longitude')){
            $ads->longitude = $request->longitude;
        }else
            $ads->longitude = null;
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'آگهی با موفقیت ویرایش شد.';
        if (count($ads->photo) == 5) {
            return redirect()->route('showAllEstateAdsInAdminPanel')->with('success_msg', $msg);
        }
        return redirect()->route('estateAds.photos.show', $ads->id)->with('success_msg', $msg);
    }

    public function saveJson(SaveJson $request)
    {
        $user = auth('sanctum')->user();
        $ads = new EstateAds();
        $ads->ads_title = $request->ads_title;
        $ads->category_id = $request->category_id;
        $category = EstateAdsCategories::find($request->category_id);
        if ($category->parent_id) {
            $parentCategory = EstateAdsCategories::find($category->parent_id);
        } else {
            $parentCategory = null;
        }
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 25);
            $imageToAddWatermark->save();
        }
        $ads->user_id = $user->id;
        $ads->address = $request->address;
        $ads->user_type = $request->user_type;
        if ($parentCategory->id == 1) {
            $subCategory_id = $request->category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            $ads->price_kharid = 0;
            $ads->rooms_count = $request->rooms_count;
            $ads->sell_or_buy = $request->sell_or_buy;
            $ads->is_in_hoome = $request->is_in_hoome;
            $ads->price_kharid = $request->price_kharid;
        } else if ($parentCategory->id == 2) {
            $subCategory_id = $request->category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            $ads->pre_pay_ejare = $request->pre_pay_ejare;
            $ads->monthly_price_ejare = $request->monthly_price_ejare;
            $ads->rooms_count = $request->rooms_count;
            $ads->sell_or_buy = $request->sell_or_buy;
            $ads->is_in_hoome = $request->is_in_hoome;
            $ads->ejare_or_kharid = 'kharid';
        } else if ($parentCategory->id == 3) {
            $subCategory_id = $request->category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            $ads->price_kharid = 0;
            $ads->rooms_count = $request->rooms_count;
            $ads->sell_or_buy = $request->sell_or_buy;
            $ads->is_in_hoome = $request->is_in_hoome;
            $ads->sanad_edari = $request->sanad_edari;
            $ads->ejare_or_kharid = 'kharid';
            $ads->price_kharid = $request->price_kharid;
        } else if ($parentCategory->id == 4) {
            $subCategory_id = $request->category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = $request->meters;
            $ads->pre_pay_ejare = $request->pre_pay_ejare;
            $ads->monthly_price_ejare = $request->monthly_price_ejare;
            $ads->rooms_count = $request->rooms_count;
            $ads->sell_or_buy = $request->sell_or_buy;
            $ads->is_in_hoome = $request->is_in_hoome;
            $ads->ejare_or_kharid = 'ejare';
        } else {
            $subCategory_id = $request->category_id;
            $ads->category_id = $subCategory_id;
            $ads->meters = 0;
            $ads->sell_or_buy = $request->sell_or_buy;
            $ads->pre_pay_ejare = 0;
            $ads->monthly_price_ejare = 0;
            $ads->is_in_hoome = 0;
            $ads->rooms_count = 0;
            $ads->price_kharid = 0;
            $ads->ejare_or_kharid = 'kharid';
        }
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->created_at_2 = Carbon::now()->toDateTimeString();
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();
        if ($request->hasFile('photos')) {
            $photos = $request->photos;
            foreach ($photos as $photo) {
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo'), $imgName);
                $ads->photo()->create([
                    'file_name' => $imgName
                ]);

                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 25);
                $imageToAddWatermark->save();
            }
        }

        return response()->json(['status' => 200]);
    }

    public function getJson(GetJson $request, City $city)
    {
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $adsList = new EstateAds();
        $adsList = $adsList->selectFields(EstateAds::FIELDS)
            ->orderBy('estates_ads.created_at', 'desc')
            ->where('estates_ads.status', '=', 'approved')
            ->offset($offset)->limit($limit)
            ->where('city.id', '=', $city->id)
            ->where('estates_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('estates_ads.valid_until' , '>=' , Carbon::now()->toDateString());
        if ($request->has('category_id')) {
            $adsList = $adsList->whereRaw('(estates_ads.category_id = ' . $request->category_id . ' or estate_categories.parent_id = ' . $request->category_id . ')');
        }
        $adsList = $adsList->get();
        foreach ($adsList as $index => $row) {
            $photos = EstateAdsPhoto::where('estates_ads_id', $row->id)->get();
            foreach ($photos as $pIndex => $pRow) {
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $adsList[$index]->photos = $photos;

            if ($row->thumbnail_photo) {
                $adsList[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $adsList[$index]->elapsed_time = getElapsedTime($row->created_at_2);
            $adsList[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
        }
        return response()->json(['status' => 200, 'list' => $adsList]);
    }

    public function getUserAds(Request $request)
    {
        $user = auth('sanctum')->user();
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $adsObj = new EstateAds();
        $adsList = $adsObj->selectFields(EstateAds::FIELDS)
            ->where('estates_ads.user_id', '=', $user->id)
            ->where('estates_ads.status', '!=', 'deleted')
            ->offset($offset)->limit($limit)
            ->orderBy('estates_ads.created_at', 'desc')
            ->where('estates_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('estates_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->get();
        foreach ($adsList as $index => $row) {
            $adsList[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
            $adsList[$index]->elapsed_time = getElapsedTime($row->created_at_2);

            $photos = EstateAdsPhoto::where('estates_ads_id', $row->id)->get();
            foreach ($photos as $pIndex => $pRow) {
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $adsList[$index]->photos = $photos;

            $adsList[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
        }
        return response()->json(['status' => 200, 'list' => $adsList]);
    }

    public function delete(Request $request, EstateAds $ads)
    {
        $ads->status = 'deleted';
        $ads->save();

        return response()->json(['status' => 200]);
    }

    public function updateJson(Request $request, EstateAds $ads)
    {
        $user = auth('sanctum')->user();
        $ads->ads_title = $request->ads_title;
        $ads->category_id = $request->category_id;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 25);
            $imageToAddWatermark->save();
        } else {
            if ($request->has('delete_thumbnail_photo')) {
                $ads->thumbnail_photo = null;
            }
        }
        $ads->user_id = $user->id;
        $ads->address = $request->address;
        $ads->user_type = $request->user_type;
        if ($request->has('is_in_hoome') && $request->is_in_hoome == 1) {
            $ads->is_in_hoome = $request->is_in_hoome;
        } else {
            $ads->is_in_hoome = 0;
        }
        if ($request->has('price_kharid')) {
            $ads->price_kharid = $request->price_kharid;
        } else {
            $ads->price_kharid = null;
        }
        $ads->sell_or_buy = $request->sell_or_buy;
        $ads->ejare_or_kharid = $request->ejare_or_kharid;
        $ads->pre_pay_ejare = $request->pre_pay_ejare;
        $ads->monthly_price_ejare = $request->monthly_price_ejare;

        $ads->rooms_count = $request->rooms_count;
        $ads->meters = $request->meters;
        $ads->type_karbari = $request->type_karbari;
        $ads->sanad_edari = $request->has('sanad_edari');
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->status = 'pending';
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();
        if ($request->hasFile('photos')) {
            $photos = $request->photos;
            foreach ($photos as $photo) {
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo'), $imgName);
                $ads->photo()->create([
                    'file_name' => $imgName
                ]);
                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 25);
                $imageToAddWatermark->save();
            }
        }

        if ($request->has('delete_photo')) {
            $photosToDelete = $request->delete_photo;
            foreach ($photosToDelete as $id) {
                $photo = EstateAdsPhoto::find($id);
                $photo->delete();
            }
        }
        return response()->json(['status' => 200]);
    }

    public function searchJson(Request $request)
    {
        $adsObj = new EstateAds();
        $ads = $adsObj->selectFields(EstateAds::FIELDS)
            ->orderBy('estates_ads.created_at', 'desc')
            ->where('estates_ads.status', '=', 'approved')
            ->where('estates_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('estates_ads.valid_until' , '>=' , Carbon::now()->toDateString());
        if ($request->has('region_id')) {
            $ads = $ads->where('estates_ads.region_id', '=', $request->region_id);
        } else {
            if ($request->has('city_id')) {
                $ads = $ads->where('city.id', '=', $request->city_id);
            } else {
                if ($request->has('province_id')) {
                    $ads = $ads->where('province.id', '=', $request->province_id);
                }
            }
        }

        if ($request->has('is_in_hoome')) {
            $ads = $ads->where('estates_ads.is_in_hoome', '=', $request->is_in_hoome);
        }
        if ($request->has('rooms_count')) {
            $ads = $ads->where('estates_ads.rooms_count', '=', $request->rooms_count);
        }
        if ($request->has('user_type')) {
            $ads = $ads->where('estates_ads.user_type', '=', $request->user_type);
        }
        if ($request->has('sell_or_buy')) {
            $ads = $ads->where('estates_ads.sell_or_buy', '=', $request->sell_or_buy);
        }
        if ($request->has('price_kharid_from') && $request->has('price_kharid_to')) {
            if($request->price_kharid_from == 0 ){
                $priceKharidFrom = 1;
            }else{
                $priceKharidFrom = $request->price_kharid_from;
            }
            $ads = $ads->where('estates_ads.price_kharid', '>=', $priceKharidFrom)
                ->where('estates_ads.price_kharid', '<=', $request->price_kharid_to);
        }
        if ($request->has('meters_from') && $request->has('meters_to')) {
            $ads = $ads->where('estates_ads.meters', '<=', $request->meters_to)
                ->where('estates_ads.meters', '>=', $request->meters_from);
        }
        if ($request->has('monthly_price_from') && $request->has('monthly_price_to')) {
            if($request->monthly_price_from == 0 ){
                $monthlyPriceFrom = 1;
            }else{
                $monthlyPriceFrom = $request->monthly_price_from;
            }
            $ads = $ads->where('estates_ads.monthly_price_ejare', '<=', $request->monthly_price_to)
                ->where('estates_ads.monthly_price_ejare', '>=', $monthlyPriceFrom);
        }
        if ($request->has('vadiee_from') && $request->has('vadiee_to')) {
            if($request->vadiee_from == 0){
                $vadieFrom = 1;
            }else{
                $vadieFrom = $request->vadiee_from;
            }
            $ads = $ads->where('estates_ads.pre_pay_ejare', '<=', $request->vadiee_to)
                ->where('estates_ads.pre_pay_ejare', '>=', $vadieFrom);
        }
        if ($request->has('sanad_edari')) {
            $ads = $ads->where('estates_ads.sanad_edari', '=', $request->sanad_edari);
        }
        if ($request->has('keyword')) {
            $ads = $ads->whereRaw('( MATCH(estates_ads.ads_title , estates_ads.description) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) )');
        }
        if ($request->has('category_id')) {
            $ads = $ads->whereRaw('(
                estates_ads.category_id = ' . $request->category_id . '
                or estate_categories.parent_id = ' . $request->category_id . '
            )');
        }
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;

        $list = $ads->offset($offset)->limit($limit)->get();
        foreach ($list as $index => $row) {
            $photos = EstateAdsPhoto::where('estates_ads_id', $row->id)->get();

            foreach ($photos as $pIndex => $pRow) {
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $list[$index]->photos = $photos;
            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);
            $list[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
        }
        return response()->json(['status' => 200, 'list' => $list]);
    }

    public function deleteThumbnailPhoto(Request $request, EstateAds $ads)
    {
        $ads->thumbnail_photo = null;
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'حذف تصویر';
        $msg->msg = 'تصویر بندانگشتی با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg', $msg);
    }
}
