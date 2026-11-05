package com.ravenshade.iconpack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class WidgetSettingsManager {

    public static final String PREF_NAME = "ravenshade_widgets";

    // 1. Black Raven Clock Keys
    public static final String KEY_TITLE = "widget_title";
    public static final String KEY_SUBTITLE = "widget_subtitle";
    public static final String KEY_TAG = "widget_tag";
    public static final String KEY_SHOW_TAG = "widget_show_tag";
    public static final String KEY_SHOW_BATTERY = "widget_show_battery";
    public static final String KEY_GLASS_STYLE = "widget_glass_style";

    // 2. Music Widget Keys
    public static final String KEY_MUSIC_BRAND = "widget_music_brand";
    public static final String KEY_MUSIC_TITLE = "widget_music_title";
    public static final String KEY_MUSIC_ARTIST = "widget_music_artist";
    public static final String KEY_MUSIC_TAG = "widget_music_tag";

    // 3. Shuffle Wallpaper Widget Keys
    public static final String KEY_SHUFFLE_TITLE = "widget_shuffle_title";
    public static final String KEY_SHUFFLE_SUBTITLE = "widget_shuffle_sub";
    public static final String KEY_SHUFFLE_TAG = "widget_shuffle_tag";

    // 4. Quick Dock Keys
    public static final String KEY_DOCK_TITLE = "widget_dock_title";
    public static final String KEY_DOCK_TAG = "widget_dock_tag";

    // 5. Anime Clock Keys
    public static final String KEY_ANIME_TITLE = "widget_anime_title";
    public static final String KEY_ANIME_TAG = "widget_anime_tag";

    // Defaults
    public static final String DEFAULT_TITLE = "RAVEN SHADE";
    public static final String DEFAULT_SUBTITLE = "OBSIDIAN LIQUID GLASS";
    public static final String DEFAULT_TAG = "RAVENSHADE EDITION";

    public static final String DEFAULT_MUSIC_BRAND = "RAVEN AUDIO";
    public static final String DEFAULT_MUSIC_TITLE = "Raven Waves";
    public static final String DEFAULT_MUSIC_ARTIST = "Obsidian Crimson Session";
    public static final String DEFAULT_MUSIC_TAG = "NOW PLAYING";

    public static final String DEFAULT_SHUFFLE_TITLE = "POP";
    public static final String DEFAULT_SHUFFLE_SUBTITLE = "WALLPAPER";
    public static final String DEFAULT_SHUFFLE_TAG = "";

    public static final String DEFAULT_DOCK_TITLE = "RAVEN DOCK";
    public static final String DEFAULT_DOCK_TAG = "QUICK ACCESS";

    public static final String DEFAULT_ANIME_TITLE = "SHADOW RAVEN";
    public static final String DEFAULT_ANIME_TAG = "ZONE ACTIVE";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Black Raven Clock
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

    // Music Widget
    public static String getMusicBrand(Context context) {
        return getPrefs(context).getString(KEY_MUSIC_BRAND, DEFAULT_MUSIC_BRAND);
    }
    public static void setMusicBrand(Context context, String val) {
        getPrefs(context).edit().putString(KEY_MUSIC_BRAND, val).apply();
    }
    public static String getMusicDefaultTitle(Context context) {
        return getPrefs(context).getString(KEY_MUSIC_TITLE, DEFAULT_MUSIC_TITLE);
    }
    public static void setMusicDefaultTitle(Context context, String val) {
        getPrefs(context).edit().putString(KEY_MUSIC_TITLE, val).apply();
    }
    public static String getMusicDefaultArtist(Context context) {
        return getPrefs(context).getString(KEY_MUSIC_ARTIST, DEFAULT_MUSIC_ARTIST);
    }
    public static void setMusicDefaultArtist(Context context, String val) {
        getPrefs(context).edit().putString(KEY_MUSIC_ARTIST, val).apply();
    }
    public static String getMusicTag(Context context) {
        return getPrefs(context).getString(KEY_MUSIC_TAG, DEFAULT_MUSIC_TAG);
    }
    public static void setMusicTag(Context context, String val) {
        getPrefs(context).edit().putString(KEY_MUSIC_TAG, val).apply();
    }

    // Shuffle Widget
    public static String getShuffleTitle(Context context) {
        return getPrefs(context).getString(KEY_SHUFFLE_TITLE, DEFAULT_SHUFFLE_TITLE);
    }
    public static void setShuffleTitle(Context context, String val) {
        getPrefs(context).edit().putString(KEY_SHUFFLE_TITLE, val).apply();
    }
    public static String getShuffleSubtitle(Context context) {
        return getPrefs(context).getString(KEY_SHUFFLE_SUBTITLE, DEFAULT_SHUFFLE_SUBTITLE);
    }
    public static void setShuffleSubtitle(Context context, String val) {
        getPrefs(context).edit().putString(KEY_SHUFFLE_SUBTITLE, val).apply();
    }
    public static String getShuffleTag(Context context) {
        return getPrefs(context).getString(KEY_SHUFFLE_TAG, DEFAULT_SHUFFLE_TAG);
    }
    public static void setShuffleTag(Context context, String val) {
        getPrefs(context).edit().putString(KEY_SHUFFLE_TAG, val).apply();
    }

    // Quick Dock Widget
    public static String getDockTitle(Context context) {
        return getPrefs(context).getString(KEY_DOCK_TITLE, DEFAULT_DOCK_TITLE);
    }
    public static void setDockTitle(Context context, String val) {
        getPrefs(context).edit().putString(KEY_DOCK_TITLE, val).apply();
    }
    public static String getDockTag(Context context) {
        return getPrefs(context).getString(KEY_DOCK_TAG, DEFAULT_DOCK_TAG);
    }
    public static void setDockTag(Context context, String val) {
        getPrefs(context).edit().putString(KEY_DOCK_TAG, val).apply();
    }

    // Anime Mini Clock
    public static String getAnimeTitle(Context context) {
        return getPrefs(context).getString(KEY_ANIME_TITLE, DEFAULT_ANIME_TITLE);
    }
    public static void setAnimeTitle(Context context, String val) {
        getPrefs(context).edit().putString(KEY_ANIME_TITLE, val).apply();
    }
    public static String getAnimeTag(Context context) {
        return getPrefs(context).getString(KEY_ANIME_TAG, DEFAULT_ANIME_TAG);
    }
    public static void setAnimeTag(Context context, String val) {
        getPrefs(context).edit().putString(KEY_ANIME_TAG, val).apply();
    }

    public static void resetDefaults(Context context) {
        getPrefs(context).edit()
                .putString(KEY_TITLE, DEFAULT_TITLE)
                .putString(KEY_SUBTITLE, DEFAULT_SUBTITLE)
                .putString(KEY_TAG, DEFAULT_TAG)
                .putBoolean(KEY_SHOW_TAG, true)
                .putBoolean(KEY_SHOW_BATTERY, true)
                .putString(KEY_GLASS_STYLE, "crystal")
                .putString(KEY_MUSIC_BRAND, DEFAULT_MUSIC_BRAND)
                .putString(KEY_MUSIC_TITLE, DEFAULT_MUSIC_TITLE)
                .putString(KEY_MUSIC_ARTIST, DEFAULT_MUSIC_ARTIST)
                .putString(KEY_MUSIC_TAG, DEFAULT_MUSIC_TAG)
                .putString(KEY_SHUFFLE_TITLE, DEFAULT_SHUFFLE_TITLE)
                .putString(KEY_SHUFFLE_SUBTITLE, DEFAULT_SHUFFLE_SUBTITLE)
                .putString(KEY_SHUFFLE_TAG, DEFAULT_SHUFFLE_TAG)
                .putString(KEY_DOCK_TITLE, DEFAULT_DOCK_TITLE)
                .putString(KEY_DOCK_TAG, DEFAULT_DOCK_TAG)
                .putString(KEY_ANIME_TITLE, DEFAULT_ANIME_TITLE)
                .putString(KEY_ANIME_TAG, DEFAULT_ANIME_TAG)
                .apply();
        updateAllWidgets(context);
    }

    public static void updateAllWidgets(Context context) {
        try {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);

            // 1. Black Raven Clock (4x2)
            ComponentName ravenComp = new ComponentName(context, BlackRavenWidgetProvider.class);
            int[] ravenIds = manager.getAppWidgetIds(ravenComp);
            for (int id : ravenIds) {
                BlackRavenWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 2. Anime Mini Clock (2x2)
            ComponentName miniComp = new ComponentName(context, AnimeClockWidgetProvider.class);
            int[] miniIds = manager.getAppWidgetIds(miniComp);
            for (int id : miniIds) {
                AnimeClockWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 3. Quick Dock (4x1)
            ComponentName dockComp = new ComponentName(context, QuickDockWidgetProvider.class);
            int[] dockIds = manager.getAppWidgetIds(dockComp);
            for (int id : dockIds) {
                QuickDockWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 4. Music Widget (4x2)
            ComponentName musicComp = new ComponentName(context, MusicWidgetProvider.class);
            int[] musicIds = manager.getAppWidgetIds(musicComp);
            for (int id : musicIds) {
                MusicWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 5. Music Widget (2x2)
            ComponentName music2x2Comp = new ComponentName(context, MusicWidget2x2Provider.class);
            int[] music2x2Ids = manager.getAppWidgetIds(music2x2Comp);
            for (int id : music2x2Ids) {
                MusicWidget2x2Provider.updateAppWidget(context, manager, id);
            }

            // 6. Wallpaper Shuffle Widget (1x1)
            ComponentName shuffleComp = new ComponentName(context, WallpaperShuffleWidgetProvider.class);
            int[] shuffleIds = manager.getAppWidgetIds(shuffleComp);
            for (int id : shuffleIds) {
                WallpaperShuffleWidgetProvider.updateAppWidget(context, manager, id);
            }

            // Broadcast update
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ravenIds);
            context.sendBroadcast(updateIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
