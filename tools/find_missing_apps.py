import os
import re

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
drawables = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

with open(r'E:\Android\installed_apps_list.md', 'r', encoding='utf-8') as f:
    lines = f.readlines()

installed_apps = []
for line in lines:
    parts = [p.strip() for p in line.split('|')]
    if len(parts) >= 5 and parts[1].isdigit():
        num = int(parts[1])
        name = parts[2]
        pkg = parts[3].strip('`')
        draw = parts[4].strip('`')
        installed_apps.append((num, name, pkg, draw))

missing_apps = []
themed_apps = []

for num, name, pkg, draw in installed_apps:
    pkg_draw = pkg.replace('.', '_')
    if draw in drawables or pkg_draw in drawables:
        themed_apps.append((num, name, pkg, draw))
    else:
        missing_apps.append((num, name, pkg, draw))

print(f"Total apps in installed list: {len(installed_apps)}")
print(f"Themed apps: {len(themed_apps)}")
print(f"Missing apps ({len(missing_apps)}):")
for num, name, pkg, draw in missing_apps:
    print(f"  #{num}: {name} ({pkg}) -> needs '{draw}'")
