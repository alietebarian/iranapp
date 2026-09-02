package com.ideabonyan.iranapp.Models;

import com.ideabonyan.iranapp.Interface.ShowVipAdInAdListActivityInterface;
import com.ideabonyan.iranapp.Interface.ShowVipAdInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by SIM on 8/9/2017.
 */

public class VipAd {

    static String vip_ads_id;
    static String vip_ads_photo;
    static String id;
    static String title;
    static String latitude;
    static String longitude;
    static String city_id;
    static String address;
    static String type;
    static String sub_category_id;
    static String updates_count;
    static String email;
    static String mobile;
    static String tel1;
    static String tel2;
    static String link;
    static String discount;
    static String working_time;
    static String telegram;
    static String instagram;
    static String notes;
    static String status;
    static String ads_owner_name;
    static String ads_plan_id;
    static String created_at;
    static String updated_at;
    static String user_id;
    static String max_number_of_update;
    static String[] photos_id;
    static String[] photos_ads_id;
    static String[] photos_file_name;
    static String[] photos_created_at;
    static String[] photos_updated_at;
    static List<PhotosData> photos;

    public static List<PhotosData> getPhotos() {
        return photos;
    }



    public static String getVip_ads_id() {
        return vip_ads_id;
    }

    public static String getVip_ads_photo() {
        return vip_ads_photo;
    }

    public static String getId() {
        return id;
    }

    public static String getTitle() {
        return title;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static String getCity_id() {
        return city_id;
    }

    public static String getAddress() {
        return address;
    }

    public static String getType() {
        return type;
    }

    public static String getSub_category_id() {
        return sub_category_id;
    }

    public static String getUpdates_count() {
        return updates_count;
    }

    public static String getEmail() {
        return email;
    }

    public static String getMobile() {
        return mobile;
    }

    public static String getTel1() {
        return tel1;
    }

    public static String getTel2() {
        return tel2;
    }

    public static String getLink() {
        return link;
    }

    public static String getDiscount() {
        return discount;
    }

    public static String getWorking_time() {
        return working_time;
    }

    public static String getTelegram() {
        return telegram;
    }

    public static String getInstagram() {
        return instagram;
    }

    public static String getNotes() {
        return notes;
    }

    public static String getStatus() {
        return status;
    }

    public static String getAds_plan_id() {
        return ads_plan_id;
    }

    public static String getCreated_at() {
        return created_at;
    }

    public static String getUpdated_at() {
        return updated_at;
    }

    public static String[] getPhotos_id() {
        return photos_id;
    }

    public static String[] getPhotos_ads_id() {
        return photos_ads_id;
    }

    public static String[] getPhotos_file_name() {
        return photos_file_name;
    }

    public static String[] getPhotos_created_at() {
        return photos_created_at;
    }

    public static String[] getPhotos_updated_at() {
        return photos_updated_at;
    }

    public static String getUser_id() {
        return user_id;
    }

    public static String getMax_number_of_update() {
        return max_number_of_update;
    }

    public static void vipAdObject(JSONObject jsonObject, String callerActivity) {
        VipAd vipAd  = new VipAd();

        try {

            vipAd.vip_ads_id = jsonObject.getString("vip_ads_id");
            vipAd.vip_ads_photo = jsonObject.getString("vip_ads_photo");
            vipAd.id = jsonObject.getString("id");
            vipAd.title = jsonObject.getString("title");
            vipAd.latitude = jsonObject.getString("latitude");
            vipAd.longitude = jsonObject.getString("longitude");
            vipAd.city_id = jsonObject.getString("city_id");
            vipAd.address = jsonObject.getString("address");
            vipAd.type = jsonObject.getString("type");
            vipAd.sub_category_id = jsonObject.getString("sub_category_id");
            vipAd.updates_count = jsonObject.getString("updates_count");
            vipAd.email = jsonObject.getString("email");
            vipAd.mobile = jsonObject.getString("mobile");
            vipAd.tel1 = jsonObject.getString("tel1");
            vipAd.tel2 = jsonObject.getString("tel2");
            vipAd.link = jsonObject.getString("link");
            vipAd.discount = jsonObject.getString("discount");
            vipAd.working_time = jsonObject.getString("working_time");
            vipAd.telegram = jsonObject.getString("telegram");
            vipAd.instagram = jsonObject.getString("instagram");
            vipAd.notes = jsonObject.getString("notes");
            vipAd.status = jsonObject.getString("status");
            vipAd.ads_owner_name = jsonObject.getString("ads_owner_name");
            vipAd.ads_plan_id = jsonObject.getString("ads_plan_id");
            vipAd.created_at = jsonObject.getString("created_at");
            vipAd.updated_at = jsonObject.getString("updated_at");
            vipAd.user_id = jsonObject.getString("user_id");
            vipAd.max_number_of_update = jsonObject.getString("max_number_of_updates");

            JSONArray jsonArray = jsonObject.getJSONArray("photos");

            vipAd.photos = PhotosData.Import(jsonArray);

            vipAd.photos_id = new String[jsonArray.length()];
            vipAd.photos_ads_id = new String[jsonArray.length()];
            vipAd.photos_file_name = new String[jsonArray.length()];
            vipAd.photos_created_at = new String[jsonArray.length()];
            vipAd.photos_updated_at = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                vipAd.photos_id[i] = jsonObject1.getString("id");
                vipAd.photos_ads_id[i] = jsonObject1.getString("ads_id");
                vipAd.photos_file_name[i] = jsonObject1.getString("file_name");
                vipAd.photos_created_at[i] = jsonObject1.getString("created_at");
                vipAd.photos_updated_at[i] = jsonObject1.getString("updated_at");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // data is received in MainActivity
        if (callerActivity.equals("mainActivity")) {
            showVipAdInterface.onAdFound(vipAd, callerActivity);
        } else {
            showVipAdInAdListActivityInterface.onAdFound(vipAd, callerActivity);
        }
    }

    public static VipAd getvipAdObject(JSONObject jsonObject) {
        VipAd vipAd  = new VipAd();

        try {

            vipAd.vip_ads_id = jsonObject.getString("vip_ads_id");
            vipAd.vip_ads_photo = jsonObject.getString("vip_ads_photo");
            vipAd.id = jsonObject.getString("id");
            vipAd.title = jsonObject.getString("title");
            vipAd.latitude = jsonObject.getString("latitude");
            vipAd.longitude = jsonObject.getString("longitude");
            vipAd.city_id = jsonObject.getString("city_id");
            vipAd.address = jsonObject.getString("address");
            vipAd.type = jsonObject.getString("type");
            vipAd.sub_category_id = jsonObject.getString("sub_category_id");
            vipAd.updates_count = jsonObject.getString("updates_count");
            vipAd.email = jsonObject.getString("email");
            vipAd.mobile = jsonObject.getString("mobile");
            vipAd.tel1 = jsonObject.getString("tel1");
            vipAd.tel2 = jsonObject.getString("tel2");
            vipAd.link = jsonObject.getString("link");
            vipAd.discount = jsonObject.getString("discount");
            vipAd.working_time = jsonObject.getString("working_time");
            vipAd.telegram = jsonObject.getString("telegram");
            vipAd.instagram = jsonObject.getString("instagram");
            vipAd.notes = jsonObject.getString("notes");
            vipAd.status = jsonObject.getString("status");
            vipAd.ads_plan_id = jsonObject.getString("ads_plan_id");
            vipAd.created_at = jsonObject.getString("created_at");
            vipAd.updated_at = jsonObject.getString("updated_at");


            JSONArray jsonArray = jsonObject.getJSONArray("photos");

            vipAd.photos = PhotosData.Import(jsonArray);

            vipAd.photos_id = new String[jsonArray.length()];
            vipAd.photos_ads_id = new String[jsonArray.length()];
            vipAd.photos_file_name = new String[jsonArray.length()];
            vipAd.photos_created_at = new String[jsonArray.length()];
            vipAd.photos_updated_at = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                vipAd.photos_id[i] = jsonObject1.getString("id");
                vipAd.photos_ads_id[i] = jsonObject1.getString("ads_id");
                vipAd.photos_file_name[i] = jsonObject1.getString("file_name");
                vipAd.photos_created_at[i] = jsonObject1.getString("created_at");
                vipAd.photos_updated_at[i] = jsonObject1.getString("updated_at");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return vipAd;
    }


    static ShowVipAdInterface showVipAdInterface;
    public static void binddata(ShowVipAdInterface mshowVipAdInterface) {
        showVipAdInterface = mshowVipAdInterface;
    }
    static ShowVipAdInAdListActivityInterface showVipAdInAdListActivityInterface;
    public static void binddataForOtherActivities(ShowVipAdInAdListActivityInterface mshowVipAdInAdListActivityInterface) {
        showVipAdInAdListActivityInterface = mshowVipAdInAdListActivityInterface;
    }
}
