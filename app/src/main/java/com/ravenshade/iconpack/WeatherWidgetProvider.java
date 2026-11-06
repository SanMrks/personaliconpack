package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final int ACCENT_COLOR = 0xFFFF2E4D;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent != null ? intent.getAction() : null;
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RavenWeatherManager.refreshWeatherAsync(context, false);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_weather", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Dynamic Wallpaper-Adaptive Frosted Liquid Glass Surface (2x2)
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 200, 200, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        // Weather Details
        int temp = RavenWeatherManager.getTemperature(context);
        int high = RavenWeatherManager.getHigh(context);
        int low = RavenWeatherManager.getLow(context);
        String unit = WidgetSettingsManager.getWeatherUnit(context);
        if ("F".equalsIgnoreCase(unit)) {
            temp = (int) Math.round(temp * 1.8 + 32);
            high = (int) Math.round(high * 1.8 + 32);
            low = (int) Math.round(low * 1.8 + 32);
        }

        int idTemp = context.getResources().getIdentifier("widget_weather_temp", "id", pkg);
        if (idTemp != 0) views.setTextViewText(idTemp, temp + "°");

        int idUnit = context.getResources().getIdentifier("widget_weather_unit", "id", pkg);
        if (idUnit != 0) views.setTextViewText(idUnit, "F".equalsIgnoreCase(unit) ? "FAHRENHEIT" : "CELSIUS");

        int idCond = context.getResources().getIdentifier("widget_weather_condition", "id", pkg);
        if (idCond != 0) views.setTextViewText(idCond, RavenWeatherManager.getCondition(context));

        int idTag = context.getResources().getIdentifier("widget_weather_tag", "id", pkg);
        if (idTag != 0) {
            views.setTextViewText(idTag, WidgetSettingsManager.getWeatherTag(context));
            views.setTextColor(idTag, ACCENT_COLOR);
        }

        int idCity = context.getResources().getIdentifier("widget_weather_city", "id", pkg);
        if (idCity != 0) views.setTextViewText(idCity, WidgetSettingsManager.getWeatherCity(context));

        int idRange = context.getResources().getIdentifier("widget_weather_range", "id", pkg);
        if (idRange != 0) views.setTextViewText(idRange, "H: " + high + "°  L: " + low + "°");

        int idIcon = context.getResources().getIdentifier("widget_weather_icon", "id", pkg);
        if (idIcon != 0) {
            String iconName = RavenWeatherManager.getIconName(context);
            int iconRes = context.getResources().getIdentifier(iconName, "drawable", pkg);
            if (iconRes == 0) iconRes = context.getResources().getIdentifier("ic_weather_sunny", "drawable", pkg);
            if (iconRes != 0) views.setImageViewResource(idIcon, iconRes);
        }

        // Click to open app / refresh
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                context, 301, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        int idRoot = context.getResources().getIdentifier("widget_weather_root", "id", pkg);
        if (idRoot != 0) views.setOnClickPendingIntent(idRoot, pi);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
