package com.ideabonyan.iranapp.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the activation code through Google's SMS User Consent API: Play services shows the user
 * the one incoming SMS and asks "allow?", so the app never holds RECEIVE_SMS / READ_SMS.
 * Holding those permissions is what made Play Protect flag the APK as an SMS-stealing app.
 *
 * Usage: {@link #start} when the code is about to be sent, pass the consent intent from the
 * listener to startActivityForResult(..., {@link #REQUEST_SMS_CONSENT}), read the text back with
 * {@link #messageFrom} in onActivityResult, and {@link #stop} when the screen closes.
 */
public class SmsReceiver extends BroadcastReceiver {
    public static final int REQUEST_SMS_CONSENT = 2001;

    public interface ConsentListener {
        void onConsentIntent(Intent consentIntent);
    }

    private final ConsentListener listener;

    private SmsReceiver(ConsentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) return;

        Bundle extras = intent.getExtras();
        if (extras == null) return;
        Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
        if (status == null || status.getStatusCode() != CommonStatusCodes.SUCCESS) return;

        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
        if (!isPlayServicesConsentIntent(context, consentIntent)) return;

        // Never let the received intent grant access to our own content URIs.
        consentIntent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        listener.onConsentIntent(consentIntent);
    }

    // Guards against intent redirection: only launch the consent screen if it belongs to Play services.
    private static boolean isPlayServicesConsentIntent(Context context, Intent consentIntent) {
        if (consentIntent == null) return false;
        ComponentName target = consentIntent.resolveActivity(context.getPackageManager());
        return target != null && "com.google.android.gms".equals(target.getPackageName());
    }

    /**
     * Starts listening for the next SMS (Play services waits up to 5 minutes).
     * Returns null on devices without Play services — the user then types the code by hand.
     */
    public static SmsReceiver start(Context context, ConsentListener listener) {
        if (context == null) return null;
        try {
            SmsRetriever.getClient(context).startSmsUserConsent(null);
            SmsReceiver receiver = new SmsReceiver(listener);
            ContextCompat.registerReceiver(context, receiver,
                    new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                    SmsRetriever.SEND_PERMISSION, null, ContextCompat.RECEIVER_EXPORTED);
            return receiver;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void stop(Context context, SmsReceiver receiver) {
        if (context == null || receiver == null) return;
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
            // Already unregistered.
        }
    }

    // The SMS text the user agreed to share, or null if they declined.
    public static String messageFrom(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return null;
        return data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
    }

    /**
     * Returns the first standalone number of exactly {@code length} digits in the SMS, or null.
     * Persian/Arabic digits are converted first, and numbers of any other length are ignored.
     */
    public static String extractCode(String message, int length) {
        if (message == null) return null;

        StringBuilder latinDigits = new StringBuilder(message.length());
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            latinDigits.append(Character.isDigit(c) ? (char) ('0' + Character.digit(c, 10)) : c);
        }

        Matcher matcher = Pattern.compile("(?<![0-9])[0-9]{" + length + "}(?![0-9])").matcher(latinDigits);
        return matcher.find() ? matcher.group() : null;
    }

}
