package com.ravenshade.iconpack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoWallpaperReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (AutoWallpaperManager.ACTION_TRIGGER.equals(action)) {
            if (AutoWallpaperManager.isAutoEnabled(context)) {
                AutoWallpaperManager.changeWallpaper(context, true, 0, null);
                // Schedule the next cycle
                AutoWallpaperManager.scheduleNext(context);
            }
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            if (AutoWallpaperManager.isAutoEnabled(context)) {
                AutoWallpaperManager.scheduleNext(context);
            }
        }
    }
}
