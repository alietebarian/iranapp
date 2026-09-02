package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/18.
 */

public class Estates implements Serializable {
    private String id;
    private String thumbnail_photo;
    private String region_id;
    private String user_id;
    private String category_id;
    private String user_type;
    private String is_in_hoome;
    private String sell_or_buy;
    private String ejare_or_kharid;
    private String price_kharid;
    private String pre_pay_ejare;
    private String monthly_price_ejare;
    private String rooms_count;
    private String meters;
    private String type_karbari;
    private String sanad_edari;
    private String valid_since;
    private String valid_until;
    private String status;
    private String ads_title;
    private String description;
    private String longitude;
    private String latitude;
    private String address;
    private String telephone1;
    private String telephone2;
    private String ads_owner_name;
    private String created_at;
    private String updated_at;
    private String region_name;
    private String city_id;
    private String city_name;
    private String province_id;
    private String province_name;
    private String user_first_name;
    private String user_last_name;
    private String category_name;
    private String elapsed_time;
    private String fa_created_at;
    private String category_parent_id;
    private List<PhotosData> photosDatas;

    public String getCategory_parent_id() {
        return category_parent_id;
    }

    public void setCategory_parent_id(String category_parent_id) {
        this.category_parent_id = category_parent_id;
    }

    public String getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(String elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public String getFa_created_at() {
        return fa_created_at;
    }

    public void setFa_created_at(String fa_created_at) {
        this.fa_created_at = fa_created_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail_photo() {
        return thumbnail_photo;
    }

    public void setThumbnail_photo(String thumbnail_photo) {
        this.thumbnail_photo = thumbnail_photo;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getIs_in_hoome() {
        return is_in_hoome;
    }

    public void setIs_in_hoome(String is_in_hoome) {
        this.is_in_hoome = is_in_hoome;
    }

    public String getSell_or_buy() {
        return sell_or_buy;
    }

    public void setSell_or_buy(String sell_or_buy) {
        this.sell_or_buy = sell_or_buy;
    }

    public String getEjare_or_kharid() {
        return ejare_or_kharid;
    }

    public void setEjare_or_kharid(String ejare_or_kharid) {
        this.ejare_or_kharid = ejare_or_kharid;
    }

    public String getPrice_kharid() {
        return price_kharid;
    }

    public void setPrice_kharid(String price_kharid) {
        this.price_kharid = price_kharid;
    }

    public String getPre_pay_ejare() {
        return pre_pay_ejare;
    }

    public void setPre_pay_ejare(String pre_pay_ejare) {
        this.pre_pay_ejare = pre_pay_ejare;
    }

    public String getMonthly_price_ejare() {
        return monthly_price_ejare;
    }

    public void setMonthly_price_ejare(String monthly_price_ejare) {
        this.monthly_price_ejare = monthly_price_ejare;
    }

    public String getRooms_count() {
        return rooms_count;
    }

    public void setRooms_count(String rooms_count) {
        this.rooms_count = rooms_count;
    }

    public String getMeters() {
        return meters;
    }

    public void setMeters(String meters) {
        this.meters = meters;
    }

    public String getType_karbari() {
        return type_karbari;
    }

    public void setType_karbari(String type_karbari) {
        this.type_karbari = type_karbari;
    }

    public String getSanad_edari() {
        return sanad_edari;
    }

    public void setSanad_edari(String sanad_edari) {
        this.sanad_edari = sanad_edari;
    }

    public String getValid_since() {
        return valid_since;
    }

    public void setValid_since(String valid_since) {
        this.valid_since = valid_since;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAds_title() {
        return ads_title;
    }

    public void setAds_title(String ads_title) {
        this.ads_title = ads_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getAds_owner_name() {
        return ads_owner_name;
    }

    public void setAds_owner_name(String ads_owner_name) {
        this.ads_owner_name = ads_owner_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getUser_first_name() {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }

    public String getUser_last_name() {
        return user_last_name;
    }

    public void setUser_last_name(String user_last_name) {
        this.user_last_name = user_last_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<PhotosData> getPhotosDatas() {
        return photosDatas;
    }

    public void setPhotosDatas(List<PhotosData> photosDatas) {
        this.photosDatas = photosDatas;
    }

    public static List<Estates> Import_list(JSONArray jsonArray) {
        List<Estates> estates = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id=jsonObject.getString("id");
                String thumbnail_photo=jsonObject.getString("thumbnail_photo");
                String region_id=jsonObject.getString("region_id");
                String user_id=jsonObject.getString("user_id");
                String category_id=jsonObject.getString("category_id");
                String user_type=jsonObject.getString("user_type");
                String is_in_hoome=jsonObject.getString("is_in_hoome");
                String sell_or_buy=jsonObject.getString("sell_or_buy");
                String ejare_or_kharid=jsonObject.getString("ejare_or_kharid");
                String price_kharid=jsonObject.getString("price_kharid");
                String pre_pay_ejare=jsonObject.getString("pre_pay_ejare");
                String monthly_price_ejare=jsonObject.getString("monthly_price_ejare");
                String rooms_count=jsonObject.getString("rooms_count");
                String meters=jsonObject.getString("meters");
                String type_karbari=jsonObject.getString("type_karbari");
                String sanad_edari=jsonObject.getString("sanad_edari");
                String valid_since=jsonObject.getString("valid_since");
                String valid_until=jsonObject.getString("valid_until");
                String status=jsonObject.getString("status");
                String ads_title=jsonObject.getString("ads_title");
                String description=jsonObject.getString("description");
                String latitude=jsonObject.getString("latitude");
                String longitude=jsonObject.getString("longitude");
                String address=jsonObject.getString("address");
                String telephone1=jsonObject.getString("telephone1");
                String telephone2=jsonObject.getString("telephone2");
                String ads_owner_name=jsonObject.getString("ads_owner_name");
                String created_at=jsonObject.getString("created_at");
                String updated_at=jsonObject.getString("updated_at");
                String region_name=jsonObject.getString("region_name");
                String city_id=jsonObject.getString("city_id");
                String city_name=jsonObject.getString("city_name");
                String province_id=jsonObject.getString("province_id");
                String province_name=jsonObject.getString("province_name");
                String user_first_name=jsonObject.getString("user_first_name");
                String user_last_name=jsonObject.getString("user_last_name");
                String category_name=jsonObject.getString("category_name");
                String elapsed_time=jsonObject.getString("elapsed_time");
                String fa_created_at=jsonObject.getString("fa_created_at");
                String category_parent_id=jsonObject.getString("category_parent_id");
                List<PhotosData>photosDatas=PhotosData.Import(jsonObject.getJSONArray("photos"));

                Estates estates1=new Estates();
                estates1.setId(id);
                estates1.setThumbnail_photo(thumbnail_photo);
                estates1.setRegion_id(region_id);
                estates1.setUser_id(user_id);
                estates1.setCategory_id(category_id);
                estates1.setUser_type(user_type);
                estates1.setIs_in_hoome(is_in_hoome);
                estates1.setSell_or_buy(sell_or_buy);
                estates1.setEjare_or_kharid(ejare_or_kharid);
                estates1.setPrice_kharid(price_kharid);
                estates1.setPre_pay_ejare(pre_pay_ejare);
                estates1.setMonthly_price_ejare(monthly_price_ejare);
                estates1.setRooms_count(rooms_count);
                estates1.setMeters(meters);
                estates1.setType_karbari(type_karbari);
                estates1.setSanad_edari(sanad_edari);
                estates1.setValid_since(valid_since);
                estates1.setValid_until(valid_until);
                estates1.setStatus(status);
                estates1.setAds_title(ads_title);
                estates1.setDescription(description);
                estates1.setLatitude(latitude);
                estates1.setLongitude(longitude);
                estates1.setAddress(address);
                estates1.setTelephone1(telephone1);
                estates1.setTelephone2(telephone2);
                estates1.setAds_owner_name(ads_owner_name);
                estates1.setCreated_at(created_at);
                estates1.setUpdated_at(updated_at);
                estates1.setRegion_name(region_name);
                estates1.setCity_id(city_id);
                estates1.setCity_name(city_name);
                estates1.setProvince_id(province_id);
                estates1.setProvince_name(province_name);
                estates1.setUser_first_name(user_first_name);
                estates1.setUser_last_name(user_last_name);
                estates1.setCategory_name(category_name);
                estates1.setElapsed_time(elapsed_time);
                estates1.setFa_created_at(fa_created_at);
                estates1.setCategory_parent_id(category_parent_id);
                estates1.setPhotosDatas(photosDatas);

                estates.add(estates1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return estates;
    }


}
