import os
import re

with open(r'E:\Android\installed_apps_underscores.txt', 'r', encoding='utf-8') as f:
    lines = f.readlines()

targets = []
for line in lines:
    line = line.strip()
    if not line:
        continue
    # Format: "1. app_mihon_debug (Mihon)"
    m = re.match(r'^\d+\.\s*([a-zA-Z0-9_]+)\s*\((.*)\)$', line)
    if m:
        target_name = m.group(1).lower()
        app_title = m.group(2)
        targets.append((target_name, app_title))

print(f"Total targets from installed_apps_underscores.txt: {len(targets)}")

ICONS_DIR = r"E:\Android\icons"
existing_icons = set(os.listdir(ICONS_DIR))

found = 0
missing = []

for target_name, app_title in targets:
    fname = f"{target_name}.png"
    if fname in existing_icons:
        found += 1
    else:
        missing.append((target_name, app_title))

print(f"Direct exact match in icons/: {found} / {len(targets)}")
if missing:
    print(f"Missing ({len(missing)}):")
    for t, title in missing:
        print(f"  {t} ({title})")
