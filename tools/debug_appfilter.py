import re
import os

with open(r'E:\Android\app\src\main\res\xml\appfilter.xml', 'r', encoding='utf-8') as f:
    content = f.read()

items = re.findall(r'<item component="([^"]+)" drawable="([^"]+)"', content)
print(f"Total items in appfilter: {len(items)}")

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
drawables_on_disk = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

missing_drawables = set()
for comp, d in items:
    if d not in drawables_on_disk:
        missing_drawables.add(d)

print(f"Items pointing to missing drawables on disk: {len(missing_drawables)}")
if missing_drawables:
    print("Missing drawables:", missing_drawables)

# Also check 159 device activities from step 1704 dump
with open(r'C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt', 'r', encoding='utf-8') as f:
    dump = f.read()

device_acts = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', dump, re.MULTILINE)
print(f"Total device activities in dump: {len(device_acts)}")

mapped_device_acts = 0
unmapped_device_acts = []
for act in device_acts:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    matched = False
    for comp, d in items:
        if full_cls in comp or cls in comp or comp == pkg:
            matched = True
            break
    if matched:
        mapped_device_acts += 1
    else:
        unmapped_device_acts.append(act)

print(f"Mapped device activities: {mapped_device_acts} / {len(device_acts)}")
print(f"Unmapped device activities ({len(unmapped_device_acts)}):")
for u in unmapped_device_acts:
    print("  UNMAPPED:", u)
