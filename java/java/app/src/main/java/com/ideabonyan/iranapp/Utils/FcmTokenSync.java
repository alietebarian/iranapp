package com.ideabonyan.iranapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.UserData.UserSessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps the server's copy of this device's FCM token current.
 *
 * Until 2.0.3 the token was sent once per install, so a token FCM later replaced never reached
 * the server — and when the app moved to a new Firebase project every device got a new one,
 * leaving the server pushing to tokens that can't be delivered. Now the token is compared with
 * the last one the server accepted on every launch and on every new token; a changed token is
 * sent together with the old one (old_token), so the server moves the device's city, choices
 * and logged-in user over to it. A failed send is retried on the next launch.
 *
 * Kept in its own preferences file so logging out (which clears the "user" file) does not
 * forget what the server has.
 */
public class FcmTokenSync {

    private static final String PREFS = "fcm_token_sync";
    private static final String KEY_SERVER_TOKEN = "server_token";

    /** Call before the new token is stored in UserSessionManager, which still holds the old one. */
    public static void sync(Context context, String token) {
        if (token == null || token.isEmpty() || token.equals("0")) return;

        final Context app = context.getApplicationContext();
        final SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        final UserSessionManager session = new UserSessionManager(app);

        String serverToken = prefs.getString(KEY_SERVER_TOKEN, null);
        if (serverToken == null) {
            // Nothing accepted yet: on the first run of 2.0.3+ after an update the server may
            // know the token an older version stored. If that is this same token, it may never
            // have been sent at all, so it is sent rather than assumed known.
            String legacy = session.getNotigy_code();
            if (!legacy.equals("0") && !legacy.equals(token)) {
                serverToken = legacy;
                prefs.edit().putString(KEY_SERVER_TOKEN, legacy).commit();
            }
        }
        if (token.equals(serverToken)) return;

        final Map<String, String> params = new HashMap<>();
        params.put("token", token);
        if (serverToken != null) params.put("old_token", serverToken);

        StringRequest request = new StringRequest(Request.Method.POST, StaticData.NOTIFICATIONS_IF_NOT_LOGGED_IN,
                response -> {
                    try {
                        if ("204".equals(new JSONObject(response).optString("status"))) {
                            prefs.edit().putString(KEY_SERVER_TOKEN, token).apply();
                            session.setNotificationInfo("2");
                        }
                    } catch (Exception ignored) {
                        // Retried on the next launch.
                    }
                },
                error -> {
                    // No internet or server error: retried on the next launch.
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        VolleySingleton.GetInstance(app).AddToRequestQueue(request);
    }
}
