package com.ravenshade.iconpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.RemoteViews;

public class QuickDockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier("widget_quick_dock", "layout", pkg);
        if (layoutId == 0) return;

        RemoteViews views = new RemoteViews(pkg, layoutId);

        // 1. Phone / Dialer
        int idPhone = context.getResources().getIdentifier("widget_dock_phone", "id", pkg);
        if (idPhone != 0) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            PendingIntent phonePi = PendingIntent.getActivity(context, 201, phoneIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idPhone, phonePi);
        }

        // 2. WhatsApp / Messaging
        int idWa = context.getResources().getIdentifier("widget_dock_whatsapp", "id", pkg);
        if (idWa != 0) {
            Intent waIntent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
            if (waIntent == null) {
                waIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING);
            }
            PendingIntent waPi = PendingIntent.getActivity(context, 202, waIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idWa, waPi);
        }

        // 3. Browser
        int idBrowser = context.getResources().getIdentifier("widget_dock_browser", "id", pkg);
        if (idBrowser != 0) {
            Intent browserIntent = context.getPackageManager().getLaunchIntentForPackage("com.vivaldi.browser");
            if (browserIntent == null) {
                browserIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.chrome");
            }
            if (browserIntent == null) {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            }
            PendingIntent browserPi = PendingIntent.getActivity(context, 203, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idBrowser, browserPi);
        }

        // 4. Music
        int idMusic = context.getResources().getIdentifier("widget_dock_music", "id", pkg);
        if (idMusic != 0) {
            Intent musicIntent = context.getPackageManager().getLaunchIntentForPackage("com.convx.music");
            if (musicIntent == null) {
                musicIntent = context.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
            }
            if (musicIntent == null) {
                musicIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC);
            }
            PendingIntent musicPi = PendingIntent.getActivity(context, 204, musicIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idMusic, musicPi);
        }

        // 5. Camera
        int idCamera = context.getResources().getIdentifier("widget_dock_camera", "id", pkg);
        if (idCamera != 0) {
            Intent camIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            if (camIntent.resolveActivity(context.getPackageManager()) == null) {
                camIntent = context.getPackageManager().getLaunchIntentForPackage("com.nothing.camera");
            }
            PendingIntent camPi = PendingIntent.getActivity(context, 205, camIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(idCamera, camPi);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
