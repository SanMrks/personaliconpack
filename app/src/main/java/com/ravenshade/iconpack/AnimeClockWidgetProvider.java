package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnimeClockWidgetProvider extends AppWidgetProvider {

    private static final int ACCENT_COLOR = 0xFFFF2E4D;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent != null ? intent.getAction() : null;
        if (Intent.ACTION_BATTERY_CHANGED.equals(action) ||
            Intent.ACTION_TIME_TICK.equals(action) ||
            Intent.ACTION_TIME_CHANGED.equals(action) ||
            AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, AnimeClockWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_anime_clock", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Dynamic Wallpaper-Adaptive Frosted Liquid Glass Surface
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 300, 300, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        // 1. User Editable Settings
        String customTitle = WidgetSettingsManager.getAnimeTitle(context);
        boolean showBattery = WidgetSettingsManager.getShowBattery(context);

        int idBrand = context.getResources().getIdentifier("widget_mini_brand", "id", pkg);
        if (idBrand != 0) {
            views.setTextViewText(idBrand, customTitle);
            views.setTextColor(idBrand, ACCENT_COLOR);
        }

        // 2. Time & Date
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE | MMM d", Locale.getDefault());

        int idTime = context.getResources().getIdentifier("widget_mini_time", "id", pkg);
        int idDate = context.getResources().getIdentifier("widget_mini_date", "id", pkg);
        if (idTime != 0) views.setTextViewText(idTime, timeFormat.format(now));
        if (idDate != 0) views.setTextViewText(idDate, dateFormat.format(now));

        // 3. Battery Badge
        int idBattery = context.getResources().getIdentifier("widget_mini_battery", "id", pkg);
        if (idBattery != 0) {
            if (showBattery) {
                views.setViewVisibility(idBattery, View.VISIBLE);
                int pct = 85;
                boolean isCharging = false;
                try {
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = context.registerReceiver(null, ifilter);
                    if (batteryStatus != null) {
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        if (level >= 0 && scale > 0) {
                            pct = (int) ((level / (float) scale) * 100);
                        }
                        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                        isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                      status == BatteryManager.BATTERY_STATUS_FULL);
                    }
                } catch (Exception ignored) {}
                views.setTextViewText(idBattery, isCharging ? ("⚡ " + pct + "% CHARGING") : (pct + "% BATTERY"));
            } else {
                views.setViewVisibility(idBattery, View.GONE);
            }
        }

        // 4. Tap -> Open Alarm / Clock or MainActivity
        int idRoot = context.getResources().getIdentifier("widget_mini_root", "id", pkg);
        if (idRoot != 0) {
            Intent clockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            if (clockIntent.resolveActivity(context.getPackageManager()) == null) {
                clockIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.deskclock");
            }
            if (clockIntent == null) {
                clockIntent = new Intent(context, MainActivity.class);
            }
            PendingIntent clockPi = PendingIntent.getActivity(context, 301, clockIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idRoot, clockPi);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
