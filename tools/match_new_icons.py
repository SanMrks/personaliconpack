import os
import re

with open(r'E:\Android\installed_apps_list.md', 'r', encoding='utf-8') as f:
    lines = f.readlines()

installed_apps = []
for line in lines:
    parts = [p.strip() for p in line.split('|')]
    if len(parts) >= 5 and parts[1].isdigit():
        num = parts[1]
        name = parts[2]
        pkg = parts[3].strip('`')
        draw = parts[4].strip('`')
        installed_apps.append((name, pkg, draw))

print(f"Total installed apps loaded: {len(installed_apps)}")

# List new raw files in icons/
ICONS_DIR = r"E:\Android\icons"
all_icons = os.listdir(ICONS_DIR)
new_raw = [f for f in all_icons if any(c.isupper() or c in '- ' for c in f)]
print(f"Total new raw icon files: {len(new_raw)}")

# Match new raw files against installed apps
for f in sorted(new_raw):
    base = os.path.splitext(f)[0]
    clean_base = re.sub(r'[^a-zA-Z0-9]', '', base).lower()
    matches = []
    for name, pkg, draw in installed_apps:
        clean_name = re.sub(r'[^a-zA-Z0-9]', '', name).lower()
        clean_draw = re.sub(r'[^a-zA-Z0-9]', '', draw).lower()
        if clean_base in clean_name or clean_name in clean_base or clean_base in clean_draw or clean_draw in clean_base:
            matches.append((name, pkg, draw))
    if matches:
        print(f"MATCH: '{f}' -> {[m[0] + ' (' + m[1] + ' : ' + m[2] + ')' for m in matches[:2]]}")
    else:
        print(f"NO MATCH: '{f}'")
