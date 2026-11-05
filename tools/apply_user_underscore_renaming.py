import os
import re
import shutil
import time
from PIL import Image

ROOT_DIR = r"E:\Android"
ICONS_DIR = os.path.join(ROOT_DIR, "icons")
BACKUP_DIR = os.path.join(ROOT_DIR, "icons_backup")
RES_NODPI = os.path.join(ROOT_DIR, "app", "src", "main", "res", "drawable-nodpi")
RES_XXXHDPI = os.path.join(ROOT_DIR, "app", "src", "main", "res", "drawable-xxxhdpi")
RES_XML = os.path.join(ROOT_DIR, "app", "src", "main", "res", "xml")
ASSETS_DIR = os.path.join(ROOT_DIR, "app", "src", "main", "assets")
MAIN_JAVA = os.path.join(ROOT_DIR, "app", "src", "main", "java", "com", "ravenshade", "iconpack", "MainActivity.java")
MANIFEST_XML = os.path.join(ROOT_DIR, "app", "src", "main", "AndroidManifest.xml")
DUMP_PATH = r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt"
UNDERSCORES_TXT = os.path.join(ROOT_DIR, "installed_apps_underscores.txt")

# 1. Complete mapping of 146 apps from installed_apps_underscores.txt
MAPPING_146 = {
    'app_mihon_debug': 'Mihon.png',
    'app_morphe_android_youtube': 'YouTube.png',
    'app_morphe_manager': 'Morphe.png',
    'app_ninjavpn_android': 'Ninja-VPN.png',
    'app_podium': 'Podium.png',
    'app_pwhs_blockads': 'BlockAds.png',
    'com_adobe_photoshop_retail': 'Photoshop.png',
    'com_adobe_premiererush_videoeditor': 'Rush.png',
    'com_agc_toolkit': 'AGC-Toolkit.png',
    'com_aionyxe_kyng': 'Kyng.png',
    'com_akshaykadam_pixelboard': 'PixelBoard.png',
    'com_amazon_dee_app': 'Amazon-Alexa.png',
    'com_amazon_mshop_android_shopping': 'Amazon-Shopping.png',
    'com_amkette_evofoxdojo': 'EvoFox-Dojo.png',
    'com_android_chrome': 'Chrome-Beta.png',
    'com_android_settings': 'Settings.png',
    'com_android_vending': 'Play-Store.png',
    'com_animepixel_wallpapers': 'Anime-Pixel.png',
    'com_apkvision_apps': 'APKVision-Store.png',
    'com_application_zomato': 'Zomato.png',
    'com_application_zomato_district': 'District.png',
    'com_bandainamcoent_bleach_mh_ww': 'Mirrors-High.png',
    'com_bilibili_app_in': 'Bilibili.png',
    'com_brianali_shiori': 'Shiori.png',
    'com_buge_appmanager': 'Buge-App.png',
    'com_canva_editor': 'Canva.png',
    'com_convx_music': 'Convx.png',
    'com_cricfytv_sports': 'CRICFy-TV.png',
    'com_crunchyroll_crunchyroid': 'Crunchyroll.png',
    'com_custom_iconpack': 'Ravenshade.png',
    'com_cyanchill_missingcore_music': 'Music.png',
    'com_dark_lumina_iconpack': 'Lumina-Dark.png',
    'com_digibites_accubattery': 'AccuBattery.png',
    'com_discord': 'Discord.png',
    'com_ecffri_arrows': 'Arrows.png',
    'com_epicgames_portal': 'Epic-Games.png',
    'com_facebook_katana': 'Facebook.png',
    'com_fadseclab_fadocx': 'Fadocx.png',
    'com_flare_filmcam': 'FlareCam.png',
    'com_flipkart_android': 'Flipkart.png',
    'com_google_android_apps_adm': 'Find-Hub.png',
    'com_google_android_apps_authenticator2': 'Authenticator.png',
    'com_google_android_apps_chromecast_app': 'Home.png',
    'com_google_android_apps_credentialmanager': 'Passwords.png',
    'com_google_android_apps_kids_familylink': 'Family-Link.png',
    'com_google_android_apps_maps': 'Maps.png',
    'com_google_android_apps_messaging': 'Messages.png',
    'com_google_android_apps_nbu_paisa_user': 'GPay.png',
    'com_google_android_apps_paidtasks': 'Rewards.png',
    'com_google_android_apps_photos': 'Photos.png',
    'com_google_android_apps_subscriptions_red': 'Google-One.png',
    'com_google_android_apps_walletnfcrel': 'Wallet.png',
    'com_google_android_apps_wallpaper': 'Wallpapers.png',
    'com_google_android_calculator': 'Calculator.png',
    'com_google_android_calendar': 'Calendar.png',
    'com_google_android_contacts': 'Contacts.png',
    'com_google_android_deskclock': 'Clock.png',
    'com_google_android_dialer': 'Phone.png',
    'com_google_android_gm': 'Gmail.png',
    'com_google_ar_lens': 'Lens.png',
    'com_harry_wallpie': 'WallsPy.png',
    'com_hinnka_mycamera': 'Google-Camera.png',
    'com_huami_watch_pop': 'Zepp-Active.png',
    'com_hunteralex_fivegonly': '5G-Only.png',
    'com_indigomadina_spheroid': 'Spheroid.png',
    'com_instagram_android': 'Instagram-Lite.png',
    'com_instagram_barcelona': 'Threads.png',
    'com_instagram_lite': 'Instagram-Lite.png',
    'com_jayeldragon_marvelcosmic_preview': 'Marvel-Contest.png',
    'com_jio_myjio': 'MyJio.png',
    'com_jlab_app': 'JLab.png',
    'com_jpl_jiomart': 'JioMart.png',
    'com_levelinfinite_sgameglobal': 'Honor-of-Kings.png',
    'com_lonelycatgames_xplore': 'X-Plore.png',
    'com_meesho_supply': 'Meesho.png',
    'com_microsoft_appmanager': 'Link-to-Windows.png',
    'com_miui_gallery': 'Mi-Gallery.png',
    'com_mobile_legends': 'mobile_legends.png',
    'com_neptune_dawnsedge': 'Dawns-Wake.png',
    'com_netflix_mediaclient': 'Netflix.png',
    'com_nic_mparivahan': 'NextGen-Mobility.png',
    'com_niksoftware_snapseed': 'Snapseed.png',
    'com_noon_buyerapp': 'noon.png',
    'com_nothing_camera': 'Camera.png',
    'com_nothing_essentialapps': 'Essential-Apps.png',
    'com_nothing_soundrecorder': 'Recorder.png',
    'com_nothing_weather': 'Weather.png',
    'com_nuvio_app': 'Nuvio.png',
    'com_oakever_meowdoku': 'Meowdoku.png',
    'com_one4studio_one4home': 'One4Home.png',
    'com_openai_chatgpt': 'ChatGPT.png',
    'com_orion_store': 'Orion-Store.png',
    'com_ottchecker_mobile': 'OTT-Checker.png',
    'com_pantheraplay_boxheadpremium': 'Box-Head-Player.png',
    'com_pau_liqglass': 'Liquid-Glass.png',
    'com_phonepe_app': 'PhonePe.png',
    'com_photoroom_app': 'Photoroom.png',
    'com_picsart_studio': 'Picsart.png',
    'com_pinterest': 'Pinterest.png',
    'com_playdigious_evoland2_epic': 'Evoland-2.png',
    'com_playdigious_sor4_epic': 'Streets-of-Rage.png',
    'com_prism_live': 'PRISM-Live.png',
    'com_reddit_frontpage': 'Reddit.png',
    'com_rollerbush_games': 'The-Arcade.png',
    'com_sbi_lotusintouch': 'YONO-SBI.png',
    'com_snapchat_android': 'Snapchat.png',
    'com_spotify_music': 'Spotify.png',
    'com_stremio_one': 'Stremio.png',
    'com_swordfish_lemuroid': 'Lemuroid.png',
    'com_taptap_global': 'TapTap.png',
    'com_teampacheworks_hades2': 'Hades-II.png',
    'com_thesouledstore': 'The-Souled-Store.png',
    'com_tplink_aginet': 'Aginet.png',
    'com_twitter_android': 'X-Itachi.png',
    'com_ubercab': 'Uber.png',
    'com_valvesoftware_android_steam_community': 'Steam.png',
    'com_vivaldi_browser': 'Vivaldi.png',
    'com_voidpet_dungeon': 'Voidpet-Dungeon.png',
    'com_whatsapp': 'WhatsApp.png',
    'com_whereismytrain_android': 'Where-Is-My-Train.png',
    'com_xd_etheria_gp_glb': 'Etheria-Restart.png',
    'com_xd_ssrpgen': 'Sword-of-Convallaria.png',
    'com_zarz_mornye': 'Mornye.png',
    'com_zhenxiang_superimage_pro': 'SuperImage-Pro.png',
    'com_zjcs_android_us': 'The-Arcade.png',
    'domilopment_apkextractor': 'APK-Extractor.png',
    'fork_risin42_nagramx': 'Telegram.png',
    'ginlemon_iconpackstudio': 'Icon-Pack-Studio.png',
    'idm_internet_download_manager_plus': '1DM-Plus.png',
    'im_thebot_messenger': 'Botim.png',
    'in_mobility_keralasavaariconsumer': 'Kerala-Savaari.png',
    'in_startv_hotstar': 'JioHotstar.png',
    'in_swiggy_android': 'Swiggy.png',
    'io_wallpaperengine_weclient': 'Wallpaper-Engine.png',
    'net_one97_paytm': 'Paytm.png',
    'net_quetta_browser': 'Quetta.png',
    'net_zedge_android': 'Zedge.png',
    'org_codeaurora_snapcam': 'Photon-Camera.png',
    'org_cris_aikyam': 'RailOne.png',
    'org_fdroid_fdroid': 'F-Droid.png',
    'org_kustom_widget': 'KWGT.png',
    'org_kustom_widget_pro': 'KWGT-Pro.png',
    'org_localsend_localsend_app': 'LocalSend.png',
    'org_videolan_vlc': 'VLC.png',
    'ru_tech_imageresizershrinker': 'Image-Toolbox.png',
    'tv_trakt_trakt': 'Trakt.png'
}

# 2. Additional system apps on device
ADDITIONAL_SYSTEM_APPS = {
    'com_ravenshade_iconpack': 'Ravenshade.png',
    'com_google_android_googlequicksearchbox': 'Google.png',
    'com_google_android_apps_docs': 'Google-Drive.png',
    'com_google_android_keep': 'Keep-Notes.png',
    'com_google_android_videos': 'Google-TV.png',
    'com_google_android_apps_safetyhub': 'Safety.png',
    'com_android_stk': 'SIM-Toolkit.png',
    'com_nothing_smartcenter': 'Nothing-X.png',
    'com_google_android_apps_youtube_music': 'YT-Music.png',
    'com_google_android_apps_nbu_files': 'Files.png',
    'com_google_android_youtube': 'YouTube.png',
    'com_nothing_gallery': 'Gallery-Default.png',
    'com_google_android_apps_tachyon': 'Meet.png',
    'com_google_android_apps_bard': 'Gemini.png',
}

# Clean human-readable titles
TITLES = {}
with open(UNDERSCORES_TXT, 'r', encoding='utf-8') as f:
    for line in f:
        line = line.strip()
        m = re.match(r'^\d+\.\s*([a-zA-Z0-9_]+)\s*\((.*)\)$', line)
        if m:
            target = m.group(1).lower()
            title = m.group(2)
            TITLES[target] = title

TITLES['com_ravenshade_iconpack'] = 'Ravenshade'
TITLES['com_google_android_googlequicksearchbox'] = 'Google'
TITLES['com_google_android_apps_docs'] = 'Google Drive'
TITLES['com_google_android_keep'] = 'Keep Notes'
TITLES['com_google_android_videos'] = 'Google TV'
TITLES['com_google_android_apps_safetyhub'] = 'Safety'
TITLES['com_android_stk'] = 'SIM Toolkit'
TITLES['com_nothing_smartcenter'] = 'Nothing X'
TITLES['com_google_android_apps_youtube_music'] = 'YouTube Music'
TITLES['com_google_android_apps_nbu_files'] = 'Files'
TITLES['com_google_android_youtube'] = 'YouTube'
TITLES['com_nothing_gallery'] = 'Nothing Gallery'
TITLES['com_google_android_apps_tachyon'] = 'Google Meet'
TITLES['com_google_android_apps_bard'] = 'Gemini'

ALL_MAPPINGS = {**MAPPING_146, **ADDITIONAL_SYSTEM_APPS}

print("==================================================")
print(f"Total apps to process: {len(ALL_MAPPINGS)}")
print("==================================================")

# Step 1: Backup current icons if not already backed up
if not os.path.exists(BACKUP_DIR) or len(os.listdir(BACKUP_DIR)) < 100:
    os.makedirs(BACKUP_DIR, exist_ok=True)
    for f in os.listdir(ICONS_DIR):
        src = os.path.join(ICONS_DIR, f)
        if os.path.isfile(src):
            shutil.copy2(src, os.path.join(BACKUP_DIR, f))
    print(f"Backed up {len(os.listdir(BACKUP_DIR))} files to {BACKUP_DIR}")
else:
    print(f"Backup verified with {len(os.listdir(BACKUP_DIR))} files.")

# Step 2: Enlarge & Process Bitmaps (8% zoom, Lanczos, edge-to-edge)
t0 = time.time()
os.makedirs(RES_NODPI, exist_ok=True)
os.makedirs(RES_XXXHDPI, exist_ok=True)

# Clear icons folder of old names and write exclusively underscore names
for f in os.listdir(ICONS_DIR):
    p = os.path.join(ICONS_DIR, f)
    if os.path.isfile(p):
        os.remove(p)

count = 0
for target_name, src_filename in ALL_MAPPINGS.items():
    src_path = os.path.join(BACKUP_DIR, src_filename)
    if not os.path.exists(src_path):
        src_path = os.path.join(ICONS_DIR, src_filename)
    if not os.path.exists(src_path):
        print(f"Warning: Missing source {src_filename} for {target_name}")
        continue

    im = Image.open(src_path).convert("RGBA")
    w, h = im.size
    scale = 1.08  # 8% enlargement to expand squircle edge-to-edge
    nw, nh = int(w * scale), int(h * scale)
    scaled = im.resize((nw, nh), Image.Resampling.LANCZOS)
    left = (nw - w) // 2
    top = (nh - h) // 2
    cropped = scaled.crop((left, top, left + w, top + h))

    target_file = f"{target_name}.png"
    p_icon = os.path.join(ICONS_DIR, target_file)
    cropped.save(p_icon, format="PNG")

    # Fast file copy into res/drawable-nodpi and res/drawable-xxxhdpi
    shutil.copy2(p_icon, os.path.join(RES_NODPI, target_file))
    shutil.copy2(p_icon, os.path.join(RES_XXXHDPI, target_file))
    count += 1

# Generate app_icon.png
app_icon_src = os.path.join(ICONS_DIR, "com_ravenshade_iconpack.png")
if not os.path.exists(app_icon_src):
    app_icon_src = os.path.join(ICONS_DIR, "com_nothing_essentialapps.png")
if os.path.exists(app_icon_src):
    shutil.copy2(app_icon_src, os.path.join(ICONS_DIR, "app_icon.png"))
    shutil.copy2(app_icon_src, os.path.join(RES_NODPI, "app_icon.png"))
    shutil.copy2(app_icon_src, os.path.join(RES_XXXHDPI, "app_icon.png"))

print(f"Processed and saved {count} icons in {time.time() - t0:.2f} seconds!")

# Step 3: Device Activities & Universal Package Matching
with open(DUMP_PATH, 'r', encoding='utf-8') as f:
    dump_text = f.read()

device_acts = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', dump_text, re.MULTILINE)

EXACT_PKG_TO_DRAWABLE = {
    'app.mihon.debug': 'app_mihon_debug',
    'app.morphe.android.youtube': 'app_morphe_android_youtube',
    'app.morphe.manager': 'app_morphe_manager',
    'app.ninjavpn.android': 'app_ninjavpn_android',
    'app.podium': 'app_podium',
    'app.pwhs.blockads': 'app_pwhs_blockads',
    'com.adobe.photoshop.retail': 'com_adobe_photoshop_retail',
    'com.adobe.premiererush.videoeditor': 'com_adobe_premiererush_videoeditor',
    'com.agc.toolKit': 'com_agc_toolkit',
    'com.aionyxe.kyng': 'com_aionyxe_kyng',
    'com.akshaykadam.pixelboard': 'com_akshaykadam_pixelboard',
    'com.amazon.dee.app': 'com_amazon_dee_app',
    'com.amazon.mShop.android.shopping': 'com_amazon_mshop_android_shopping',
    'com.amkette.evofoxdojo': 'com_amkette_evofoxdojo',
    'com.android.chrome': 'com_android_chrome',
    'com.android.settings': 'com_android_settings',
    'com.android.vending': 'com_android_vending',
    'com.animepixel.wallpapers': 'com_animepixel_wallpapers',
    'com.apkvision.apps': 'com_apkvision_apps',
    'com.application.zomato': 'com_application_zomato',
    'com.application.zomato.district': 'com_application_zomato_district',
    'com.bandainamcoent.bleach_mh_ww': 'com_bandainamcoent_bleach_mh_ww',
    'com.bilibili.app.in': 'com_bilibili_app_in',
    'com.brianali.shiori': 'com_brianali_shiori',
    'com.buge.appmanager': 'com_buge_appmanager',
    'com.canva.editor': 'com_canva_editor',
    'com.convx.music': 'com_convx_music',
    'com.cricfytv.sports': 'com_cricfytv_sports',
    'com.crunchyroll.crunchyroid': 'com_crunchyroll_crunchyroid',
    'com.custom.iconpack': 'com_ravenshade_iconpack',
    'com.ravenshade.iconpack': 'com_ravenshade_iconpack',
    'com.cyanchill.missingcore.music': 'com_cyanchill_missingcore_music',
    'com.dark.lumina.iconpack': 'com_dark_lumina_iconpack',
    'com.digibites.accubattery': 'com_digibites_accubattery',
    'com.discord': 'com_discord',
    'com.ecffri.arrows': 'com_ecffri_arrows',
    'com.epicgames.portal': 'com_epicgames_portal',
    'com.facebook.katana': 'com_facebook_katana',
    'com.fadseclab.fadocx': 'com_fadseclab_fadocx',
    'com.flare.filmcam': 'com_flare_filmcam',
    'com.flipkart.android': 'com_flipkart_android',
    'com.google.android.apps.adm': 'com_google_android_apps_adm',
    'com.google.android.apps.authenticator2': 'com_google_android_apps_authenticator2',
    'com.google.android.apps.chromecast.app': 'com_google_android_apps_chromecast_app',
    'com.google.android.apps.credentialmanager': 'com_google_android_apps_credentialmanager',
    'com.google.android.apps.kids.familylink': 'com_google_android_apps_kids_familylink',
    'com.google.android.apps.maps': 'com_google_android_apps_maps',
    'com.google.android.apps.messaging': 'com_google_android_apps_messaging',
    'com.google.android.apps.nbu.paisa.user': 'com_google_android_apps_nbu_paisa_user',
    'com.google.android.apps.paidtasks': 'com_google_android_apps_paidtasks',
    'com.google.android.apps.photos': 'com_google_android_apps_photos',
    'com.google.android.apps.subscriptions.red': 'com_google_android_apps_subscriptions_red',
    'com.google.android.apps.walletnfcrel': 'com_google_android_apps_walletnfcrel',
    'com.google.android.apps.wallpaper': 'com_google_android_apps_wallpaper',
    'com.google.android.calculator': 'com_google_android_calculator',
    'com.google.android.calendar': 'com_google_android_calendar',
    'com.google.android.contacts': 'com_google_android_contacts',
    'com.google.android.deskclock': 'com_google_android_deskclock',
    'com.google.android.dialer': 'com_google_android_dialer',
    'com.google.android.gm': 'com_google_android_gm',
    'com.google.ar.lens': 'com_google_ar_lens',
    'com.harry.wallpie': 'com_harry_wallpie',
    'com.hinnka.mycamera': 'com_hinnka_mycamera',
    'com.huami.watch.pop': 'com_huami_watch_pop',
    'com.hunteralex.fivegonly': 'com_hunteralex_fivegonly',
    'com.indigomadina.spheroid': 'com_indigomadina_spheroid',
    'com.instagram.android': 'com_instagram_android',
    'com.instagram.barcelona': 'com_instagram_barcelona',
    'com.instagram.lite': 'com_instagram_lite',
    'com.jayeldragon.marvelcosmic.preview': 'com_jayeldragon_marvelcosmic_preview',
    'com.jio.myjio': 'com_jio_myjio',
    'com.jlab.app': 'com_jlab_app',
    'com.jpl.jiomart': 'com_jpl_jiomart',
    'com.levelinfinite.sgameGlobal': 'com_levelinfinite_sgameglobal',
    'com.lonelycatgames.Xplore': 'com_lonelycatgames_xplore',
    'com.meesho.supply': 'com_meesho_supply',
    'com.microsoft.appmanager': 'com_microsoft_appmanager',
    'com.miui.gallery': 'com_miui_gallery',
    'com.mobile.legends': 'com_mobile_legends',
    'com.neptune.dawnsedge': 'com_neptune_dawnsedge',
    'com.netflix.mediaclient': 'com_netflix_mediaclient',
    'com.nic.mparivahan': 'com_nic_mparivahan',
    'com.niksoftware.snapseed': 'com_niksoftware_snapseed',
    'com.noon.buyerapp': 'com_noon_buyerapp',
    'com.nothing.camera': 'com_nothing_camera',
    'com.nothing.essentialapps': 'com_nothing_essentialapps',
    'com.nothing.soundrecorder': 'com_nothing_soundrecorder',
    'com.nothing.weather': 'com_nothing_weather',
    'com.nuvio.app': 'com_nuvio_app',
    'com.oakever.meowdoku': 'com_oakever_meowdoku',
    'com.one4studio.one4home': 'com_one4studio_one4home',
    'com.openai.chatgpt': 'com_openai_chatgpt',
    'com.orion.store': 'com_orion_store',
    'com.ottchecker.mobile': 'com_ottchecker_mobile',
    'com.pantheraplay.boxheadpremium': 'com_pantheraplay_boxheadpremium',
    'com.pau.liqglass': 'com_pau_liqglass',
    'com.phonepe.app': 'com_phonepe_app',
    'com.photoroom.app': 'com_photoroom_app',
    'com.picsart.studio': 'com_picsart_studio',
    'com.pinterest': 'com_pinterest',
    'com.playdigious.evoland2.epic': 'com_playdigious_evoland2_epic',
    'com.playdigious.sor4.epic': 'com_playdigious_sor4_epic',
    'com.prism.live': 'com_prism_live',
    'com.reddit.frontpage': 'com_reddit_frontpage',
    'com.rollerbush.games': 'com_rollerbush_games',
    'com.sbi.lotusintouch': 'com_sbi_lotusintouch',
    'com.snapchat.android': 'com_snapchat_android',
    'com.spotify.music': 'com_spotify_music',
    'com.stremio.one': 'com_stremio_one',
    'com.swordfish.lemuroid': 'com_swordfish_lemuroid',
    'com.taptap.global': 'com_taptap_global',
    'com.teampacheworks.hades2': 'com_teampacheworks_hades2',
    'com.thesouledstore': 'com_thesouledstore',
    'com.tplink.aginet': 'com_tplink_aginet',
    'com.twitter.android': 'com_twitter_android',
    'com.ubercab': 'com_ubercab',
    'com.valvesoftware.android.steam.community': 'com_valvesoftware_android_steam_community',
    'com.vivaldi.browser': 'com_vivaldi_browser',
    'com.voidpet.dungeon': 'com_voidpet_dungeon',
    'com.whatsapp': 'com_whatsapp',
    'com.whereismytrain.android': 'com_whereismytrain_android',
    'com.xd.etheria.gp.glb': 'com_xd_etheria_gp_glb',
    'com.xd.ssrpgen': 'com_xd_ssrpgen',
    'com.zarz.mornye': 'com_zarz_mornye',
    'com.zhenxiang.superimage.pro': 'com_zhenxiang_superimage_pro',
    'com.zjcs.android.us': 'com_zjcs_android_us',
    'domilopment.apkextractor': 'domilopment_apkextractor',
    'fork.risin42.nagramx': 'fork_risin42_nagramx',
    'ginlemon.iconpackstudio': 'ginlemon_iconpackstudio',
    'idm.internet.download.manager.plus': 'idm_internet_download_manager_plus',
    'im.thebot.messenger': 'im_thebot_messenger',
    'in.mobility.keralasavaariconsumer': 'in_mobility_keralasavaariconsumer',
    'in.startv.hotstar': 'in_startv_hotstar',
    'in.swiggy.android': 'in_swiggy_android',
    'io.wallpaperengine.weclient': 'io_wallpaperengine_weclient',
    'net.one97.paytm': 'net_one97_paytm',
    'net.quetta.browser': 'net_quetta_browser',
    'net.zedge.android': 'net_zedge_android',
    'org.codeaurora.snapcam': 'org_codeaurora_snapcam',
    'org.cris.aikyam': 'org_cris_aikyam',
    'org.fdroid.fdroid': 'org_fdroid_fdroid',
    'org.kustom.widget': 'org_kustom_widget',
    'org.kustom.widget.pro': 'org_kustom_widget_pro',
    'org.localsend.localsend_app': 'org_localsend_localsend_app',
    'org.videolan.vlc': 'org_videolan_vlc',
    'ru.tech.imageresizershrinker': 'ru_tech_imageresizershrinker',
    'tv.trakt.trakt': 'tv_trakt_trakt',
    # System apps
    'com.android.stk': 'com_android_stk',
    'com.google.android.apps.bard': 'com_google_android_apps_bard',
    'com.google.android.apps.docs': 'com_google_android_apps_docs',
    'com.google.android.apps.nbu.files': 'com_google_android_apps_nbu_files',
    'com.google.android.apps.safetyhub': 'com_google_android_apps_safetyhub',
    'com.google.android.apps.tachyon': 'com_google_android_apps_tachyon',
    'com.google.android.apps.youtube.music': 'com_google_android_apps_youtube_music',
    'com.google.android.googlequicksearchbox': 'com_google_android_googlequicksearchbox',
    'com.google.android.keep': 'com_google_android_keep',
    'com.google.android.videos': 'com_google_android_videos',
    'com.google.android.youtube': 'com_google_android_youtube',
    'com.nothing.gallery': 'com_nothing_gallery',
    'com.nothing.smartcenter': 'com_nothing_smartcenter',
}

# Step 4: Generate Universal appfilter.xml
af_lines = [
    '<?xml version="1.0" encoding="utf-8"?>',
    '<resources>',
    '  <scale factor="1.25" />'
]
seen_items = set()

def add_af(comp, drawable):
    key = (comp, drawable)
    if key not in seen_items:
        seen_items.add(key)
        af_lines.append(f'  <item component="{comp}" drawable="{drawable}" />')

matched_acts = 0
for act in device_acts:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    drawable = EXACT_PKG_TO_DRAWABLE.get(pkg)
    if not drawable:
        norm_pkg = pkg.replace('.', '_').lower()
        drawable = EXACT_PKG_TO_DRAWABLE.get(norm_pkg) or ALL_MAPPINGS.get(norm_pkg)

    if drawable:
        matched_acts += 1
        add_af(f"ComponentInfo{{{pkg}/{full_cls}}}", drawable)
        add_af(f"ComponentInfo{{{pkg}/{cls}}}", drawable)
        add_af(f"{pkg}", drawable)

# Also explicitly add Ravenshade package
add_af("ComponentInfo{com.ravenshade.iconpack/com.ravenshade.iconpack.MainActivity}", "com_ravenshade_iconpack")
add_af("com.ravenshade.iconpack", "com_ravenshade_iconpack")
add_af("ComponentInfo{com.custom.iconpack/com.custom.iconpack.MainActivity}", "com_ravenshade_iconpack")
add_af("com.custom.iconpack", "com_ravenshade_iconpack")

af_lines.append('</resources>')
af_content = "\n".join(af_lines) + "\n"

with open(os.path.join(RES_XML, "appfilter.xml"), 'w', encoding='utf-8') as f:
    f.write(af_content)
with open(os.path.join(ASSETS_DIR, "appfilter.xml"), 'w', encoding='utf-8') as f:
    f.write(af_content)

print(f"Mapped {matched_acts} / {len(device_acts)} activities in appfilter.xml ({len(seen_items)} items)!")

# Step 5: Generate Universal drawable.xml
all_drawables = sorted(list(ALL_MAPPINGS.keys()) + ["app_icon"])
draw_lines = [
    '<?xml version="1.0" encoding="utf-8"?>',
    '<resources>',
    '  <version>1</version>',
    '  <category title="Ravenshade Icons">'
]
for d in all_drawables:
    draw_lines.append(f'    <item drawable="{d}" />')
draw_lines.append('  </category>')
draw_lines.append('</resources>')
draw_content = "\n".join(draw_lines) + "\n"

with open(os.path.join(RES_XML, "drawable.xml"), 'w', encoding='utf-8') as f:
    f.write(draw_content)
with open(os.path.join(ASSETS_DIR, "drawable.xml"), 'w', encoding='utf-8') as f:
    f.write(draw_content)

# Step 6: Generate Universal theme_resources.xml (ADW / Apex)
tr_lines = [
    '<?xml version="1.0" encoding="utf-8"?>',
    '<theme>',
    '  <scale factor="1.25" />'
]
for d in all_drawables:
    tr_lines.append(f'  <item drawable="{d}" />')
tr_lines.append('</theme>')
tr_content = "\n".join(tr_lines) + "\n"

with open(os.path.join(RES_XML, "theme_resources.xml"), 'w', encoding='utf-8') as f:
    f.write(tr_content)
with open(os.path.join(ASSETS_DIR, "theme_resources.xml"), 'w', encoding='utf-8') as f:
    f.write(tr_content)

# Step 7: Generate Universal iconpack.xml (Go Launcher)
ip_lines = [
    '<?xml version="1.0" encoding="utf-8"?>',
    '<iconpack>'
]
for d in all_drawables:
    ip_lines.append(f'  <item drawable="{d}" />')
ip_lines.append('</iconpack>')
ip_content = "\n".join(ip_lines) + "\n"

with open(os.path.join(RES_XML, "iconpack.xml"), 'w', encoding='utf-8') as f:
    f.write(ip_content)
with open(os.path.join(ASSETS_DIR, "iconpack.xml"), 'w', encoding='utf-8') as f:
    f.write(ip_content)

print(f"Generated drawable.xml, theme_resources.xml, iconpack.xml ({len(all_drawables)} drawables)!")

# Step 8: Update MainActivity.java CUSTOM_ICONS
dash_items = []
seen_pkgs = set()

for target_name in MAPPING_146.keys():
    pkg_name = None
    for p, d in EXACT_PKG_TO_DRAWABLE.items():
        if d == target_name:
            pkg_name = p
            break
    if not pkg_name:
        pkg_name = target_name.replace('_', '.')
    title = TITLES.get(target_name, target_name)
    safe_title = title.replace('"', '\\"')
    dash_items.append(f'        new CustomIconInfo("{safe_title}", "{pkg_name}", "{target_name}")')
    seen_pkgs.add(pkg_name)

for target_name in ADDITIONAL_SYSTEM_APPS.keys():
    pkg_name = None
    for p, d in EXACT_PKG_TO_DRAWABLE.items():
        if d == target_name:
            pkg_name = p
            break
    if not pkg_name:
        pkg_name = target_name.replace('_', '.')
    if pkg_name not in seen_pkgs:
        seen_pkgs.add(pkg_name)
        title = TITLES.get(target_name, target_name)
        safe_title = title.replace('"', '\\"')
        dash_items.append(f'        new CustomIconInfo("{safe_title}", "{pkg_name}", "{target_name}")')

replacement_code = "    private static final CustomIconInfo[] CUSTOM_ICONS = {\n" + ",\n".join(dash_items) + "\n    };"

with open(MAIN_JAVA, 'r', encoding='utf-8') as f:
    java_code = f.read()

new_java_code = re.sub(
    r'    private static final CustomIconInfo\[\] CUSTOM_ICONS = \{.*?\n    \};',
    replacement_code,
    java_code,
    flags=re.DOTALL
)

with open(MAIN_JAVA, 'w', encoding='utf-8') as f:
    f.write(new_java_code)

print(f"Updated MainActivity.java with {len(dash_items)} custom icons!")

# Step 9: Make AndroidManifest.xml Universal
UNIVERSAL_MANIFEST = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ravenshade.iconpack"
    android:versionCode="25"
    android:versionName="4.1">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.SET_WALLPAPER" />

    <uses-sdk
        android:minSdkVersion="26"
        android:targetSdkVersion="34" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/app_icon"
        android:label="@string/app_name"
        android:theme="@android:style/Theme.NoTitleBar"
        android:supportsRtl="true">

        <activity
            android:name="com.ravenshade.iconpack.MainActivity"
            android:exported="true"
            android:icon="@drawable/app_icon"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <!-- Nova Launcher -->
            <intent-filter>
                <action android:name="com.novalauncher.THEME" />
                <category android:name="com.novalauncher.category.CUSTOM_ICON_PACK" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.teslacoilsw.launcher.THEME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- ADW / Apex / Lawnchair / Nothing / OnePlus / Xiaomi / System Launchers -->
            <intent-filter>
                <action android:name="org.adw.launcher.THEMES" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.anddoes.launcher.THEME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Action Launcher -->
            <intent-filter>
                <action android:name="com.actionlauncher.THEME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Go Launcher EX -->
            <intent-filter>
                <action android:name="com.gau.go.launcherex.theme" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Smart Launcher -->
            <intent-filter>
                <action android:name="ginlemon.smartlauncher.THEMES" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Solo Launcher -->
            <intent-filter>
                <action android:name="com.morgoo.launcher.THEME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Icon Picker intents for launchers that support manual icon selection -->
            <intent-filter>
                <action android:name="org.adw.launcher.icons.ACTION_PICK_ICON" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.novalauncher.icons.ACTION_PICK_ICON" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <meta-data
                android:name="org.adw.launcher.theme.category"
                android:value="iconpack" />
            <meta-data
                android:name="com.anddoes.launcher.theme.category"
                android:value="iconpack" />
            <meta-data
                android:name="com.novalauncher.THEME"
                android:resource="@xml/appfilter" />
            <meta-data
                android:name="com.teslacoilsw.launcher.THEME"
                android:resource="@xml/appfilter" />
            <meta-data
                android:name="org.adw.launcher.THEMES"
                android:resource="@xml/appfilter" />
        </activity>

        <!-- Fullscreen Wallpaper Viewer Activity -->
        <activity
            android:name="com.ravenshade.iconpack.WallpaperViewerActivity"
            android:exported="true"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />

    </application>

</manifest>
"""

with open(MANIFEST_XML, 'w', encoding='utf-8') as f:
    f.write(UNIVERSAL_MANIFEST)

print("Updated AndroidManifest.xml with Universal Launcher Support (versionCode 25, versionName 4.1)")
print("ALL PREPARATION COMPLETE!")
