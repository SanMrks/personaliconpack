with open('app/src/main/AndroidManifest.xml', 'r', encoding='utf-8') as f:
    c = f.read()

perm_target = '<uses-permission android:name="android.permission.SET_WALLPAPER" />'
perm_addition = '''<uses-permission android:name="android.permission.SET_WALLPAPER" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />'''
c = c.replace(perm_target, perm_addition)

receiver_block = '''        <!-- 4. Raven Shade Automatic Wallpaper Receiver -->
        <receiver
            android:name="com.ravenshade.iconpack.AutoWallpaperReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="com.ravenshade.iconpack.ACTION_AUTO_WALLPAPER_TRIGGER" />
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>'''
c = c.replace('    </application>', receiver_block)

with open('app/src/main/AndroidManifest.xml', 'w', encoding='utf-8') as f:
    f.write(c)
print('Updated AndroidManifest.xml successfully')
