package com.ideabonyan.iranapp.UserData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class UserSessionManager {

    public static final int PRIVATE_MODE = 0;
    public static final String SHARED_PREF_NAME = "user";

    public String customer_id = "customer_id";
    public String firstname = "firstname";
    public String lastname = "lastname";
    public String Phone = "Phone";
    public String is_varryfy = "is_varryfy";
    public String send_news_notifications = "send_news_notifications";
    public String send_ads_notifications = "send_ads_notifications";
    public String notigy_code = "notigy_code";
    public String app_version = "app_version";
    public String fistSee = "fistSee";
    public Context context;
    SharedPreferences sharePre;
    Editor editor;
    private String tempPassword = "tempPassword";
    private String loginToken = "loginToken";
    private String provinceInfo = "provinceInfo";
    private String cityInfo = "cityInfo";
    private String provinceName = "provinceName";
    private String cityName = "cityName";
    private String notificationInfo = "notificationInfo";
    private String dont_show_new_version = "dont_show_new_version";
    private String showIntroPage = "showIntroPage";

    public UserSessionManager() {
        // TOD Auto-generated constructor stub
    }

    public UserSessionManager(Context context) {
        this.context = context;
        sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        editor = sharePre.edit();
    }

    public void setUser(String customer_id, String firstname, String lastname, String phone, String is_varryfy) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.customer_id, customer_id);
        editor.putString(this.firstname, firstname);
        editor.putString(this.lastname, lastname);
        editor.putString(this.Phone, phone);
        editor.putString(this.is_varryfy, is_varryfy);
        editor.putString(this.send_news_notifications, send_news_notifications);
        editor.putString(this.send_ads_notifications, send_ads_notifications);
        editor.commit();
    }


    public User getUser() {
        User customer = new User();
        if (sharePre.contains(customer_id)) {
            customer.setuser_id(sharePre.getString(customer_id, null));
            customer.setFirstname(sharePre.getString(firstname, null));
            customer.setLastname(sharePre.getString(lastname, null));
            customer.setPhone(sharePre.getString(Phone, null));
            customer.setIsVerrified(sharePre.getString(is_varryfy, null));
            customer.setSend_news_notifications(sharePre.getString(send_news_notifications, null));
            customer.setSend_ads_notifications(sharePre.getString(send_ads_notifications, null));
//            Log.v("isVerrified",sharePre.getString(isVerrified, null));


            return customer;
        }
        return customer;
    }

    public List<String> getNotificationStatus() {
        List<String> data = new ArrayList<>();
//        if (sharePre.contains(customer_id)){
        data.add(sharePre.getString(send_news_notifications, null));
        data.add(sharePre.getString(send_ads_notifications, null));
//        }
        return data;
    }

    public void setNotificationStatus(String news, String ads) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.send_news_notifications, news);
        editor.putString(this.send_ads_notifications, ads);
        editor.commit();
    }


    public String getapp_version() {
        User customer = new User();
        if (sharePre.contains(app_version)) {
            return sharePre.getString(app_version, "2.0");
        }
        return "2.0";
    }

    public void setapp_version(String app_version) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.app_version, app_version);
        editor.commit();
    }

    public String getIs_varryfy() {
        if (sharePre.contains(is_varryfy)) {
            return sharePre.getString(is_varryfy, "0");
        }
        return "0";
    }

    public void setIs_varryfy(String is_varryfy) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.is_varryfy, is_varryfy);
        Log.v("setIs_varryfy", is_varryfy);
        editor.commit();
    }


    ////////////////////////////////////////
    ////    FIREBASE TOKEN
    ////////////////////////////////////////
    public String getNotigy_code() {
        return sharePre.getString(notigy_code, "0");
    }

    public void setNotigy_code(String notigy_code) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.notigy_code, notigy_code);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    PASSWORD
    ////////////////////////////////////////
    public String getPassword() {
        return sharePre.getString(tempPassword, "0");
    }

    public void setPassword(String password) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.tempPassword, password);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    LOGIN TOKEN
    ////////////////////////////////////////
    public String getLoginToken() {
        return sharePre.getString(loginToken, "0");
    }

    public void setLoginToken(String token) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.loginToken, token);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    PROVINCE NAME
    ////////////////////////////////////////
    public String getProvinceName() {
        return sharePre.getString(provinceName, "0");
    }

    public void setProvinceName(String provinceName) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.provinceName, provinceName);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    CITY NAME
    ////////////////////////////////////////
    public String getCityName() {
        return sharePre.getString(cityName, "0");
    }

    public void setCityName(String cityName) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.cityName, cityName);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    PROVINCE INFO
    ////////////////////////////////////////
    public String getProvinceInfo() {
        return sharePre.getString(provinceInfo, "0");
    }

    public void setProvinceInfo(String provinceInfo) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.provinceInfo, provinceInfo);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    CITY INFO
    ////////////////////////////////////////
    public String getCityInfo() {
        return sharePre.getString(cityInfo, "0");
    }

    public void setCityInfo(String cityInfo) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.cityInfo, cityInfo);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    NOTIFICATION INFO               => "1" is for unconfigured, "2" is for configured
    ////////////////////////////////////////


    /// ////////////////////////////////////////
    ////    showIntroPage
    ////////////////////////////////////////
    public int getShowIntroPage() {
        return sharePre.getInt(showIntroPage, 0);
    }

    public void setShowIntroPage(int showIntroPage) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putInt(this.showIntroPage, showIntroPage);
        editor.apply();
    }


    ////////////////////////////////////////
    ////    NOTIFICATION INFO               => "1" is for unconfigured, "2" is for configured
    ////////////////////////////////////////
    public String getNotificationInfo() {
        return sharePre.getString(notificationInfo, "0");
    }

    public void setNotificationInfo(String notificationInfo) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.notificationInfo, notificationInfo);
        editor.apply();
    }


    public String getDont_show_new_version() {
        return sharePre.getString(dont_show_new_version, "0");
    }

    public void setDont_show_new_version(String dont_show_new_version) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.dont_show_new_version, dont_show_new_version);
        editor.apply();
    }

    public String getFistSee() {
        return sharePre.getString(fistSee, "0");
    }

    public void setFistSee(String fistSee) {
        SharedPreferences sharePre = context.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        Editor editor = sharePre.edit();
        editor.putString(this.fistSee, fistSee);
        editor.apply();
    }

}

