package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
            Toast.makeText(context, "🔀 Shuffling wallpaper from Google Drive...", Toast.LENGTH_SHORT).show();
            AutoWallpaperManager.changeWallpaper(context, true, 0, new AutoWallpaperManager.WallpaperCallback() {
                @Override
                public void onSuccess(final String title) {
                    Toast.makeText(context, "✓ Applied: " + title, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(final String message) {
                    Toast.makeText(context, "Shuffle failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_wallpaper_shuffle", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        int idRoot = context.getResources().getIdentifier("widget_shuffle_root", "id", pkg);
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
