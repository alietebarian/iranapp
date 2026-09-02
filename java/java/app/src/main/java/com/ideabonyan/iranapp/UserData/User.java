package com.ideabonyan.iranapp.UserData;

import android.content.Context;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;


public class User {
    String user_id;
    String firstname;
    String lastname;
    String Phone;
    String isVerrified;
    String send_news_notifications;
    String send_ads_notifications;

    public boolean isLoggedIn(){
        if(user_id==null) return false;
        else return true;
    }

    public boolean isVerrified(){

        if(isVerrified.equals("1")) {
            Log.v("isvarrtfy","1");

            return true;
        } else {
            Log.v("isvarrtfy","0");

            return false;
        }
    }

    public String getIsVerrified() {
        return isVerrified;
    }

    public void setIsVerrified(String isVerrified) {
        this.isVerrified = isVerrified;
    }

    public String getuser_id() {
        return user_id;
    }

    public void setuser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }



    public User() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSend_news_notifications() {
        return send_news_notifications;
    }

    public void setSend_news_notifications(String send_news_notifications) {
        this.send_news_notifications = send_news_notifications;
    }

    public String getSend_ads_notifications() {
        return send_ads_notifications;
    }

    public void setSend_ads_notifications(String send_ads_notifications) {
        this.send_ads_notifications = send_ads_notifications;
    }

    public User(String user_id, String firstname, String lastname, String phone, String is_verrified, String send_news_notifications, String send_ads_notifications) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isVerrified=is_verrified;
        Phone = phone;
        this.send_news_notifications = send_news_notifications;
        this.send_ads_notifications = send_ads_notifications;

    }

    public static User UserLogin(JSONObject jsonObject, Context context){

        User user = new User();
        try {
            String id=jsonObject.getString("id");
            String first_name=jsonObject.getString("first_name");
            String last_name=jsonObject.getString("last_name");
            String mobile=jsonObject.getString("mobile");
            String confirmed=jsonObject.getString("confirmed");

//            Log.i("111111111111",confirmed);

            user.setuser_id(id);
            user.setFirstname(first_name);
            user.setLastname(last_name);
            user.setPhone(mobile);
            user.setIsVerrified(confirmed);

            UserSessionManager userSessionManager = new UserSessionManager(context);
            userSessionManager.setIs_varryfy(confirmed);
            UserHelper.SaveUserInfo(user, context);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
