import os
import re

layouts = [
    "widget_black_raven.xml",
    "widget_anime_clock.xml",
    "widget_music.xml",
    "widget_music_2x2.xml",
    "widget_quick_dock.xml",
    "widget_wallpaper_shuffle.xml",
    "widget_weather.xml"
]

dir_path = "app/src/main/res/layout"

for name in layouts:
    p = os.path.join(dir_path, name)
    if os.path.exists(p):
        with open(p, "r", encoding="utf-8") as f:
            c = f.read()
        # Replace android:background="@drawable/..." on root FrameLayout with @android:color/transparent
        # We match android:background="@drawable/bg_widget_..."
        c_new = re.sub(
            r'(<FrameLayout[^>]*?)android:background="@drawable/bg_widget_[^"]+"',
            r'\1android:background="@android:color/transparent"',
            c,
            count=1
        )
        if c_new != c:
            with open(p, "w", encoding="utf-8") as f:
                f.write(c_new)
            print(f"Updated {name} to single layer")
        else:
            print(f"No match in {name}")
