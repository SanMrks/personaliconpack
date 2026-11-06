with open("app/src/main/AndroidManifest.xml", "r", encoding="utf-8") as f:
    c = f.read()
c = c.replace('versionCode="35"', 'versionCode="36"')
c = c.replace('versionName="5.6"', 'versionName="5.7"')
with open("app/src/main/AndroidManifest.xml", "w", encoding="utf-8") as f:
    f.write(c)
print("Updated AndroidManifest.xml successfully")
