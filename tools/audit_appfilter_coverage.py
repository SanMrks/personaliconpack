import os
import re

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
available = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

with open(r'E:\Android\app\src\main\res\xml\appfilter.xml', 'r', encoding='utf-8') as f:
    af_text = f.read()

items = re.findall(r'<item component="([^"]+)" drawable="([^"]+)"', af_text)

# Also load all 159 activities on device
with open(r'C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt', 'r', encoding='utf-8') as f:
    dump = f.read()

acts = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', dump, re.MULTILINE)

# Map from component to drawable
comp_to_drawable = {}
for comp, d in items:
    comp_to_drawable[comp] = d

print(f"Total available drawables on disk: {len(available)}")
print(f"Total items in appfilter: {len(items)}")
print(f"Total device activities: {len(acts)}")

fully_resolved = []
unresolved = []

for act in acts:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    comp_full = f"ComponentInfo{{{pkg}/{full_cls}}}"
    comp_short = f"ComponentInfo{{{pkg}/{cls}}}"

    d = comp_to_drawable.get(comp_full) or comp_to_drawable.get(comp_short) or comp_to_drawable.get(pkg)
    if d and d in available:
        fully_resolved.append((act, d))
    else:
        unresolved.append((act, d))

print(f"\nFully resolved on device: {len(fully_resolved)} / {len(acts)}")
print(f"Unresolved on device ({len(unresolved)}):")
for act, d in unresolved:
    print(f"  {act} -> mapped to '{d}', exists on disk? {d in available if d else False}")
