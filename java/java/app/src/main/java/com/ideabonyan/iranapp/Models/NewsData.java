package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NewsData {

    String id;
    String title;
    String passage;
    String created_at;
    String updated_at;
    List<PhotosData> photos;

    static public List<NewsData> Import(JSONObject jsonObject) throws JSONException {
        List<NewsData> news = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++){
            NewsData newsData = new NewsData();
            JSONObject json = jsonArray.getJSONObject(i);
            newsData.id = json.getString("id");
            newsData.title = json.getString("title");
            newsData.passage = json.getString("passage");
            newsData.created_at = json.getString("created_at");
            newsData.updated_at = json.getString("updated_at");

            newsData.photos = PhotosData.Import(json.getJSONArray("photos"));

            news.add(newsData);
        }

        return news;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPassage() {
        return passage;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
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

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setPhotos(List<PhotosData> photos) {
        this.photos = photos;
    }
}
