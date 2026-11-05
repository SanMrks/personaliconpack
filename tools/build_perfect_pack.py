import os
import re
import shutil
from PIL import Image

RAW_DIR = r"E:\Android\raw_144_icons"
ICONS_DIR = r"E:\Android\icons"
RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
OUT_RES_AF = r"E:\Android\app\src\main\res\xml\appfilter.xml"
OUT_ASSETS_AF = r"E:\Android\app\src\main\assets\appfilter.xml"
MAIN_JAVA = r"E:\Android\app\src\main\java\com\ravenshade\iconpack\MainActivity.java"
DUMP_PATH = r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt"

# 1. Match raw files from raw_144_icons to packages and canonical drawable names
RAW_TO_APP = {
    "001_Shiori.png": ("Shiori", "com.brianali.shiori", ["shiori"]),
    "002_SIM_Toolkit.png": ("SIM Toolkit", "com.android.stk", ["sim_toolkit", "com_android_stk"]),
    "003_Snapchat.png": ("Snapchat", "com.snapchat.android", ["snapchat", "com_snapchat_android"]),
    "004_Snapseed.png": ("Snapseed", "com.niksoftware.snapseed", ["snapseed", "com_niksoftware_snapseed"]),
    "005_Spheroid.png": ("Spheroid", "com.indigomadina.spheroid", ["spheroid", "com_indigomadina_spheroid"]),
    "006_Messages.png": ("Messages", "com.google.android.apps.messaging", ["messages", "com_google_android_apps_messaging"]),
    "007_Mihon.png": ("Mihon", "app.mihon.debug", ["mihon", "app_mihon_debug"]),
    "008_Mirrors_High.png": ("Bleach: Mirrors High", "com.bandainamcoent.bleach_mh_ww", ["bleach", "com_bandainamcoent_bleach_mh_ww"]),
    "009_Mobile_Legends.png": ("Mobile Legends", "com.mobile.legends", ["mobile_legends"]),
    "010_Mornye.png": ("Mornye", "com.zarz.mornye", ["mornye", "com_zarz_mornye"]),
    "011_Fadocx.png": ("FaDocx", "com.fadseclab.fadocx", ["fadocx", "com_fadseclab_fadocx", "facebook", "com_facebook_katana"]),
    "012_Family_Link.png": ("Family Link", "com.google.android.apps.kids.familylink", ["family_link", "com_google_android_apps_kids_familylink"]),
    "013_Files.png": ("Files", "com.google.android.apps.nbu.files", ["files", "com_google_android_apps_nbu_files"]),
    "014_Find_Hub.png": ("Find Device", "com.google.android.apps.adm", ["find_hub", "find_my_device", "com_google_android_apps_adm"]),
    "015_1DM_Plus.png": ("1DM+ Downloader", "idm.internet.download.manager.plus", ["one_dm", "idm_internet_download_manager_plus"]),
    "016_5G_Only.png": ("5G Only", "com.hunteralex.fivegonly", ["fiveg_only", "fivegonly", "com_hunteralex_fivegonly"]),
    "017_AccuBattery.png": ("AccuBattery", "com.digibites.accubattery", ["accubattery", "com_digibites_accubattery"]),
    "018_Aignet.png": ("TP-Link Aginet", "com.tplink.aginet", ["tplink_aginet", "com_tplink_aginet"]),
    "019_Spotify.png": ("Spotify", "com.spotify.music", ["spotify", "com_spotify_music"]),
    "020_Steam.png": ("Steam", "com.valvesoftware.android.steam.community", ["steam", "com_valvesoftware_android_steam_community"]),
    "021_Streets_of_Rage_4.png": ("Streets of Rage 4", "com.playdigious.sor4.epic", ["streets_of_rage4", "com_playdigious_sor4_epic"]),
    "022_Stremio.png": ("Stremio", "com.stremio.one", ["stremio", "com_stremio_one"]),
    "023_SuperImage_Pro.png": ("SuperImage Pro", "com.zhenxiang.superimage.pro", ["superimage", "com_zhenxiang_superimage_pro"]),
    "024_Morphe.png": ("Morphe Manager", "app.morphe.manager", ["morphe_manager", "app_morphe_manager"]),
    "025_Music.png": ("Missing Core Music", "com.cyanchill.missingcore.music", ["missingcore", "com_cyanchill_missingcore_music"]),
    "026_MyJio.png": ("MyJio", "com.jio.myjio", ["myjio", "com_jio_myjio"]),
    "027_Nagram_XF.png": ("Telegram Nagram", "fork.risin42.nagramx", ["telegram_nagram", "telegram", "fork_risin42_nagramx"]),
    "028_Netflix.png": ("Netflix", "com.netflix.mediaclient", ["netflix", "com_netflix_mediaclient"]),
    "029_Flipkart.png": ("Flipkart", "com.flipkart.android", ["flipkart", "com_flipkart_android"]),
    "030_Gallery_Color.png": ("MIUI Gallery", "com.miui.gallery", ["mi_gallery", "com_miui_gallery"]),
    "031_Gallery.png": ("Nothing Gallery", "com.nothing.gallery", ["nothing_gallery", "com_nothing_gallery"]),
    "032_Gemini.png": ("Gemini", "com.google.android.apps.bard", ["gemini", "com_google_android_apps_bard"]),
    "033_Gmail.png": ("Gmail", "com.google.android.gm", ["gmail", "com_google_android_gm"]),
    "034_Amazon_Alexa.png": ("Amazon Alexa", "com.amazon.dee.app", ["alexa", "com_amazon_dee_app"]),
    "035_Amazon_Shopping.png": ("Amazon Shopping", "com.amazon.mShop.android.shopping", ["amazon", "com_amazon_mshop_android_shopping"]),
    "036_Anime_Pixel.png": ("Anime Pixel Wallpapers", "com.animepixel.wallpapers", ["anime_wallpapers", "com_animepixel_wallpapers"]),
    "037_APK_Extractor_Store.png": ("APK Extractor", "domilopment.apkextractor", ["apk_extractor", "domilopment_apkextractor"]),
    "038_Swiggy.png": ("Swiggy", "in.swiggy.android", ["swiggy", "in_swiggy_android"]),
    "039_Sword_of_Convallaria.png": ("Sword of Convallaria", "com.xd.ssrpgen", ["convallaria", "com_xd_ssrpgen"]),
    "040_Sword_x_Staff.png": ("Sword x Staff", "com.xd.ssrpgen", ["sword_x_staff"]),
    "041_TapTap.png": ("TapTap", "com.taptap.global", ["taptap", "com_taptap_global"]),
    "042_The_Arcade.png": ("The Arcade", "com.rollerbush.games", ["the_arcade", "rollerbush", "com_rollerbush_games"]),
    "043_NextGen_mParivahan.png": ("NextGen mParivahan", "com.nic.mparivahan", ["mparivahan", "nextgen_mparivahan", "com_nic_mparivahan"]),
    "044_Ninja_VPN.png": ("Ninja VPN", "app.ninjavpn.android", ["ninjavpn", "app_ninjavpn_android"]),
    "045_noon.png": ("Noon Shopping", "com.noon.buyerapp", ["noon", "com_noon_buyerapp"]),
    "046_Nothing_X.png": ("Nothing X", "com.nothing.smartcenter", ["nothing_x", "com_nothing_smartcenter"]),
    "047_Nuvio.png": ("Nuvio", "com.nuvio.app", ["nuvio", "com_nuvio_app"]),
    "048_Google.png": ("Google", "com.google.android.googlequicksearchbox", ["google", "com_google_android_googlequicksearchbox"]),
    "049_Google_One.png": ("Google One", "com.google.android.apps.subscriptions.red", ["google_one", "com_google_android_apps_subscriptions_red"]),
    "050_Google_TV.png": ("Google TV", "com.google.android.videos", ["google_tv", "com_google_android_videos"]),
    "051_GPay.png": ("Google Pay", "com.google.android.apps.nbu.paisa.user", ["gpay", "google_pay", "com_google_android_apps_nbu_paisa_user"]),
    "052_Hades_II.png": ("Hades II Companion", "com.teampacheworks.hades2", ["hades2", "com_teampacheworks_hades2"]),
    "053_Arrows.png": ("Arrows", "com.ecffri.arrows", ["arrows"]),
    "054_bilibili.png": ("Bilibili", "com.bilibili.app.in", ["bilibili", "com_bilibili_app_in"]),
    "055_BlockAds.png": ("BlockAds", "app.pwhs.blockads", ["blockads", "app_pwhs_blockads"]),
    "056_botim.png": ("Botim Messenger", "im.thebot.messenger", ["bot_messenger", "botim"]),
    "057_The_Souled_Store.png": ("The Souled Store", "com.thesouledstore", ["souled_store", "com_thesouledstore"]),
    "058_Threads.png": ("Threads", "com.instagram.barcelona", ["threads", "com_instagram_barcelona"]),
    "059_Trakt.png": ("Trakt", "tv.trakt.trakt", ["trakt", "tv_trakt_trakt"]),
    "060_Uber.png": ("Uber", "com.ubercab", ["uber", "com_ubercab"]),
    "061_Vivaldi.png": ("Vivaldi Browser", "com.vivaldi.browser", ["vivaldi", "com_vivaldi_browser"]),
    "062_One4Home.png": ("One4Home", "com.one4studio.one4home", ["one4home", "com_one4studio_one4home"]),
    "063_Orion_Store.png": ("Orion Store", "com.orion.store", ["orion_store", "com_orion_store"]),
    "064_OTT_Checker.png": ("OTT Checker", "com.ottchecker.mobile", ["ott_checker", "com_ottchecker_mobile"]),
    "065_Paytm.png": ("Paytm", "net.one97.paytm", ["paytm", "net_one97_paytm"]),
    "066_Home.png": ("Google Home", "com.google.android.apps.chromecast.app", ["google_home", "com_google_android_apps_chromecast_app"]),
    "067_Honor_of_Kings.png": ("Honor of Kings", "com.levelinfinite.sgameGlobal", ["honor_of_kings", "com_levelinfinite_sgameglobal"]),
    "068_Icon_Pack_Studio.png": ("Icon Pack Studio", "ginlemon.iconpackstudio", ["icon_pack_studio", "ginlemon_iconpackstudio"]),
    "069_Image_Toolbox.png": ("Image Toolbox", "ru.tech.imageresizershrinker", ["image_toolbox", "ru_tech_imageresizershrinker"]),
    "070_Instagram.png": ("Instagram", "com.instagram.android", ["instagram", "com_instagram_android", "instagram_lite", "com_instagram_lite"]),
    "071_Box_Head_Premium.png": ("Boxhead Player", "com.pantheraplay.boxheadpremium", ["boxhead"]),
    "072_Calculator.png": ("Calculator", "com.google.android.calculator", ["calculator"]),
    "073_Calendar.png": ("Google Calendar", "com.google.android.calendar", ["calendar"]),
    "074_Camera.png": ("Nothing Camera", "com.nothing.camera", ["nothing_camera", "com_nothing_camera"]),
    "075_VLC.png": ("VLC for Android", "org.videolan.vlc", ["vlc", "org_videolan_vlc"]),
    "076_Voidpet_Dungeon.png": ("Voidpet Dungeon", "com.voidpet.dungeon", ["voidpet", "com_voidpet_dungeon"]),
    "077_Wallet.png": ("Google Wallet", "com.google.android.apps.walletnfcrel", ["google_wallet", "com_google_android_apps_walletnfcrel"]),
    "078_Wallpaper_Engine.png": ("Wallpaper Engine", "io.wallpaperengine.weclient", ["wallpaper_engine", "io_wallpaperengine_weclient"]),
    "079_Wallpapers.png": ("Google Wallpapers", "com.google.android.apps.wallpaper", ["google_wallpapers", "com_google_android_apps_wallpaper"]),
    "080_Phone.png": ("Phone (Dialer)", "com.google.android.dialer", ["phone", "com_google_android_dialer"]),
    "081_PhonePe.png": ("PhonePe", "com.phonepe.app", ["phonepe", "com_phonepe_app"]),
    "082_Photon_Camera.png": ("Photon Camera", "com.hinnka.mycamera", ["photon_camera", "google_camera"]),
    "083_Photoroom.png": ("PhotoRoom", "com.photoroom.app", ["photoroom", "com_photoroom_app"]),
    "084_Instagram_Lite.png": ("Instagram Lite", "com.instagram.lite", ["instagram_lite", "com_instagram_lite"]),
    "085_JioHotstar.png": ("JioHotstar", "in.startv.hotstar", ["hotstar", "in_startv_hotstar"]),
    "086_JioMart.png": ("JioMart", "com.jpl.jiomart", ["jiomart", "com_jpl_jiomart"]),
    "087_JLab.png": ("JLab Audio", "com.jlab.app", ["jlab", "com_jlab_app"]),
    "088_Keep_Notes.png": ("Google Keep", "com.google.android.keep", ["keep", "google_keep", "com_google_android_keep"]),
    "089_Camera_GC.png": ("Google Camera / GCam", "org.codeaurora.snapcam", ["google_camera", "org_codeaurora_snapcam", "snap_camera"]),
    "090_Canva.png": ("Canva", "com.canva.editor", ["canva"]),
    "091_ChatGPT_Beta.png": ("ChatGPT", "com.openai.chatgpt", ["chatgpt", "com_openai_chatgpt"]),
    "092_Clock.png": ("Clock", "com.google.android.deskclock", ["clock"]),
    "093_WallsPy.png": ("Wallpie / WallsPy", "com.harry.wallpie", ["wallpie", "com_harry_wallpie"]),
    "094_Weather.png": ("Nothing Weather", "com.nothing.weather", ["nothing_weather", "com_nothing_weather"]),
    "095_WhatsApp.png": ("WhatsApp", "com.whatsapp", ["whatsapp", "com_whatsapp"]),
    "096_Where_is_My_Train.png": ("Where is my Train", "com.whereismytrain.android", ["where_is_my_train", "com_whereismytrain_android"]),
    "097_X.png": ("X (Twitter)", "com.twitter.android", ["x", "com_twitter_android"]),
    "098_Photoshop.png": ("Photoshop", "com.adobe.photoshop.retail", ["photoshop", "com_adobe_photoshop_retail"]),
    "099_Picsart.png": ("PicsArt", "com.picsart.studio", ["picsart", "com_picsart_studio"]),
    "100_Pinterest.png": ("Pinterest", "com.pinterest", ["pinterest", "com_pinterest"]),
    "101_Play_Store.png": ("Google Play Store", "com.android.vending", ["playstore", "com_android_vending"]),
    "102_Kerala_Savaari.png": ("Kerala Savaari", "in.mobility.keralasavaariconsumer", ["kerala_savaari", "in_mobility_keralasavaariconsumer"]),
    "103_KWGT.png": ("KWGT Kustom Widget", "org.kustom.widget", ["kwgt", "org_kustom_widget"]),
    "104_KWGT_Pro.png": ("KWGT Pro Key", "org.kustom.widget.pro", ["kwgt_pro", "org_kustom_widget_pro"]),
    "105_Kyng.png": ("Kyng", "com.aionyxe.kyng", ["kyng", "com_aionyxe_kyng"]),
    "106_Lemuroid.png": ("Lemuroid", "com.swordfish.lemuroid", ["lemuroid", "com_swordfish_lemuroid"]),
    "107_Contacts.png": ("Contacts", "com.google.android.contacts", ["contacts"]),
    "108_CRICFy_TV.png": ("Cricfy TV", "com.cricfytv.sports", ["cricfytv"]),
    "109_Crunchyroll.png": ("Crunchyroll", "com.crunchyroll.crunchyroid", ["crunchyroll"]),
    "110_Dawns_Wake.png": ("Dawn's Edge", "com.neptune.dawnsedge", ["dawnsedge", "com_neptune_dawnsedge"]),
    "111_X-plore.png": ("X-plore File Manager", "com.lonelycatgames.Xplore", ["xplore", "com_lonelycatgames_xplore"]),
    "112_YONO_SBI.png": ("YONO SBI", "com.sbi.lotusintouch", ["yono_sbi", "com_sbi_lotusintouch"]),
    "113_YouTube.png": ("YouTube", "com.google.android.youtube", ["youtube", "com_google_android_youtube"]),
    "114_YouTube_Alt.png": ("YouTube Goku", "app.morphe.android.youtube", ["youtube_goku", "youtube_morphe", "app_morphe_android_youtube"]),
    "115_YT_Music.png": ("YouTube Music", "com.google.android.apps.youtube.music", ["yt_music", "com_google_android_apps_youtube_music"]),
    "116_Podium.png": ("Podium", "app.podium", ["podium", "app_podium"]),
    "117_PRISM_Live.png": ("PRISM Live Studio", "com.prism.live", ["prism_live", "com_prism_live"]),
    "118_Quetta.png": ("Quetta Browser", "net.quetta.browser", ["quetta", "net_quetta_browser"]),
    "119_Recorder.png": ("Nothing Sound Recorder", "com.nothing.soundrecorder", ["nothing_recorder", "com_nothing_soundrecorder"]),
    "120_Lens.png": ("Google Lens", "com.google.ar.lens", ["lens", "com_google_ar_lens"]),
    "121_Link_to_Windows.png": ("Link to Windows", "com.microsoft.appmanager", ["link_to_windows", "com_microsoft_appmanager"]),
    "122_Liquid_Glass.png": ("Liquid Glass", "com.pau.liqglass", ["liquid_glass", "com_pau_liqglass"]),
    "123_LocalSend.png": ("LocalSend", "org.localsend.localsend_app", ["localsend", "org_localsend_localsend_app"]),
    "124_Discord.png": ("Discord", "com.discord", ["discord", "com_discord"]),
    "125_District.png": ("District by Zomato", "com.application.zomato.district", ["zomato_district"]),
    "126_Drive.png": ("Google Drive", "com.google.android.apps.docs", ["google_drive", "com_google_android_apps_docs"]),
    "127_Epic_Games.png": ("Epic Games Store", "com.epicgames.portal", ["epicgames", "com_epicgames_portal"]),
    "128_Essential_Apps.png": ("Nothing Essential Apps", "com.nothing.essentialapps", ["essentialapps", "nothing_essential", "app_icon", "com_nothing_essentialapps"]),
    "129_Zedge.png": ("Zedge", "net.zedge.android", ["zedge", "net_zedge_android"]),
    "130_Zepp_Active.png": ("Zepp Active / Pop Watch", "com.huami.watch.pop", ["smartwatch", "com_huami_watch_pop"]),
    "131_Zomato.png": ("Zomato", "com.application.zomato", ["zomato", "com_application_zomato"]),
    "132_Reddit.png": ("Reddit", "com.reddit.frontpage", ["reddit", "com_reddit_frontpage"]),
    "133_Rewards.png": ("Google Opinion Rewards", "com.google.android.apps.paidtasks", ["opinion_rewards", "com_google_android_apps_paidtasks"]),
    "134_Rush.png": ("Adobe Premiere Rush", "com.adobe.premiererush.videoeditor", ["premiere_rush", "com_adobe_premiererush_videoeditor"]),
    "135_Safety.png": ("Personal Safety", "com.google.android.apps.safetyhub", ["safety_hub", "com_google_android_apps_safetyhub"]),
    "136_Settings.png": ("Settings", "com.android.settings", ["settings", "com_android_settings"]),
    "137_Maps.png": ("Google Maps", "com.google.android.apps.maps", ["maps", "com_google_android_apps_maps"]),
    "138_Marvel_Cosmic_Invaders.png": ("Marvel Cosmic Preview", "com.jayeldragon.marvelcosmic.preview", ["marvel_cosmic", "com_jayeldragon_marvelcosmic_preview"]),
    "139_Meesho.png": ("Meesho", "com.meesho.supply", ["meesho", "com_meesho_supply"]),
    "140_Meet.png": ("Google Meet", "com.google.android.apps.tachyon", ["google_meet", "com_google_android_apps_tachyon"]),
    "141_Meowdoku.png": ("Meowdoku", "com.oakever.meowdoku", ["meowdoku", "com_oakever_meowdoku"]),
    "142_Etheria_Restart.png": ("Etheria: Restart", "com.xd.etheria.gp.glb", ["etheria", "com_xd_etheria_gp_glb"]),
    "143_EvoFox_Dojo.png": ("EvoFox Dojo", "com.amkette.evofoxdojo", ["evofoxdojo", "com_amkette_evofoxdojo"]),
    "144_Evoland_2.png": ("Evoland 2", "com.playdigious.evoland2.epic", ["evoland2", "com_playdigious_evoland2_epic"]),
}

# 2. Process and enlarge images by 5%, copy to icons/ and res/drawable-nodpi/
scale = 1.05
print("Processing and enlarging 144 icons...")

for filename, (app_name, pkg, drawables) in RAW_TO_APP.items():
    raw_p = os.path.join(RAW_DIR, filename)
    if not os.path.exists(raw_p):
        print("Missing raw file:", filename)
        continue

    im = Image.open(raw_p).convert("RGBA")
    w, h = im.size
    new_w = int(w * scale)
    new_h = int(h * scale)
    scaled = im.resize((new_w, new_h), Image.Resampling.LANCZOS)
    left = (new_w - w) // 2
    top = (new_h - h) // 2
    cropped = scaled.crop((left, top, left + w, top + h))

    for d in drawables:
        target_name = f"{d}.png"
        for folder in [ICONS_DIR, RES_DIR]:
            out_p = os.path.join(folder, target_name)
            cropped.save(out_p, format="PNG", optimize=True)

# Also ensure app_icon is available
app_icon_src = os.path.join(RES_DIR, "essentialapps.png")
if os.path.exists(app_icon_src):
    shutil.copy2(app_icon_src, os.path.join(RES_DIR, "app_icon.png"))
    shutil.copy2(app_icon_src, os.path.join(ICONS_DIR, "app_icon.png"))

print(f"Total icons in res/drawable-nodpi/: {len(os.listdir(RES_DIR))}")

# 3. Build comprehensive appfilter.xml
with open(DUMP_PATH, 'r', encoding='utf-8') as f:
    text = f.read()

device_acts = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', text, re.MULTILINE)
available_drawables = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

xml_lines = ['<?xml version="1.0" encoding="utf-8"?>', '<resources>', '  <scale factor="1.18" />']
seen = set()

def add_item(comp, drawable):
    key = (comp, drawable)
    if key not in seen:
        seen.add(key)
        xml_lines.append(f'  <item component="{comp}" drawable="{drawable}" />')

# Build pkg -> drawables map
PKG_TO_DRAWABLES = {}
for filename, (app_name, pkg, drawables) in RAW_TO_APP.items():
    valid_draws = [d for d in drawables if d in available_drawables]
    if pkg not in PKG_TO_DRAWABLES:
        PKG_TO_DRAWABLES[pkg] = []
    for vd in valid_draws:
        if vd not in PKG_TO_DRAWABLES[pkg]:
            PKG_TO_DRAWABLES[pkg].append(vd)

# Add our own pack
PKG_TO_DRAWABLES["com.ravenshade.iconpack"] = ["app_icon", "essentialapps"]

# Also map Chrome and existing system apps
PKG_TO_DRAWABLES["com.android.chrome"] = ["chrome"]
PKG_TO_DRAWABLES["com.google.android.apps.photos"] = ["photos", "com_google_android_apps_photos"]
PKG_TO_DRAWABLES["org.fdroid.fdroid"] = ["fdroid", "org_fdroid_fdroid"]
PKG_TO_DRAWABLES["com.agc.toolKit"] = ["agc_toolkit"]
PKG_TO_DRAWABLES["com.akshaykadam.pixelboard"] = ["pixelboard", "com_akshaykadam_pixelboard"]
PKG_TO_DRAWABLES["com.apkvision.apps"] = ["apkvision", "com_apkvision_apps"]
PKG_TO_DRAWABLES["com.buge.appmanager"] = ["appmanager", "com_buge_appmanager"]
PKG_TO_DRAWABLES["com.convx.music"] = ["convx_music", "com_convx_music"]
PKG_TO_DRAWABLES["com.facebook.katana"] = ["facebook", "com_facebook_katana", "fadocx", "com_fadseclab_fadocx"]
PKG_TO_DRAWABLES["com.flare.filmcam"] = ["filmcam", "com_flare_filmcam"]
PKG_TO_DRAWABLES["com.google.android.apps.authenticator2"] = ["authenticator", "com_google_android_apps_authenticator2"]
PKG_TO_DRAWABLES["com.google.android.apps.credentialmanager"] = ["credentials", "com_google_android_apps_credentialmanager"]
PKG_TO_DRAWABLES["com.zjcs.android.us"] = ["zjcs_app", "the_arcade"]
PKG_TO_DRAWABLES["org.cris.aikyam"] = ["cris_aikyam", "railone"]

matched_count = 0
for act in device_acts:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    if pkg in PKG_TO_DRAWABLES:
        draws = PKG_TO_DRAWABLES[pkg]
        if draws:
            matched_count += 1
            for d in draws:
                add_item(f"ComponentInfo{{{pkg}/{full_cls}}}", d)
                add_item(f"ComponentInfo{{{pkg}/{cls}}}", d)
                add_item(f"{pkg}", d)

xml_lines.append('</resources>')
content = "\n".join(xml_lines) + "\n"

with open(OUT_RES_AF, 'w', encoding='utf-8') as f:
    f.write(content)
with open(OUT_ASSETS_AF, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"Matched {matched_count} / {len(device_acts)} activities on device!")
print(f"Generated {len(xml_lines)} lines in appfilter.xml ({len(seen)} unique items)!")

# 4. Update MainActivity CUSTOM_ICONS
dashboard_items = []
seen_dash_pkgs = set()
for filename, (app_name, pkg, drawables) in sorted(RAW_TO_APP.items()):
    if pkg not in seen_dash_pkgs and drawables:
        seen_dash_pkgs.add(pkg)
        d = drawables[0]
        safe_name = app_name.replace('"', '\\"')
        dashboard_items.append(f'        new CustomIconInfo("{safe_name}", "{pkg}", "{d}")')

dashboard_items.append('        new CustomIconInfo("Ravenshade", "com.ravenshade.iconpack", "app_icon")')

replacement_code = "    private static final CustomIconInfo[] CUSTOM_ICONS = {\n" + ",\n".join(dashboard_items) + "\n    };"

with open(MAIN_JAVA, 'r', encoding='utf-8') as f:
    code = f.read()

new_code = re.sub(r'    private static final CustomIconInfo\[\] CUSTOM_ICONS = \{.*?\n    \};', replacement_code, code, flags=re.DOTALL)
with open(MAIN_JAVA, 'w', encoding='utf-8') as f:
    f.write(new_code)

print(f"Updated MainActivity.java with {len(dashboard_items)} custom icons!")
