import os
import re

DUMP_PATH = r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt"
RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
OUT_RES = r"E:\Android\app\src\main\res\xml\appfilter.xml"
OUT_ASSETS = r"E:\Android\app\src\main\assets\appfilter.xml"

with open(DUMP_PATH, 'r', encoding='utf-8') as f:
    text = f.read()

raw_activities = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', text, re.MULTILINE)
available_drawables = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

# Comprehensive mapping: package -> list of preferred drawables
PKG_MAP = {
    # Ravenshade Icon Pack itself
    "com.ravenshade.iconpack": ["app_icon", "essentialapps"],

    # System & Tools
    "com.android.chrome": ["chrome"],
    "com.android.settings": ["settings", "com_android_settings"],
    "com.android.vending": ["playstore", "com_android_vending"],
    "com.android.stk": ["sim_toolkit", "com_android_stk"],
    "com.nothing.camera": ["nothing_camera", "com_nothing_camera"],
    "com.nothing.soundrecorder": ["nothing_recorder", "com_nothing_soundrecorder"],
    "com.nothing.weather": ["nothing_weather", "com_nothing_weather"],
    "com.nothing.gallery": ["nothing_gallery", "com_nothing_gallery"],
    "com.nothing.smartcenter": ["nothing_x", "com_nothing_smartcenter"],
    "com.nothing.essentialapps": ["essentialapps", "nothing_essential", "app_icon", "com_nothing_essentialapps"],

    # Cameras
    "org.codeaurora.snapcam": ["google_camera", "org_codeaurora_snapcam", "snap_camera"],
    "com.hinnka.mycamera": ["google_camera", "com_hinnka_mycamera"],

    # Google Suite
    "com.google.android.googlequicksearchbox": ["google", "com_google_android_googlequicksearchbox"],
    "com.google.android.apps.docs": ["google_drive", "com_google_android_apps_docs"],
    "com.google.android.gm": ["gmail", "com_google_android_gm"],
    "com.google.android.apps.bard": ["gemini", "com_google_android_apps_bard"],
    "com.google.android.apps.nbu.paisa.user": ["gpay", "google_pay", "com_google_android_apps_nbu_paisa_user"],
    "com.google.android.apps.chromecast.app": ["google_home", "com_google_android_apps_chromecast_app"],
    "com.google.android.apps.subscriptions.red": ["google_one", "com_google_android_apps_subscriptions_red"],
    "com.google.android.videos": ["google_tv", "com_google_android_videos"],
    "com.google.android.apps.kids.familylink": ["family_link", "com_google_android_apps_kids_familylink"],
    "com.google.android.apps.nbu.files": ["files", "com_google_android_apps_nbu_files"],
    "com.google.android.apps.adm": ["find_hub", "find_my_device", "com_google_android_apps_adm"],
    "com.google.android.keep": ["keep", "google_keep", "com_google_android_keep"],
    "com.google.android.calculator": ["calculator"],
    "com.google.android.calendar": ["calendar"],
    "com.google.android.contacts": ["contacts"],
    "com.google.android.deskclock": ["clock"],
    "com.google.android.dialer": ["phone", "com_google_android_dialer"],
    "com.google.android.youtube": ["youtube", "youtube_standard", "youtube_goku", "com_google_android_youtube"],
    "com.google.android.apps.youtube.music": ["yt_music", "com_google_android_apps_youtube_music"],
    "com.google.android.apps.maps": ["maps", "com_google_android_apps_maps"],
    "com.google.android.apps.messaging": ["messages", "com_google_android_apps_messaging"],
    "com.google.android.apps.photos": ["photos", "com_google_android_apps_photos"],
    "com.google.android.apps.tachyon": ["google_meet", "com_google_android_apps_tachyon"],
    "com.google.android.apps.authenticator2": ["authenticator", "com_google_android_apps_authenticator2"],
    "com.google.android.apps.safetyhub": ["safety_hub", "com_google_android_apps_safetyhub"],
    "com.google.android.apps.credentialmanager": ["credentials", "com_google_android_apps_credentialmanager"],
    "com.google.android.apps.paidtasks": ["opinion_rewards", "com_google_android_apps_paidtasks"],
    "com.google.android.apps.walletnfcrel": ["google_wallet", "com_google_android_apps_walletnfcrel"],
    "com.google.android.apps.wallpaper": ["google_wallpapers", "com_google_android_apps_wallpaper"],
    "com.google.ar.lens": ["lens", "com_google_ar_lens"],

    # Social & Chat
    "com.whatsapp": ["whatsapp", "com_whatsapp"],
    "com.facebook.katana": ["facebook", "com_facebook_katana"],
    "com.twitter.android": ["x", "com_twitter_android"],
    "com.discord": ["discord", "com_discord"],
    "com.snapchat.android": ["snapchat", "com_snapchat_android"],
    "com.reddit.frontpage": ["reddit", "com_reddit_frontpage"],
    "com.pinterest": ["pinterest", "com_pinterest"],
    "com.instagram.android": ["instagram", "com_instagram_android"],
    "com.instagram.barcelona": ["threads", "com_instagram_barcelona"],
    "com.instagram.lite": ["instagram_lite", "com_instagram_lite"],
    "fork.risin42.nagramx": ["telegram_nagram", "telegram", "fork_risin42_nagramx"],
    "im.thebot.messenger": ["bot_messenger"],

    # Media & Entertainment
    "com.spotify.music": ["spotify", "com_spotify_music"],
    "com.netflix.mediaclient": ["netflix", "com_netflix_mediaclient"],
    "com.stremio.one": ["stremio", "com_stremio_one"],
    "in.startv.hotstar": ["hotstar", "in_startv_hotstar"],
    "com.crunchyroll.crunchyroid": ["crunchyroll"],
    "com.bilibili.app.in": ["bilibili", "com_bilibili_app_in"],
    "app.morphe.android.youtube": ["youtube_morphe", "youtube", "app_morphe_android_youtube"],
    "app.morphe.manager": ["morphe_manager", "app_morphe_manager"],
    "org.videolan.vlc": ["vlc", "org_videolan_vlc"],
    "tv.trakt.trakt": ["trakt", "tv_trakt_trakt"],
    "com.cricfytv.sports": ["cricfytv"],
    "com.convx.music": ["convx_music", "com_convx_music"],
    "com.cyanchill.missingcore.music": ["missingcore", "com_cyanchill_missingcore_music"],
    "com.prism.live": ["prism_live", "com_prism_live"],

    # Games
    "com.mobile.legends": ["mobile_legends"],
    "com.levelinfinite.sgameGlobal": ["honor_of_kings", "com_levelinfinite_sgameglobal"],
    "com.playdigious.evoland2.epic": ["evoland2", "com_playdigious_evoland2_epic"],
    "com.playdigious.sor4.epic": ["streets_of_rage4", "com_playdigious_sor4_epic"],
    "com.swordfish.lemuroid": ["lemuroid", "com_swordfish_lemuroid"],
    "com.teampacheworks.hades2": ["hades2", "com_teampacheworks_hades2"],
    "com.xd.etheria.gp.glb": ["etheria", "com_xd_etheria_gp_glb"],
    "com.xd.ssrpgen": ["convallaria", "com_xd_ssrpgen"],
    "com.bandainamcoent.bleach_mh_ww": ["bleach", "com_bandainamcoent_bleach_mh_ww"],
    "com.pantheraplay.boxheadpremium": ["boxhead"],
    "com.ecffri.arrows": ["arrows"],
    "com.neptune.dawnsedge": ["dawnsedge", "com_neptune_dawnsedge"],
    "com.oakever.meowdoku": ["meowdoku", "com_oakever_meowdoku"],
    "com.rollerbush.games": ["the_arcade", "rollerbush", "com_rollerbush_games"],
    "com.valvesoftware.android.steam.community": ["steam", "com_valvesoftware_android_steam_community"],
    "com.taptap.global": ["taptap", "com_taptap_global"],
    "com.epicgames.portal": ["epicgames", "com_epicgames_portal"],
    "com.voidpet.dungeon": ["voidpet", "com_voidpet_dungeon"],
    "com.jayeldragon.marvelcosmic.preview": ["marvel_cosmic", "com_jayeldragon_marvelcosmic_preview"],
    "com.aionyxe.kyng": ["kyng", "com_aionyxe_kyng"],
    "com.amkette.evofoxdojo": ["evofoxdojo", "com_amkette_evofoxdojo"],

    # Utilities & Tools
    "idm.internet.download.manager.plus": ["one_dm", "idm_internet_download_manager_plus"],
    "com.hunteralex.fivegonly": ["fiveg_only", "fivegonly", "com_hunteralex_fivegonly"],
    "com.amazon.dee.app": ["alexa", "com_amazon_dee_app"],
    "domilopment.apkextractor": ["apk_extractor", "domilopment_apkextractor"],
    "com.apkvision.apps": ["apkvision", "com_apkvision_apps"],
    "com.buge.appmanager": ["appmanager", "com_buge_appmanager"],
    "com.openai.chatgpt": ["chatgpt", "com_openai_chatgpt"],
    "app.pwhs.blockads": ["blockads", "app_pwhs_blockads"],
    "app.ninjavpn.android": ["ninjavpn", "app_ninjavpn_android"],
    "app.podium": ["podium", "app_podium"],
    "app.mihon.debug": ["mihon", "app_mihon_debug"],
    "com.canva.editor": ["canva"],
    "com.digibites.accubattery": ["accubattery"],
    "com.fadseclab.fadocx": ["fadocx", "com_fadseclab_fadocx"],
    "com.flare.filmcam": ["filmcam", "com_flare_filmcam"],
    "ginlemon.iconpackstudio": ["icon_pack_studio", "ginlemon_iconpackstudio"],
    "org.fdroid.fdroid": ["fdroid", "org_fdroid_fdroid"],
    "org.kustom.widget": ["kwgt", "org_kustom_widget"],
    "org.kustom.widget.pro": ["kwgt_pro", "org_kustom_widget_pro"],
    "org.localsend.localsend_app": ["localsend", "org_localsend_localsend_app"],
    "ru.tech.imageresizershrinker": ["image_toolbox", "ru_tech_imageresizershrinker"],
    "com.zhenxiang.superimage.pro": ["superimage", "com_zhenxiang_superimage_pro"],
    "com.microsoft.appmanager": ["link_to_windows", "com_microsoft_appmanager"],
    "com.lonelycatgames.Xplore": ["xplore", "com_lonelycatgames_xplore"],
    "com.tplink.aginet": ["tplink_aginet", "com_tplink_aginet"],
    "com.pau.liqglass": ["liquid_glass", "com_pau_liqglass"],
    "com.dark.lumina.iconpack": ["lumina", "com_dark_lumina_iconpack"],
    "com.animepixel.wallpapers": ["anime_wallpapers", "com_animepixel_wallpapers"],
    "com.harry.wallpie": ["wallpie", "com_harry_wallpie"],
    "io.wallpaperengine.weclient": ["wallpaper_engine", "io_wallpaperengine_weclient"],
    "net.zedge.android": ["zedge", "net_zedge_android"],
    "com.one4studio.one4home": ["one4home", "com_one4studio_one4home"],
    "com.orion.store": ["orion_store", "com_orion_store"],
    "com.ottchecker.mobile": ["ott_checker", "com_ottchecker_mobile"],
    "com.akshaykadam.pixelboard": ["pixelboard", "com_akshaykadam_pixelboard"],
    "com.brianali.shiori": ["shiori", "com_brianali_shiori"],
    "com.indigomadina.spheroid": ["spheroid", "com_indigomadina_spheroid"],
    "com.zarz.mornye": ["mornye", "com_zarz_mornye"],
    "com.nuvio.app": ["nuvio", "com_nuvio_app"],
    "net.quetta.browser": ["quetta", "net_quetta_browser"],
    "com.vivaldi.browser": ["vivaldi", "com_vivaldi_browser"],
    "com.huami.watch.pop": ["smartwatch", "com_huami_watch_pop"],
    "com.jlab.app": ["jlab", "com_jlab_app"],
    "com.agc.toolKit": ["agc_toolkit"],

    # Shopping & Food & Travel
    "com.amazon.mShop.android.shopping": ["amazon", "com_amazon_mshop_android_shopping"],
    "com.flipkart.android": ["flipkart", "com_flipkart_android"],
    "com.meesho.supply": ["meesho", "com_meesho_supply"],
    "com.noon.buyerapp": ["noon", "com_noon_buyerapp"],
    "com.thesouledstore": ["souled_store", "com_thesouledstore"],
    "com.jpl.jiomart": ["jiomart", "com_jpl_jiomart"],
    "com.application.zomato": ["zomato", "com_application_zomato"],
    "com.application.zomato.district": ["zomato_district"],
    "in.swiggy.android": ["swiggy", "in_swiggy_android"],
    "com.ubercab": ["uber", "com_ubercab"],
    "in.mobility.keralasavaariconsumer": ["kerala_savaari", "in_mobility_keralasavaariconsumer"],
    "org.cris.aikyam": ["cris_aikyam", "org_cris_aikyam"],
    "com.whereismytrain.android": ["where_is_my_train", "com_whereismytrain_android"],
    "com.jio.myjio": ["myjio", "com_jio_myjio"],
    "net.one97.paytm": ["paytm", "net_one97_paytm"],
    "com.phonepe.app": ["phonepe", "com_phonepe_app"],
    "com.sbi.lotusintouch": ["yono_sbi", "com_sbi_lotusintouch"],
    "com.nic.mparivahan": ["mparivahan", "com_nic_mparivahan"],
    "com.zjcs.android.us": ["zjcs_app", "com_zjcs_android_us"],

    # Creative / Photo
    "com.adobe.photoshop.retail": ["photoshop", "com_adobe_photoshop_retail"],
    "com.adobe.premiererush.videoeditor": ["premiere_rush", "com_adobe_premiererush_videoeditor"],
    "com.niksoftware.snapseed": ["snapseed", "com_niksoftware_snapseed"],
    "com.photoroom.app": ["photoroom", "com_photoroom_app"],
    "com.picsart.studio": ["picsart", "com_picsart_studio"],
    "com.miui.gallery": ["mi_gallery", "com_miui_gallery"],
}

xml_lines = ['<?xml version="1.0" encoding="utf-8"?>', '<resources>', '  <scale factor="1.18" />']
seen = set()

def add_item(comp, drawable):
    key = (comp, drawable)
    if key not in seen:
        seen.add(key)
        xml_lines.append(f'  <item component="{comp}" drawable="{drawable}" />')

matched_count = 0
for act in raw_activities:
    pkg, cls = act.split('/', 1)
    if cls.startswith('.'):
        full_cls = pkg + cls
    elif '.' not in cls:
        full_cls = pkg + '.' + cls
    else:
        full_cls = cls

    if pkg in PKG_MAP:
        drawables = [d for d in PKG_MAP[pkg] if d in available_drawables]
        if drawables:
            matched_count += 1
            for d in drawables:
                # 1. Full class path ComponentInfo
                add_item(f"ComponentInfo{{{pkg}/{full_cls}}}", d)
                # 2. Shorthand ComponentInfo
                add_item(f"ComponentInfo{{{pkg}/{cls}}}", d)
                # 3. Direct Package Name (fallback)
                add_item(f"{pkg}", d)

xml_lines.append('</resources>')
content = "\n".join(xml_lines) + "\n"

with open(OUT_RES, 'w', encoding='utf-8') as f:
    f.write(content)

with open(OUT_ASSETS, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"Matched {matched_count} of {len(raw_activities)} activities on device!")
print(f"Generated {len(xml_lines)} lines in appfilter.xml ({len(seen)} unique items)!")
