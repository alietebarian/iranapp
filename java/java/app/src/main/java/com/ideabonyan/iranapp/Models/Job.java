package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/20.
 */

public class Job implements Serializable {
    private String id;
    private String user_id;
    private String specialty_id;
    private String region_id;
    private String education_level;
    private String agremment_type;
    private String description;
    private String type;
    private String thumbnail_photo;
    private String status;
    private String valid_since;
    private String valid_until;
    private String longitude;
    private String latitude;
    private String address;
    private String telephone1;
    private String telephone2;
    private String ads_owner_name;
    private String ads_title;
    private String created_at;
    private String updated_at;
    private String region_name;
    private String city_id;
    private String city_name;
    private String province_id;
    private String province_name;
    private String user_first_name;
    private String user_last_name;
    private String specialty;
    private String fa_created_at;
    private String elapsed_time;
    private String person_or_company;
    private List<PhotosData> photos;

    public Job() {
    }

    public String getPerson_or_company() {
        return person_or_company;
    }

    public void setPerson_or_company(String person_or_company) {
        this.person_or_company = person_or_company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSpecialty_id() {
        return specialty_id;
    }

    public void setSpecialty_id(String specialty_id) {
        this.specialty_id = specialty_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getEducation_level() {
        return education_level;
    }

    public void setEducation_level(String education_level) {
        this.education_level = education_level;
    }

    public String getAgremment_type() {
        return agremment_type;
    }

    public void setAgremment_type(String agremment_type) {
        this.agremment_type = agremment_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail_photo() {
        return thumbnail_photo;
    }

    public void setThumbnail_photo(String thumbnail_photo) {
        this.thumbnail_photo = thumbnail_photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAds_title() {
        return ads_title;
    }

    public void setAds_title(String ads_title) {
        this.ads_title = ads_title;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getFa_created_at() {
        return fa_created_at;
    }

    public void setFa_created_at(String fa_created_at) {
        this.fa_created_at = fa_created_at;
    }

    public String getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(String elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public List<PhotosData> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotosData> photos) {
        this.photos = photos;
    }

    public static List<Job> Import(JSONArray jsonArray) {
        List<Job> jobs = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String user_id = jsonObject.getString("user_id");
                String specialty_id = jsonObject.getString("specialty_id");
                String region_id = jsonObject.getString("region_id");
                String education_level = jsonObject.getString("education_level");
                String agremment_type = jsonObject.getString("agremment_type");
                String description = jsonObject.getString("description");
                String type = jsonObject.getString("type");
                String thumbnail_photo = jsonObject.getString("thumbnail_photo");
                String status = jsonObject.getString("status");
                String valid_since = jsonObject.getString("valid_since");
                String valid_until = jsonObject.getString("valid_until");
                String longitude = jsonObject.getString("longitude");
                String latitude = jsonObject.getString("latitude");
                String address = jsonObject.getString("address");
                String telephone1 = jsonObject.getString("telephone1");
                String telephone2 = jsonObject.getString("telephone2");
                String ads_owner_name = jsonObject.getString("ads_owner_name");
                String ads_title = jsonObject.getString("ads_title");
                String created_at = jsonObject.getString("created_at");
                String updated_at = jsonObject.getString("updated_at");
                String region_name = jsonObject.getString("region_name");
                String city_id = jsonObject.getString("city_id");
                String city_name = jsonObject.getString("city_name");
                String province_id = jsonObject.getString("province_id");
                String province_name = jsonObject.getString("province_name");
                String user_first_name = jsonObject.getString("user_first_name");
                String user_last_name = jsonObject.getString("user_last_name");
                String specialty = jsonObject.getString("specialty");
                String fa_created_at = jsonObject.getString("fa_created_at");
                String elapsed_time = jsonObject.getString("elapsed_time");
                String person_or_company = jsonObject.getString("person_or_company");
                List<PhotosData> photosDatas = PhotosData.Import(jsonObject.getJSONArray("photos"));

                Job job = new Job();
                job.setId(id);
                job.setPerson_or_company(person_or_company);
                job.setUser_id(user_id);
                job.setSpecialty_id(specialty_id);
                job.setRegion_id(region_id);
                job.setEducation_level(education_level);
                job.setAgremment_type(agremment_type);
                job.setDescription(description);
                job.setType(type);
                job.setThumbnail_photo(thumbnail_photo);
                job.setStatus(status);
                job.setValid_since(valid_since);
                job.setValid_until(valid_until);
                job.setLongitude(longitude);
                job.setLatitude(latitude);
                job.setAddress(address);
                job.setTelephone1(telephone1);
                job.setTelephone2(telephone2);
                job.setAds_owner_name(ads_owner_name);
                job.setAds_title(ads_title);
                job.setCreated_at(created_at);
                job.setUpdated_at(updated_at);
                job.setRegion_name(region_name);
                job.setCity_id(city_id);
                job.setCity_name(city_name);
                job.setProvince_id(province_id);
                job.setProvince_name(province_name);
                job.setUser_first_name(user_first_name);
                job.setUser_last_name(user_last_name);
                job.setSpecialty(specialty);
                job.setPhotos(photosDatas);
                job.setFa_created_at(fa_created_at);
                job.setElapsed_time(elapsed_time);
                jobs.add(job);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobs;

    }
}
