import os
import re

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
MAIN_JAVA = r"E:\Android\app\src\main\java\com\ravenshade\iconpack\MainActivity.java"

with open(r'E:\Android\installed_apps_list.md', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Collect clean app list from installed_apps_list.md
app_list = []
seen_pkgs = set()
for line in lines:
    parts = [p.strip() for p in line.split('|')]
    if len(parts) >= 5 and parts[1].isdigit():
        name = parts[2]
        pkg = parts[3].strip('`')
        draw = parts[4].strip('`')
        if pkg not in seen_pkgs:
            seen_pkgs.add(pkg)
            app_list.append((name, pkg, draw))

available = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

custom_icons = []
for name, pkg, draw in app_list:
    # check if drawable exists or has an alias
    d = draw
    if d not in available:
        alt_pkg = pkg.replace('.', '_')
        if alt_pkg in available:
            d = alt_pkg
        elif d == "google_wallpapers" and "google_wallpapers" in available:
            d = "google_wallpapers"
    if d in available:
        custom_icons.append((name, pkg, d))

# Add Ravenshade itself
custom_icons.append(("Ravenshade", "com.ravenshade.iconpack", "app_icon"))

print(f"Total matching custom icons for dashboard: {len(custom_icons)}")

# Generate Java code for CUSTOM_ICONS
java_items = []
for name, pkg, draw in custom_icons:
    # Escape quotes
    safe_name = name.replace('"', '\\"')
    java_items.append(f'        new CustomIconInfo("{safe_name}", "{pkg}", "{draw}")')

replacement_code = "    private static final CustomIconInfo[] CUSTOM_ICONS = {\n" + ",\n".join(java_items) + "\n    };"

with open(MAIN_JAVA, 'r', encoding='utf-8') as f:
    code = f.read()

new_code = re.sub(r'    private static final CustomIconInfo\[\] CUSTOM_ICONS = \{.*?\n    \};', replacement_code, code, flags=re.DOTALL)

with open(MAIN_JAVA, 'w', encoding='utf-8') as f:
    f.write(new_code)

print("Updated MainActivity.java successfully!")
