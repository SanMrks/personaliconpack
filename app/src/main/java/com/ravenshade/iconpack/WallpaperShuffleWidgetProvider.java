package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WallpaperShuffleWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_SHUFFLE = "com.ravenshade.iconpack.ACTION_SHUFFLE_WALLPAPER_WIDGET";

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

        if (ACTION_SHUFFLE.equals(action)) {
            Toast.makeText(context, "💥 Popping new wallpaper from Raven's Gallery...", Toast.LENGTH_SHORT).show();
            AutoWallpaperManager.changeWallpaper(context, true, 0, new AutoWallpaperManager.WallpaperCallback() {
                @Override
                public void onSuccess(final String title) {
                    Toast.makeText(context, "✓ Applied: " + title, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(final String message) {
                    Toast.makeText(context, "Failed to load: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WallpaperShuffleWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_wallpaper_shuffle", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // Completely Transparent Rectangular Squircle Glass Surface (18dp)
        int idBg = context.getResources().getIdentifier("widget_glass_bg", "id", pkg);
        if (idBg != 0) {
            try {
                Bitmap glassBm = RavenGlassThemeManager.createLiquidGlassBitmap(context, 240, 240, 18f);
                if (glassBm != null) views.setImageViewBitmap(idBg, glassBm);
            } catch (Throwable ignored) {}
        }

        int idRoot = context.getResources().getIdentifier("widget_shuffle_root", "id", pkg);
        int idTitle = context.getResources().getIdentifier("widget_shuffle_title", "id", pkg);
        int idSub = context.getResources().getIdentifier("widget_shuffle_subtitle", "id", pkg);
        int idTag = context.getResources().getIdentifier("widget_shuffle_tag", "id", pkg);

        if (idTitle != 0) views.setTextViewText(idTitle, WidgetSettingsManager.getShuffleTitle(context));
        if (idSub != 0) views.setTextViewText(idSub, WidgetSettingsManager.getShuffleSubtitle(context));
        if (idTag != 0) views.setTextViewText(idTag, WidgetSettingsManager.getShuffleTag(context));

        if (idRoot != 0) {
            Intent intent = new Intent(context, WallpaperShuffleWidgetProvider.class);
            intent.setAction(ACTION_SHUFFLE);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pi = PendingIntent.getBroadcast(context, 501, intent, flags);
            views.setOnClickPendingIntent(idRoot, pi);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
