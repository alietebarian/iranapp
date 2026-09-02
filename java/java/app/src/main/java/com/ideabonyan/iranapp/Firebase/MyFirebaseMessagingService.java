//package com.ideabonyan.iranapp.Firebase;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.ideabonyan.iranapp.Interface.NewNotificationCame;
//
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
//
//    private NotificationUtils notificationUtils;
//
//    @Override
//    public void onMessageReceived(final RemoteMessage remoteMessage) {
//        Log.e(TAG, "From: " + remoteMessage.getFrom());
//
//        if (remoteMessage == null)
//            return;
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
////            handleNotification(remoteMessage.getNotification().getBody());
//
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Log.e(TAG, "its hear: " + remoteMessage.getNotification().getBody());
//
////                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
////                pushNotification.putExtra("message", remoteMessage.getNotification().getBody());
////                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
////                String state="0";
////                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
////                notificationUtils.playNotificationSound();
////                try {
////                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
////                     state=json.getString("state");
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
////                Show_Notification_Message_Dialog.message=remoteMessage.getNotification().getBody();
////                Show_Notification_Message_Dialog.state=Integer.parseInt(state);
////                Intent dialogIntent = new Intent(this, Show_Notification_Message_Dialog.class);
////                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(dialogIntent);
//
//                if (remoteMessage.getData().size() > 0) {
//                    Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//
//
//                    newNotificationCame.onNewFirebaseNotification(remoteMessage.getNotification().getBody(),
//                            remoteMessage.getData().get("status"), remoteMessage.getData().get("content_id"));
////            try {
////                JSONObject json = new JSONObject(remoteMessage.getData().toString());
////                handleDataMessage(json);
////                Log.e(TAG, "start: " + json.toString());
////
////
////            } catch (Exception e) {
////                Log.e(TAG, "Exception: " + e.getMessage());
////            }
//                }
//
//            }
//        }
//
////         Check if message contains a data payload.
//
//    }
//
////    private void handleNotification(String message) {
////        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
////            // app is in foreground, broadcast the push message
////            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
////            pushNotification.putExtra("message", message);
////            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
////
////            // play notification sound
////            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
////            notificationUtils.playNotificationSound();
////        }else{
////            // If the app is in background, firebase itself handles the notification
////        }
////    }
//
////    private void handleDataMessage(JSONObject json) {
////        Log.e(TAG, "push json: " + json.toString());
////
////        try {
////            Log.v("start","itshear");
//////            JSONObject data = json.getJSONObject("data");
////
//////            String title = data.getString("title");
//////            String message = data.getString("message");
//////            boolean isBackground = data.getBoolean("is_background");
//////            String imageUrl = data.getString("image");
//////            String timestamp = data.getString("timestamp");
////
////            String title = json.getString("data");
////            String message = json.getString("data");
//////            boolean isBackground = data.getBoolean("is_background");
//////            String imageUrl = data.getString("image");
//////            String timestamp = data.getString("timestamp");
//////            JSONObject payload = data.getJSONObject("payload");
//////
//////            Log.e("gcm", "title: " + title);
//////            Log.e("gcm", "message: " + message);
//////            Log.e("gcm", "isBackground: " + isBackground);
//////            Log.e("gcm", "payload: " + payload.toString());
//////            Log.e("gcm", "imageUrl: " + imageUrl);
//////            Log.e("gcm", "timestamp: " + timestamp);
////
////
////            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
////                // app is in foreground, broadcast the push message
////                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
////                pushNotification.putExtra("message", message);
////                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
////
////                // play notification sound
////                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
////                notificationUtils.playNotificationSound();
////            } else {
////                // app is in background, show the notification in notification tray
////                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
////                resultIntent.putExtra("message", message);
////
////                // check for image attachment
//////                if (TextUtils.isEmpty(imageUrl)) {
////                Long tsLong = System.currentTimeMillis()/1000;
////                String ts = tsLong.toString();
////                    showNotificationMessage(getApplicationContext(), title, message, ts, resultIntent);
//////                } else {
//////                    // image is present, show notification with image
//////                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//////                }
////            }
////        } catch (JSONException e) {
////            Log.e(TAG, "Json Exception: " + e.getMessage());
////        } catch (Exception e) {
////            Log.e(TAG, "Exception: " + e.getMessage());
////        }
////    }
//
//    /**
//     * Showing notification with text only
//     */
//    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }
//
//
//
//    static NewNotificationCame newNotificationCame;
//    public static void bindData(NewNotificationCame nnewNotificationCame){
//        newNotificationCame = nnewNotificationCame;
//    }
//}