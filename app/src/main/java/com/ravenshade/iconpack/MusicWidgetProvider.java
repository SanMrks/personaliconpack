package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class MusicWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_MUSIC_PLAY_PAUSE = "com.ravenshade.iconpack.ACTION_MUSIC_PLAY_PAUSE";
    public static final String ACTION_MUSIC_PREV = "com.ravenshade.iconpack.ACTION_MUSIC_PREV";
    public static final String ACTION_MUSIC_NEXT = "com.ravenshade.iconpack.ACTION_MUSIC_NEXT";
    public static final String ACTION_MUSIC_OPEN = "com.ravenshade.iconpack.ACTION_MUSIC_OPEN";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent == null) return;
        String action = intent.getAction();

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (ACTION_MUSIC_PLAY_PAUSE.equals(action)) {
            sendMediaKey(am, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            updateAll(context);
        } else if (ACTION_MUSIC_PREV.equals(action)) {
            sendMediaKey(am, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            updateAll(context);
        } else if (ACTION_MUSIC_NEXT.equals(action)) {
            sendMediaKey(am, KeyEvent.KEYCODE_MEDIA_NEXT);
            updateAll(context);
        } else if (ACTION_MUSIC_OPEN.equals(action)) {
            launchMusicApp(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            updateAll(context);
        }
    }

    private static void sendMediaKey(AudioManager am, int keyCode) {
        if (am == null) return;
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent up = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
        am.dispatchMediaKeyEvent(down);
        am.dispatchMediaKeyEvent(up);
    }

    private static void launchMusicApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.convx.music");
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
        }
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.youtube.music");
        }
        if (intent == null) {
            intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName comp = new ComponentName(context, MusicWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(comp);
        for (int id : ids) {
            updateAppWidget(context, manager, id);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_music", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isMusicActive = (am != null && am.isMusicActive());

        int idPlayBtn = context.getResources().getIdentifier("widget_music_btn_play", "id", pkg);
        int idState = context.getResources().getIdentifier("widget_music_state", "id", pkg);
        int idTrackBox = context.getResources().getIdentifier("widget_music_track_box", "id", pkg);
        int idPrevBtn = context.getResources().getIdentifier("widget_music_btn_prev", "id", pkg);
        int idNextBtn = context.getResources().getIdentifier("widget_music_btn_next", "id", pkg);

        // Icon for Play / Pause
        int icPlay = context.getResources().getIdentifier("ic_play_arrow", "drawable", pkg);
        int icPause = context.getResources().getIdentifier("ic_pause", "drawable", pkg);

        if (idPlayBtn != 0) {
            views.setImageViewResource(idPlayBtn, isMusicActive ? icPause : icPlay);
        }
        if (idState != 0) {
            views.setTextViewText(idState, isMusicActive ? "PLAYING" : "PAUSED");
        }

        // Attach PendingIntents
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        if (idPlayBtn != 0) {
            Intent playIntent = new Intent(context, MusicWidgetProvider.class);
            playIntent.setAction(ACTION_MUSIC_PLAY_PAUSE);
            views.setOnClickPendingIntent(idPlayBtn, PendingIntent.getBroadcast(context, 401, playIntent, flags));
        }

        if (idPrevBtn != 0) {
            Intent prevIntent = new Intent(context, MusicWidgetProvider.class);
            prevIntent.setAction(ACTION_MUSIC_PREV);
            views.setOnClickPendingIntent(idPrevBtn, PendingIntent.getBroadcast(context, 402, prevIntent, flags));
        }

        if (idNextBtn != 0) {
            Intent nextIntent = new Intent(context, MusicWidgetProvider.class);
            nextIntent.setAction(ACTION_MUSIC_NEXT);
            views.setOnClickPendingIntent(idNextBtn, PendingIntent.getBroadcast(context, 403, nextIntent, flags));
        }

        if (idTrackBox != 0) {
            Intent openIntent = new Intent(context, MusicWidgetProvider.class);
            openIntent.setAction(ACTION_MUSIC_OPEN);
            views.setOnClickPendingIntent(idTrackBox, PendingIntent.getBroadcast(context, 404, openIntent, flags));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
