package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

public class MusicWidget2x2Provider extends AppWidgetProvider {

    public static final String ACTION_MUSIC_2X2_PLAY_PAUSE = "com.ravenshade.iconpack.ACTION_MUSIC_2X2_PLAY_PAUSE";
    public static final String ACTION_MUSIC_2X2_PREV = "com.ravenshade.iconpack.ACTION_MUSIC_2X2_PREV";
    public static final String ACTION_MUSIC_2X2_NEXT = "com.ravenshade.iconpack.ACTION_MUSIC_2X2_NEXT";
    public static final String ACTION_MUSIC_2X2_OPEN = "com.ravenshade.iconpack.ACTION_MUSIC_2X2_OPEN";

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent == null) return;
        String action = intent.getAction();

        if (ACTION_MUSIC_2X2_PLAY_PAUSE.equals(action)) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            boolean amActive = (am != null && am.isMusicActive());
            boolean currentPlaying = RavenMusicManager.isPlaying(context) || amActive;
            boolean nextPlaying = !currentPlaying;

            // 1. Instant 0ms toggle for responsive tactile feedback
            RavenMusicManager.getPrefs(context).edit().putBoolean("music_is_playing", nextPlaying).apply();
            updateAll(context);

            // 2. Dispatch command
            RavenMusicManager.sendPlayPause(context);

            // 3. Delayed sync passes
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 300);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 750);

        } else if (ACTION_MUSIC_2X2_PREV.equals(action)) {
            RavenMusicManager.sendPrevious(context);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 350);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 800);

        } else if (ACTION_MUSIC_2X2_NEXT.equals(action)) {
            RavenMusicManager.sendNext(context);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 350);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateAll(context);
                }
            }, 800);

        } else if (ACTION_MUSIC_2X2_OPEN.equals(action)) {
            launchMusicApp(context);

        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            updateAll(context);
        }
    }

    private static void launchMusicApp(Context context) {
        String activePkg = RavenMusicManager.getPlayingPackage(context);
        Intent intent = null;
        if (activePkg != null && !activePkg.isEmpty()) {
            intent = context.getPackageManager().getLaunchIntentForPackage(activePkg);
        }
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.convx.music");
        }
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
        }
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.youtube.music");
        }
        if (intent == null) {
            intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName comp = new ComponentName(context, MusicWidget2x2Provider.class);
        int[] ids = manager.getAppWidgetIds(comp);
        for (int id : ids) {
            updateAppWidget(context, manager, id);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_music_2x2", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Dynamic Completely Transparent Glass Surface with 18dp squircle rim
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 300, 300, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean amActive = (am != null && am.isMusicActive());
        boolean isPlaying = RavenMusicManager.isPlaying(context) || amActive;

        String songTitle = RavenMusicManager.getTitle(context);
        String songArtist = RavenMusicManager.getArtist(context);

        int idPlayBtn = context.getResources().getIdentifier("widget_music_btn_play", "id", pkg);
        int idPrevBtn = context.getResources().getIdentifier("widget_music_btn_prev", "id", pkg);
        int idNextBtn = context.getResources().getIdentifier("widget_music_btn_next", "id", pkg);
        int idTitle = context.getResources().getIdentifier("widget_music_title", "id", pkg);
        int idArtist = context.getResources().getIdentifier("widget_music_artist", "id", pkg);
        int idArt = context.getResources().getIdentifier("widget_music_art", "id", pkg);
        int idCur = context.getResources().getIdentifier("widget_music_time_current", "id", pkg);
        int idTot = context.getResources().getIdentifier("widget_music_time_total", "id", pkg);

        if (idTitle != 0) views.setTextViewText(idTitle, songTitle);
        if (idArtist != 0) views.setTextViewText(idArtist, songArtist);
        if (idCur != 0) views.setTextViewText(idCur, RavenMusicManager.getCurrentTimeFormatted(context));
        if (idTot != 0) views.setTextViewText(idTot, RavenMusicManager.getTotalTimeFormatted(context));

        // Play/Pause icon
        int icPlay = context.getResources().getIdentifier("ic_play_arrow", "drawable", pkg);
        int icPause = context.getResources().getIdentifier("ic_pause", "drawable", pkg);
        if (idPlayBtn != 0) {
            views.setImageViewResource(idPlayBtn, isPlaying ? icPause : icPlay);
        }

        // Album Art
        Bitmap art = RavenMusicManager.getAlbumArt(context);
        if (idArt != 0) {
            if (art != null && !art.isRecycled()) {
                views.setImageViewBitmap(idArt, art);
            } else {
                int defaultArt = context.getResources().getIdentifier("ic_music_note", "drawable", pkg);
                if (defaultArt != 0) views.setImageViewResource(idArt, defaultArt);
            }
        }

        // PendingIntents
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        if (idPlayBtn != 0) {
            Intent playIntent = new Intent(context, MusicWidget2x2Provider.class);
            playIntent.setAction(ACTION_MUSIC_2X2_PLAY_PAUSE);
            views.setOnClickPendingIntent(idPlayBtn, PendingIntent.getBroadcast(context, 201, playIntent, flags));
        }

        if (idPrevBtn != 0) {
            Intent prevIntent = new Intent(context, MusicWidget2x2Provider.class);
            prevIntent.setAction(ACTION_MUSIC_2X2_PREV);
            views.setOnClickPendingIntent(idPrevBtn, PendingIntent.getBroadcast(context, 202, prevIntent, flags));
        }

        if (idNextBtn != 0) {
            Intent nextIntent = new Intent(context, MusicWidget2x2Provider.class);
            nextIntent.setAction(ACTION_MUSIC_2X2_NEXT);
            views.setOnClickPendingIntent(idNextBtn, PendingIntent.getBroadcast(context, 203, nextIntent, flags));
        }

        if (idArt != 0) {
            Intent openIntent = new Intent(context, MusicWidget2x2Provider.class);
            openIntent.setAction(ACTION_MUSIC_2X2_OPEN);
            views.setOnClickPendingIntent(idArt, PendingIntent.getBroadcast(context, 204, openIntent, flags));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
