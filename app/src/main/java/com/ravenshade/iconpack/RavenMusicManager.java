package com.ravenshade.iconpack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;

import java.io.File;
import java.io.FileOutputStream;

public class RavenMusicManager {

    private static final String PREF_NAME = "ravenshade_music_state";
    private static final String KEY_TITLE = "music_title";
    private static final String KEY_ARTIST = "music_artist";
    private static final String KEY_IS_PLAYING = "music_is_playing";
    private static final String KEY_PACKAGE = "music_pkg";

    private static Bitmap sCachedArt = null;

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void updateTrack(Context context, String title, String artist, boolean isPlaying, String pkg, Bitmap art) {
        if (title == null || title.trim().isEmpty()) {
            title = WidgetSettingsManager.getMusicDefaultTitle(context);
        }
        if (artist == null || artist.trim().isEmpty()) {
            artist = WidgetSettingsManager.getMusicDefaultArtist(context);
        }

        getPrefs(context).edit()
                .putString(KEY_TITLE, title)
                .putString(KEY_ARTIST, artist)
                .putBoolean(KEY_IS_PLAYING, isPlaying)
                .putString(KEY_PACKAGE, pkg != null ? pkg : "")
                .apply();

        if (art != null) {
            sCachedArt = art;
            try {
                File file = new File(context.getFilesDir(), "music_album_art.png");
                FileOutputStream fos = new FileOutputStream(file);
                art.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (Exception ignored) {}
        }

        updateAllMusicWidgets(context);
    }

    public static String getTitle(Context context) {
        String saved = getPrefs(context).getString(KEY_TITLE, null);
        if (saved != null && !saved.isEmpty()) return saved;
        return WidgetSettingsManager.getMusicDefaultTitle(context);
    }

    public static String getArtist(Context context) {
        String saved = getPrefs(context).getString(KEY_ARTIST, null);
        if (saved != null && !saved.isEmpty()) return saved;
        return WidgetSettingsManager.getMusicDefaultArtist(context);
    }

    public static boolean isPlaying(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_PLAYING, false);
    }

    public static String getPlayingPackage(Context context) {
        return getPrefs(context).getString(KEY_PACKAGE, "");
    }

    public static Bitmap getAlbumArt(Context context) {
        if (sCachedArt != null && !sCachedArt.isRecycled()) {
            return sCachedArt;
        }
        try {
            File file = new File(context.getFilesDir(), "music_album_art.png");
            if (file.exists() && file.length() > 0) {
                sCachedArt = BitmapFactory.decodeFile(file.getAbsolutePath());
                return sCachedArt;
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static void sendPlayPause(Context context) {
        if (RavenNotificationListener.dispatchPlayPause(context)) {
            return;
        }
        dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    public static void sendNext(Context context) {
        if (RavenNotificationListener.dispatchNext(context)) {
            return;
        }
        dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    public static void sendPrevious(Context context) {
        if (RavenNotificationListener.dispatchPrevious(context)) {
            return;
        }
        dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    private static void dispatchMediaKey(Context context, int keyCode) {
        try {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                long eventTime = SystemClock.uptimeMillis();
                KeyEvent down = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
                KeyEvent up = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
                am.dispatchMediaKeyEvent(down);
                am.dispatchMediaKeyEvent(up);
            }
        } catch (Exception ignored) {}
    }

    public static void updateAllMusicWidgets(Context context) {
        try {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);

            // 1. 4x2 Music Widget
            ComponentName comp4x2 = new ComponentName(context, MusicWidgetProvider.class);
            int[] ids4x2 = manager.getAppWidgetIds(comp4x2);
            for (int id : ids4x2) {
                MusicWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 2. 2x2 Music Widget
            ComponentName comp2x2 = new ComponentName(context, MusicWidget2x2Provider.class);
            int[] ids2x2 = manager.getAppWidgetIds(comp2x2);
            for (int id : ids2x2) {
                MusicWidget2x2Provider.updateAppWidget(context, manager, id);
            }
        } catch (Exception ignored) {}
    }
}
