package com.ideabonyan.iranapp.Utils;

import com.ideabonyan.iranapp.BuildConfig;

/**
 * Starts the User-Agent of every request with "IranApp/<versionCode>", so the server can tell this
 * build from versions before 202, which it refuses once their update is mandatory
 * (BlockOutdatedApps on the server).
 *
 * Volley goes through HttpURLConnection, which reads the http.agent property on every request, so
 * {@link #install()} in IranApplication covers all of it; OkHttp requests set the header themselves
 * from {@link #value()}.
 */
public final class AppUserAgent {

    private static String value;

    private AppUserAgent() {
    }

    public static synchronized String value() {
        if (value == null) {
            String system = System.getProperty("http.agent");
            value = "IranApp/" + BuildConfig.VERSION_CODE
                    + (system == null || system.isEmpty() ? "" : " " + system);
        }
        return value;
    }

    public static void install() {
        System.setProperty("http.agent", value());
    }
}
