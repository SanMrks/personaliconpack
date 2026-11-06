package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.RemoteViews;

public class QuickDockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent != null ? intent.getAction() : null;
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, QuickDockWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_quick_dock", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Dynamic Wallpaper-Adaptive Frosted Liquid Glass Capsule Surface
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 540, 140, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        String[] slotIdNames = {
                "widget_dock_phone",
                "widget_dock_whatsapp",
                "widget_dock_browser",
                "widget_dock_music",
                "widget_dock_camera"
        };

        for (int slot = 0; slot < 5; slot++) {
            int viewId = context.getResources().getIdentifier(slotIdNames[slot], "id", pkg);
            if (viewId == 0) continue;

            String appPkg = WidgetSettingsManager.getDockSlotPackage(context, slot);
            PendingIntent pi = null;
            Bitmap customIcon = null;

            if (appPkg != null && !appPkg.isEmpty()) {
                PackageManager pm = context.getPackageManager();
                try {
                    Intent launchIntent = pm.getLaunchIntentForPackage(appPkg);
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        pi = PendingIntent.getActivity(context, 200 + slot, launchIntent, flags);
                    }
                    Drawable d = pm.getApplicationIcon(appPkg);
                    if (d != null) {
                        int sz = (int) (48 * context.getResources().getDisplayMetrics().density);
                        customIcon = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(customIcon);
                        d.setBounds(0, 0, sz, sz);
                        d.draw(c);
                    }
                } catch (Throwable ignored) {}
            }

            if (customIcon != null) {
                views.setImageViewBitmap(viewId, customIcon);
            }

            if (pi != null) {
                views.setOnClickPendingIntent(viewId, pi);
            } else {
                setupDefaultSlotAction(context, views, slot, viewId, flags);
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setupDefaultSlotAction(Context context, RemoteViews views, int slot, int viewId, int flags) {
        PackageManager pm = context.getPackageManager();
        Intent intent = null;
        if (slot == 0) {
            intent = new Intent(Intent.ACTION_DIAL);
        } else if (slot == 1) {
            intent = pm.getLaunchIntentForPackage("com.whatsapp");
            if (intent == null) intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING);
        } else if (slot == 2) {
            intent = pm.getLaunchIntentForPackage("com.vivaldi.browser");
            if (intent == null) intent = pm.getLaunchIntentForPackage("com.android.chrome");
            if (intent == null) intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        } else if (slot == 3) {
            intent = pm.getLaunchIntentForPackage("com.convx.music");
            if (intent == null) intent = pm.getLaunchIntentForPackage("com.spotify.music");
            if (intent == null) intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC);
        } else if (slot == 4) {
            intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            if (intent.resolveActivity(pm) == null) intent = pm.getLaunchIntentForPackage("com.nothing.camera");
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(context, 250 + slot, intent, flags);
            views.setOnClickPendingIntent(viewId, pi);
        }
    }
}
