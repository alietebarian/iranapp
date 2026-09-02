package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdPlans {

    String id;
    String num_of_stars;
    String max_number_of_photos;
    String price;
    String plan_title;
    String num_of_updates;
    String interval_days;

    public static List<AdPlans> Import(JSONObject jsonObject){
        List<AdPlans> adPlanses = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++){
                AdPlans adPlan = new AdPlans();
                JSONObject json = jsonArray.getJSONObject(i);

                adPlan.id = json.getString("id");
                adPlan.num_of_stars = json.getString("num_of_stars");
                adPlan.max_number_of_photos = json.getString("max_number_of_photos");
                adPlan.price = json.getString("price");
                adPlan.plan_title = json.getString("plan_title");
                adPlan.num_of_updates = json.getString("num_of_updates");
                adPlan.interval_days = json.getString("interval_days");

                adPlanses.add(adPlan);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return adPlanses;
    }

    public String getId() {
        return id;
    }

    public String getNum_of_stars() {
        return num_of_stars;
    }

    public String getMax_number_of_photos() {
        return max_number_of_photos;
    }

    public String getPrice() {
        return price;
    }

    public String getPlan_title() {
        return plan_title;
    }

    public String getNum_of_updates() {
        return num_of_updates;
    }

    public String getInterval_days() {
        return interval_days;
    }
}
