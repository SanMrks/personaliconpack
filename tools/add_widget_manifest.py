path = "app/src/main/AndroidManifest.xml"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()

receiver_xml = """        <!-- 9. Raven Music Large 2 (4x2) -->
        <receiver
            android:name="com.ravenshade.iconpack.MusicLarge2WidgetProvider"
            android:exported="true"
            android:label="Music Large 2">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_PLAY_PAUSE" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_PREV" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_NEXT" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_VOL_DOWN" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_VOL_UP" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC2_OPEN" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_music_large2_info" />
        </receiver>

"""

target = "        <!-- 9. Raven Shade Music Notification & MediaSession Listener -->"
if "MusicLarge2WidgetProvider" not in c and target in c:
    c = c.replace(target, receiver_xml + target)
    with open(path, "w", encoding="utf-8") as f:
        f.write(c)
    print("Added MusicLarge2WidgetProvider to AndroidManifest.xml successfully")
else:
    print("Already added or target not found")
