package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/16.
 */

public class Modell {
    private String id;
    private String name;

    public Modell(String id, String name) {
        this.id = id;
        this.name = name;
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

    public static List<Modell>Import_list(JSONObject jsonObject, String value){
        List<Modell>brands=new ArrayList<>();
        try {
            JSONArray jsonArray=jsonObject.getJSONArray(value);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                Modell brand=new Modell(jsonObject1.getString("id"),jsonObject1.getString("name"));
                brands.add(brand);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return brands;
    }
}
