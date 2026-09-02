package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIM on 8/8/2017.
 */

public class HomeSubCategories {

    String id;
    String name;
    String image;
    String category_id;

    public HomeSubCategories() {
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public HomeSubCategories(String id, String name, String image) {
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

    static int waitTime;

    public static int getWaitTime() {
        return waitTime;
    }

    public static void setWaitTime(int waitTime) {
        HomeSubCategories.waitTime = waitTime;
    }

    static List<HomeSubCategories> categories;

    public static List<HomeSubCategories> Categories(final JSONObject jsonObject) throws JSONException {
        categories = new ArrayList<>();

        waitTime = 0;

        try {
            JSONObject vipAdObject = jsonObject.getJSONObject("vip_ad");
            if (vipAdObject != null) {
                VipAd.vipAdObject(vipAdObject, "mainActivity");
                waitTime = 600;
            }
        } catch (JSONException e) {
        }


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                HomeSubCategories category = new HomeSubCategories();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String category_id = jsonObject1.getString("id");
                String category_name = jsonObject1.getString("name");
                String category_image = jsonObject1.getString("icon");
                if (jsonObject1.has("category_id")) {
                    String categoryParent_id = jsonObject1.getString("category_id");
                    category.setCategory_id(categoryParent_id);
                }


                category.setId(category_id);
                category.setName(category_name);
                category.setImage(category_image);

                categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//            }
//        }, waitTime);
        return categories;
    }
}
