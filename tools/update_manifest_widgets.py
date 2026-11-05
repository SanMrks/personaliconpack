import re

with open("app/src/main/AndroidManifest.xml", "r", encoding="utf-8") as f:
    text = f.read()

receivers = """
        <!-- 1. Ravenshade Black Raven Widget (4x2 / 4x1) -->
        <receiver
            android:name="com.ravenshade.iconpack.BlackRavenWidgetProvider"
            android:exported="true"
            android:label="Black Raven Clock">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="android.intent.action.BATTERY_CHANGED" />
                <action android:name="android.intent.action.TIME_TICK" />
                <action android:name="android.intent.action.TIME_SET" />
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_black_raven_info" />
        </receiver>

        <!-- 2. Ravenshade Quick Dock Widget (4x1) -->
        <receiver
            android:name="com.ravenshade.iconpack.QuickDockWidgetProvider"
            android:exported="true"
            android:label="Ravenshade Dock">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_quick_dock_info" />
        </receiver>

        <!-- 3. Ravenshade Anime Clock Mini Widget (2x2) -->
        <receiver
            android:name="com.ravenshade.iconpack.AnimeClockWidgetProvider"
            android:exported="true"
            android:label="Ravenshade Mini Clock">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="android.intent.action.BATTERY_CHANGED" />
                <action android:name="android.intent.action.TIME_TICK" />
                <action android:name="android.intent.action.TIME_SET" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_anime_clock_info" />
        </receiver>
"""

pos = text.rfind("</application>")
if pos != -1:
    new_text = text[:pos] + receivers + "\n    " + text[pos:]
    with open("app/src/main/AndroidManifest.xml", "w", encoding="utf-8") as f:
        f.write(new_text)
    print("Added widget receivers to AndroidManifest.xml successfully!")
