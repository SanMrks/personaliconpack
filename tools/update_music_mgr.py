path = "app/src/main/java/com/ravenshade/iconpack/RavenMusicManager.java"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()

target = """            // 2. 2x2 Music Widget
            ComponentName comp2x2 = new ComponentName(context, MusicWidget2x2Provider.class);
            int[] ids2x2 = manager.getAppWidgetIds(comp2x2);
            for (int id : ids2x2) {
                MusicWidget2x2Provider.updateAppWidget(context, manager, id);
            }"""

replacement = """            // 2. 2x2 Music Widget
            ComponentName comp2x2 = new ComponentName(context, MusicWidget2x2Provider.class);
            int[] ids2x2 = manager.getAppWidgetIds(comp2x2);
            for (int id : ids2x2) {
                MusicWidget2x2Provider.updateAppWidget(context, manager, id);
            }

            // 3. Music Large 2 (4x2 Minimalist Glass)
            ComponentName compLarge2 = new ComponentName(context, MusicLarge2WidgetProvider.class);
            int[] idsLarge2 = manager.getAppWidgetIds(compLarge2);
            for (int id : idsLarge2) {
                MusicLarge2WidgetProvider.updateAppWidget(context, manager, id);
            }"""

if target in c:
    c = c.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(c)
    print("Updated RavenMusicManager.java")
else:
    print("Target not found in RavenMusicManager.java")
