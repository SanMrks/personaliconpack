import os
import re

with open(r'C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\.system_generated\steps\1704\output.txt', 'r', encoding='utf-8') as f:
    text = f.read()

raw_activities = re.findall(r'^\s*([a-zA-Z0-9_.]+/.*)$', text, re.MULTILINE)

RES_DIR = r"E:\Android\app\src\main\res\drawable-nodpi"
available_drawables = set(f[:-4] for f in os.listdir(RES_DIR) if f.endswith('.png'))

print(f"Total raw activities: {len(raw_activities)}")
print(f"Total available drawables: {len(available_drawables)}")

# Mapping rules: package / pattern -> list of drawable names to try
# If any drawable name is in available_drawables, we map it!
PKG_MAP = {
    # 5G Only
    "com.hunteralex.fivegonly": ["fiveg_only", "fivegonly", "com_hunteralex_fivegonly"],
    # Alexa
    "com.amazon.dee.app": ["alexa", "com_amazon_dee_app"],
    # Amazon Shopping
    "com.amazon.mShop.android.shopping": ["amazon", "com_amazon_mshop_android_shopping"],
    # Anime Pixel Wallpapers
    "com.animepixel.wallpapers": ["anime_wallpapers", "com_animepixel_wallpapers"],
    # APK Extractor
    "domilopment.apkextractor": ["apk_extractor", "domilopment_apkextractor"],
    # APKVision
    "com.apkvision.apps": ["apkvision", "com_apkvision_apps"],
    # Authenticator
    "com.google.android.apps.authenticator2": ["authenticator", "com_google_android_apps_authenticator2"],
    # Bilibili
    "com.bilibili.app.in": ["bilibili", "com_bilibili_app_in"],
    # Blockads
    "app.pwhs.blockads": ["blockads", "app_pwhs_blockads"],
    # App Manager (Buge)
    "com.buge.appmanager": ["appmanager", "com_buge_appmanager"],
    # Both Camera apps
    "com.nothing.camera": ["nothing_camera", "com_nothing_camera"],
    "org.codeaurora.snapcam": ["google_camera", "org_codeaurora_snapcam", "snap_camera"],
    "com.hinnka.mycamera": ["google_camera", "com_hinnka_mycamera"],
    # ChatGPT
    "com.openai.chatgpt": ["chatgpt", "com_openai_chatgpt"],
    # Convx Music
    "com.convx.music": ["convx_music", "com_convx_music"],
    # Dawn's Edge (Dawns Wake)
    "com.neptune.dawnsedge": ["dawnsedge", "com_neptune_dawnsedge"],
    # Discord
    "com.discord": ["discord", "com_discord"],
    # Google Drive (Docs)
    "com.google.android.apps.docs": ["google_drive", "com_google_android_apps_docs"],
    # Epic Games
    "com.epicgames.portal": ["epicgames", "com_epicgames_portal"],
    # Essential apps
    "com.nothing.essentialapps": ["essentialapps", "nothing_essential", "app_icon", "com_nothing_essentialapps"],

    # More Google Apps
    "com.google.android.googlequicksearchbox": ["google", "com_google_android_googlequicksearchbox"],
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
    "com.android.chrome": ["chrome"],
    "com.google.android.calculator": ["calculator"],
    "com.google.android.calendar": ["calendar"],
    "com.google.android.contacts": ["contacts"],
    "com.google.android.deskclock": ["clock"],
    "com.google.android.youtube": ["youtube", "com_google_android_youtube"],
    "app.morphe.android.youtube": ["youtube", "youtube_morphe"],

    # Other Installed Apps
    "com.aionyxe.kyng": ["kyng", "com_aionyxe_kyng"],
    "com.amkette.evofoxdojo": ["evofoxdojo", "com_amkette_evofoxdojo"],
    "com.canva.editor": ["canva"],
    "com.cricfytv.sports": ["cricfytv"],
    "com.crunchyroll.crunchyroid": ["crunchyroll"],
    "com.digibites.accubattery": ["accubattery"],
    "com.ecffri.arrows": ["arrows"],
    "com.fadseclab.fadocx": ["fadocx", "com_fadseclab_fadocx"],
    "com.flare.filmcam": ["filmcam", "com_flare_filmcam"],
    "com.flipkart.android": ["flipkart", "com_flipkart_android"],
    "com.instagram.lite": ["instagram_lite", "com_instagram_lite"],
    "com.jlab.app": ["jlab", "com_jlab_app"],
    "com.jpl.jiomart": ["jiomart", "com_jpl_jiomart"],
    "com.levelinfinite.sgameGlobal": ["honor_of_kings", "com_levelinfinite_sgameglobal"],
    "com.mobile.legends": ["mobile_legends"],
    "com.nothing.gallery": ["nothing_gallery", "com_nothing_gallery"],
    "com.pantheraplay.boxheadpremium": ["boxhead"],
    "com.playdigious.evoland2.epic": ["evoland2", "com_playdigious_evoland2_epic"],
    "com.swordfish.lemuroid": ["lemuroid", "com_swordfish_lemuroid"],
    "com.teampacheworks.hades2": ["hades2", "com_teampacheworks_hades2"],
    "com.tplink.aginet": ["tplink_aginet", "com_tplink_aginet"],
    "com.twitter.android": ["x", "com_twitter_android"],
    "com.whatsapp": ["whatsapp", "com_whatsapp"],
    "com.xd.etheria.gp.glb": ["etheria", "com_xd_etheria_gp_glb"],
    "ginlemon.iconpackstudio": ["icon_pack_studio", "ginlemon_iconpackstudio"],
    "idm.internet.download.manager.plus": ["one_dm", "idm_internet_download_manager_plus"],
    "im.thebot.messenger": ["bot_messenger"],
    "in.mobility.keralasavaariconsumer": ["kerala_savaari", "in_mobility_keralasavaariconsumer"],
    "in.startv.hotstar": ["hotstar", "in_startv_hotstar"],
    "org.fdroid.fdroid": ["fdroid", "org_fdroid_fdroid"],
    "org.kustom.widget": ["kwgt", "org_kustom_widget"],
    "org.kustom.widget.pro": ["kwgt_pro", "org_kustom_widget_pro"],
    "ru.tech.imageresizershrinker": ["image_toolbox", "ru_tech_imageresizershrinker"],
    "com.application.zomato.district": ["zomato_district"],
    "com.agc.toolKit": ["agc_toolkit"],
    "com.ravenshade.iconpack": ["app_icon", "essentialapps"],
}

mapped_activities = []
for act in raw_activities:
    pkg, cls = act.split('/', 1)
    if pkg in PKG_MAP:
        drawables = [d for d in PKG_MAP[pkg] if d in available_drawables]
        if drawables:
            mapped_activities.append((pkg, act, cls, drawables))

print(f"\nSuccessfully matched {len(mapped_activities)} of {len(raw_activities)} activities on the phone!")
for pkg, act, cls, drawables in mapped_activities:
    print(f"  {act}  ->  {drawables}")
