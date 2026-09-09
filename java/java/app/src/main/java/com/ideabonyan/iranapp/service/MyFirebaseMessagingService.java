package com.ideabonyan.iranapp.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.NewNotificationCame;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Config;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.NotificationUtils;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService implements Get_Insert_Edit_Data {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private UserSessionManager userSessionManager;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        storeRegIdInPref(token);

        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setNotigy_code(token);
        editor.apply();

        checkForNotificationSettings(token);
    }

    private void checkForNotificationSettings(String token) {
        if (!userSessionManager.getNotificationInfo().equals("2")) {
            if (!token.equals("0")) {
                String url = StaticData.NOTIFICATIONS_IF_NOT_LOGGED_IN + "?token=" + token;
                Map<String, String> params = new HashMap<>();
                Get_Volley_Call_Back.binddata(this);
                Get_Volley_Call_Back.Call_Volley(getApplicationContext(), params, url, Request.Method.POST, 1);
                userSessionManager.setNotificationStatus("1", "1");
            }
        }
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

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Log.e(TAG, "its hear: " + remoteMessage.getNotification().getBody());

                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", remoteMessage.getNotification().getBody());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                String state="0";
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
//                String store_id="";
//                try {
//                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                     state=json.getString("state");
//                    store_id=json.getString("store_id");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(this, "get notify", Toast.LENGTH_SHORT).show();
                newNotificationCame.onNewFirebaseNotification(remoteMessage.getNotification().getBody(),
                        remoteMessage.getData().get("status"), remoteMessage.getData().get("content_id"));
//                Intent dialogIntent = new Intent(this, Show_Notification_Message_Dialog.class);
//                dialogIntent.putExtra("message",remoteMessage.getNotification().getBody());
//                dialogIntent.putExtra("state",Integer.parseInt(state));
//                dialogIntent.putExtra("store_id",store_id);
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(dialogIntent);
                
            }else{
                // If the app is in background, firebase itself handles the notification
            }
        }

        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                handleDataMessage(json);
//                Log.e(TAG, "start: " + json.toString());
//
//
//            } catch (Exception e) {
//                Log.e(TAG, "Exception: " + e.getMessage());
//            }
//        }
    }

//    private void handleNotification(String message) {
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
//        }else{
//            // If the app is in background, firebase itself handles the notification
//        }
//    }

//    private void handleDataMessage(JSONObject json) {
//        Log.e(TAG, "push json: " + json.toString());
//
//        try {
//            Log.v("start","itshear");
////            JSONObject data = json.getJSONObject("data");
//
////            String title = data.getString("title");
////            String message = data.getString("message");
////            boolean isBackground = data.getBoolean("is_background");
////            String imageUrl = data.getString("image");
////            String timestamp = data.getString("timestamp");
//
//            String title = json.getString("data");
//            String message = json.getString("data");
////            boolean isBackground = data.getBoolean("is_background");
////            String imageUrl = data.getString("image");
////            String timestamp = data.getString("timestamp");
////            JSONObject payload = data.getJSONObject("payload");
////
////            Log.e("gcm", "title: " + title);
////            Log.e("gcm", "message: " + message);
////            Log.e("gcm", "isBackground: " + isBackground);
////            Log.e("gcm", "payload: " + payload.toString());
////            Log.e("gcm", "imageUrl: " + imageUrl);
////            Log.e("gcm", "timestamp: " + timestamp);
//
//
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {
//                // app is in background, show the notification in notification tray
//                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
////                if (TextUtils.isEmpty(imageUrl)) {
//                Long tsLong = System.currentTimeMillis()/1000;
//                String ts = tsLong.toString();
//                    showNotificationMessage(getApplicationContext(), title, message, ts, resultIntent);
////                } else {
////                    // image is present, show notification with image
////                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
////                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    static NewNotificationCame newNotificationCame;
    public static void bindData(NewNotificationCame nnewNotificationCame){
        newNotificationCame = nnewNotificationCame;
    }
}