package com.ideabonyan.iranapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ideabonyan.iranapp.Interface.SmsListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final int REQUEST_RECEIVE_SMS = 2001;
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null || !Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        SmsMessage[] parts = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (parts == null) return;

        // A long SMS arrives in several parts; join them so the code is never split in two.
        StringBuilder body = new StringBuilder();
        for (SmsMessage part : parts) {
            if (part != null && part.getMessageBody() != null) body.append(part.getMessageBody());
        }

        mListener.messageReceived(body.toString());
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    // Clears the listener only if it is still the caller's, so a screen closing late
    // doesn't disconnect the screen that replaced it.
    public static void unbindListener(SmsListener listener) {
        if (mListener == listener) mListener = null;
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

    // Without RECEIVE_SMS the receiver never fires, so ask again on the screen that waits for the code.
    public static void requestPermissionIfNeeded(Activity activity) {
        if (activity == null) return;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_RECEIVE_SMS);
        }
    }

}
