package com.ravenshade.iconpack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class WidgetSettingsManager {

    public static final String PREF_NAME = "ravenshade_widgets";
    public static final String KEY_TITLE = "widget_title";
    public static final String KEY_SUBTITLE = "widget_subtitle";
    public static final String KEY_TAG = "widget_tag";
    public static final String KEY_SHOW_TAG = "widget_show_tag";
    public static final String KEY_SHOW_BATTERY = "widget_show_battery";
    public static final String KEY_GLASS_STYLE = "widget_glass_style";

    public static final String DEFAULT_TITLE = "RAVEN SHADE";
    public static final String DEFAULT_SUBTITLE = "OBSIDIAN LIQUID GLASS";
    public static final String DEFAULT_TAG = "RAVENSHADE EDITION";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getTitle(Context context) {
        return getPrefs(context).getString(KEY_TITLE, DEFAULT_TITLE);
    }

    public static void setTitle(Context context, String title) {
        getPrefs(context).edit().putString(KEY_TITLE, title).apply();
    }

    public static String getSubtitle(Context context) {
        return getPrefs(context).getString(KEY_SUBTITLE, DEFAULT_SUBTITLE);
    }

    public static void setSubtitle(Context context, String subtitle) {
        getPrefs(context).edit().putString(KEY_SUBTITLE, subtitle).apply();
    }

    public static String getTag(Context context) {
        return getPrefs(context).getString(KEY_TAG, DEFAULT_TAG);
    }

    public static void setTag(Context context, String tag) {
        getPrefs(context).edit().putString(KEY_TAG, tag).apply();
    }

    public static boolean getShowTag(Context context) {
        return getPrefs(context).getBoolean(KEY_SHOW_TAG, true);
    }

    public static void setShowTag(Context context, boolean show) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_TAG, show).apply();
    }

    public static boolean getShowBattery(Context context) {
        return getPrefs(context).getBoolean(KEY_SHOW_BATTERY, true);
    }

    public static void setShowBattery(Context context, boolean show) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_BATTERY, show).apply();
    }

    public static String getGlassStyle(Context context) {
        return getPrefs(context).getString(KEY_GLASS_STYLE, "crystal");
    }

    public static void setGlassStyle(Context context, String style) {
        getPrefs(context).edit().putString(KEY_GLASS_STYLE, style).apply();
    }

    public static void resetDefaults(Context context) {
        getPrefs(context).edit()
                .putString(KEY_TITLE, DEFAULT_TITLE)
                .putString(KEY_SUBTITLE, DEFAULT_SUBTITLE)
                .putString(KEY_TAG, DEFAULT_TAG)
                .putBoolean(KEY_SHOW_TAG, true)
                .putBoolean(KEY_SHOW_BATTERY, true)
                .putString(KEY_GLASS_STYLE, "crystal")
                .apply();
        updateAllWidgets(context);
    }

    public static void updateAllWidgets(Context context) {
        try {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);

            // 1. Black Raven Clock
            ComponentName ravenComp = new ComponentName(context, BlackRavenWidgetProvider.class);
            int[] ravenIds = manager.getAppWidgetIds(ravenComp);
            for (int id : ravenIds) {
                BlackRavenWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 2. Anime Mini Clock
            ComponentName miniComp = new ComponentName(context, AnimeClockWidgetProvider.class);
            int[] miniIds = manager.getAppWidgetIds(miniComp);
            for (int id : miniIds) {
                AnimeClockWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 3. Quick Dock
            ComponentName dockComp = new ComponentName(context, QuickDockWidgetProvider.class);
            int[] dockIds = manager.getAppWidgetIds(dockComp);
            for (int id : dockIds) {
                QuickDockWidgetProvider.updateAppWidget(context, manager, id);
            }

            // Send standard broadcast update
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ravenIds);
            context.sendBroadcast(updateIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
