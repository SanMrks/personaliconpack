package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

public class MusicLarge2WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_MUSIC_PLAY_PAUSE = "com.ravenshade.iconpack.ACTION_MUSIC2_PLAY_PAUSE";
    public static final String ACTION_MUSIC_PREV = "com.ravenshade.iconpack.ACTION_MUSIC2_PREV";
    public static final String ACTION_MUSIC_NEXT = "com.ravenshade.iconpack.ACTION_MUSIC2_NEXT";
    public static final String ACTION_MUSIC_VOL_DOWN = "com.ravenshade.iconpack.ACTION_MUSIC2_VOL_DOWN";
    public static final String ACTION_MUSIC_VOL_UP = "com.ravenshade.iconpack.ACTION_MUSIC2_VOL_UP";
    public static final String ACTION_MUSIC_OPEN = "com.ravenshade.iconpack.ACTION_MUSIC2_OPEN";

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

        if (ACTION_MUSIC_PLAY_PAUSE.equals(action)) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            boolean amActive = (am != null && am.isMusicActive());
            boolean currentPlaying = RavenMusicManager.isPlaying(context) || amActive;
            boolean nextPlaying = !currentPlaying;

            // 1. Instant 0ms local state toggle for responsive tactile feedback
            RavenMusicManager.getPrefs(context).edit().putBoolean("music_is_playing", nextPlaying).apply();
            updateAll(context);

            // 2. Dispatch transport command
            RavenMusicManager.sendPlayPause(context);

            // 3. Delayed sync passes to capture media session state confirmation
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

        } else if (ACTION_MUSIC_PREV.equals(action)) {
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

        } else if (ACTION_MUSIC_NEXT.equals(action)) {
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

        } else if (ACTION_MUSIC_VOL_DOWN.equals(action)) {
            try {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (am != null) {
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
                }
            } catch (Throwable ignored) {}

        } else if (ACTION_MUSIC_VOL_UP.equals(action)) {
            try {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (am != null) {
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
                }
            } catch (Throwable ignored) {}

        } else if (ACTION_MUSIC_OPEN.equals(action)) {
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
        ComponentName comp = new ComponentName(context, MusicLarge2WidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(comp);
        for (int id : ids) {
            updateAppWidget(context, manager, id);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_music_large2", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Completely transparent glass surface with rectangular squircle rim (18dp)
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 360, 180, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean amActive = (am != null && am.isMusicActive());
        boolean isPlaying = RavenMusicManager.isPlaying(context) || amActive;

        int idArt = context.getResources().getIdentifier("widget_music2_art", "id", pkg);
        int idVolDown = context.getResources().getIdentifier("widget_music2_vol_down", "id", pkg);
        int idVolUp = context.getResources().getIdentifier("widget_music2_vol_up", "id", pkg);
        int idPrevBtn = context.getResources().getIdentifier("widget_music2_btn_prev", "id", pkg);
        int idNextBtn = context.getResources().getIdentifier("widget_music2_btn_next", "id", pkg);
        int idPlayBtn = context.getResources().getIdentifier("widget_music2_btn_play", "id", pkg);

        // 1. Rectangular Curved Album Art (16dp Squircle)
        if (idArt != 0) {
            Bitmap art = RavenMusicManager.getAlbumArt(context);
            if (art != null && !art.isRecycled()) {
                Bitmap rectArt = createRoundedRectArtBitmap(context, art, 160, 16f);
                if (rectArt != null) {
                    views.setImageViewBitmap(idArt, rectArt);
                }
            } else {
                int defaultArt = context.getResources().getIdentifier("ic_music_note", "drawable", pkg);
                if (defaultArt != 0) views.setImageViewResource(idArt, defaultArt);
            }
        }

        // 2. Big 34dp Icons for responsive touch targets
        int icPlay = context.getResources().getIdentifier("ic_play_arrow_34", "drawable", pkg);
        int icPause = context.getResources().getIdentifier("ic_pause_34", "drawable", pkg);
        if (idPlayBtn != 0 && icPlay != 0 && icPause != 0) {
            views.setImageViewResource(idPlayBtn, isPlaying ? icPause : icPlay);
        }

        int icPrev = context.getResources().getIdentifier("ic_skip_previous_34", "drawable", pkg);
        if (idPrevBtn != 0 && icPrev != 0) {
            views.setImageViewResource(idPrevBtn, icPrev);
        }

        int icNext = context.getResources().getIdentifier("ic_skip_next_34", "drawable", pkg);
        if (idNextBtn != 0 && icNext != 0) {
            views.setImageViewResource(idNextBtn, icNext);
        }

        int icMinus = context.getResources().getIdentifier("ic_volume_minus_28", "drawable", pkg);
        if (idVolDown != 0 && icMinus != 0) {
            views.setImageViewResource(idVolDown, icMinus);
        }

        int icPlus = context.getResources().getIdentifier("ic_volume_plus_28", "drawable", pkg);
        if (idVolUp != 0 && icPlus != 0) {
            views.setImageViewResource(idVolUp, icPlus);
        }

        // 3. Interactive Pending Intents
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        if (idArt != 0) {
            Intent openIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            openIntent.setAction(ACTION_MUSIC_OPEN);
            views.setOnClickPendingIntent(idArt, PendingIntent.getBroadcast(context, 701, openIntent, flags));
        }

        if (idVolDown != 0) {
            Intent volDownIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            volDownIntent.setAction(ACTION_MUSIC_VOL_DOWN);
            views.setOnClickPendingIntent(idVolDown, PendingIntent.getBroadcast(context, 702, volDownIntent, flags));
        }

        if (idVolUp != 0) {
            Intent volUpIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            volUpIntent.setAction(ACTION_MUSIC_VOL_UP);
            views.setOnClickPendingIntent(idVolUp, PendingIntent.getBroadcast(context, 703, volUpIntent, flags));
        }

        if (idPrevBtn != 0) {
            Intent prevIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            prevIntent.setAction(ACTION_MUSIC_PREV);
            views.setOnClickPendingIntent(idPrevBtn, PendingIntent.getBroadcast(context, 704, prevIntent, flags));
        }

        if (idNextBtn != 0) {
            Intent nextIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            nextIntent.setAction(ACTION_MUSIC_NEXT);
            views.setOnClickPendingIntent(idNextBtn, PendingIntent.getBroadcast(context, 705, nextIntent, flags));
        }

        if (idPlayBtn != 0) {
            Intent playIntent = new Intent(context, MusicLarge2WidgetProvider.class);
            playIntent.setAction(ACTION_MUSIC_PLAY_PAUSE);
            views.setOnClickPendingIntent(idPlayBtn, PendingIntent.getBroadcast(context, 706, playIntent, flags));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Crops raw album art into a modern rectangular curved squircle (16dp radius) with specular border
     */
    public static Bitmap createRoundedRectArtBitmap(Context context, Bitmap src, int sizePx, float radiusDp) {
        if (src == null || src.isRecycled() || sizePx <= 0) return null;
        try {
            float density = context.getResources().getDisplayMetrics().density;
            float radiusPx = radiusDp * density;

            Bitmap output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setFilterBitmap(true);

            RectF rect = new RectF(0, 0, sizePx, sizePx);
            canvas.drawRoundRect(rect, radiusPx, radiusPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Rect srcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
            Rect dstRect = new Rect(0, 0, sizePx, sizePx);
            canvas.drawBitmap(src, srcRect, dstRect, paint);

            // Specular border rim
            Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(1.4f * density);
            strokePaint.setColor(0x60FFFFFF);
            canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint);

            return output;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
