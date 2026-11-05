import os
import shutil
import re

ICONS_DIR = r"E:\Android\icons"
RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"

# 1. Remove bad duplicate file if exists
bad_dup = os.path.join(ICONS_DIR, "org_codeaurora_snapcam (2).png")
if os.path.exists(bad_dup):
    os.remove(bad_dup)
    print("Removed duplicate file:", bad_dup)

# 2. Rename map for uppercase/hyphenated files in icons/
RENAME_MAP = {
    "Facebook.png": "facebook.png",
    "Fadocx.png": "fadocx.png",
    "Family-Link.png": "family_link.png",
    "Files.png": "files.png",
    "Find-Hub.png": "find_hub.png",
    "GPay.png": "gpay.png",
    "Gemini.png": "gemini.png",
    "Gmail.png": "gmail.png",
    "Google-Drive.png": "google_drive.png",
    "Google-One.png": "google_one.png",
    "Google-TV.png": "google_tv.png",
    "Google.png": "google.png",
    "Home.png": "google_home.png",
    "com_levelinfinite_sgameGlobal.png": "com_levelinfinite_sgameglobal.png",
}

for f in os.listdir(ICONS_DIR):
    for old_name, new_name in RENAME_MAP.items():
        if f.lower() == old_name.lower():
            old_p = os.path.join(ICONS_DIR, f)
            temp_p = os.path.join(ICONS_DIR, f"temp_{new_name}")
            new_p = os.path.join(ICONS_DIR, new_name)
            if f != new_name:
                shutil.move(old_p, temp_p)
                if os.path.exists(new_p):
                    os.remove(new_p)
                shutil.move(temp_p, new_p)
                print(f"Renamed: {f} -> {new_name}")

# 3. Comprehensive alias pairs (src -> dst). If src exists, copy to dst in both ICONS_DIR and RES_DIR
ALIASES = [
    # Google suite
    ("google.png", "com_google_android_googlequicksearchbox.png"),
    ("google_drive.png", "com_google_android_apps_docs.png"),
    ("gmail.png", "com_google_android_gm.png"),
    ("gemini.png", "com_google_android_apps_bard.png"),
    ("gpay.png", "google_pay.png"),
    ("gpay.png", "com_google_android_apps_nbu_paisa_user.png"),
    ("google_home.png", "com_google_android_apps_chromecast_app.png"),
    ("google_one.png", "com_google_android_apps_subscriptions_red.png"),
    ("google_tv.png", "com_google_android_videos.png"),
    ("family_link.png", "com_google_android_apps_kids_familylink.png"),
    ("files.png", "com_google_android_apps_nbu_files.png"),
    ("find_hub.png", "find_my_device.png"),
    ("find_hub.png", "com_google_android_apps_adm.png"),
    ("com_google_android_keep.png", "keep.png"),
    ("com_google_android_keep.png", "google_keep.png"),

    # Social & Media
    ("facebook.png", "com_facebook_katana.png"),
    ("fadocx.png", "com_fadseclab_fadocx.png"),
    ("com_flare_filmcam.png", "filmcam.png"),
    ("com_flipkart_android.png", "flipkart.png"),
    ("com_instagram_lite.png", "instagram_lite.png"),
    ("com_jlab_app.png", "jlab.png"),
    ("com_jpl_jiomart.png", "jiomart.png"),
    ("in_mobility_keralasavaariconsumer.png", "kerala_savaari.png"),
    ("in_startv_hotstar.png", "hotstar.png"),

    # Games
    ("com_aionyxe_kyng.png", "kyng.png"),
    ("com_amkette_evofoxdojo.png", "evofoxdojo.png"),
    ("com_epicgames_portal.png", "epicgames.png"),
    ("epicgames.png", "com_epicgames_portal.png"),
    ("com_levelinfinite_sgameglobal.png", "honor_of_kings.png"),
    ("com_playdigious_evoland2_epic.png", "evoland2.png"),
    ("com_swordfish_lemuroid.png", "lemuroid.png"),
    ("com_teampacheworks_hades2.png", "hades2.png"),
    ("com_xd_etheria_gp_glb.png", "etheria.png"),

    # Tools & Utilities
    ("ginlemon_iconpackstudio.png", "icon_pack_studio.png"),
    ("org_fdroid_fdroid.png", "fdroid.png"),
    ("org_kustom_widget.png", "kwgt.png"),
    ("org_kustom_widget_pro.png", "kwgt_pro.png"),
    ("ru_tech_imageresizershrinker.png", "image_toolbox.png"),
    ("tplink_aginet.png", "com_tplink_aginet.png"),

    # Nothing OS specific
    ("com_nothing_gallery.png", "nothing_gallery.png"),
    ("com_nothing_essentialapps.png", "essentialapps.png"),
    ("essentialapps.png", "nothing_essential.png"),
    ("essentialapps.png", "app_icon.png"),
    ("essentialapps.png", "com_nothing_essentialapps.png"),
    ("nothing_camera.png", "com_nothing_camera.png"),

    # Previously mapped apps that user requested
    ("alexa.png", "com_amazon_dee_app.png"),
    ("com_amazon_dee_app.png", "alexa.png"),
    ("anime_wallpapers.png", "com_animepixel_wallpapers.png"),
    ("apk_extractor.png", "domilopment_apkextractor.png"),
    ("apkvision.png", "com_apkvision_apps.png"),
    ("authenticator.png", "com_google_android_apps_authenticator2.png"),
    ("bilibili.png", "com_bilibili_app_in.png"),
    ("blockads.png", "app_pwhs_blockads.png"),
    ("appmanager.png", "com_buge_appmanager.png"),
    ("google_camera.png", "org_codeaurora_snapcam.png"),
    ("google_camera.png", "com_hinnka_mycamera.png"),
    ("org_codeaurora_snapcam.png", "google_camera.png"),
    ("chatgpt.png", "com_openai_chatgpt.png"),
    ("convx_music.png", "com_convx_music.png"),
    ("com_convx_music.png", "convx_music.png"),
    ("dawnsedge.png", "com_neptune_dawnsedge.png"),
    ("discord.png", "com_discord.png"),
    ("fiveg_only.png", "fivegonly.png"),
    ("fivegonly.png", "fiveg_only.png"),
    ("fiveg_only.png", "com_hunteralex_fivegonly.png"),
    ("whatsapp.png", "com_whatsapp.png"),
    ("com_whatsapp.png", "whatsapp.png"),
    ("one_dm.png", "idm_internet_download_manager_plus.png"),
    ("youtube.png", "com_google_android_youtube.png"),
    ("x.png", "com_twitter_android.png"),
]

for src, dst in ALIASES:
    src_p = os.path.join(ICONS_DIR, src)
    dst_p = os.path.join(ICONS_DIR, dst)
    if os.path.exists(src_p):
        if not os.path.exists(dst_p):
            shutil.copy2(src_p, dst_p)
            print(f"Created alias in icons/: {dst} (from {src})")

# 4. Synchronize all .png files from icons/ to res/drawable-nodpi/
all_icons = [f for f in os.listdir(ICONS_DIR) if f.endswith(".png")]
for f in all_icons:
    # Double check valid resource name
    name, _ = os.path.splitext(f)
    if re.match(r"^[a-z0-9_]+$", name):
        shutil.copy2(os.path.join(ICONS_DIR, f), os.path.join(RES_DIR, f))
    else:
        print(f"WARNING: Skipping non-compliant file {f}")

print(f"\nFinished! Total valid icons in icons/: {len(all_icons)}")
print(f"Total valid icons in res/drawable-nodpi/: {len(os.listdir(RES_DIR))}")
