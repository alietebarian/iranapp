package com.ideabonyan.iranapp.Models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotosData implements Serializable {

    String id;
//    String ads_id;
    String name;
    String created_at;
    String updated_at;

    static public List<PhotosData> Import(JSONArray jsonArray) throws JSONException {
        List<PhotosData> photos = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++){
            PhotosData photo = new PhotosData();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            photo.id = jsonObject.getString("id");
//            photo.ads_id = jsonObject.getString("ads_id");
            photo.name = jsonObject.getString("file_name");
            photo.created_at = jsonObject.getString("created_at");
            photo.updated_at = jsonObject.getString("updated_at");

            photos.add(photo);
        }

        return photos;
    }

    public String getId() {
        return id;
    }

//    public String getAds_id() {
//        return ads_id;
//    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
