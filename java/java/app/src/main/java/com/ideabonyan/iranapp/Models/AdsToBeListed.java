package com.ideabonyan.iranapp.Models;


import com.ideabonyan.iranapp.Activity.AdListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdsToBeListed implements Serializable{

    String id;
    String title;
    String latitude;
    String longitude;
    String city_id;
    String province_id;
    String address;
    String type;
    String category_id;
    String sub_category_id;
    String updates_count;
    String email;
    String mobile;
    String tel1;
    String tel2;
    String link;
    String discount;
    String working_time;
    String telegram;
    String instagram;
    String notes;
    String status;
    String ads_owner_name;
    String ads_plan_id;
    String valid_since;
    String valid_until;
    String created_at;
    String updated_at;
    String num_of_stars;
    String ordering_factor;
    String plan_title;
    String category_name;
    String sub_category_name;
    String city_name;
    String province_name;
    String user_id;
    String max_number_of_update;
    List<PhotosData> photos;


    static public List<AdsToBeListed> Import(JSONObject jsonObject) throws JSONException {
        List<AdsToBeListed> adsToBeListedList = new ArrayList<>();


        try {
            JSONObject vipAdJsonObject = jsonObject.getJSONObject("vip_ad");
            if (vipAdJsonObject != null) {
                if (!AdListActivity.isTheAdShownYet) {
                    VipAd.vipAdObject(vipAdJsonObject, "adsToBeListed");
                    AdListActivity.isTheAdShownYet = true;
                }
            }
        }catch (JSONException e){}

        JSONArray jsonArray = jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            AdsToBeListed adsToBeListed = new AdsToBeListed();
            JSONObject json = jsonArray.getJSONObject(i);

            adsToBeListed.id = json.getString("id");
            adsToBeListed.title = json.getString("title");
            adsToBeListed.latitude = json.getString("latitude");
            adsToBeListed.longitude = json.getString("longitude");
            adsToBeListed.city_id = json.getString("city_id");
            adsToBeListed.province_id = json.getString("province_id");
            adsToBeListed.address = json.getString("address");
            adsToBeListed.type = json.getString("type");
            adsToBeListed.category_id = json.getString("category_id");
            adsToBeListed.sub_category_id = json.getString("sub_category_id");
            adsToBeListed.updates_count = json.getString("updates_count");
            adsToBeListed.email = json.getString("email");
            adsToBeListed.mobile = json.getString("mobile");
            adsToBeListed.tel1 = json.getString("tel1");
            adsToBeListed.tel2 = json.getString("tel2");
            adsToBeListed.link = json.getString("link");
            adsToBeListed.discount = json.getString("discount");
            adsToBeListed.working_time = json.getString("working_time");
            adsToBeListed.telegram = json.getString("telegram");
            adsToBeListed.instagram = json.getString("instagram");
            adsToBeListed.notes = json.getString("notes");
            adsToBeListed.status = json.getString("status");
            adsToBeListed.ads_owner_name = json.getString("ads_owner_name");
            adsToBeListed.ads_plan_id = json.getString("ads_plan_id");
            adsToBeListed.valid_since = json.getString("valid_since");
            adsToBeListed.valid_until = json.getString("valid_until");
            adsToBeListed.created_at = json.getString("created_at");
            adsToBeListed.updated_at = json.getString("updated_at");
            adsToBeListed.category_name = json.getString("category_name");
            adsToBeListed.sub_category_name = json.getString("sub_category_name");
            adsToBeListed.city_name = json.getString("city_name");
            adsToBeListed.province_name = json.getString("province_name");
            adsToBeListed.num_of_stars = json.getString("num_of_stars");
            adsToBeListed.ordering_factor = json.getString("ordering_factor");
            adsToBeListed.plan_title = json.getString("plan_title");
            adsToBeListed.user_id = json.getString("user_id");
            adsToBeListed.max_number_of_update = json.getString("max_number_of_update");

             adsToBeListed.photos = PhotosData.Import(json.getJSONArray("photos"));

            adsToBeListedList.add(adsToBeListed);
        }

        return adsToBeListedList;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public String getUpdates_count() {
        return updates_count;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getTel1() {
        return tel1;
    }

    public String getTel2() {
        return tel2;
    }

    public String getLink() {
        return link;
    }

    public String getDiscount() {
        return discount;
    }

    public String getWorking_time() {
        return working_time;
    }

    public String getTelegram() {
        return telegram;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public String getAds_plan_id() {
        return ads_plan_id;
    }

    public String getValid_since() {
        return valid_since;
    }

    public String getValid_until() {
        return valid_until;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getNum_of_stars() {
        return num_of_stars;
    }

    public String getOrdering_factor() {
        return ordering_factor;
    }

    public String getPlan_title() {
        return plan_title;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getProvince_name() {
        return province_name;
    }

    public List<PhotosData> getPhotos() {
        return photos;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public void setUpdates_count(String updates_count) {
        this.updates_count = updates_count;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setTel1(String tel1) {
        this.tel1 = tel1;
    }

    public void setTel2(String tel2) {
        this.tel2 = tel2;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setWorking_time(String working_time) {
        this.working_time = working_time;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAds_plan_id(String ads_plan_id) {
        this.ads_plan_id = ads_plan_id;
    }

    public void setValid_since(String valid_since) {
        this.valid_since = valid_since;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setNum_of_stars(String num_of_stars) {
        this.num_of_stars = num_of_stars;
    }

    public void setOrdering_factor(String ordering_factor) {
        this.ordering_factor = ordering_factor;
    }

    public void setPlan_title(String plan_title) {
        this.plan_title = plan_title;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public void setPhotos(List<PhotosData> photos) {
        this.photos = photos;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMax_number_of_update() {
        return max_number_of_update;
    }

    public void setMax_number_of_update(String max_number_of_update) {
        this.max_number_of_update = max_number_of_update;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getAds_owner_name() {
        return ads_owner_name;
    }

    public void setAds_owner_name(String ads_owner_name) {
        this.ads_owner_name = ads_owner_name;
    }
}

