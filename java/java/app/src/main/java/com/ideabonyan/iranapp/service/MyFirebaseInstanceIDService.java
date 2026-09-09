package com.ideabonyan.iranapp.service;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Config;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements Get_Insert_Edit_Data {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }
    UserSessionManager userSessionManager;
    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        Log.v("finalregId",token);
        userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setNotigy_code(token);
        Log.v("shod","ahos");


        //new SignUpActivity().saveFirebaseToken(token);
//        inserttoken(token);


        editor.commit();

        checkForNotificationSettings(token);
    }


    private void checkForNotificationSettings(String token) {
        User user = UserHelper.LoadUserInfo(getApplicationContext());
//        if (!user.isLoggedIn() || !user.isVerrified()){
        if (!userSessionManager.getNotificationInfo().equals("2")){

//                int i = 0;
//                while (i < 5) {

//                    Log.i("111111111111", token);
            if (!token.equals("0")) {
                String url = StaticData.NOTIFICATIONS_IF_NOT_LOGGED_IN + "?token=" + token;
                Map<String, String> params = new HashMap<String, String>();
                Get_Volley_Call_Back.binddata(this);
                Get_Volley_Call_Back.Call_Volley(getApplicationContext(), params, url, Request.Method.POST, 1);
//                        break;
//                    }
//                    i++;
                userSessionManager.setNotificationStatus("1", "1");

            }
        }
//        }
    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("204")) {
                userSessionManager.setNotificationInfo("2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


//    public void inserttoken(String token) {
//        String CLIPS_JSON_URL = ConstValue.inserttoken + token;
//
//        Log.v("CLIPS_JSON_URLtoken", CLIPS_JSON_URL);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, CLIPS_JSON_URL, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Log.v("CLIPS_JSON_URL", jsonObject.toString());
//                String status = null;
//                try {
//                    status = jsonObject.getString("status");
//                    Log.v("statos",status);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//            }
//        });
//        VolleySingleton.GetInstance(getApplicationContext()).AddToRequestQueue(jsonObjectRequest);
//
//    }
}