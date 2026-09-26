package com.ideabonyan.iranapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.BuildConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Counts this installation for the install statistics (App\Support\InstallStats on the server).
 *
 * On first launch a random install ID is generated and kept; it is sent once, together with the
 * store the APK came from, and again whenever the app version changes. A failed send (no
 * internet) is simply retried on the next launch. Unlike the old count, which relied on Firebase
 * handing out a token, this works on devices without Google services and when Google is blocked.
 *
 * Kept in its own preferences file so logging out (which clears the "user" file) does not
 * make the same device count as a new install.
 */
public class InstallTracker {

    private static final String PREFS = "install_tracker";
    private static final String KEY_INSTALL_ID = "install_id";
    private static final String KEY_IS_UPGRADE = "is_upgrade";
    private static final String KEY_REPORTED_VERSION = "reported_version";

    public static void report(Context context) {
        final Context app = context.getApplicationContext();
        final SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String installId = prefs.getString(KEY_INSTALL_ID, null);
        if (installId == null) {
            installId = UUID.randomUUID().toString();
            // Decided once, on the first launch of a version that has this class: if an older
            // version was installed earlier and then updated, the device was already counted
            // the old way and must not be counted again.
            prefs.edit()
                    .putString(KEY_INSTALL_ID, installId)
                    .putBoolean(KEY_IS_UPGRADE, wasUpdatedFromOlderVersion(app))
                    .apply();
        }

        final String version = BuildConfig.VERSION_NAME;
        if (version.equals(prefs.getString(KEY_REPORTED_VERSION, null))) return;

        final Map<String, String> params = new HashMap<>();
        params.put("install_id", installId);
        params.put("installer_package", installerPackage(app));
        params.put("is_upgrade", prefs.getBoolean(KEY_IS_UPGRADE, false) ? "1" : "0");
        params.put("app_version", version);
        params.put("android_version", Build.VERSION.RELEASE);
        params.put("device_model", (Build.MANUFACTURER + " " + Build.MODEL).trim());

        StringRequest request = new StringRequest(Request.Method.POST, StaticData.INSTALL_STATS,
                response -> {
                    try {
                        String status = new JSONObject(response).optString("status");
                        if ("200".equals(status) || "201".equals(status)) {
                            prefs.edit().putString(KEY_REPORTED_VERSION, version).apply();
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

    /** The app that installed this APK, or "" when it was installed from a file. */
    private static String installerPackage(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            String installer;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                installer = pm.getInstallSourceInfo(context.getPackageName()).getInstallingPackageName();
            } else {
                installer = pm.getInstallerPackageName(context.getPackageName());
            }
            return installer == null ? "" : installer;
        } catch (Exception e) {
            return "";
        }
    }

    /** True when this APK replaced an earlier installation of the app. */
    private static boolean wasUpdatedFromOlderVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.lastUpdateTime > info.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
