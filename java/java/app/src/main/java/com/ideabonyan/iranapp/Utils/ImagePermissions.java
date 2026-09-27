package com.ideabonyan.iranapp.Utils;

import android.Manifest;
import android.os.Build;

/**
 * The runtime permission for picking ad photos from the gallery. Android 13+ never grants
 * READ_EXTERNAL_STORAGE, so asking for it there left the new-ad / edit-ad screens stuck.
 */
public class ImagePermissions {
    public static final String READ_IMAGES = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;
}
