package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/16.
 */

public class cylinder_volumes {
    private String id;
    private String value;

    public cylinder_volumes(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static List<cylinder_volumes>Import_list(JSONObject jsonObject, String value){
        List<cylinder_volumes>brands=new ArrayList<>();
        try {
            JSONArray jsonArray=jsonObject.getJSONArray(value);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                cylinder_volumes brand=new cylinder_volumes(jsonObject1.getString("id"),jsonObject1.getString("value"));
                brands.add(brand);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return brands;
    }
}
