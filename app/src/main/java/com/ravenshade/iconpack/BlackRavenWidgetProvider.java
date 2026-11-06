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

public class BlackRavenWidgetProvider extends AppWidgetProvider {

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
            Intent.ACTION_TIMEZONE_CHANGED.equals(action) ||
            AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, BlackRavenWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_black_raven", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Dynamic Wallpaper-Adaptive Frosted Liquid Glass Surface
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 360, 180, 28);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        // 1. User Editable Settings
        String customTitle = WidgetSettingsManager.getTitle(context);
        String customSubtitle = WidgetSettingsManager.getSubtitle(context);
        String customTag = WidgetSettingsManager.getTag(context);
        boolean showTag = WidgetSettingsManager.getShowTag(context);
        boolean showBattery = WidgetSettingsManager.getShowBattery(context);

        int idTitle = context.getResources().getIdentifier("widget_title", "id", pkg);
        int idWeather = context.getResources().getIdentifier("widget_weather", "id", pkg);
        int idTag = context.getResources().getIdentifier("widget_tag", "id", pkg);
        int idBatContainer = context.getResources().getIdentifier("widget_battery_container", "id", pkg);

        if (idTitle != 0) {
            views.setTextViewText(idTitle, customTitle);
            views.setTextColor(idTitle, ACCENT_COLOR);
        }
        if (idWeather != 0) views.setTextViewText(idWeather, customSubtitle);
        if (idTag != 0) {
            views.setTextViewText(idTag, customTag);
            views.setViewVisibility(idTag, showTag ? View.VISIBLE : View.GONE);
        }
        if (idBatContainer != 0) {
            views.setViewVisibility(idBatContainer, showBattery ? View.VISIBLE : View.GONE);
        }

        // 2. Current Time & Date
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE | d MMM", Locale.getDefault());

        int idTime = context.getResources().getIdentifier("widget_time", "id", pkg);
        int idDate = context.getResources().getIdentifier("widget_date", "id", pkg);
        if (idTime != 0) views.setTextViewText(idTime, timeFormat.format(now));
        if (idDate != 0) views.setTextViewText(idDate, dateFormat.format(now));

        // 3. Battery Status & Glowing Bar
        if (showBattery) {
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

            String batText = isCharging ? ("⚡ " + pct + "% CHARGING") : (pct + "%");
            int idBatPct = context.getResources().getIdentifier("widget_battery", "id", pkg);
            if (idBatPct == 0) idBatPct = context.getResources().getIdentifier("widget_battery_pct", "id", pkg);
            int idBatBar = context.getResources().getIdentifier("widget_battery_progress", "id", pkg);
            if (idBatBar == 0) idBatBar = context.getResources().getIdentifier("widget_battery_bar", "id", pkg);
            int idBatFlipper = context.getResources().getIdentifier("widget_charging_anim", "id", pkg);
            if (idBatFlipper == 0) idBatFlipper = context.getResources().getIdentifier("widget_battery_flipper", "id", pkg);
            if (idBatPct != 0) views.setTextViewText(idBatPct, batText);
            if (idBatBar != 0) {
                views.setProgressBar(idBatBar, 100, pct, false);
                views.setViewVisibility(idBatBar, isCharging ? View.GONE : View.VISIBLE);
            }
            if (idBatFlipper != 0) {
                views.setViewVisibility(idBatFlipper, isCharging ? View.VISIBLE : View.GONE);
            }
        }

        // 4. Interactive PendingIntents (Clock, Calendar, Battery, Raven Shade Dashboard)
        // Clock tap -> Open Alarm / Clock
        int idClockBox = context.getResources().getIdentifier("widget_clock_container", "id", pkg);
        if (idClockBox != 0) {
            Intent clockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            if (clockIntent.resolveActivity(context.getPackageManager()) == null) {
                clockIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.deskclock");
                if (clockIntent == null) {
                    clockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR);
                }
            }
            if (clockIntent != null) {
                PendingIntent clockPi = PendingIntent.getActivity(context, 101, clockIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(idClockBox, clockPi);
            }
        }

        // Date tap -> Open Calendar
        int idDateBox = context.getResources().getIdentifier("widget_date_container", "id", pkg);
        if (idDateBox != 0) {
            Intent calIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR);
            if (calIntent.resolveActivity(context.getPackageManager()) == null) {
                calIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");
            }
            if (calIntent != null) {
                PendingIntent calPi = PendingIntent.getActivity(context, 102, calIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(idDateBox, calPi);
            }
        }

        // Battery tap -> Battery Settings
        if (idBatContainer != 0) {
            Intent batIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
            if (batIntent.resolveActivity(context.getPackageManager()) == null) {
                batIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            }
            PendingIntent batPi = PendingIntent.getActivity(context, 103, batIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idBatContainer, batPi);
        }

        // Title tap -> Open Raven Shade App
        if (idTitle != 0) {
            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPi = PendingIntent.getActivity(context, 104, appIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idTitle, appPi);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
