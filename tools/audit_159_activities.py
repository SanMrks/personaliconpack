import re

with open(r'C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt', 'r', encoding='utf-8') as f:
    text = f.read()

device_acts = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', text, re.MULTILINE)

with open(r'E:\Android\app\src\main\res\xml\appfilter.xml', 'r', encoding='utf-8') as f:
    af_text = f.read()

items = re.findall(r'<item component="([^"]+)" drawable="([^"]+)"', af_text)
item_map = {}
for comp, d in items:
    item_map[comp] = d

unmapped = []
mapped = []

for act in device_acts:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    comp_full = f"ComponentInfo{{{pkg}/{full_cls}}}"
    comp_short = f"ComponentInfo{{{pkg}/{cls}}}"

    d = item_map.get(comp_full) or item_map.get(comp_short) or item_map.get(pkg)
    if d:
        mapped.append((act, d))
    else:
        unmapped.append(act)

print(f"Total on-device activities: {len(device_acts)}")
print(f"Mapped: {len(mapped)}")
print(f"Unmapped: {len(unmapped)}")
print("\nUnmapped activities:")
for u in unmapped:
    print("  ", u)
