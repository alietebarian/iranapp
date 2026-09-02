package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliReza on 2017/10/21.
 */

public class Specialities {
    private String id;
    private String name;

    public Specialities(String id, String name) {
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

    public static List<Specialities>Import(JSONArray jsonArray){
        List<Specialities> specialities =new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                Specialities specialities1=new Specialities(id,name);
                specialities.add(specialities1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return specialities;
    }
}
