package com.ideabonyan.iranapp.Models;

import com.ideabonyan.iranapp.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIM on 8/8/2017.
 */

public class HomeCategories {

    String id;
    String name;
    String image;

    public HomeCategories() {
    }

    public HomeCategories(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public static List<HomeCategories> Categories(JSONObject jsonObject) {
        List<HomeCategories> categories = new ArrayList<>();

        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = dataObject.getJSONArray("categories");



            // VipAd class takes json objects, and shows the dialog by itself.
            if (!MainActivity.isSplashAdShownYet) {
                try {
                    JSONObject vipAdObject = dataObject.getJSONObject("vip_ad");
                    if (vipAdObject != null) {
                        VipAd.vipAdObject(vipAdObject, "mainActivity");
                        MainActivity.isSplashAdShownYet = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String category_id = jsonObject1.getString("id");
                String category_name = jsonObject1.getString("name");
                String category_image = jsonObject1.getString("icon");


                HomeCategories category = new HomeCategories();
                category.setId(category_id);
                category.setName(category_name);
                category.setImage(category_image);

                categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
