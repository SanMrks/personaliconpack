import re

# 1. BlackRavenWidgetProvider.java
p_raven = "app/src/main/java/com/ravenshade/iconpack/BlackRavenWidgetProvider.java"
with open(p_raven, "r", encoding="utf-8") as f:
    code_raven = f.read()

code_raven = code_raven.replace(
    "createLiquidGlassBitmap(context, 750, 360, 28)",
    "createLiquidGlassBitmap(context, 360, 180, 28)"
)
code_raven = code_raven.replace(
    'int idTag = context.getResources().getIdentifier("widget_subtitle", "id", pkg);',
    'int idTag = context.getResources().getIdentifier("widget_tag", "id", pkg);'
)

old_bat = """            int idBatPct = context.getResources().getIdentifier("widget_battery_pct", "id", pkg);
            int idBatBar = context.getResources().getIdentifier("widget_battery_bar", "id", pkg);
            int idBatFlipper = context.getResources().getIdentifier("widget_battery_flipper", "id", pkg);"""

new_bat = """            int idBatPct = context.getResources().getIdentifier("widget_battery", "id", pkg);
            if (idBatPct == 0) idBatPct = context.getResources().getIdentifier("widget_battery_pct", "id", pkg);
            int idBatBar = context.getResources().getIdentifier("widget_battery_progress", "id", pkg);
            if (idBatBar == 0) idBatBar = context.getResources().getIdentifier("widget_battery_bar", "id", pkg);
            int idBatFlipper = context.getResources().getIdentifier("widget_charging_anim", "id", pkg);
            if (idBatFlipper == 0) idBatFlipper = context.getResources().getIdentifier("widget_battery_flipper", "id", pkg);"""

code_raven = code_raven.replace(old_bat, new_bat)

with open(p_raven, "w", encoding="utf-8") as f:
    f.write(code_raven)
print("Updated BlackRavenWidgetProvider.java")

# 2. AnimeClockWidgetProvider.java
p_anime = "app/src/main/java/com/ravenshade/iconpack/AnimeClockWidgetProvider.java"
with open(p_anime, "r", encoding="utf-8") as f:
    code_anime = f.read()

code_anime = code_anime.replace(
    "createLiquidGlassBitmap(context, 400, 400, 28)",
    "createLiquidGlassBitmap(context, 200, 200, 28)"
)
with open(p_anime, "w", encoding="utf-8") as f:
    f.write(code_anime)
print("Updated AnimeClockWidgetProvider.java")

# 3. MusicWidgetProvider.java
p_music = "app/src/main/java/com/ravenshade/iconpack/MusicWidgetProvider.java"
with open(p_music, "r", encoding="utf-8") as f:
    code_music = f.read()

code_music = code_music.replace(
    "createLiquidGlassBitmap(context, 750, 360, 28)",
    "createLiquidGlassBitmap(context, 360, 180, 28)"
)
with open(p_music, "w", encoding="utf-8") as f:
    f.write(code_music)
print("Updated MusicWidgetProvider.java")

# 4. MusicWidget2x2Provider.java
p_music2 = "app/src/main/java/com/ravenshade/iconpack/MusicWidget2x2Provider.java"
with open(p_music2, "r", encoding="utf-8") as f:
    code_music2 = f.read()

code_music2 = code_music2.replace(
    "createLiquidGlassBitmap(context, 400, 400, 28)",
    "createLiquidGlassBitmap(context, 200, 200, 28)"
)
with open(p_music2, "w", encoding="utf-8") as f:
    f.write(code_music2)
print("Updated MusicWidget2x2Provider.java")

# 5. QuickDockWidgetProvider.java
p_dock = "app/src/main/java/com/ravenshade/iconpack/QuickDockWidgetProvider.java"
with open(p_dock, "r", encoding="utf-8") as f:
    code_dock = f.read()

code_dock = code_dock.replace(
    "createLiquidGlassBitmap(context, 750, 180, 34)",
    "createLiquidGlassBitmap(context, 360, 90, 34)"
)
with open(p_dock, "w", encoding="utf-8") as f:
    f.write(code_dock)
print("Updated QuickDockWidgetProvider.java")

# 6. WallpaperShuffleWidgetProvider.java
p_shuffle = "app/src/main/java/com/ravenshade/iconpack/WallpaperShuffleWidgetProvider.java"
with open(p_shuffle, "r", encoding="utf-8") as f:
    code_shuffle = f.read()

code_shuffle = code_shuffle.replace(
    "createLiquidGlassCircle(context, 300)",
    "createLiquidGlassCircle(context, 160)"
)
code_shuffle = code_shuffle.replace(
    "Toast.makeText(context, \"✓ Applied: \" + title, Toast.LENGTH_SHORT).show();\n                    WidgetSettingsManager.updateAllWidgets(context);",
    "Toast.makeText(context, \"✓ Applied: \" + title, Toast.LENGTH_SHORT).show();"
)
with open(p_shuffle, "w", encoding="utf-8") as f:
    f.write(code_shuffle)
print("Updated WallpaperShuffleWidgetProvider.java")

# 7. WeatherWidgetProvider.java
p_weather = "app/src/main/java/com/ravenshade/iconpack/WeatherWidgetProvider.java"
with open(p_weather, "r", encoding="utf-8") as f:
    code_weather = f.read()

code_weather = code_weather.replace(
    "createLiquidGlassBitmap(context, 400, 400, 28)",
    "createLiquidGlassBitmap(context, 200, 200, 28)"
)
with open(p_weather, "w", encoding="utf-8") as f:
    f.write(code_weather)
print("Updated WeatherWidgetProvider.java")

# 8. WidgetSettingsManager.java
p_settings = "app/src/main/java/com/ravenshade/iconpack/WidgetSettingsManager.java"
with open(p_settings, "r", encoding="utf-8") as f:
    code_settings = f.read()

# remove broadcast update
old_broadcast = """            // Broadcast update
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ravenIds);
            context.sendBroadcast(updateIntent);"""

code_settings = code_settings.replace(old_broadcast, "// Widgets updated directly above")
with open(p_settings, "w", encoding="utf-8") as f:
    f.write(code_settings)
print("Updated WidgetSettingsManager.java")
