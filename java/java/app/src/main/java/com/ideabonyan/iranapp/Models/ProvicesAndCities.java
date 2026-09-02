package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProvicesAndCities {

    String id;
    String name;

    public ProvicesAndCities() {
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


    public static List<ProvicesAndCities> cities(JSONObject jsonObject) {
        List<ProvicesAndCities> cities = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String city_id = jsonObject1.getString("id");
                String city_name = jsonObject1.getString("name");


                ProvicesAndCities city = new ProvicesAndCities();
                city.setId(city_id);
                city.setName(city_name);

                cities.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cities;
    }





//    static List<ProvicesAndCities> provinces;

//    public static List<ProvicesAndCities> getProvince(String provinceId, Context context) {
//        String url = StaticData.PROVINCE;
//
//        if (provinceId != null) {
//            url = StaticData.DOMAIN_WITH_API + "/provinces/" + provinceId + "/cities";
//        }
//        Log.v("CLIPS_JSON_URL", url);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    provinces = ProvicesAndCities.cities(jsonObject);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        VolleySingleton.GetInstance(context).AddToRequestQueue(stringRequest);
//        return provinces;
//
//    }



//    public static List<ProvicesAndCities> getProvince1(String provinceId, Context context) {
//
//
//
//
//        String url = StaticData.PROVINCE;
//
//        if (provinceId != null) {
//            url = StaticData.DOMAIN_WITH_API + "/provinces/" + provinceId + "/cities";
//        }
//
//
//
//        Log.i("11111111111111", url);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET,
//                url,
//                new Response.Listener<String>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onResponse(String response) {
//
//
//                Log.i("11111111111111", "222222222222222222");
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    provinces = ProvicesAndCities.cities(jsonObject);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("11111111111111", error.toString());
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
////        VolleySingleton.GetInstance(context).AddToRequestQueue(stringRequest);
//
//        MySingleton mySingleton = new MySingleton(context);
//        mySingleton.getInstance(context).addToRequestQueue(stringRequest);
//
//        Log.i(TAG, "getProvince: " + provinces);
//
//        return provinces;
//    }
}
