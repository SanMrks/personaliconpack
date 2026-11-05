package com.ravenshade.iconpack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RavenWeatherManager
 * Provides live weather conditions and forecasts via Open-Meteo free API
 * with instant offline cached fallbacks.
 */
public class RavenWeatherManager {

    private static final String PREF_NAME = "raven_weather_data";
    private static final String KEY_TEMP = "weather_temp";
    private static final String KEY_HIGH = "weather_high";
    private static final String KEY_LOW = "weather_low";
    private static final String KEY_CONDITION = "weather_condition";
    private static final String KEY_ICON_NAME = "weather_icon_name";
    private static final String KEY_LAST_FETCH = "weather_last_fetch";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getTemperature(Context context) {
        return getPrefs(context).getInt(KEY_TEMP, 28);
    }

    public static int getHigh(Context context) {
        return getPrefs(context).getInt(KEY_HIGH, 31);
    }

    public static int getLow(Context context) {
        return getPrefs(context).getInt(KEY_LOW, 21);
    }

    public static String getCondition(Context context) {
        return getPrefs(context).getString(KEY_CONDITION, "Partly Cloudy");
    }

    public static String getIconName(Context context) {
        return getPrefs(context).getString(KEY_ICON_NAME, "ic_weather_cloudy");
    }

    /**
     * Refreshes weather in background if last fetch was > 20 mins ago or forced
     */
    public static void refreshWeatherAsync(final Context context, boolean force) {
        long last = getPrefs(context).getLong(KEY_LAST_FETCH, 0);
        long now = System.currentTimeMillis();
        if (!force && (now - last < 20 * 60 * 1000)) {
            return; // within cache window
        }

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Open-Meteo free API for Bengaluru coordinates (or standard location)
                    // Latitude: 12.9716, Longitude: 77.5946
                    URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=12.9716&longitude=77.5946&current_weather=true&daily=temperature_2m_max,temperature_2m_min&timezone=auto");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(10000);
                    conn.setRequestProperty("User-Agent", "RavenShade/5.6");

                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) sb.append(line);
                        reader.close();

                        JSONObject json = new JSONObject(sb.toString());
                        JSONObject current = json.optJSONObject("current_weather");
                        if (current != null) {
                            double tempC = current.optDouble("temperature", 28.0);
                            int code = current.optInt("weathercode", 1);
                            int isDay = current.optInt("is_day", 1);

                            JSONObject daily = json.optJSONObject("daily");
                            double maxC = tempC + 3;
                            double minC = tempC - 7;
                            if (daily != null) {
                                if (daily.optJSONArray("temperature_2m_max") != null) {
                                    maxC = daily.optJSONArray("temperature_2m_max").optDouble(0, maxC);
                                }
                                if (daily.optJSONArray("temperature_2m_min") != null) {
                                    minC = daily.optJSONArray("temperature_2m_min").optDouble(0, minC);
                                }
                            }

                            String cond = parseWeatherCode(code);
                            String icon = parseWeatherIcon(code, isDay == 1);

                            getPrefs(context).edit()
                                    .putInt(KEY_TEMP, (int) Math.round(tempC))
                                    .putInt(KEY_HIGH, (int) Math.round(maxC))
                                    .putInt(KEY_LOW, (int) Math.round(minC))
                                    .putString(KEY_CONDITION, cond)
                                    .putString(KEY_ICON_NAME, icon)
                                    .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
                                    .apply();

                            MAIN_HANDLER.post(new Runnable() {
                                @Override public void run() {
                                    WidgetSettingsManager.updateAllWidgets(context);
                                }
                            });
                        }
                    }
                } catch (Exception ignored) {
                    // Gracefully keep cached values
                }
            }
        });
    }

    private static String parseWeatherCode(int code) {
        if (code == 0) return "Clear Sky";
        if (code == 1) return "Mainly Clear";
        if (code == 2) return "Partly Cloudy";
        if (code == 3) return "Overcast";
        if (code == 45 || code == 48) return "Obsidian Fog";
        if (code >= 51 && code <= 55) return "Light Drizzle";
        if (code >= 61 && code <= 65) return "Crimson Rain";
        if (code >= 71 && code <= 77) return "Crystal Snow";
        if (code >= 80 && code <= 82) return "Passing Showers";
        if (code >= 95) return "Thunderstorm";
        return "Obsidian Breeze";
    }

    private static String parseWeatherIcon(int code, boolean isDay) {
        if (code == 0) return isDay ? "ic_weather_sunny" : "ic_weather_night";
        if (code == 1 || code == 2) return isDay ? "ic_weather_cloudy" : "ic_weather_night";
        if (code == 3 || code == 45 || code == 48) return "ic_weather_cloudy";
        if (code >= 51 && code <= 67 || code >= 80 && code <= 82) return "ic_weather_rain";
        if (code >= 95) return "ic_weather_thunder";
        return "ic_weather_cloudy";
    }
}
