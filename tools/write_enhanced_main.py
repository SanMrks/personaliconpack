import re

# Complete, beautiful, production-ready MainActivity.java generator
script = '''package com.ravenshade.iconpack;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);
    private static final String DRIVE_FOLDER_ID = "1hI8Ava7c3N8REXjiPA1yc6oilLS24zXh";
    private static final String LIVE_CACHE_FILE = "live_drive_wallpapers.txt";

    public static class CustomIconInfo {
        public final String name;
        public final String packageName;
        public final String drawableName;

        public CustomIconInfo(String name, String packageName, String drawableName) {
            this.name = name;
            this.packageName = packageName;
            this.drawableName = drawableName;
        }
    }

    private static final CustomIconInfo[] CUSTOM_ICONS = {
        new CustomIconInfo("Mihon", "app.mihon.debug", "app_mihon_debug"),
        new CustomIconInfo("YouTube ReVanced / Morphe", "app.morphe.android.youtube", "app_morphe_android_youtube"),
        new CustomIconInfo("Morphe Manager", "app.morphe.manager", "app_morphe_manager"),
        new CustomIconInfo("Ninja VPN", "app.ninjavpn.android", "app_ninjavpn_android"),
        new CustomIconInfo("PhonePe", "com.phonepe.app", "com_phonepe_app"),
        new CustomIconInfo("Amazon Shopping", "in.amazon.mShop.android.shopping", "in_amazon_mshop_android_shopping"),
        new CustomIconInfo("Snapchat", "com.snapchat.android", "com_snapchat_android"),
        new CustomIconInfo("Flipkart", "com.flipkart.android", "com_flipkart_android"),
        new CustomIconInfo("Myntra", "com.myntra.android", "com_myntra_android"),
        new CustomIconInfo("Instagram", "com.instagram.android", "com_instagram_android"),
        new CustomIconInfo("Google Play Store", "com.android.vending", "com_android_vending"),
        new CustomIconInfo("Spotify", "com.spotify.music", "com_spotify_music"),
        new CustomIconInfo("Telegram", "org.telegram.messenger", "org_telegram_messenger"),
        new CustomIconInfo("WhatsApp", "com.whatsapp", "com_whatsapp"),
        new CustomIconInfo("X / Twitter", "com.twitter.android", "com_twitter_android"),
        new CustomIconInfo("Discord", "com.discord", "com_discord"),
        new CustomIconInfo("Netflix", "com.netflix.mediaclient", "com_netflix_mediaclient"),
        new CustomIconInfo("Reddit", "com.reddit.frontpage", "com_reddit_frontpage"),
        new CustomIconInfo("Pinterest", "com.pinterest", "com_pinterest"),
        new CustomIconInfo("Uber", "com.ubercab", "com_ubercab"),
        new CustomIconInfo("Swiggy", "in.swiggy.android", "in_swiggy_android"),
        new CustomIconInfo("Zomato", "com.application.zomato", "com_application_zomato"),
        new CustomIconInfo("Google Chrome", "com.android.chrome", "com_android_chrome"),
        new CustomIconInfo("Google Maps", "com.google.android.apps.maps", "com_google_android_apps_maps"),
        new CustomIconInfo("Google Photos", "com.google.android.apps.photos", "com_google_android_apps_photos"),
        new CustomIconInfo("Gmail", "com.google.android.gm", "com_google_android_gm"),
        new CustomIconInfo("Vivaldi Browser", "com.vivaldi.browser", "com_vivaldi_browser"),
        new CustomIconInfo("Brave Browser", "com.brave.browser", "com_brave_browser"),
        new CustomIconInfo("Firefox", "org.mozilla.firefox", "org_mozilla_firefox"),
        new CustomIconInfo("Nothing Camera", "com.nothing.camera", "com_nothing_camera"),
        new CustomIconInfo("Nothing Weather", "com.nothing.weather", "com_nothing_weather"),
        new CustomIconInfo("Nothing Recorder", "com.nothing.soundrecorder", "com_nothing_soundrecorder"),
        new CustomIconInfo("Google Phone / Dialer", "com.google.android.dialer", "com_google_android_dialer"),
        new CustomIconInfo("Google Messages", "com.google.android.apps.messaging", "com_google_android_apps_messaging"),
        new CustomIconInfo("Google Clock", "com.google.android.deskclock", "com_google_android_deskclock"),
        new CustomIconInfo("Google Calendar", "com.google.android.calendar", "com_google_android_calendar"),
        new CustomIconInfo("Google Calculator", "com.google.android.calculator", "com_google_android_calculator"),
        new CustomIconInfo("Google Contacts", "com.google.android.contacts", "com_google_android_contacts"),
        new CustomIconInfo("Settings", "com.android.settings", "com_android_settings"),
        new CustomIconInfo("Steam", "com.valvesoftware.android.steam.community", "com_valvesoftware_android_steam_community"),
        new CustomIconInfo("Twitch", "tv.twitch.android.app", "tv_twitch_android_app"),
        new CustomIconInfo("Crunchyroll", "com.crunchyroll.crunchyroid", "com_crunchyroll_crunchyroid"),
        new CustomIconInfo("Anime Slayer", "com.app.animeslayer", "com_app_animeslayer"),
        new CustomIconInfo("Bleach Mobile", "com.bandainamcoent.bleach_mh_ww", "com_bandainamcoent_bleach_mh_ww"),
        new CustomIconInfo("Convx Music", "com.convx.music", "com_convx_music"),
        new CustomIconInfo("Google Keep", "com.google.android.keep", "com_google_android_keep"),
        new CustomIconInfo("Google Drive", "com.google.android.apps.docs", "com_google_android_apps_docs"),
        new CustomIconInfo("YouTube Music", "com.google.android.apps.youtube.music", "com_google_android_apps_youtube_music"),
        new CustomIconInfo("Gemini", "com.google.android.apps.bard", "com_google_android_apps_bard"),
        new CustomIconInfo("Nothing Gallery", "com.nothing.gallery", "com_nothing_gallery"),
        new CustomIconInfo("KWGT Pro", "org.kustom.widget.pro", "org_kustom_widget_pro"),
        new CustomIconInfo("VLC Media Player", "org.videolan.vlc", "org_videolan_vlc")
    };

    private static final String[] LOCAL_WALLPAPERS = {
        "wallpaper_01.jpg", "wallpaper_02.jpeg", "wallpaper_03.jpg", "wallpaper_04.png",
        "wallpaper_05.jpg", "wallpaper_06.png", "wallpaper_07.jpg", "wallpaper_08.png",
        "wallpaper_09.jpg", "wallpaper_10.png", "wallpaper_11.jpg", "wallpaper_12.png", "wallpaper_13.jpg"
    };

    public static class CloudWallpaper {
        public final String title;
        public final String fullUrl;
        public final String thumbUrl;
        public final String cacheKey;

        public CloudWallpaper(String title, String fullUrl, String thumbUrl, String cacheKey) {
            this.title = title;
            this.fullUrl = fullUrl;
            this.thumbUrl = thumbUrl;
            this.cacheKey = cacheKey;
        }
    }

    private final List<CloudWallpaper> driveItems = new ArrayList<>();
    private ScrollView mainScrollView;
    private View widgetsSectionAnchor;
    private View autoWpSectionAnchor;
    private View wallpapersSectionAnchor;
    private LinearLayout galleryDrive;
    private TextView statWallpapersCount;
    private TextView driveSectionBadge;
    private boolean isSyncing = false;
    private float density = 1.0f;

    // Live Auto Wallpaper UI references
    private TextView tvAutoWpStatus;
    private TextView tvCurrentWpTitle;
    private ImageView ivCurrentWpThumb;
    private List<Button> intervalButtons = new ArrayList<>();

    // Broadcast receiver for live wallpaper changes
    private final BroadcastReceiver wpChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && AutoWallpaperManager.ACTION_WALLPAPER_CHANGED.equals(intent.getAction())) {
                String title = intent.getStringExtra("title");
                String thumb = intent.getStringExtra("thumb");
                updateCurrentWallpaperUI(title, thumb);
            }
        }
    };

    // --- LIQUID GLASS UI HELPERS (APPLE iOS 26 × OBSIDIAN & CRIMSON) ---

    public static GradientDrawable createLiquidScreenBackground() {
        return new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                    Color.parseColor("#050505"), // Pure Black
                    Color.parseColor("#120406"), // Deep Obsidian Crimson
                    Color.parseColor("#050505")
                });
    }

    public static GradientDrawable createLiquidGlassCard(float cornerRadius) {
        GradientDrawable glass = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    Color.parseColor("#21FFFFFF"), // High optical clarity specular top
                    Color.parseColor("#0D050508"), // Ultra-translucent black glass base
                    Color.parseColor("#180A0406")  // Delicate obsidian crimson tint
                });
        glass.setCornerRadius(cornerRadius);
        glass.setStroke(3, Color.parseColor("#4DFFFFFF")); // Polished specular glass bevel rim
        return glass;
    }

    public static GradientDrawable createGlassPill(String bgColor, String strokeColor, float radius) {
        GradientDrawable pill = new GradientDrawable();
        pill.setColor(Color.parseColor(bgColor));
        pill.setCornerRadius(radius);
        pill.setStroke(2, Color.parseColor(strokeColor));
        return pill;
    }

    public static Button createLiquidRedButton(Activity activity, String text) {
        Button btn = new Button(activity);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.08f);

        GradientDrawable liquidGradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                    Color.parseColor("#FFDC143C"), // Crimson Red
                    Color.parseColor("#FF8B0000")  // Deep Crimson
                });
        liquidGradient.setCornerRadius(34);
        liquidGradient.setStroke(2, Color.parseColor("#80FF6B60"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setCornerRadius(34);
            mask.setColor(Color.WHITE);
            btn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")), liquidGradient, mask));
        } else {
            btn.setBackground(liquidGradient);
        }
        return btn;
    }

    public Button createLiquidGlassPillButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.05f);

        GradientDrawable glass = new GradientDrawable();
        glass.setColor(Color.parseColor("#1AFFFFFF"));
        glass.setCornerRadius(34);
        glass.setStroke(2, Color.parseColor("#59FFFFFF"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setCornerRadius(34);
            mask.setColor(Color.WHITE);
            btn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#33FFFFFF")), glass, mask));
        } else {
            btn.setBackground(glass);
        }
        btn.setOnClickListener(listener);
        return btn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getResources().getDisplayMetrics().density;

        mainScrollView = new ScrollView(this);
        mainScrollView.setBackground(createLiquidScreenBackground());
        mainScrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding((int)(16 * density), (int)(18 * density), (int)(16 * density), (int)(60 * density));
        mainScrollView.addView(root);

        // --- TOP BAR: MENU, TITLE, SHARE ---
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, 0, 0, (int)(16 * density));

        FrameLayout menuBtn = createTopBarCircularButton("ic_menu", new View.OnClickListener() {
            @Override public void onClick(View v) { showApplyGuideDialog(); }
        });
        topBar.addView(menuBtn);

        TextView topTitle = new TextView(this);
        topTitle.setText("RAVEN SHADE");
        topTitle.setTextColor(Color.WHITE);
        topTitle.setTextSize(18);
        topTitle.setTypeface(Typeface.DEFAULT_BOLD);
        topTitle.setLetterSpacing(0.16f);
        topTitle.setGravity(Gravity.CENTER);
        topTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        topBar.addView(topTitle);

        FrameLayout shareBtn = createTopBarCircularButton("ic_share", new View.OnClickListener() {
            @Override public void onClick(View v) { shareApp(); }
        });
        topBar.addView(shareBtn);
        root.addView(topBar);

        // =========================================================================
        // 1. BRANDING PRIORITY 1 & 2: OFFICIAL RAVEN SHADE HERO CARD
        // =========================================================================
        LinearLayout brandHeroCard = new LinearLayout(this);
        brandHeroCard.setOrientation(LinearLayout.VERTICAL);
        brandHeroCard.setGravity(Gravity.CENTER_HORIZONTAL);
        brandHeroCard.setPadding((int)(20 * density), (int)(24 * density), (int)(20 * density), (int)(24 * density));
        brandHeroCard.setBackground(createLiquidGlassCard(44));
        brandHeroCard.setElevation(14);

        // Logo Container with Ambient Crimson Glow & Specular Glass Chamfer
        FrameLayout logoContainer = new FrameLayout(this);
        int logoBoxSize = (int)(190 * density);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(logoBoxSize, logoBoxSize);
        logoLp.bottomMargin = (int)(18 * density);
        logoContainer.setLayoutParams(logoLp);

        // Ambient Crimson Halo Glow
        View haloView = new View(this);
        GradientDrawable haloDrawable = new GradientDrawable();
        haloDrawable.setShape(GradientDrawable.OVAL);
        haloDrawable.setColor(Color.parseColor("#33DC143C")); // Signature Crimson Glow
        haloDrawable.setStroke((int)(3 * density), Color.parseColor("#4DDC143C"));
        haloView.setBackground(haloDrawable);
        logoContainer.addView(haloView);

        // Official High-Res 1024x1024 Raven Shade Logo
        ImageView ivLogo = new ImageView(this);
        int logoRes = getResources().getIdentifier("raven_shade_logo", "drawable", getPackageName());
        if (logoRes == 0) logoRes = getResources().getIdentifier("app_icon", "drawable", getPackageName());
        if (logoRes != 0) ivLogo.setImageResource(logoRes);
        ivLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivLogo.setClipToOutline(true);
            ivLogo.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 32 * density);
                }
            });
        }
        FrameLayout.LayoutParams ivLogoLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int pad = (int)(8 * density);
        ivLogoLp.setMargins(pad, pad, pad, pad);
        ivLogo.setLayoutParams(ivLogoLp);
        logoContainer.addView(ivLogo);
        brandHeroCard.addView(logoContainer);

        // Name: RAVEN SHADE
        TextView tvBrandName = new TextView(this);
        tvBrandName.setText("RAVEN SHADE");
        tvBrandName.setTextColor(Color.WHITE);
        tvBrandName.setTextSize(26);
        tvBrandName.setTypeface(Typeface.DEFAULT_BOLD);
        tvBrandName.setLetterSpacing(0.20f);
        tvBrandName.setGravity(Gravity.CENTER);
        brandHeroCard.addView(tvBrandName);

        // Subtitle: Apple Liquid Glass Suite
        TextView tvBrandSub = new TextView(this);
        tvBrandSub.setText("APPLE LIQUID GLASS \u2022 CRIMSON & OBSIDIAN SUITE");
        tvBrandSub.setTextColor(Color.parseColor("#B8B8B8"));
        tvBrandSub.setTextSize(10);
        tvBrandSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvBrandSub.setLetterSpacing(0.10f);
        tvBrandSub.setGravity(Gravity.CENTER);
        tvBrandSub.setPadding(0, (int)(4 * density), 0, (int)(16 * density));
        brandHeroCard.addView(tvBrandSub);

        // Identity Badges (160 Icons | 3 Widgets | 100 Wallpapers)
        LinearLayout badgesRow = new LinearLayout(this);
        badgesRow.setOrientation(LinearLayout.HORIZONTAL);
        badgesRow.setGravity(Gravity.CENTER);
        badgesRow.addView(createGlassBadge("160 ICONS"));
        badgesRow.addView(createBadgeSpacer());
        badgesRow.addView(createGlassBadge("3 USABLE WIDGETS"));
        badgesRow.addView(createBadgeSpacer());
        badgesRow.addView(createGlassBadge("100 DRIVE WALLPAPERS"));
        brandHeroCard.addView(badgesRow);

        // Main App Controls: Quick Navigation Bar
        LinearLayout quickNavRow = new LinearLayout(this);
        quickNavRow.setOrientation(LinearLayout.HORIZONTAL);
        quickNavRow.setGravity(Gravity.CENTER);
        quickNavRow.setPadding(0, (int)(20 * density), 0, 0);

        Button btnQuickApply = createLiquidRedButton(this, "\u26A1 Apply Pack");
        btnQuickApply.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        btnQuickApply.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showApplyLauncherChooser(); }
        });
        quickNavRow.addView(btnQuickApply);

        View qSpacer1 = new View(this);
        qSpacer1.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        quickNavRow.addView(qSpacer1);

        Button btnQuickWidget = createLiquidGlassPillButton("✨ Edit Widgets", new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (widgetsSectionAnchor != null) mainScrollView.smoothScrollTo(0, widgetsSectionAnchor.getTop() - 20);
            }
        });
        btnQuickWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        quickNavRow.addView(btnQuickWidget);

        View qSpacer2 = new View(this);
        qSpacer2.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        quickNavRow.addView(qSpacer2);

        Button btnQuickWp = createLiquidGlassPillButton("🖼 Auto Wallpapers", new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (autoWpSectionAnchor != null) mainScrollView.smoothScrollTo(0, autoWpSectionAnchor.getTop() - 20);
            }
        });
        btnQuickWp.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        quickNavRow.addView(btnQuickWp);

        brandHeroCard.addView(quickNavRow);
        root.addView(brandHeroCard);

        // =========================================================================
        // 2. BRANDING PRIORITY 4: WIDGET CUSTOMIZATION STUDIO (LIVE VISUAL EDITOR)
        // =========================================================================
        widgetsSectionAnchor = createSectionTitle("WIDGET CUSTOMIZATION STUDIO");
        widgetsSectionAnchor.setPadding(12, (int)(36 * density), 12, (int)(8 * density));
        root.addView(widgetsSectionAnchor);

        TextView widgetStudioSub = new TextView(this);
        widgetStudioSub.setText("Real-time Apple iOS 26 Liquid Glass Editor \u2022 Changes sync to Home Screen");
        widgetStudioSub.setTextColor(Color.parseColor("#B8B8B8"));
        widgetStudioSub.setTextSize(12);
        widgetStudioSub.setPadding(16, 0, 16, (int)(16 * density));
        root.addView(widgetStudioSub);

        // Live Interactive Liquid Glass Preview Card
        final LinearLayout previewCard = new LinearLayout(this);
        previewCard.setOrientation(LinearLayout.VERTICAL);
        previewCard.setPadding((int)(20 * density), (int)(18 * density), (int)(20 * density), (int)(18 * density));
        previewCard.setBackground(createLiquidGlassCard(28));
        previewCard.setElevation(8);

        // Preview Header: Title & Subtitle
        LinearLayout prevHeader = new LinearLayout(this);
        prevHeader.setOrientation(LinearLayout.HORIZONTAL);
        prevHeader.setGravity(Gravity.CENTER_VERTICAL);

        final TextView prevTitle = new TextView(this);
        prevTitle.setText(WidgetSettingsManager.getTitle(this));
        prevTitle.setTextColor(Color.parseColor("#DC143C")); // Crimson
        prevTitle.setTextSize(14);
        prevTitle.setTypeface(Typeface.DEFAULT_BOLD);
        prevTitle.setLetterSpacing(0.10f);
        prevTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        prevHeader.addView(prevTitle);

        final TextView prevSub = new TextView(this);
        prevSub.setText(WidgetSettingsManager.getSubtitle(this));
        prevSub.setTextColor(Color.parseColor("#CCFFFFFF"));
        prevSub.setTextSize(11);
        prevSub.setTypeface(Typeface.DEFAULT_BOLD);
        prevHeader.addView(prevSub);
        previewCard.addView(prevHeader);

        // Preview Center: Real-time Clock & Date
        LinearLayout prevClockRow = new LinearLayout(this);
        prevClockRow.setOrientation(LinearLayout.HORIZONTAL);
        prevClockRow.setGravity(Gravity.CENTER_VERTICAL);
        prevClockRow.setPadding(0, (int)(10 * density), 0, (int)(10 * density));

        Date now = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE | d MMM", Locale.getDefault());

        TextView prevClock = new TextView(this);
        prevClock.setText(timeFmt.format(now));
        prevClock.setTextColor(Color.WHITE);
        prevClock.setTextSize(34);
        prevClock.setTypeface(Typeface.DEFAULT_BOLD);
        prevClockRow.addView(prevClock);

        LinearLayout prevDateCol = new LinearLayout(this);
        prevDateCol.setOrientation(LinearLayout.VERTICAL);
        prevDateCol.setPadding((int)(14 * density), 0, 0, 0);

        TextView prevDate = new TextView(this);
        prevDate.setText(dateFmt.format(now));
        prevDate.setTextColor(Color.parseColor("#E6FFFFFF"));
        prevDate.setTextSize(13);
        prevDate.setTypeface(Typeface.DEFAULT_BOLD);
        prevDateCol.addView(prevDate);

        final TextView prevTag = new TextView(this);
        prevTag.setText(WidgetSettingsManager.getTag(this));
        prevTag.setTextColor(Color.parseColor("#80FF5A73"));
        prevTag.setTextSize(9);
        prevTag.setLetterSpacing(0.06f);
        prevTag.setPadding(0, (int)(2 * density), 0, 0);
        prevTag.setVisibility(WidgetSettingsManager.getShowTag(this) ? View.VISIBLE : View.GONE);
        prevDateCol.addView(prevTag);

        prevClockRow.addView(prevDateCol);
        previewCard.addView(prevClockRow);

        // Preview Bottom: Battery Bar
        final LinearLayout prevBatContainer = new LinearLayout(this);
        prevBatContainer.setOrientation(LinearLayout.VERTICAL);
        prevBatContainer.setVisibility(WidgetSettingsManager.getShowBattery(this) ? View.VISIBLE : View.GONE);

        LinearLayout prevBatTextRow = new LinearLayout(this);
        prevBatTextRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView prevBatLabel = new TextView(this);
        prevBatLabel.setText("BATTERY STATUS");
        prevBatLabel.setTextColor(Color.parseColor("#99FFFFFF"));
        prevBatLabel.setTextSize(10);
        prevBatLabel.setTypeface(Typeface.DEFAULT_BOLD);
        prevBatLabel.setLetterSpacing(0.05f);
        prevBatLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        prevBatTextRow.addView(prevBatLabel);

        TextView prevBatPct = new TextView(this);
        prevBatPct.setText("85%");
        prevBatPct.setTextColor(Color.parseColor("#DC143C"));
        prevBatPct.setTextSize(11);
        prevBatPct.setTypeface(Typeface.DEFAULT_BOLD);
        prevBatTextRow.addView(prevBatPct);
        prevBatContainer.addView(prevBatTextRow);

        ProgressBar prevBatBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        prevBatBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(6 * density)));
        int trackRes = getResources().getIdentifier("bg_battery_track", "drawable", getPackageName());
        int progRes = getResources().getIdentifier("bg_battery_progress", "drawable", getPackageName());
        if (trackRes != 0) prevBatBar.setBackgroundResource(trackRes);
        if (progRes != 0) prevBatBar.setProgressDrawable(getResources().getDrawable(progRes));
        prevBatBar.setProgress(85);
        prevBatContainer.addView(prevBatBar);
        previewCard.addView(prevBatContainer);

        root.addView(previewCard);

        // Widget Customization Controls Form Card
        LinearLayout formCard = new LinearLayout(this);
        formCard.setOrientation(LinearLayout.VERTICAL);
        formCard.setPadding((int)(18 * density), (int)(18 * density), (int)(18 * density), (int)(18 * density));
        formCard.setBackground(createLiquidGlassCard(32));
        formCard.setElevation(6);
        LinearLayout.LayoutParams formLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        formLp.topMargin = (int)(14 * density);
        formCard.setLayoutParams(formLp);

        // 1. Edit Title
        formCard.addView(createFieldLabel("WIDGET TITLE"));
        final EditText etTitle = createGlassEditText(WidgetSettingsManager.getTitle(this));
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                prevTitle.setText(s.toString().isEmpty() ? "RAVEN SHADE" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        formCard.addView(etTitle);

        // 2. Edit Subtitle
        formCard.addView(createFieldLabel("SUBTITLE / GLASS BADGE"));
        final EditText etSub = createGlassEditText(WidgetSettingsManager.getSubtitle(this));
        etSub.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                prevSub.setText(s.toString().isEmpty() ? "OBSIDIAN LIQUID GLASS" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        formCard.addView(etSub);

        // 3. Edit Custom Tag
        formCard.addView(createFieldLabel("CUSTOM TAG LABEL"));
        final EditText etTag = createGlassEditText(WidgetSettingsManager.getTag(this));
        etTag.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                prevTag.setText(s.toString().isEmpty() ? "RAVENSHADE EDITION" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        formCard.addView(etTag);

        // 4. Toggle Switches Row (Show Tag & Show Battery)
        LinearLayout switchesRow = new LinearLayout(this);
        switchesRow.setOrientation(LinearLayout.HORIZONTAL);
        switchesRow.setPadding(0, (int)(12 * density), 0, (int)(12 * density));

        final Switch switchTag = new Switch(this);
        switchTag.setText("Show Tag  ");
        switchTag.setTextColor(Color.WHITE);
        switchTag.setChecked(WidgetSettingsManager.getShowTag(this));
        switchTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean isChecked) {
                prevTag.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        switchesRow.addView(switchTag);

        View sSpacer = new View(this);
        sSpacer.setLayoutParams(new LinearLayout.LayoutParams((int)(20 * density), 1));
        switchesRow.addView(sSpacer);

        final Switch switchBat = new Switch(this);
        switchBat.setText("Show Battery  ");
        switchBat.setTextColor(Color.WHITE);
        switchBat.setChecked(WidgetSettingsManager.getShowBattery(this));
        switchBat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean isChecked) {
                prevBatContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        switchesRow.addView(switchBat);
        formCard.addView(switchesRow);

        // Form Buttons: Save & Sync + Reset
        LinearLayout formBtnRow = new LinearLayout(this);
        formBtnRow.setOrientation(LinearLayout.HORIZONTAL);
        formBtnRow.setPadding(0, (int)(8 * density), 0, 0);

        Button btnSaveWidget = createLiquidRedButton(this, "💾 Save & Sync Widgets");
        btnSaveWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.6f));
        btnSaveWidget.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                WidgetSettingsManager.setTitle(MainActivity.this, etTitle.getText().toString().trim());
                WidgetSettingsManager.setSubtitle(MainActivity.this, etSub.getText().toString().trim());
                WidgetSettingsManager.setTag(MainActivity.this, etTag.getText().toString().trim());
                WidgetSettingsManager.setShowTag(MainActivity.this, switchTag.isChecked());
                WidgetSettingsManager.setShowBattery(MainActivity.this, switchBat.isChecked());
                WidgetSettingsManager.updateAllWidgets(MainActivity.this);
                Toast.makeText(MainActivity.this, "✓ Widget settings saved & synced to Home Screen!", Toast.LENGTH_SHORT).show();
            }
        });
        formBtnRow.addView(btnSaveWidget);

        View fbSpacer = new View(this);
        fbSpacer.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        formBtnRow.addView(fbSpacer);

        Button btnResetWidget = createLiquidGlassPillButton("↺ Reset", new View.OnClickListener() {
            @Override public void onClick(View v) {
                WidgetSettingsManager.resetDefaults(MainActivity.this);
                etTitle.setText(WidgetSettingsManager.DEFAULT_TITLE);
                etSub.setText(WidgetSettingsManager.DEFAULT_SUBTITLE);
                etTag.setText(WidgetSettingsManager.DEFAULT_TAG);
                switchTag.setChecked(true);
                switchBat.setChecked(true);
                Toast.makeText(MainActivity.this, "↺ Reset to Raven Shade defaults", Toast.LENGTH_SHORT).show();
            }
        });
        btnResetWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        formBtnRow.addView(btnResetWidget);

        formCard.addView(formBtnRow);
        root.addView(formCard);

        // Horizontal Row of Usable Widgets (+ Add to Home Screen)
        HorizontalScrollView hsvWidgets = new HorizontalScrollView(this);
        hsvWidgets.setHorizontalScrollBarEnabled(false);
        hsvWidgets.setPadding(0, (int)(18 * density), 0, (int)(18 * density));

        LinearLayout widgetsRow = new LinearLayout(this);
        widgetsRow.setOrientation(LinearLayout.HORIZONTAL);

        widgetsRow.addView(createWidgetPreviewCard(
            "Black Raven Clock (4x2)",
            "Digital Clock \u2022 Date \u2022 Live Battery Bar",
            BlackRavenWidgetProvider.class,
            1
        ));

        widgetsRow.addView(createWidgetPreviewCard(
            "Quick App Dock (4x1)",
            "Obsidian Dock \u2022 5 Quick Launch Shortcuts",
            QuickDockWidgetProvider.class,
            2
        ));

        widgetsRow.addView(createWidgetPreviewCard(
            "Anime Mini Clock (2x2)",
            "Compact Obsidian & Crimson Square Clock",
            AnimeClockWidgetProvider.class,
            3
        ));

        hsvWidgets.addView(widgetsRow);
        root.addView(hsvWidgets);

        // =========================================================================
        // 3. BRANDING PRIORITY 5: AUTOMATIC WALLPAPER CHANGER (GOOGLE DRIVE)
        // =========================================================================
        autoWpSectionAnchor = createSectionTitle("AUTOMATIC WALLPAPER CHANGER");
        autoWpSectionAnchor.setPadding(12, (int)(36 * density), 12, (int)(8 * density));
        root.addView(autoWpSectionAnchor);

        TextView autoWpSub = new TextView(this);
        autoWpSub.setText("Powered by your Google Drive collection \u2022 High-Res Random Shuffle");
        autoWpSub.setTextColor(Color.parseColor("#B8B8B8"));
        autoWpSub.setTextSize(12);
        autoWpSub.setPadding(16, 0, 16, (int)(16 * density));
        root.addView(autoWpSub);

        // Auto Wallpaper Master Card
        LinearLayout autoWpCard = new LinearLayout(this);
        autoWpCard.setOrientation(LinearLayout.VERTICAL);
        autoWpCard.setPadding((int)(20 * density), (int)(20 * density), (int)(20 * density), (int)(20 * density));
        autoWpCard.setBackground(createLiquidGlassCard(36));
        autoWpCard.setElevation(8);

        // Master Switch Header
        LinearLayout masterRow = new LinearLayout(this);
        masterRow.setOrientation(LinearLayout.HORIZONTAL);
        masterRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout masterTextCol = new LinearLayout(this);
        masterTextCol.setOrientation(LinearLayout.VERTICAL);
        masterTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvMasterTitle = new TextView(this);
        tvMasterTitle.setText("Auto-Change Wallpapers");
        tvMasterTitle.setTextColor(Color.WHITE);
        tvMasterTitle.setTextSize(16);
        tvMasterTitle.setTypeface(Typeface.DEFAULT_BOLD);
        masterTextCol.addView(tvMasterTitle);

        tvAutoWpStatus = new TextView(this);
        tvAutoWpStatus.setText(AutoWallpaperManager.isAutoEnabled(this)
                ? ("\u25CF Active: Every " + AutoWallpaperManager.getIntervalMinutes(this) + " mins")
                : "\u25CB Inactive (Manual only)");
        tvAutoWpStatus.setTextColor(AutoWallpaperManager.isAutoEnabled(this) ? Color.parseColor("#44D62C") : Color.parseColor("#8E8E93"));
        tvAutoWpStatus.setTextSize(11);
        tvAutoWpStatus.setPadding(0, 2, 0, 0);
        masterTextCol.addView(tvAutoWpStatus);
        masterRow.addView(masterTextCol);

        final Switch switchAutoWp = new Switch(this);
        switchAutoWp.setChecked(AutoWallpaperManager.isAutoEnabled(this));
        switchAutoWp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean isChecked) {
                AutoWallpaperManager.setAutoEnabled(MainActivity.this, isChecked);
                tvAutoWpStatus.setText(isChecked
                        ? ("\u25CF Active: Every " + AutoWallpaperManager.getIntervalMinutes(MainActivity.this) + " mins")
                        : "\u25CB Inactive (Manual only)");
                tvAutoWpStatus.setTextColor(isChecked ? Color.parseColor("#44D62C") : Color.parseColor("#8E8E93"));
                Toast.makeText(MainActivity.this, isChecked ? "✓ Automatic wallpaper changer enabled" : "Automatic wallpaper changer paused", Toast.LENGTH_SHORT).show();
            }
        });
        masterRow.addView(switchAutoWp);
        autoWpCard.addView(masterRow);

        // Divider
        View awpDiv1 = new View(this);
        awpDiv1.setBackgroundColor(Color.parseColor("#26FFFFFF"));
        LinearLayout.LayoutParams awpDivLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        awpDivLp.setMargins(0, (int)(16 * density), 0, (int)(16 * density));
        awpDiv1.setLayoutParams(awpDivLp);
        autoWpCard.addView(awpDiv1);

        // Interval Segmented Buttons Row
        autoWpCard.addView(createFieldLabel("SCHEDULING INTERVAL"));

        HorizontalScrollView hsvIntervals = new HorizontalScrollView(this);
        hsvIntervals.setHorizontalScrollBarEnabled(false);
        LinearLayout intervalsRow = new LinearLayout(this);
        intervalsRow.setOrientation(LinearLayout.HORIZONTAL);

        final int[] intervals = {5, 10, 30, 60, 1440};
        final String[] intervalLabels = {"5 Min", "10 Min", "30 Min", "1 Hour", "24 Hours (Daily)"};

        for (int i = 0; i < intervals.length; i++) {
            final int interval = intervals[i];
            final Button iBtn = new Button(this);
            iBtn.setText(intervalLabels[i]);
            iBtn.setTextSize(11);
            iBtn.setTypeface(Typeface.DEFAULT_BOLD);
            iBtn.setPadding((int)(16 * density), (int)(8 * density), (int)(16 * density), (int)(8 * density));
            styleIntervalButton(iBtn, interval == AutoWallpaperManager.getIntervalMinutes(this));

            iBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AutoWallpaperManager.setIntervalMinutes(MainActivity.this, interval);
                    for (int j = 0; j < intervalButtons.size(); j++) {
                        styleIntervalButton(intervalButtons.get(j), intervals[j] == interval);
                    }
                    if (AutoWallpaperManager.isAutoEnabled(MainActivity.this)) {
                        tvAutoWpStatus.setText("\u25CF Active: Every " + interval + " mins");
                    }
                    Toast.makeText(MainActivity.this, "Interval set to " + interval + " minutes", Toast.LENGTH_SHORT).show();
                }
            });
            intervalButtons.add(iBtn);
            LinearLayout.LayoutParams iLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, (int)(38 * density));
            iLp.rightMargin = (int)(8 * density);
            iBtn.setLayoutParams(iLp);
            intervalsRow.addView(iBtn);
        }
        hsvIntervals.addView(intervalsRow);
        autoWpCard.addView(hsvIntervals);

        // Manual Controls Bar (Shuffle Now, Prev, Next)
        LinearLayout manualBar = new LinearLayout(this);
        manualBar.setOrientation(LinearLayout.HORIZONTAL);
        manualBar.setPadding(0, (int)(20 * density), 0, 0);

        Button btnShuffleNow = createLiquidRedButton(this, "🔀 Change Wallpaper Now");
        btnShuffleNow.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 2.0f));
        btnShuffleNow.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Retrieving random wallpaper from Google Drive...", Toast.LENGTH_SHORT).show();
                AutoWallpaperManager.changeWallpaper(MainActivity.this, true, 0, new AutoWallpaperManager.WallpaperCallback() {
                    @Override public void onSuccess(final String title) {
                        Toast.makeText(MainActivity.this, "✓ Applied: " + title, Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onError(final String msg) {
                        Toast.makeText(MainActivity.this, "Failed: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        manualBar.addView(btnShuffleNow);

        View mSpacer1 = new View(this);
        mSpacer1.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        manualBar.addView(mSpacer1);

        Button btnPrevWp = createLiquidGlassPillButton("◀", new View.OnClickListener() {
            @Override public void onClick(View v) {
                AutoWallpaperManager.changeWallpaper(MainActivity.this, false, -1, null);
                Toast.makeText(MainActivity.this, "Applying previous wallpaper...", Toast.LENGTH_SHORT).show();
            }
        });
        btnPrevWp.setLayoutParams(new LinearLayout.LayoutParams((int)(46 * density), (int)(46 * density)));
        manualBar.addView(btnPrevWp);

        View mSpacer2 = new View(this);
        mSpacer2.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        manualBar.addView(mSpacer2);

        Button btnNextWp = createLiquidGlassPillButton("▶", new View.OnClickListener() {
            @Override public void onClick(View v) {
                AutoWallpaperManager.changeWallpaper(MainActivity.this, false, 1, null);
                Toast.makeText(MainActivity.this, "Applying next wallpaper...", Toast.LENGTH_SHORT).show();
            }
        });
        btnNextWp.setLayoutParams(new LinearLayout.LayoutParams((int)(46 * density), (int)(46 * density)));
        manualBar.addView(btnNextWp);

        autoWpCard.addView(manualBar);

        // Current Active Wallpaper Card
        LinearLayout currentWpCard = new LinearLayout(this);
        currentWpCard.setOrientation(LinearLayout.HORIZONTAL);
        currentWpCard.setGravity(Gravity.CENTER_VERTICAL);
        currentWpCard.setPadding((int)(12 * density), (int)(10 * density), (int)(12 * density), (int)(10 * density));
        currentWpCard.setBackground(createGlassPill("#12FFFFFF", "#26FFFFFF", 20 * density));
        LinearLayout.LayoutParams cWpLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cWpLp.topMargin = (int)(16 * density);
        currentWpCard.setLayoutParams(cWpLp);

        ivCurrentWpThumb = new ImageView(this);
        int thumbSz = (int)(40 * density);
        LinearLayout.LayoutParams thumbLp = new LinearLayout.LayoutParams(thumbSz, thumbSz);
        thumbLp.rightMargin = (int)(12 * density);
        ivCurrentWpThumb.setLayoutParams(thumbLp);
        ivCurrentWpThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivCurrentWpThumb.setImageResource(getResources().getIdentifier("app_icon", "drawable", getPackageName()));
        currentWpCard.addView(ivCurrentWpThumb);

        LinearLayout currentWpTextCol = new LinearLayout(this);
        currentWpTextCol.setOrientation(LinearLayout.VERTICAL);

        TextView tvCurrentWpHeader = new TextView(this);
        tvCurrentWpHeader.setText("CURRENT ACTIVE WALLPAPER");
        tvCurrentWpHeader.setTextColor(Color.parseColor("#99FFFFFF"));
        tvCurrentWpHeader.setTextSize(9);
        tvCurrentWpHeader.setLetterSpacing(0.06f);
        currentWpTextCol.addView(tvCurrentWpHeader);

        tvCurrentWpTitle = new TextView(this);
        tvCurrentWpTitle.setText(AutoWallpaperManager.getCurrentTitle(this));
        tvCurrentWpTitle.setTextColor(Color.WHITE);
        tvCurrentWpTitle.setTextSize(13);
        tvCurrentWpTitle.setTypeface(Typeface.DEFAULT_BOLD);
        currentWpTextCol.addView(tvCurrentWpTitle);

        currentWpCard.addView(currentWpTextCol);
        autoWpCard.addView(currentWpCard);

        root.addView(autoWpCard);

        // =========================================================================
        // 4. WALLPAPERS VAULT (LIVE CLOUD + BUNDLED)
        // =========================================================================
        wallpapersSectionAnchor = createSectionTitle("WALLPAPERS VAULT");
        wallpapersSectionAnchor.setPadding(12, (int)(36 * density), 12, (int)(14 * density));
        root.addView(wallpapersSectionAnchor);

        List<CloudWallpaper> initialItems = loadInitialDriveWallpapers();
        driveItems.addAll(initialItems);

        LinearLayout driveSectionHeader = createGallerySubHeader("GOOGLE DRIVE CLOUD", driveItems.size(), true);
        driveSectionHeader.setPadding(12, 8, 12, (int)(18 * density));
        root.addView(driveSectionHeader);

        HorizontalScrollView hsvDrive = new HorizontalScrollView(this);
        hsvDrive.setHorizontalScrollBarEnabled(false);
        hsvDrive.setPadding(0, 0, 0, (int)(18 * density));

        galleryDrive = new LinearLayout(this);
        galleryDrive.setOrientation(LinearLayout.HORIZONTAL);
        populateDriveCards();
        hsvDrive.addView(galleryDrive);
        root.addView(hsvDrive);

        LinearLayout localSectionHeader = createGallerySubHeader("BUNDLED VAULT", LOCAL_WALLPAPERS.length, false);
        localSectionHeader.setPadding(12, (int)(24 * density), 12, (int)(18 * density));
        root.addView(localSectionHeader);

        HorizontalScrollView hsvLocal = new HorizontalScrollView(this);
        hsvLocal.setHorizontalScrollBarEnabled(false);
        hsvLocal.setPadding(0, 0, 0, (int)(24 * density));

        LinearLayout galleryLocal = new LinearLayout(this);
        galleryLocal.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < LOCAL_WALLPAPERS.length; i++) {
            final String fileName = LOCAL_WALLPAPERS[i];
            final int index = i + 1;
            galleryLocal.addView(createLiquidWallpaperCard("Wallpaper #" + index, null, fileName, null));
        }
        hsvLocal.addView(galleryLocal);
        root.addView(hsvLocal);

        // =========================================================================
        // 5. HANDCRAFTED ICON VAULT (CAROUSEL + EXPLORE + SUPPORT)
        // =========================================================================
        View iconsSectionAnchor = createSectionTitle("HANDCRAFTED ICON VAULT");
        iconsSectionAnchor.setPadding(12, (int)(28 * density), 12, (int)(14 * density));
        root.addView(iconsSectionAnchor);

        LinearLayout heroCard = new LinearLayout(this);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        heroCard.setPadding((int)(20 * density), (int)(20 * density), (int)(20 * density), (int)(20 * density));
        heroCard.setBackground(createLiquidGlassCard(36));
        heroCard.setElevation(8);

        HorizontalScrollView hsvIcons = new HorizontalScrollView(this);
        hsvIcons.setHorizontalScrollBarEnabled(false);
        LinearLayout showcaseRow = new LinearLayout(this);
        showcaseRow.setOrientation(LinearLayout.HORIZONTAL);
        showcaseRow.setGravity(Gravity.CENTER_VERTICAL);

        for (int i = 0; i < CUSTOM_ICONS.length; i++) {
            showcaseRow.addView(createShowcaseIconItem(CUSTOM_ICONS[i]));
        }
        hsvIcons.addView(showcaseRow);
        heroCard.addView(hsvIcons);

        root.addView(heroCard);

        setContentView(mainScrollView);

        // Register wallpaper receiver
        IntentFilter filter = new IntentFilter(AutoWallpaperManager.ACTION_WALLPAPER_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(wpChangedReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(wpChangedReceiver, filter);
        }

        syncLiveDriveWallpapers(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(wpChangedReceiver); } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncLiveDriveWallpapers(false);
    }

    private void styleIntervalButton(Button btn, boolean isSelected) {
        GradientDrawable bg = new GradientDrawable();
        if (isSelected) {
            bg.setColor(Color.parseColor("#FFDC143C")); // Crimson Red
            bg.setStroke(2, Color.parseColor("#FFFF6B60"));
            btn.setTextColor(Color.WHITE);
        } else {
            bg.setColor(Color.parseColor("#14FFFFFF"));
            bg.setStroke(2, Color.parseColor("#33FFFFFF"));
            btn.setTextColor(Color.parseColor("#D4D4D8"));
        }
        bg.setCornerRadius(24);
        btn.setBackground(bg);
    }

    private void updateCurrentWallpaperUI(final String title, final String thumbUrl) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (tvCurrentWpTitle != null && title != null) {
                    tvCurrentWpTitle.setText(title);
                }
                if (ivCurrentWpThumb != null && thumbUrl != null) {
                    loadThumbnailInto(thumbUrl, ivCurrentWpThumb);
                }
            }
        });
    }

    private View createGlassBadge(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(10);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.06f);
        tv.setPadding((int)(12 * density), (int)(6 * density), (int)(12 * density), (int)(6 * density));
        tv.setBackground(createGlassPill("#18FFFFFF", "#3DFFFFFF", 18 * density));
        return tv;
    }

    private View createBadgeSpacer() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        return v;
    }

    private TextView createFieldLabel(String label) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(Color.parseColor("#DC143C")); // Signature Crimson
        tv.setTextSize(11);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.08f);
        tv.setPadding(4, (int)(10 * density), 4, (int)(6 * density));
        return tv;
    }

    private EditText createGlassEditText(String initialText) {
        EditText et = new EditText(this);
        et.setText(initialText);
        et.setTextColor(Color.WHITE);
        et.setTextSize(14);
        et.setPadding((int)(14 * density), (int)(12 * density), (int)(14 * density), (int)(12 * density));
        et.setBackground(createGlassPill("#14FFFFFF", "#3DFFFFFF", 14 * density));
        return et;
    }

    private FrameLayout createTopBarCircularButton(String iconName, View.OnClickListener listener) {
        FrameLayout btn = new FrameLayout(this);
        btn.setClickable(true);
        btn.setFocusable(true);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(Color.parseColor("#33150608"));
        bg.setStroke(2, Color.parseColor("#4DFF3322"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#33FFFFFF")), bg, null));
        } else {
            btn.setBackground(bg);
        }

        ImageView iv = new ImageView(this);
        int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (resId != 0) iv.setImageResource(resId);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams((int)(20 * density), (int)(20 * density));
        ivLp.gravity = Gravity.CENTER;
        iv.setLayoutParams(ivLp);
        btn.addView(iv);

        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams((int)(40 * density), (int)(40 * density));
        btn.setLayoutParams(btnLp);
        btn.setOnClickListener(listener);
        return btn;
    }

    private View createShowcaseIconItem(final CustomIconInfo info) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding((int)(8 * density), 0, (int)(8 * density), 0);

        ImageView iv = new ImageView(this);
        int size = (int)(62 * density);
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(size, size);
        iv.setLayoutParams(ivLp);

        int resId = getResources().getIdentifier(info.drawableName, "drawable", getPackageName());
        if (resId != 0) iv.setImageResource(resId);
        item.addView(iv);

        TextView tv = new TextView(this);
        tv.setText(info.name);
        tv.setTextColor(Color.parseColor("#E0E0E0"));
        tv.setTextSize(10);
        tv.setGravity(Gravity.CENTER);
        tv.setMaxLines(1);
        tv.setPadding(0, 4, 0, 0);
        item.addView(tv);

        item.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showSingleIconDetail(info); }
        });
        return item;
    }

    private View createSectionTitle(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.12f);
        return tv;
    }

    private LinearLayout createGallerySubHeader(String title, int count, boolean isCloud) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(Color.parseColor("#FF5449"));
        tv.setTextSize(12);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.08f);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        row.addView(tv);

        TextView badge = new TextView(this);
        badge.setText(count + " ITEMS");
        badge.setTextColor(Color.parseColor("#B8AAAA"));
        badge.setTextSize(10);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        row.addView(badge);
        return row;
    }

    private void populateDriveCards() {
        if (galleryDrive == null) return;
        galleryDrive.removeAllViews();
        for (int i = 0; i < driveItems.size(); i++) {
            final CloudWallpaper item = driveItems.get(i);
            galleryDrive.addView(createLiquidWallpaperCard(item.title, item.thumbUrl, null, item.fullUrl));
        }
    }

    private View createLiquidWallpaperCard(final String title, final String thumbUrl, final String localAsset, final String fullUrl) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int w = (int)(140 * density);
        int h = (int)(220 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        lp.rightMargin = (int)(12 * density);
        card.setLayoutParams(lp);
        card.setBackground(createLiquidGlassCard(24));
        card.setPadding(6, 6, 6, 6);

        final ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setClipToOutline(true);
            iv.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20 * density);
                }
            });
        }

        if (localAsset != null) {
            try {
                InputStream is = getAssets().open("wallpapers/" + localAsset);
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                iv.setImageBitmap(bm);
            } catch (Exception ignored) {}
        } else if (thumbUrl != null) {
            loadThumbnailInto(thumbUrl, iv);
        }

        card.addView(iv);
        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WallpaperViewerActivity.class);
                intent.putExtra("title", title);
                if (localAsset != null) intent.putExtra("local_asset", localAsset);
                if (fullUrl != null) intent.putExtra("full_url", fullUrl);
                startActivity(intent);
            }
        });
        return card;
    }

    private void loadThumbnailInto(final String urlStr, final ImageView iv) {
        THREAD_POOL.execute(new Runnable() {
            @Override public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(12000);
                    if (conn.getResponseCode() == 200) {
                        final Bitmap bm = BitmapFactory.decodeStream(conn.getInputStream());
                        runOnUiThread(new Runnable() {
                            @Override public void run() { iv.setImageBitmap(bm); }
                        });
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private View createWidgetPreviewCard(final String title, final String subtitle, final Class<?> providerClass, final int type) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int cardWidth = (int)(290 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, (int)(14 * density), 0);
        card.setLayoutParams(lp);
        card.setBackground(createLiquidGlassCard(32));
        card.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));

        LinearLayout preview = new LinearLayout(this);
        preview.setOrientation(LinearLayout.VERTICAL);
        preview.setPadding((int)(14 * density), (int)(14 * density), (int)(14 * density), (int)(14 * density));
        preview.setBackground(createGlassPill("#12FFFFFF", "#38FFFFFF", 20 * density));
        preview.setGravity(Gravity.CENTER);

        Date now = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE | d MMM", Locale.getDefault());

        if (type == 1) {
            LinearLayout head = new LinearLayout(this);
            head.setOrientation(LinearLayout.HORIZONTAL);
            TextView t = new TextView(this);
            t.setText(WidgetSettingsManager.getTitle(this));
            t.setTextColor(Color.parseColor("#DC143C"));
            t.setTextSize(12);
            t.setTypeface(Typeface.DEFAULT_BOLD);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            head.addView(t);

            TextView g = new TextView(this);
            g.setText(WidgetSettingsManager.getSubtitle(this));
            g.setTextColor(Color.parseColor("#CCFFFFFF"));
            g.setTextSize(10);
            head.addView(g);
            preview.addView(head);

            TextView clk = new TextView(this);
            clk.setText(timeFmt.format(now));
            clk.setTextColor(Color.WHITE);
            clk.setTextSize(28);
            clk.setTypeface(Typeface.DEFAULT_BOLD);
            preview.addView(clk);
        } else if (type == 2) {
            LinearLayout dock = new LinearLayout(this);
            dock.setOrientation(LinearLayout.HORIZONTAL);
            dock.setGravity(Gravity.CENTER);
            String[] icons = {"com_google_android_dialer", "com_whatsapp", "com_vivaldi_browser", "com_convx_music", "com_nothing_camera"};
            int sz = (int)(38 * density);
            for (String ic : icons) {
                int res = getResources().getIdentifier(ic, "drawable", getPackageName());
                if (res != 0) {
                    ImageView iv = new ImageView(this);
                    LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(sz, sz);
                    ivLp.setMargins(6, 0, 6, 0);
                    iv.setLayoutParams(ivLp);
                    iv.setImageResource(res);
                    dock.addView(iv);
                }
            }
            preview.addView(dock);
        } else {
            TextView b = new TextView(this);
            b.setText(WidgetSettingsManager.getTitle(this));
            b.setTextColor(Color.parseColor("#DC143C"));
            b.setTextSize(11);
            b.setTypeface(Typeface.DEFAULT_BOLD);
            preview.addView(b);

            TextView clk = new TextView(this);
            clk.setText(timeFmt.format(now));
            clk.setTextColor(Color.WHITE);
            clk.setTextSize(24);
            clk.setTypeface(Typeface.DEFAULT_BOLD);
            preview.addView(clk);
        }
        card.addView(preview);

        TextView wTitle = new TextView(this);
        wTitle.setText(title);
        wTitle.setTextColor(Color.WHITE);
        wTitle.setTextSize(13);
        wTitle.setTypeface(Typeface.DEFAULT_BOLD);
        wTitle.setPadding(0, 8, 0, 2);
        card.addView(wTitle);

        TextView wSub = new TextView(this);
        wSub.setText(subtitle);
        wSub.setTextColor(Color.parseColor("#B8B8B8"));
        wSub.setTextSize(10);
        wSub.setPadding(0, 0, 0, 8);
        card.addView(wSub);

        Button addBtn = createSmallPillButton("+ Add to Home Screen", new View.OnClickListener() {
            @Override public void onClick(View v) { pinWidget(providerClass); }
        });
        card.addView(addBtn);
        return card;
    }

    private Button createSmallPillButton(String label, View.OnClickListener onClickListener) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setAllCaps(false);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#FFDC143C"), Color.parseColor("#FF8B0000")}
        );
        bg.setCornerRadius(24);
        btn.setBackground(bg);
        btn.setPadding(24, 12, 24, 12);
        btn.setOnClickListener(onClickListener);
        return btn;
    }

    private void pinWidget(Class<?> providerClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
            if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported()) {
                ComponentName myProvider = new ComponentName(this, providerClass);
                appWidgetManager.requestPinAppWidget(myProvider, null, null);
                Toast.makeText(this, "Opening Home Screen placement...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle("Add to Home Screen")
                .setMessage("1. Touch and hold any empty area on your Home Screen.\\n2. Tap 'Widgets'.\\n3. Select 'Raven Shade' and place your widget!")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void showSingleIconDetail(CustomIconInfo info) {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(40, 40, 40, 40);
        box.setBackground(createLiquidGlassCard(32));

        ImageView iv = new ImageView(this);
        int resId = getResources().getIdentifier(info.drawableName, "drawable", getPackageName());
        if (resId != 0) iv.setImageResource(resId);
        box.addView(iv, new LinearLayout.LayoutParams((int)(90 * density), (int)(90 * density)));

        TextView tv = new TextView(this);
        tv.setText(info.name);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(0, 16, 0, 4);
        box.addView(tv);

        TextView pkgTv = new TextView(this);
        pkgTv.setText(info.packageName);
        pkgTv.setTextColor(Color.parseColor("#B8AAAA"));
        pkgTv.setTextSize(11);
        box.addView(pkgTv);

        d.setContentView(box);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
    }

    private void showApplyLauncherChooser() {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(36, 36, 36, 36);
        box.setBackground(createLiquidGlassCard(36));

        TextView title = new TextView(this);
        title.setText("Apply Raven Shade");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, 20);
        box.addView(title);

        box.addView(createDialogOptionButton("Nothing Launcher", new View.OnClickListener() {
            @Override public void onClick(View v) { applyLauncherIntent("com.nothing.launcher"); }
        }));
        box.addView(createDialogOptionButton("Nova Launcher", new View.OnClickListener() {
            @Override public void onClick(View v) { applyLauncherIntent("com.teslacoilsw.launcher"); }
        }));
        box.addView(createDialogOptionButton("Lawnchair Launcher", new View.OnClickListener() {
            @Override public void onClick(View v) { applyLauncherIntent("ch.deletescape.lawnchair.plah"); }
        }));

        d.setContentView(box);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
    }

    private void applyLauncherIntent(String pkg) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Launcher not installed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to launch", Toast.LENGTH_SHORT).show();
        }
    }

    private void showApplyGuideDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("How to Apply Raven Shade")
                .setMessage("1. Long press an empty area on your Home Screen.\\n2. Tap 'Customisation' \u2192 'Icon pack'.\\n3. Select 'Raven Shade' from the list.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private Button createDialogOptionButton(String text, View.OnClickListener listener) {
        Button btn = createLiquidRedButton(this, text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(46 * density));
        lp.bottomMargin = (int)(10 * density);
        btn.setLayoutParams(lp);
        btn.setOnClickListener(listener);
        return btn;
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Raven Shade Customization App");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out Raven Shade - Apple Liquid Glass & Crimson customization app for Android!");
        startActivity(Intent.createChooser(intent, "Share Raven Shade via"));
    }

    private List<CloudWallpaper> loadInitialDriveWallpapers() {
        List<CloudWallpaper> list = new ArrayList<>();
        try {
            InputStream is = getAssets().open("google_drive_links.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            int counter = 1;
            Pattern p = Pattern.compile("(?:/file/d/|id=|/d/)([a-zA-Z0-9_-]{20,})");
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String title = "Wallpaper #" + counter;
                    String raw = line;
                    if (line.contains("|")) {
                        String[] parts = line.split("\\\\|", 2);
                        title = parts[0].trim();
                        raw = parts[1].trim();
                    }
                    Matcher m = p.matcher(raw);
                    if (m.find()) {
                        String id = m.group(1);
                        String full = "https://lh3.googleusercontent.com/d/" + id;
                        String thumb = "https://lh3.googleusercontent.com/d/" + id + "=w400";
                        list.add(new CloudWallpaper(title, full, thumb, id));
                        counter++;
                    }
                }
            }
            reader.close();
        } catch (Exception ignored) {}
        return list;
    }

    private void syncLiveDriveWallpapers(final boolean userInitiated) {
        if (isSyncing) return;
        isSyncing = true;
        THREAD_POOL.execute(new Runnable() {
            @Override public void run() {
                try {
                    String endpoint = "https://drive.google.com/embeddedfolderview?id=" + DRIVE_FOLDER_ID;
                    URL url = new URL(endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        Pattern pattern = Pattern.compile("https://drive\\\\.google\\\\.com/file/d/([a-zA-Z0-9_-]+)/view");
                        String line;
                        List<CloudWallpaper> fresh = new ArrayList<>();
                        int c = 1;
                        while ((line = reader.readLine()) != null) {
                            Matcher m = pattern.matcher(line);
                            while (m.find()) {
                                String id = m.group(1);
                                String full = "https://lh3.googleusercontent.com/d/" + id;
                                String thumb = "https://lh3.googleusercontent.com/d/" + id + "=w400";
                                fresh.add(new CloudWallpaper("Drive #" + c, full, thumb, id));
                                c++;
                            }
                        }
                        reader.close();
                        if (!fresh.isEmpty()) {
                            driveItems.clear();
                            driveItems.addAll(fresh);
                            runOnUiThread(new Runnable() {
                                @Override public void run() { populateDriveCards(); }
                            });
                        }
                    }
                } catch (Exception ignored) {
                } finally {
                    isSyncing = false;
                }
            }
        });
    }
}
'''

with open("app/src/main/java/com/ravenshade/iconpack/MainActivity.java", "w", encoding="utf-8") as f:
    f.write(script)

print("Saved new enhanced MainActivity.java successfully!")
