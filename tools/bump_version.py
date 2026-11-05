import re

with open("app/src/main/AndroidManifest.xml", "r", encoding="utf-8") as f:
    c = f.read()
c = re.sub(r'android:versionCode="\d+"', 'android:versionCode="30"', c)
c = re.sub(r'android:versionName="[^"]+"', 'android:versionName="5.1"', c)
with open("app/src/main/AndroidManifest.xml", "w", encoding="utf-8") as f:
    f.write(c)

with open("build_apk.bat", "r", encoding="utf-8") as f:
    b = f.read()
b = re.sub(r'--version-code \d+ --version-name "[^"]+"', '--version-code 30 --version-name "5.1"', b)
b = re.sub(r'Raven Shade Liquid Glass Customization Suite v[0-9.]+', 'Raven Shade Liquid Glass Customization Suite v5.1', b)
with open("build_apk.bat", "w", encoding="utf-8") as f:
    f.write(b)

print("Bumped version to 30 / 5.1 successfully!")
