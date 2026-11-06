package com.ravenshade.iconpack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoWallpaperManager {

    public static final String PREF_NAME = "ravenshade_auto_wallpaper";
    public static final String KEY_ENABLED = "auto_wp_enabled";
    public static final String KEY_INTERVAL = "auto_wp_interval_minutes"; // 5, 10, 30, 60, 1440
    public static final String KEY_TARGET = "auto_wp_target"; // 1 = Home, 2 = Lock, 3 = Both
    public static final String KEY_LAST_INDEX = "auto_wp_last_index";
    public static final String KEY_HISTORY = "auto_wp_history";
    public static final String KEY_CURRENT_TITLE = "auto_wp_current_title";
    public static final String KEY_CURRENT_URL = "auto_wp_current_url";

    public static final String ACTION_TRIGGER = "com.ravenshade.iconpack.ACTION_AUTO_WALLPAPER_TRIGGER";
    public static final String ACTION_WALLPAPER_CHANGED = "com.ravenshade.iconpack.ACTION_WALLPAPER_CHANGED";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static class WallpaperItem {
        public final String id;
        public final String title;
        public final String fullUrl;
        public final String thumbUrl;

        public WallpaperItem(String id, String title, String fullUrl, String thumbUrl) {
            this.id = id;
            this.title = title;
            this.fullUrl = fullUrl;
            this.thumbUrl = thumbUrl;
        }
    }

    public interface WallpaperCallback {
        void onSuccess(String title);
        void onError(String message);
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isAutoEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_ENABLED, false);
    }

    public static void setAutoEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
        if (enabled) {
            scheduleNext(context);
        } else {
            cancelSchedule(context);
        }
    }

    public static int getIntervalMinutes(Context context) {
        return getPrefs(context).getInt(KEY_INTERVAL, 30);
    }

    public static void setIntervalMinutes(Context context, int minutes) {
        getPrefs(context).edit().putInt(KEY_INTERVAL, minutes).apply();
        if (isAutoEnabled(context)) {
            scheduleNext(context);
        }
    }

    public static int getTarget(Context context) {
        return getPrefs(context).getInt(KEY_TARGET, 3); // Default: Both
    }

    public static void setTarget(Context context, int target) {
        getPrefs(context).edit().putInt(KEY_TARGET, target).apply();
    }

    public static String getCurrentTitle(Context context) {
        return getPrefs(context).getString(KEY_CURRENT_TITLE, "Raven Shade Obsidian");
    }

    public static String getCurrentUrl(Context context) {
        return getPrefs(context).getString(KEY_CURRENT_URL, "");
    }

    // --- PARSE WALLPAPERS ---
    public static List<WallpaperItem> loadWallpapers(Context context) {
        List<WallpaperItem> list = new ArrayList<>();
        File cacheFile = new File(context.getFilesDir(), "drive_cache.txt");
        InputStream is = null;
        try {
            if (cacheFile.exists() && cacheFile.length() > 0) {
                is = new FileInputStream(cacheFile);
            } else {
                is = context.getAssets().open("google_drive_links.txt");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            int counter = 1;
            Pattern idPattern = Pattern.compile("(?:/file/d/|id=|/d/)([a-zA-Z0-9_-]{20,})");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String title = "Wallpaper #" + counter;
                    String rawUrl = line;
                    if (line.contains("|")) {
                        String[] parts = line.split("\\|", 2);
                        title = parts[0].trim();
                        rawUrl = parts[1].trim();
                    }

                    Matcher m = idPattern.matcher(rawUrl);
                    if (m.find()) {
                        String id = m.group(1);
                        String full = "https://lh3.googleusercontent.com/d/" + id;
                        String thumb = "https://lh3.googleusercontent.com/d/" + id + "=w400";
                        list.add(new WallpaperItem(id, title, full, thumb));
                        counter++;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try { is.close(); } catch (Exception ignored) {}
            }
        }
        return list;
    }

    // --- APPLY WALLPAPER LOGIC ---
    public static void changeWallpaper(final Context context, final boolean isRandom, final int step, final WallpaperCallback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<WallpaperItem> items = loadWallpapers(context);
                    if (items.isEmpty()) {
                        if (callback != null) {
                            MAIN_HANDLER.post(new Runnable() {
                                @Override public void run() { callback.onError("No wallpapers found"); }
                            });
                        }
                        return;
                    }

                    SharedPreferences prefs = getPrefs(context);
                    int lastIndex = prefs.getInt(KEY_LAST_INDEX, -1);
                    int selectedIndex;

                    if (isRandom) {
                        // Pick random while avoiding recent history
                        String historyStr = prefs.getString(KEY_HISTORY, "");
                        Set<String> history = new HashSet<>(Arrays.asList(historyStr.split(",")));

                        List<Integer> availableIndices = new ArrayList<>();
                        for (int i = 0; i < items.size(); i++) {
                            if (!history.contains(items.get(i).id)) {
                                availableIndices.add(i);
                            }
                        }

                        // Reset history if all have been used
                        if (availableIndices.isEmpty()) {
                            history.clear();
                            for (int i = 0; i < items.size(); i++) availableIndices.add(i);
                        }

                        Collections.shuffle(availableIndices);
                        selectedIndex = availableIndices.get(0);

                        // Update history
                        history.add(items.get(selectedIndex).id);
                        if (history.size() > 50) {
                            // keep last 50
                            historyStr = items.get(selectedIndex).id;
                        } else {
                            historyStr = android.text.TextUtils.join(",", history);
                        }
                        prefs.edit().putString(KEY_HISTORY, historyStr).apply();
                    } else {
                        // Sequential Next / Prev
                        selectedIndex = (lastIndex + step + items.size()) % items.size();
                    }

                    final WallpaperItem item = items.get(selectedIndex);

                    // Download image
                    Bitmap bitmap = downloadBitmap(context, item.fullUrl, item.id);
                    if (bitmap == null) {
                        // Fallback to thumb if full failed
                        bitmap = downloadBitmap(context, item.thumbUrl, item.id + "_thumb");
                    }

                    if (bitmap != null) {
                        WallpaperManager wm = WallpaperManager.getInstance(context);
                        int target = getTarget(context);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            int flags = 0;
                            if (target == 1) flags = WallpaperManager.FLAG_SYSTEM;
                            else if (target == 2) flags = WallpaperManager.FLAG_LOCK;
                            else flags = WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK;

                            wm.setBitmap(bitmap, null, true, flags);
                        } else {
                            wm.setBitmap(bitmap);
                        }

                        prefs.edit()
                                .putInt(KEY_LAST_INDEX, selectedIndex)
                                .putString(KEY_CURRENT_TITLE, item.title)
                                .putString(KEY_CURRENT_URL, item.fullUrl)
                                .apply();

                        // Notify app UI
                        Intent notifyIntent = new Intent(ACTION_WALLPAPER_CHANGED);
                        notifyIntent.putExtra("title", item.title);
                        notifyIntent.putExtra("thumb", item.thumbUrl);
                        context.sendBroadcast(notifyIntent);

                        // Update widgets cleanly with a short delay for wallpaper colors to settle
                        MAIN_HANDLER.postDelayed(new Runnable() {
                            @Override public void run() {
                                WidgetSettingsManager.updateAllWidgets(context);
                            }
                        }, 250);

                        if (callback != null) {
                            MAIN_HANDLER.post(new Runnable() {
                                @Override public void run() { callback.onSuccess(item.title); }
                            });
                        }
                    } else {
                        if (callback != null) {
                            MAIN_HANDLER.post(new Runnable() {
                                @Override public void run() { callback.onError("Failed to download wallpaper"); }
                            });
                        }
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        MAIN_HANDLER.post(new Runnable() {
                            @Override public void run() { callback.onError(e.getMessage()); }
                        });
                    }
                }
            }
        });
    }

    private static Bitmap downloadBitmap(Context context, String urlStr, String cacheKey) {
        File cacheDir = new File(context.getCacheDir(), "auto_wallpapers");
        if (!cacheDir.exists()) cacheDir.mkdirs();
        File cacheFile = new File(cacheDir, cacheKey + ".jpg");

        if (cacheFile.exists() && cacheFile.length() > 0) {
            try {
                return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            } catch (Exception ignored) {}
        }

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");

            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(cacheFile);
                byte[] buf = new byte[8192];
                int read;
                while ((read = is.read(buf)) != -1) {
                    fos.write(buf, 0, read);
                }
                fos.close();
                is.close();

                return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- ALARM SCHEDULING ---
    public static void scheduleNext(Context context) {
        if (!isAutoEnabled(context)) return;

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AutoWallpaperReceiver.class);
        intent.setAction(ACTION_TRIGGER);
        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                901,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        int minutes = getIntervalMinutes(context);
        if (minutes < 5) minutes = 5;
        long triggerAt = System.currentTimeMillis() + (minutes * 60 * 1000L);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    public static void cancelSchedule(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AutoWallpaperReceiver.class);
        intent.setAction(ACTION_TRIGGER);
        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                901,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        am.cancel(pi);
    }
}
