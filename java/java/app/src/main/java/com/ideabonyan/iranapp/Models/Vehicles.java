package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/15.
 */

public class Vehicles implements Serializable {
    private String id;
    private String thumbnail_photo;
    private String region_id;
    private String user_id;
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
    private String type;
    private String price;
    private String kilometre;
    private String production_year;
    private String chassis_type;
    private String cylinder_volume;
    private String neworold;
    private String brand_id;
    private String created_at;
    private String updated_at;
    private String province_name;
    private String province_id;
    private String city_name;
    private String city_id;
    private String region_name;
    private String user_last_name;
    private String user_first_name;
    private String model_id;
    private String model_name;
    private String passed_time;
    private String fa_created_at;
    private String brand;
    private String person_or_company;

    public String getPerson_or_company() {
        return person_or_company;
    }

    public void setPerson_or_company(String person_or_company) {
        this.person_or_company = person_or_company;
    }

    List<PhotosData> photosDatas;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPassed_time() {
        return passed_time;
    }

    public void setPassed_time(String passed_time) {
        this.passed_time = passed_time;
    }

    public String getFa_created_at() {
        return fa_created_at;
    }

    public void setFa_created_at(String fa_created_at) {
        this.fa_created_at = fa_created_at;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
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

    public String getProvince_id() {
        return province_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public List<PhotosData> getPhotosDatas() {
        return photosDatas;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public void setPhotosDatas(List<PhotosData> photosDatas) {
        this.photosDatas = photosDatas;
    }

    public Vehicles() {
    }

    public String getId() {
        return id;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getKilometre() {
        return kilometre;
    }

    public void setKilometre(String kilometre) {
        this.kilometre = kilometre;
    }

    public String getProduction_year() {
        return production_year;
    }

    public void setProduction_year(String production_year) {
        this.production_year = production_year;
    }

    public String getChassis_type() {
        return chassis_type;
    }

    public void setChassis_type(String chassis_type) {
        this.chassis_type = chassis_type;
    }

    public String getCylinder_volume() {
        return cylinder_volume;
    }

    public void setCylinder_volume(String cylinder_volume) {
        this.cylinder_volume = cylinder_volume;
    }

    public String getNeworold() {
        return neworold;
    }

    public void setNeworold(String neworold) {
        this.neworold = neworold;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
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

    public static List<Vehicles> Import_List(JSONObject jsonObject, String value) {
        List<Vehicles> vehiclesList = new ArrayList<>();
        try {
            JSONArray jsonArray=jsonObject.getJSONArray(value);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String thumbnail_photo = jsonObject1.getString("thumbnail_photo");
                String region_id = jsonObject1.getString("region_id");
                String user_id = jsonObject1.getString("user_id");
                String valid_since = jsonObject1.getString("valid_since");
                String valid_until = jsonObject1.getString("valid_until");
                String status = jsonObject1.getString("status");
                String ads_title = jsonObject1.getString("ads_title");
                String description = jsonObject1.getString("description");
                String longitude = jsonObject1.getString("longitude");
                String latitude = jsonObject1.getString("latitude");
                String address = jsonObject1.getString("address");
                String telephone1 = jsonObject1.getString("telephone1");
                String telephone2 = jsonObject1.getString("telephone2");
                String ads_owner_name = jsonObject1.getString("ads_owner_name");
                String type = jsonObject1.getString("type");
                String price = jsonObject1.getString("price");
                String kilometre = jsonObject1.getString("kilometre");
                String production_year = jsonObject1.getString("production_year");
                String chassis_type = jsonObject1.getString("chassis_type");
                String cylinder_volume = jsonObject1.getString("cylinder_volume");
                String neworold = jsonObject1.getString("neworold");
                String brand_id = jsonObject1.getString("brand_id");
                String created_at = jsonObject1.getString("created_at");
                String updated_at = jsonObject1.getString("updated_at");
                String region_name = jsonObject1.getString("region_name");
                String city_id = jsonObject1.getString("city_id");
                String city_name = jsonObject1.getString("city_name");
                String province_id = jsonObject1.getString("province_id");
                String province_name = jsonObject1.getString("province_name");
                String user_first_name = jsonObject1.getString("user_first_name");
                String user_last_name = jsonObject1.getString("user_last_name");
                String brand = jsonObject1.getString("brand");
                String model_id = jsonObject1.getString("model_id");
                String model_name = jsonObject1.getString("model_name");
                String fa_created_at = jsonObject1.getString("fa_created_at");
                String passed_time = jsonObject1.getString("passed_time");
                String person_or_company = jsonObject1.getString("person_or_company");
                List<PhotosData> photosDatas = PhotosData.Import(jsonObject1.getJSONArray("photos"));

                Vehicles vehicles = new Vehicles();
                vehicles.setFa_created_at(fa_created_at);
                vehicles.setPerson_or_company(person_or_company);
                vehicles.setPassed_time(passed_time);
                vehicles.setValid_since(valid_since);
                vehicles.setModel_id(model_id);
                vehicles.setModel_name(model_name);
                vehicles.setThumbnail_photo(thumbnail_photo);
                vehicles.setUser_id(user_id);
                vehicles.setRegion_id(region_id);
                vehicles.setId(id);
                vehicles.setLongitude(longitude);
                vehicles.setDescription(description);
                vehicles.setAds_title(ads_title);
                vehicles.setStatus(status);
                vehicles.setValid_until(valid_until);
                vehicles.setAds_owner_name(ads_owner_name);
                vehicles.setTelephone2(telephone2);
                vehicles.setTelephone1(telephone1);
                vehicles.setLatitude(latitude);
                vehicles.setAddress(address);
                vehicles.setChassis_type(chassis_type);
                vehicles.setProduction_year(production_year);
                vehicles.setKilometre(kilometre);
                vehicles.setPrice(price);
                vehicles.setType(type);
                vehicles.setUpdated_at(updated_at);
                vehicles.setCreated_at(created_at);
                vehicles.setBrand_id(brand_id);
                vehicles.setNeworold(neworold);
                vehicles.setCylinder_volume(cylinder_volume);
                vehicles.setProvince_name(province_name);
                vehicles.setProvince_id(province_id);
                vehicles.setCity_name(city_name);
                vehicles.setCity_id(city_id);
                vehicles.setRegion_name(region_name);
                vehicles.setPhotosDatas(photosDatas);
                vehicles.setBrand(brand);
                vehicles.setUser_last_name(user_last_name);
                vehicles.setUser_first_name(user_first_name);

                vehiclesList.add(vehicles);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vehiclesList;
    }
}

