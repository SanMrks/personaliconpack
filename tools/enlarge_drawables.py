import os
from PIL import Image

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
all_pngs = [f for f in os.listdir(RES_DIR) if f.endswith(".png")]
print(f"Enlarging {len(all_pngs)} drawables by 5%...")

scale = 1.05  # 5% zoom to expand squircle and artwork to edges

for f in all_pngs:
    p = os.path.join(RES_DIR, f)
    try:
        im = Image.open(p).convert("RGBA")
        w, h = im.size
        new_w = int(w * scale)
        new_h = int(h * scale)
        scaled = im.resize((new_w, new_h), Image.Resampling.LANCZOS)
        left = (new_w - w) // 2
        top = (new_h - h) // 2
        cropped = scaled.crop((left, top, left + w, top + h))
        cropped.save(p, format="PNG", optimize=True)
    except Exception as e:
        print(f"Error on {f}: {e}")

print("All drawables enlarged successfully by 5%!")
