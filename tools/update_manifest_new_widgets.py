with open('app/src/main/AndroidManifest.xml', 'r', encoding='utf-8') as f:
    c = f.read()

new_receivers = '''        <!-- 5. Ravenshade Music Widget (4x2) -->
        <receiver
            android:name="com.ravenshade.iconpack.MusicWidgetProvider"
            android:exported="true"
            android:label="Raven Music">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC_PLAY_PAUSE" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC_PREV" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC_NEXT" />
                <action android:name="com.ravenshade.iconpack.ACTION_MUSIC_OPEN" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_music_info" />
        </receiver>

        <!-- 6. Ravenshade Wallpaper Shuffle Widget (1x1) -->
        <receiver
            android:name="com.ravenshade.iconpack.WallpaperShuffleWidgetProvider"
            android:exported="true"
            android:label="Shuffle Wallpaper">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.ravenshade.iconpack.ACTION_SHUFFLE_WALLPAPER_WIDGET" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_wallpaper_shuffle_info" />
        </receiver>
    </application>'''

c = c.replace('    </application>', new_receivers)

with open('app/src/main/AndroidManifest.xml', 'w', encoding='utf-8') as f:
    f.write(c)

print('Updated AndroidManifest.xml successfully!')
