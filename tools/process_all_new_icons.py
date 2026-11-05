import os
import shutil
import re

ICONS_DIR = r"E:\Android\icons"
RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"

# Map of raw icon filename -> clean drawable name
RAW_TO_CLEAN = {
    "Lens.png": "lens",
    "Link-to-Windows.png": "link_to_windows",
    "Liquid-Glass.png": "liquid_glass",
    "LocalSend.png": "localsend",
    "Lumina-Dark.png": "lumina",
    "Maps.png": "maps",
    "Marvel-Contest.png": "marvel_cosmic",
    "Meesho.png": "meesho",
    "Meet.png": "google_meet",
    "Meowdoku.png": "meowdoku",
    "Messages.png": "messages",
    "Mi-Gallery.png": "mi_gallery",
    "Mihon.png": "mihon",
    "Mirrors-High.png": "bleach",
    "Mornye.png": "mornye",
    "Morphe.png": "morphe_manager",
    "Music.png": "missingcore",
    "MyJio.png": "myjio",
    "Nagram-XF.png": "telegram_nagram",
    "Netflix.png": "netflix",
    "NextGen-Mobility.png": "kerala_savaari",
    "Ninja-VPN.png": "ninjavpn",
    "Nothing-X.png": "nothing_x",
    "Nuvio.png": "nuvio",
    "OTT-Checker.png": "ott_checker",
    "One4Home.png": "one4home",
    "Orion-Store.png": "orion_store",
    "PRISM-Live.png": "prism_live",
    "Passwords.png": "credentials",
    "Paytm.png": "paytm",
    "Phone.png": "phone",
    "PhonePe.png": "phonepe",
    "Photon-Camera.png": "photon_camera",
    "Photoroom.png": "photoroom",
    "Photos.png": "photos",
    "Photoshop.png": "photoshop",
    "Picsart.png": "picsart",
    "Pinterest.png": "pinterest",
    "PixelBoard.png": "pixelboard",
    "Play-Store.png": "playstore",
    "Podium.png": "podium",
    "Quetta.png": "quetta",
    "RailOne.png": "cris_aikyam",
    "Ravenshade.png": "app_icon",
    "Recorder.png": "nothing_recorder",
    "Reddit.png": "reddit",
    "Rewards.png": "opinion_rewards",
    "Rush.png": "premiere_rush",
    "SIM-Toolkit.png": "sim_toolkit",
    "Safety.png": "safety_hub",
    "Settings.png": "settings",
    "Shiori.png": "shiori",
    "Snapchat.png": "snapchat",
    "Snapseed.png": "snapseed",
    "Spheroid.png": "spheroid",
    "Spotify.png": "spotify",
    "Steam.png": "steam",
    "Streets-of-Rage.png": "streets_of_rage4",
    "Stremio.png": "stremio",
    "SuperImage-Pro.png": "superimage",
    "Swiggy.png": "swiggy",
    "Sword-of-Convallaria.png": "convallaria",
    "Sword-x-Staff.png": "rollerbush",
    "TapTap.png": "taptap",
    "Telegram.png": "telegram",
    "The-Arcade.png": "the_arcade",
    "The-Souled-Store.png": "souled_store",
    "Threads.png": "threads",
    "Trakt.png": "trakt",
    "Uber.png": "uber",
    "VLC.png": "vlc",
    "Vivaldi.png": "vivaldi",
    "Voidpet-Dungeon.png": "voidpet",
    "Wallet.png": "google_wallet",
    "Wallpaper-Engine.png": "wallpaper_engine",
    "Wallpapers.png": "google_wallpapers",
    "WallsPy.png": "wallpie",
    "Weather.png": "nothing_weather",
    "WhatsApp-Zoro.png": "whatsapp",
    "Where-Is-My-Train.png": "where_is_my_train",
    "X-Itachi.png": "x",
    "X-Plore.png": "xplore",
    "YONO-SBI.png": "yono_sbi",
    "YT-Music.png": "yt_music",
    "YouTube-Goku.png": "youtube_goku",
    "YouTube-Standard.png": "youtube_standard",
    "Zedge.png": "zedge",
    "Zepp-Active.png": "smartwatch",
    "Zomato.png": "zomato",
    "noon.png": "noon"
}

# 1. Safely rename files on Windows
for raw_name, clean_name in RAW_TO_CLEAN.items():
    raw_path = os.path.join(ICONS_DIR, raw_name)
    clean_path = os.path.join(ICONS_DIR, f"{clean_name}.png")
    temp_path = os.path.join(ICONS_DIR, f"__tmp_{clean_name}.png")

    if os.path.exists(raw_path):
        if raw_path.lower() != clean_path.lower():
            shutil.copy2(raw_path, clean_path)
            os.remove(raw_path)
            print(f"Renamed: {raw_name} -> {clean_name}.png")
        elif raw_name != f"{clean_name}.png":
            # Case-only rename on Windows
            shutil.move(raw_path, temp_path)
            shutil.move(temp_path, clean_path)
            print(f"Case-renamed: {raw_name} -> {clean_name}.png")

# 2. Add extra aliases (e.g. for package-level drawables)
PACKAGE_ALIASES = [
    ("lens.png", "com_google_ar_lens.png"),
    ("link_to_windows.png", "com_microsoft_appmanager.png"),
    ("liquid_glass.png", "com_pau_liqglass.png"),
    ("localsend.png", "org_localsend_localsend_app.png"),
    ("lumina.png", "com_dark_lumina_iconpack.png"),
    ("maps.png", "com_google_android_apps_maps.png"),
    ("marvel_cosmic.png", "com_jayeldragon_marvelcosmic_preview.png"),
    ("meesho.png", "com_meesho_supply.png"),
    ("google_meet.png", "com_google_android_apps_tachyon.png"),
    ("meowdoku.png", "com_oakever_meowdoku.png"),
    ("messages.png", "com_google_android_apps_messaging.png"),
    ("mi_gallery.png", "com_miui_gallery.png"),
    ("mihon.png", "app_mihon_debug.png"),
    ("bleach.png", "com_bandainamcoent_bleach_mh_ww.png"),
    ("mornye.png", "com_zarz_mornye.png"),
    ("morphe_manager.png", "app_morphe_manager.png"),
    ("missingcore.png", "com_cyanchill_missingcore_music.png"),
    ("myjio.png", "com_jio_myjio.png"),
    ("telegram_nagram.png", "fork_risin42_nagramx.png"),
    ("netflix.png", "com_netflix_mediaclient.png"),
    ("ninjavpn.png", "app_ninjavpn_android.png"),
    ("nothing_x.png", "com_nothing_smartcenter.png"),
    ("nuvio.png", "com_nuvio_app.png"),
    ("ott_checker.png", "com_ottchecker_mobile.png"),
    ("one4home.png", "com_one4studio_one4home.png"),
    ("orion_store.png", "com_orion_store.png"),
    ("prism_live.png", "com_prism_live.png"),
    ("credentials.png", "com_google_android_apps_credentialmanager.png"),
    ("paytm.png", "net_one97_paytm.png"),
    ("phone.png", "com_google_android_dialer.png"),
    ("phonepe.png", "com_phonepe_app.png"),
    ("photoroom.png", "com_photoroom_app.png"),
    ("photos.png", "com_google_android_apps_photos.png"),
    ("photoshop.png", "com_adobe_photoshop_retail.png"),
    ("picsart.png", "com_picsart_studio.png"),
    ("pinterest.png", "com_pinterest.png"),
    ("pixelboard.png", "com_akshaykadam_pixelboard.png"),
    ("playstore.png", "com_android_vending.png"),
    ("podium.png", "app_podium.png"),
    ("quetta.png", "net_quetta_browser.png"),
    ("cris_aikyam.png", "org_cris_aikyam.png"),
    ("nothing_recorder.png", "com_nothing_soundrecorder.png"),
    ("reddit.png", "com_reddit_frontpage.png"),
    ("opinion_rewards.png", "com_google_android_apps_paidtasks.png"),
    ("premiere_rush.png", "com_adobe_premiererush_videoeditor.png"),
    ("sim_toolkit.png", "com_android_stk.png"),
    ("safety_hub.png", "com_google_android_apps_safetyhub.png"),
    ("settings.png", "com_android_settings.png"),
    ("shiori.png", "com_brianali_shiori.png"),
    ("snapchat.png", "com_snapchat_android.png"),
    ("snapseed.png", "com_niksoftware_snapseed.png"),
    ("spheroid.png", "com_indigomadina_spheroid.png"),
    ("spotify.png", "com_spotify_music.png"),
    ("steam.png", "com_valvesoftware_android_steam_community.png"),
    ("streets_of_rage4.png", "com_playdigious_sor4_epic.png"),
    ("stremio.png", "com_stremio_one.png"),
    ("superimage.png", "com_zhenxiang_superimage_pro.png"),
    ("swiggy.png", "in_swiggy_android.png"),
    ("convallaria.png", "com_xd_ssrpgen.png"),
    ("rollerbush.png", "com_rollerbush_games.png"),
    ("taptap.png", "com_taptap_global.png"),
    ("telegram.png", "telegram_nagram.png"),
    ("souled_store.png", "com_thesouledstore.png"),
    ("threads.png", "com_instagram_barcelona.png"),
    ("trakt.png", "tv_trakt_trakt.png"),
    ("uber.png", "com_ubercab.png"),
    ("vlc.png", "org_videolan_vlc.png"),
    ("vivaldi.png", "com_vivaldi_browser.png"),
    ("voidpet.png", "com_voidpet_dungeon.png"),
    ("google_wallet.png", "com_google_android_apps_walletnfcrel.png"),
    ("wallpaper_engine.png", "io_wallpaperengine_weclient.png"),
    ("google_wallpapers.png", "com_google_android_apps_wallpaper.png"),
    ("wallpie.png", "com_harry_wallpie.png"),
    ("nothing_weather.png", "com_nothing_weather.png"),
    ("where_is_my_train.png", "com_whereismytrain_android.png"),
    ("xplore.png", "com_lonelycatgames_xplore.png"),
    ("yono_sbi.png", "com_sbi_lotusintouch.png"),
    ("yt_music.png", "com_google_android_apps_youtube_music.png"),
    ("zedge.png", "net_zedge_android.png"),
    ("smartwatch.png", "com_huami_watch_pop.png"),
    ("zomato.png", "com_application_zomato.png"),
    ("noon.png", "com_noon_buyerapp.png"),
    ("youtube_standard.png", "youtube.png"),
]

for src, dst in PACKAGE_ALIASES:
    src_p = os.path.join(ICONS_DIR, src)
    dst_p = os.path.join(ICONS_DIR, dst)
    if os.path.exists(src_p) and not os.path.exists(dst_p):
        shutil.copy2(src_p, dst_p)

# 3. Sync all valid icons to res/drawable-nodpi/
for f in os.listdir(ICONS_DIR):
    if f.endswith(".png"):
        name, _ = os.path.splitext(f)
        if re.match(r"^[a-z0-9_]+$", name):
            shutil.copy2(os.path.join(ICONS_DIR, f), os.path.join(RES_DIR, f))

print(f"Total icons in icons/: {len(os.listdir(ICONS_DIR))}")
print(f"Total icons in res/drawable-nodpi/: {len(os.listdir(RES_DIR))}")
