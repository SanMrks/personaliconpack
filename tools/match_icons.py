import os, re

# Read installed_apps_list.md
apps = []
with open(r"E:\Android\installed_apps_list.md", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line.startswith("|") and not line.startswith("| #") and not line.startswith("|---"):
            parts = [p.strip() for p in line.split("|")[1:-1]]
            if len(parts) >= 4:
                num = parts[0]
                name = parts[1]
                pkg = parts[2].replace("`", "")
                drawable = parts[3].replace("`", "")
                apps.append((num, name, pkg, drawable))

print(f"Loaded {len(apps)} apps from installed_apps_list.md")

# Icon files in icons folder
icons = os.listdir(r"E:\Android\icons")
print(f"Total icon files: {len(icons)}")

# Also read appfilter_user_apps.xml to get exact component names
components = {}
with open(r"E:\Android\appfilter_user_apps.xml", "r", encoding="utf-8") as f:
    for line in f:
        m = re.search(r'component="([^"]+)"', line)
        if m:
            comp = m.group(1)
            # extract package
            pkg_m = re.search(r'ComponentInfo{([^/]+)/', comp)
            if pkg_m:
                pkg = pkg_m.group(1)
                components[pkg] = comp

print(f"Loaded {len(components)} launch components from appfilter_user_apps.xml")

for ic in sorted(icons):
    clean = ic.replace(".png", "").replace("-", " ").lower()
    # Find match in apps
    best_match = None
    for num, name, pkg, drawable in apps:
        name_clean = name.lower()
        if clean in name_clean or name_clean in clean or drawable.replace("_", " ") in clean:
            best_match = (num, name, pkg, drawable)
            break
        # word matches
        clean_words = set(clean.split())
        name_words = set(name_clean.split())
        if clean_words and clean_words.issubset(name_words):
            best_match = (num, name, pkg, drawable)
            break

    print(f"{ic:25} -> {best_match if best_match else 'NO MATCH'}")
