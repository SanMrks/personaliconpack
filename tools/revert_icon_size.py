import os
import re
import shutil
import time

ROOT_DIR = r"E:\Android"
ICONS_DIR = os.path.join(ROOT_DIR, "icons")
BACKUP_DIR = os.path.join(ROOT_DIR, "icons_backup")
RES_NODPI = os.path.join(ROOT_DIR, "app", "src", "main", "res", "drawable-nodpi")
RES_XXXHDPI = os.path.join(ROOT_DIR, "app", "src", "main", "res", "drawable-xxxhdpi")
RES_XML = os.path.join(ROOT_DIR, "app", "src", "main", "res", "xml")
ASSETS_DIR = os.path.join(ROOT_DIR, "app", "src", "main", "assets")
MANIFEST_XML = os.path.join(ROOT_DIR, "app", "src", "main", "AndroidManifest.xml")
BUILD_BAT = os.path.join(ROOT_DIR, "build_apk.bat")

# Import ALL_MAPPINGS from apply_user_underscore_renaming.py
from apply_user_underscore_renaming import ALL_MAPPINGS

print("==================================================")
print("Reverting Icon Sizes to Original (1.0x Scale, Uncropped)")
print("==================================================")

# Step 1: Copy original unscaled files from icons_backup
os.makedirs(RES_NODPI, exist_ok=True)
os.makedirs(RES_XXXHDPI, exist_ok=True)

# Clear existing icons
for f in os.listdir(ICONS_DIR):
    p = os.path.join(ICONS_DIR, f)
    if os.path.isfile(p):
        os.remove(p)

count = 0
for target_name, src_filename in ALL_MAPPINGS.items():
    src_path = os.path.join(BACKUP_DIR, src_filename)
    if not os.path.exists(src_path):
        print(f"Warning: Source not found: {src_filename}")
        continue

    target_file = f"{target_name}.png"
    dst_icon = os.path.join(ICONS_DIR, target_file)
    dst_nodpi = os.path.join(RES_NODPI, target_file)
    dst_xxxhdpi = os.path.join(RES_XXXHDPI, target_file)

    shutil.copy2(src_path, dst_icon)
    shutil.copy2(src_path, dst_nodpi)
    shutil.copy2(src_path, dst_xxxhdpi)
    count += 1

# Ensure app_icon.png exists
app_icon_src = os.path.join(ICONS_DIR, "com_ravenshade_iconpack.png")
if not os.path.exists(app_icon_src):
    app_icon_src = os.path.join(ICONS_DIR, "com_nothing_essentialapps.png")
if os.path.exists(app_icon_src):
    shutil.copy2(app_icon_src, os.path.join(ICONS_DIR, "app_icon.png"))
    shutil.copy2(app_icon_src, os.path.join(RES_NODPI, "app_icon.png"))
    shutil.copy2(app_icon_src, os.path.join(RES_XXXHDPI, "app_icon.png"))

print(f"Restored {count} original unscaled icons!")

# Step 2: Revert scale factor in appfilter.xml to 1.0
for af_path in [os.path.join(RES_XML, "appfilter.xml"), os.path.join(ASSETS_DIR, "appfilter.xml")]:
    with open(af_path, 'r', encoding='utf-8') as f:
        content = f.read()
    content = re.sub(r'<scale factor="[^"]*" />', '<scale factor="1.0" />', content)
    with open(af_path, 'w', encoding='utf-8') as f:
        f.write(content)
print("Reverted appfilter.xml scale factor to 1.0!")

# Step 3: Revert scale factor in theme_resources.xml to 1.0
for tr_path in [os.path.join(RES_XML, "theme_resources.xml"), os.path.join(ASSETS_DIR, "theme_resources.xml")]:
    if os.path.exists(tr_path):
        with open(tr_path, 'r', encoding='utf-8') as f:
            content = f.read()
        content = re.sub(r'<scale factor="[^"]*" />', '<scale factor="1.0" />', content)
        with open(tr_path, 'w', encoding='utf-8') as f:
            f.write(content)
print("Reverted theme_resources.xml scale factor to 1.0!")

# Step 4: Bump version to versionCode="26", versionName="4.2"
with open(MANIFEST_XML, 'r', encoding='utf-8') as f:
    m_content = f.read()
m_content = re.sub(r'android:versionCode="\d+"', 'android:versionCode="26"', m_content)
m_content = re.sub(r'android:versionName="[^"]+"', 'android:versionName="4.2"', m_content)
with open(MANIFEST_XML, 'w', encoding='utf-8') as f:
    f.write(m_content)

with open(BUILD_BAT, 'r', encoding='utf-8') as f:
    b_content = f.read()
b_content = re.sub(r'--version-code \d+ --version-name "[^"]+"', '--version-code 26 --version-name "4.2"', b_content)
b_content = re.sub(r'Ravenshade Universal Icon Pack APK v[0-9.]+', 'Ravenshade Universal Icon Pack APK v4.2', b_content)
with open(BUILD_BAT, 'w', encoding='utf-8') as f:
    f.write(b_content)

print("Bumped version to versionCode=26, versionName=4.2 in Manifest and build script!")
print("Reversion complete. Ready to build!")
