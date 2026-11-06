path = "app/src/main/java/com/ravenshade/iconpack/WidgetSettingsManager.java"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()

target = """            // 7. Weather Widget (2x2)
            ComponentName weatherComp = new ComponentName(context, WeatherWidgetProvider.class);
            int[] weatherIds = manager.getAppWidgetIds(weatherComp);
            for (int id : weatherIds) {
                WeatherWidgetProvider.updateAppWidget(context, manager, id);
            }"""

replacement = """            // 7. Weather Widget (2x2)
            ComponentName weatherComp = new ComponentName(context, WeatherWidgetProvider.class);
            int[] weatherIds = manager.getAppWidgetIds(weatherComp);
            for (int id : weatherIds) {
                WeatherWidgetProvider.updateAppWidget(context, manager, id);
            }

            // 8. Music Large 2 (4x2 Minimalist Glass)
            ComponentName musicLarge2Comp = new ComponentName(context, MusicLarge2WidgetProvider.class);
            int[] musicLarge2Ids = manager.getAppWidgetIds(musicLarge2Comp);
            for (int id : musicLarge2Ids) {
                MusicLarge2WidgetProvider.updateAppWidget(context, manager, id);
            }"""

if target in c:
    c = c.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(c)
    print("Updated WidgetSettingsManager.java")
else:
    print("Target not found in WidgetSettingsManager.java")
