import os
import shutil

ICONS_DIR = r"E:\Android\icons"
RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"

# Create aliases for remaining apps
PAIRS = [
    ("instagram_lite.png", "instagram.png"),
    ("instagram_lite.png", "com_instagram_android.png"),
    ("fadocx.png", "facebook.png"),
    ("fadocx.png", "com_facebook_katana.png"),
    ("where_is_my_train.png", "mparivahan.png"),
    ("where_is_my_train.png", "com_nic_mparivahan.png"),
    ("the_arcade.png", "zjcs_app.png"),
    ("the_arcade.png", "com_zjcs_android_us.png"),
    ("the_arcade.png", "rollerbush.png"),
    ("the_arcade.png", "com_rollerbush_games.png"),
    ("youtube.png", "youtube_morphe.png"),
    ("youtube.png", "app_morphe_android_youtube.png"),
]

for src, dst in PAIRS:
    for folder in [ICONS_DIR, RES_DIR]:
        src_p = os.path.join(folder, src)
        dst_p = os.path.join(folder, dst)
        if os.path.exists(src_p):
            shutil.copy2(src_p, dst_p)
            print(f"Copied {src} -> {dst} in {folder}")

print("Done creating aliases!")
