package com.ravenshade.iconpack;

import android.app.Activity;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.media.RingtoneManager;
import android.provider.Settings;
import java.util.Collections;
import java.util.Comparator;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.os.BatteryManager;
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
        "wallpaper_01.jpg", "wallpaper_02.jpeg", "wallpaper_03.jpg", "wallpaper_04.jpg",
        "wallpaper_05.jpg", "wallpaper_06.jpg", "wallpaper_07.jpg", "wallpaper_08.jpg",
        "wallpaper_09.jpg", "wallpaper_10.jpg", "wallpaper_11.jpg", "wallpaper_12.jpg", "wallpaper_13.jpg"
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
    private View availableWidgetsAnchor;
    private View widgetsSectionAnchor;
    private View autoWpSectionAnchor;
    private View wallpapersSectionAnchor;
    private LinearLayout galleryDrive;
    private boolean isSyncing = false;
    private float density = 1.0f;

    // Live Auto Wallpaper UI references
    private TextView tvAutoWpStatus;
    private TextView tvCurrentWpTitle;
    private ImageView ivCurrentWpThumb;
    private List<Button> intervalButtons = new ArrayList<>();

    // Live Battery Preview references
    private TextView prevBatPct;
    private ProgressBar prevBatBar;

    // Ambient Frosted Liquid Glass Backdrop
    private ImageView ambientBackdropView;
    private View ambientScrimView;
    private FrameLayout activityContainer;

    // Broadcast receiver for live wallpaper and battery changes
    private Runnable mainUpdatePreviewRunnable = null;
    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (AutoWallpaperManager.ACTION_WALLPAPER_CHANGED.equals(action) ||
                Intent.ACTION_WALLPAPER_CHANGED.equals(action)) {
                String title = intent.getStringExtra("title");
                String thumb = intent.getStringExtra("thumb");
                if (title != null) {
                    updateCurrentWallpaperUI(title, thumb);
                }
                if (mainUpdatePreviewRunnable != null) {
                    mainUpdatePreviewRunnable.run();
                }
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                updateBatteryPreview(intent);
            }
        }
    };

    // --- FROSTED LIQUID GLASS UI HELPERS (OBSIDIAN & CRIMSON) ---

    public static GradientDrawable createScreenBackground() {
        return new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                    Color.parseColor("#060608"),
                    Color.parseColor("#120507"),
                    Color.parseColor("#060608")
                });
    }

    public static Drawable createLiquidGlassCard(float cornerRadius) {
        GradientDrawable base = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    0x2EFFFFFF, // Crisp specular reflection at top-left
                    0x1407070C, // Translucent obsidian glass core
                    0x26180408  // Caustic crimson depth
                });
        base.setCornerRadius(cornerRadius);
        base.setStroke(3, 0x60FFFFFF); // Polished specular glass bevel rim

        GradientDrawable glaze = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ 0x33FFFFFF, 0x08FFFFFF, 0x00FFFFFF });
        glaze.setCornerRadius(cornerRadius);

        LayerDrawable ld = new LayerDrawable(new Drawable[]{base, glaze});
        ld.setLayerInset(1, 4, 4, 4, 48);
        return ld;
    }

    public static Drawable createModernCard(float cornerRadius) {
        return createLiquidGlassCard(cornerRadius);
    }

    public static Drawable createAdaptiveCard(Context context, float cornerRadius) {
        return RavenGlassThemeManager.createFrostedGlassDrawable(context, cornerRadius, true);
    }

    public static Drawable createModernCard(int bgColor, int strokeColor, float radiusDp, Context context) {
        float density = context != null ? context.getResources().getDisplayMetrics().density : 2.0f;
        int bg = bgColor != 0 ? bgColor : 0x1407070C;
        int stroke = strokeColor != 0 ? strokeColor : 0x60FFFFFF;

        GradientDrawable base = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    0x2EFFFFFF,
                    bg,
                    0x24180408
                });
        base.setCornerRadius(radiusDp * density);
        base.setStroke((int)(1.4f * density), stroke);

        GradientDrawable glaze = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ 0x30FFFFFF, 0x06FFFFFF, 0x00FFFFFF });
        glaze.setCornerRadius(radiusDp * density);

        LayerDrawable ld = new LayerDrawable(new Drawable[]{base, glaze});
        ld.setLayerInset(1, (int)(2 * density), (int)(2 * density), (int)(2 * density), (int)(32 * density));
        return ld;
    }

    public static GradientDrawable createRedGradient(float radius) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.parseColor("#FFDC143C"), Color.parseColor("#808B0000") }
        );
        gd.setCornerRadius(radius);
        return gd;
    }

    public static GradientDrawable createCapsule() {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ 0x24FFFFFF, 0x1408080E }
        );
        gd.setStroke(1, 0x55FFFFFF);
        gd.setCornerRadius(16);
        return gd;
    }

    public static GradientDrawable createModernPill(String bgColor, String strokeColor, float radius) {
        GradientDrawable pill = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ 0x26FFFFFF, 0x1608080E }
        );
        pill.setCornerRadius(radius);
        pill.setStroke(2, 0x55FFFFFF);
        return pill;
    }

    public static Button createRedButton(Activity activity, String text) {
        Button btn = new Button(activity);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.08f);

        GradientDrawable redGlass = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    0xFFDC143C, // Crimson Red
                    0xFF8B0000  // Deep Crimson
                });
        redGlass.setCornerRadius(34);
        redGlass.setStroke(2, 0x99FFA3B2); // Specular rim

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setCornerRadius(34);
            mask.setColor(Color.WHITE);
            btn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")), redGlass, mask));
        } else {
            btn.setBackground(redGlass);
        }
        return btn;
    }

    public Button createPillButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.04f);
        btn.setSingleLine(true);
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setAllCaps(false);
        btn.setPadding((int)(14 * density), 0, (int)(14 * density), 0);
        btn.setMinHeight(0);
        btn.setMinimumHeight(0);
        btn.setMinWidth(0);
        btn.setMinimumWidth(0);

        GradientDrawable glassPill = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ 0x2AFFFFFF, 0x1407070D }
        );
        glassPill.setCornerRadius(34);
        glassPill.setStroke(2, 0x60FFFFFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setCornerRadius(34);
            mask.setColor(Color.WHITE);
            btn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), glassPill, mask));
        } else {
            btn.setBackground(glassPill);
        }
        btn.setOnClickListener(listener);
        return btn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getResources().getDisplayMetrics().density;

        activityContainer = new FrameLayout(this);
        activityContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setupAmbientFrostedBackdrop(activityContainer);

        mainScrollView = new ScrollView(this);
        mainScrollView.setBackgroundColor(Color.TRANSPARENT);
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
            @Override public void onClick(View v) { showAppMenuDialog(); }
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
        // 1. BRANDING HERO CARD (OFFICIAL RAVEN SHADE LOGO & TYPOGRAPHY)
        // =========================================================================
        LinearLayout brandHeroCard = new LinearLayout(this);
        brandHeroCard.setOrientation(LinearLayout.VERTICAL);
        brandHeroCard.setGravity(Gravity.CENTER_HORIZONTAL);
        brandHeroCard.setPadding((int)(20 * density), (int)(24 * density), (int)(20 * density), (int)(24 * density));
        brandHeroCard.setBackground(createModernCard(44));
        brandHeroCard.setElevation(14);

        // Logo Container with Ambient Crimson Glow & Chamfer
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

        // Subtitle: Premium Customization Suite
        TextView tvBrandSub = new TextView(this);
        tvBrandSub.setText("PREMIUM OBSIDIAN & CRIMSON SUITE");
        tvBrandSub.setTextColor(Color.parseColor("#B8B8B8"));
        tvBrandSub.setTextSize(10);
        tvBrandSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvBrandSub.setLetterSpacing(0.10f);
        tvBrandSub.setGravity(Gravity.CENTER);
        tvBrandSub.setPadding(0, (int)(4 * density), 0, (int)(16 * density));
        brandHeroCard.addView(tvBrandSub);

        // Identity Badges (160 Icons | 5 Usable Widgets | 100 Wallpapers)
        LinearLayout badgesRow = new LinearLayout(this);
        badgesRow.setOrientation(LinearLayout.HORIZONTAL);
        badgesRow.setGravity(Gravity.CENTER);
        badgesRow.addView(createModernBadge("160 ICONS"));
        badgesRow.addView(createBadgeSpacer());
        badgesRow.addView(createModernBadge("5 USABLE WIDGETS"));
        badgesRow.addView(createBadgeSpacer());
        badgesRow.addView(createModernBadge("100 DRIVE WALLPAPERS"));
        brandHeroCard.addView(badgesRow);

        // Main App Controls: Quick Navigation Bar
        LinearLayout quickNavRow = new LinearLayout(this);
        quickNavRow.setOrientation(LinearLayout.HORIZONTAL);
        quickNavRow.setGravity(Gravity.CENTER);
        quickNavRow.setPadding(0, (int)(20 * density), 0, 0);

        Button btnQuickApply = createRedButton(this, "⚡ Apply Pack");
        btnQuickApply.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        btnQuickApply.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showApplyLauncherChooser(); }
        });
        quickNavRow.addView(btnQuickApply);

        View qSpacer1 = new View(this);
        qSpacer1.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        quickNavRow.addView(qSpacer1);

        Button btnQuickWidget = createPillButton("✨ Edit Widgets", new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (widgetsSectionAnchor != null) mainScrollView.smoothScrollTo(0, widgetsSectionAnchor.getTop() - 20);
            }
        });
        btnQuickWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        quickNavRow.addView(btnQuickWidget);

        View qSpacer2 = new View(this);
        qSpacer2.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        quickNavRow.addView(qSpacer2);

        Button btnQuickWp = createPillButton("🖼 Auto Wallpapers", new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (autoWpSectionAnchor != null) mainScrollView.smoothScrollTo(0, autoWpSectionAnchor.getTop() - 20);
            }
        });
        btnQuickWp.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        quickNavRow.addView(btnQuickWp);

        brandHeroCard.addView(quickNavRow);

        Button btnQuickRequest = createPillButton("📩 Request Missing Icons", new View.OnClickListener() {
            @Override public void onClick(View v) {
                RavenIconRequestManager.showIconRequestDialog(MainActivity.this);
            }
        });
        LinearLayout.LayoutParams reqBtnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int)(44 * density));
        reqBtnLp.topMargin = (int)(10 * density);
        btnQuickRequest.setLayoutParams(reqBtnLp);
        brandHeroCard.addView(btnQuickRequest);
        root.addView(brandHeroCard);

        // =========================================================================
        // 2. AVAILABLE WIDGETS SUITE (SEPARATE DEDICATED HEADING)
        // =========================================================================
        availableWidgetsAnchor = createSectionTitle("AVAILABLE WIDGETS");
        availableWidgetsAnchor.setPadding(12, (int)(36 * density), 12, (int)(8 * density));
        root.addView(availableWidgetsAnchor);

        TextView availableWidgetsSub = new TextView(this);
        availableWidgetsSub.setText("8 Interactive Widgets • 1-Tap Add to Home Screen");
        availableWidgetsSub.setTextColor(Color.parseColor("#B8B8B8"));
        availableWidgetsSub.setTextSize(12);
        availableWidgetsSub.setPadding(16, 0, 16, (int)(14 * density));
        root.addView(availableWidgetsSub);

        // Music Live Sync Status Banner (convx, Spotify, YouTube Music, etc.)
        if (!isNotificationListenerEnabled()) {
            LinearLayout musicSyncCard = new LinearLayout(this);
            musicSyncCard.setOrientation(LinearLayout.HORIZONTAL);
            musicSyncCard.setGravity(Gravity.CENTER_VERTICAL);
            musicSyncCard.setBackground(createModernCard(24));
            musicSyncCard.setPadding((int)(16 * density), (int)(12 * density), (int)(16 * density), (int)(12 * density));
            LinearLayout.LayoutParams mscLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mscLp.setMargins(0, 0, 0, (int)(12 * density));
            musicSyncCard.setLayoutParams(mscLp);

            LinearLayout mscTextCol = new LinearLayout(this);
            mscTextCol.setOrientation(LinearLayout.VERTICAL);
            mscTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

            TextView tvMscTitle = new TextView(this);
            tvMscTitle.setText("🎵 LIVE MUSIC SYNC");
            tvMscTitle.setTextColor(Color.parseColor("#FFDC143C"));
            tvMscTitle.setTextSize(11);
            tvMscTitle.setTypeface(Typeface.DEFAULT_BOLD);
            tvMscTitle.setLetterSpacing(0.08f);
            mscTextCol.addView(tvMscTitle);

            TextView tvMscDesc = new TextView(this);
            tvMscDesc.setText("Enable Notification Access so widgets can read live song title, artist & album art from convx, Spotify, etc.");
            tvMscDesc.setTextColor(Color.parseColor("#B8B8B8"));
            tvMscDesc.setTextSize(10);
            tvMscDesc.setPadding(0, 2, 0, 0);
            mscTextCol.addView(tvMscDesc);
            musicSyncCard.addView(mscTextCol);

            Button btnEnableMusic = createRedButton(this, "Enable");
            btnEnableMusic.setTextSize(11);
            btnEnableMusic.setLayoutParams(new LinearLayout.LayoutParams((int)(80 * density), (int)(38 * density)));
            btnEnableMusic.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Open Settings > Apps > Special App Access > Notification Access", Toast.LENGTH_LONG).show();
                    }
                }
            });
            musicSyncCard.addView(btnEnableMusic);
            root.addView(musicSyncCard);
        }

        // Horizontal Carousel of 6 Usable Widgets
        HorizontalScrollView hsvWidgets = new HorizontalScrollView(this);
        hsvWidgets.setHorizontalScrollBarEnabled(false);
        hsvWidgets.setPadding(0, 0, 0, (int)(18 * density));

        LinearLayout widgetsRow = new LinearLayout(this);
        widgetsRow.setOrientation(LinearLayout.HORIZONTAL);

        // 1. Black Raven Clock (4x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Black Raven Clock (4x2)",
            "Digital Clock • Date • Animated Battery",
            BlackRavenWidgetProvider.class,
            1
        ));

        // 2. Raven Music Player (4x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Raven Music Player (4x2)",
            "Now Playing • Interactive Music Controls",
            MusicWidgetProvider.class,
            4
        ));

        // 3. Raven Music Mini (2x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Raven Music Mini (2x2)",
            "Compact Square Player • Live Track Sync",
            MusicWidget2x2Provider.class,
            6
        ));

        // 4. Shuffle Wallpaper (1x1)
        widgetsRow.addView(createWidgetPreviewCard(
            "Pop Wallpaper (1x1)",
            "Pure Text • Instant Random Wallpaper",
            WallpaperShuffleWidgetProvider.class,
            5
        ));

        // 5. Quick App Dock (4x1)
        widgetsRow.addView(createWidgetPreviewCard(
            "Quick App Dock (4x1)",
            "Obsidian Dock • 5 Quick Launch Shortcuts",
            QuickDockWidgetProvider.class,
            2
        ));

        // 6. Anime Mini Clock (2x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Anime Mini Clock (2x2)",
            "Compact Obsidian & Crimson Square Clock",
            AnimeClockWidgetProvider.class,
            3
        ));

        // 7. Raven Weather (2x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Raven Weather (2x2)",
            "Live Weather • Obsidian Dark Edition",
            WeatherWidgetProvider.class,
            7
        ));

        // 8. Music Large 2 (4x2 Minimalist Glass)
        widgetsRow.addView(createWidgetPreviewCard(
            "Music Large 2 (4x2)",
            "Minimalist Circular & Pill Glass Player",
            MusicLarge2WidgetProvider.class,
            8
        ));

        hsvWidgets.addView(widgetsRow);
        root.addView(hsvWidgets);

        // =========================================================================
        // 3. WIDGET CUSTOMIZATION STUDIO (SEPARATE HEADING & ALL WIDGETS EDITABLE)
        // =========================================================================
        widgetsSectionAnchor = createSectionTitle("WIDGET CUSTOMIZATION STUDIO");
        widgetsSectionAnchor.setPadding(12, (int)(32 * density), 12, (int)(8 * density));
        root.addView(widgetsSectionAnchor);

        TextView widgetStudioSub = new TextView(this);
        widgetStudioSub.setText("Select any widget below to customize its text, titles & badges in real-time");
        widgetStudioSub.setTextColor(Color.parseColor("#B8B8B8"));
        widgetStudioSub.setTextSize(12);
        widgetStudioSub.setPadding(16, 0, 16, (int)(14 * density));
        root.addView(widgetStudioSub);

        // Widget Selector Tab Bar
        HorizontalScrollView hsvStudioTabs = new HorizontalScrollView(this);
        hsvStudioTabs.setHorizontalScrollBarEnabled(false);
        hsvStudioTabs.setPadding(0, 0, 0, (int)(12 * density));

        LinearLayout studioTabsRow = new LinearLayout(this);
        studioTabsRow.setOrientation(LinearLayout.HORIZONTAL);

        final Button tabClock = createPillButton("🕒 Clock", null);
        final Button tabMusic = createPillButton("🎵 Music", null);
        final Button tabWeather = createPillButton("⛅ Weather", null);
        final Button tabShuffle = createPillButton("💥 Pop (1x1)", null);
        final Button tabDock = createPillButton("📱 Quick Dock", null);
        final Button tabAnime = createPillButton("⛩️ Anime", null);

        int tabMargin = (int)(8 * density);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(38 * density));
        tLp.setMargins(0, 0, tabMargin, 0);
        tabClock.setLayoutParams(tLp);
        tabMusic.setLayoutParams(tLp);
        tabWeather.setLayoutParams(tLp);
        tabShuffle.setLayoutParams(tLp);
        tabDock.setLayoutParams(tLp);
        tabAnime.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(38 * density)));

        studioTabsRow.addView(tabClock);
        studioTabsRow.addView(tabMusic);
        studioTabsRow.addView(tabWeather);
        studioTabsRow.addView(tabShuffle);
        studioTabsRow.addView(tabDock);
        studioTabsRow.addView(tabAnime);
        hsvStudioTabs.addView(studioTabsRow);
        root.addView(hsvStudioTabs);

        // Dynamic preview card


        // Live Dynamic Preview Box
        final LinearLayout dynamicPreviewCard = new LinearLayout(this);
        dynamicPreviewCard.setOrientation(LinearLayout.VERTICAL);
        dynamicPreviewCard.setPadding((int)(18 * density), (int)(16 * density), (int)(18 * density), (int)(16 * density));
        dynamicPreviewCard.setBackground(createModernCard(28));
        dynamicPreviewCard.setElevation(8);
        root.addView(dynamicPreviewCard);

        // Form Card containing sub-forms for each widget
        LinearLayout formCard = new LinearLayout(this);
        formCard.setOrientation(LinearLayout.VERTICAL);
        formCard.setPadding((int)(18 * density), (int)(18 * density), (int)(18 * density), (int)(18 * density));
        formCard.setBackground(createModernCard(32));
        formCard.setElevation(6);
        LinearLayout.LayoutParams formLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        formLp.topMargin = (int)(14 * density);
        formCard.setLayoutParams(formLp);

        final Runnable[] updatePreviewRef = new Runnable[1];

        // --- SUBFORM 0: Black Raven Clock ---
        final LinearLayout formClock = new LinearLayout(this);
        formClock.setOrientation(LinearLayout.VERTICAL);

        formClock.addView(createFieldLabel("CLOCK TITLE"));
        final EditText etClockTitle = createModernEditText(WidgetSettingsManager.getTitle(this));
        formClock.addView(etClockTitle);

        formClock.addView(createFieldLabel("SUBTITLE / BADGE"));
        final EditText etClockSub = createModernEditText(WidgetSettingsManager.getSubtitle(this));
        formClock.addView(etClockSub);

        formClock.addView(createFieldLabel("CUSTOM TAG LABEL"));
        final EditText etClockTag = createModernEditText(WidgetSettingsManager.getTag(this));
        formClock.addView(etClockTag);

        LinearLayout switchesRow = new LinearLayout(this);
        switchesRow.setOrientation(LinearLayout.HORIZONTAL);
        switchesRow.setPadding(0, (int)(12 * density), 0, (int)(4 * density));

        final Switch switchTag = new Switch(this);
        switchTag.setText("Show Tag Badge  ");
        switchTag.setTextColor(Color.WHITE);
        switchTag.setTextSize(12);
        switchTag.setChecked(WidgetSettingsManager.getShowTag(this));
        switchesRow.addView(switchTag);

        final Switch switchBat = new Switch(this);
        switchBat.setText("Show Battery Bar");
        switchBat.setTextColor(Color.WHITE);
        switchBat.setTextSize(12);
        switchBat.setChecked(WidgetSettingsManager.getShowBattery(this));
        switchesRow.addView(switchBat);
        formClock.addView(switchesRow);

        // --- SUBFORM 1: Music Widget ---
        final LinearLayout formMusic = new LinearLayout(this);
        formMusic.setOrientation(LinearLayout.VERTICAL);
        formMusic.setVisibility(View.GONE);

        formMusic.addView(createFieldLabel("AUDIO BRAND HEADER"));
        final EditText etMusicBrand = createModernEditText(WidgetSettingsManager.getMusicBrand(this));
        formMusic.addView(etMusicBrand);

        formMusic.addView(createFieldLabel("DEFAULT TRACK TITLE (WHEN IDLE)"));
        final EditText etMusicTitle = createModernEditText(WidgetSettingsManager.getMusicDefaultTitle(this));
        formMusic.addView(etMusicTitle);

        formMusic.addView(createFieldLabel("DEFAULT ARTIST / SESSION (WHEN IDLE)"));
        final EditText etMusicArtist = createModernEditText(WidgetSettingsManager.getMusicDefaultArtist(this));
        formMusic.addView(etMusicArtist);

        formMusic.addView(createFieldLabel("STATUS TAG"));
        final EditText etMusicTag = createModernEditText(WidgetSettingsManager.getMusicTag(this));
        formMusic.addView(etMusicTag);

        formMusic.addView(createFieldLabel("AUDIO WAVEFORM & LINE STYLE (5 OPTIONS)"));

        final int[] selectedWaveStyle = new int[]{ WidgetSettingsManager.getMusicWaveStyle(this) };
        final LinearLayout waveButtonsCol = new LinearLayout(this);
        waveButtonsCol.setOrientation(LinearLayout.VERTICAL);
        waveButtonsCol.setPadding(0, (int)(4 * density), 0, (int)(6 * density));

        final View[] waveOptionViews = new View[5];
        final Runnable updateWaveStylesUI = new Runnable() {
            @Override
            public void run() {
                for (int s = 0; s < 5; s++) {
                    if (waveOptionViews[s] != null) {
                        boolean isSel = (s == selectedWaveStyle[0]);
                        waveOptionViews[s].setBackground(createModernCard(
                                isSel ? 0x4DDC143C : 0x1AFFFFFF,
                                isSel ? 0xFFFF2E4D : 0x33FFFFFF,
                                14, MainActivity.this));
                    }
                }
            }
        };

        for (int i = 0; i < 5; i++) {
            final int styleIndex = i;
            LinearLayout optionRow = new LinearLayout(this);
            optionRow.setOrientation(LinearLayout.HORIZONTAL);
            optionRow.setGravity(Gravity.CENTER_VERTICAL);
            optionRow.setPadding((int)(12 * density), (int)(10 * density), (int)(12 * density), (int)(10 * density));
            LinearLayout.LayoutParams opLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            opLp.bottomMargin = (int)(6 * density);
            optionRow.setLayoutParams(opLp);

            LinearLayout textCol = new LinearLayout(this);
            textCol.setOrientation(LinearLayout.VERTICAL);
            textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

            TextView tvName = new TextView(this);
            tvName.setText(RavenMusicWaveRenderer.STYLE_NAMES[styleIndex]);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextSize(12);
            tvName.setTypeface(Typeface.DEFAULT_BOLD);
            textCol.addView(tvName);

            TextView tvDesc = new TextView(this);
            tvDesc.setText(RavenMusicWaveRenderer.STYLE_DESCRIPTIONS[styleIndex]);
            tvDesc.setTextColor(Color.parseColor("#9E9E9E"));
            tvDesc.setTextSize(9);
            tvDesc.setPadding(0, 1, 0, 0);
            textCol.addView(tvDesc);
            optionRow.addView(textCol);

            // Live mini preview of waveform
            ImageView miniWave = new ImageView(this);
            int mwW = (int)(100 * density);
            int mwH = (int)(22 * density);
            miniWave.setLayoutParams(new LinearLayout.LayoutParams(mwW, mwH));
            miniWave.setScaleType(ImageView.ScaleType.FIT_XY);
            Bitmap previewBm = RavenMusicWaveRenderer.renderWaveBitmap(
                    this, styleIndex, (int)(200 * density), (int)(32 * density),
                    0xFFFF2E4D);
            miniWave.setImageBitmap(previewBm);
            optionRow.addView(miniWave);

            waveOptionViews[styleIndex] = optionRow;

            optionRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedWaveStyle[0] = styleIndex;
                    WidgetSettingsManager.setMusicWaveStyle(MainActivity.this, styleIndex);
                    updateWaveStylesUI.run();
                    if (updatePreviewRef[0] != null) updatePreviewRef[0].run();
                }
            });

            waveButtonsCol.addView(optionRow);
        }

        updateWaveStylesUI.run();
        formMusic.addView(waveButtonsCol);

        // --- SUBFORM 2: Shuffle Wallpaper ---
        final LinearLayout formShuffle = new LinearLayout(this);
        formShuffle.setOrientation(LinearLayout.VERTICAL);
        formShuffle.setVisibility(View.GONE);

        formShuffle.addView(createFieldLabel("PRIMARY BUTTON TEXT"));
        final EditText etShuffleTitle = createModernEditText(WidgetSettingsManager.getShuffleTitle(this));
        formShuffle.addView(etShuffleTitle);

        formShuffle.addView(createFieldLabel("SUBTITLE TEXT"));
        final EditText etShuffleSub = createModernEditText(WidgetSettingsManager.getShuffleSubtitle(this));
        formShuffle.addView(etShuffleSub);

        // --- SUBFORM 2: Weather Widget ---
        final LinearLayout formWeather = new LinearLayout(this);
        formWeather.setOrientation(LinearLayout.VERTICAL);
        formWeather.setVisibility(View.GONE);

        formWeather.addView(createFieldLabel("CITY / LOCATION"));
        final EditText etWeatherCity = createModernEditText(WidgetSettingsManager.getWeatherCity(this));
        formWeather.addView(etWeatherCity);

        formWeather.addView(createFieldLabel("WEATHER TAG / MOOD"));
        final EditText etWeatherTag = createModernEditText(WidgetSettingsManager.getWeatherTag(this));
        formWeather.addView(etWeatherTag);

        LinearLayout unitRow = new LinearLayout(this);
        unitRow.setOrientation(LinearLayout.HORIZONTAL);
        unitRow.setPadding(0, (int)(4 * density), 0, (int)(8 * density));
        final Switch switchUnit = new Switch(this);
        switchUnit.setText("Use Fahrenheit (°F) instead of Celsius (°C)");
        switchUnit.setTextColor(Color.WHITE);
        switchUnit.setTextSize(12);
        switchUnit.setChecked("F".equalsIgnoreCase(WidgetSettingsManager.getWeatherUnit(this)));
        unitRow.addView(switchUnit);
        formWeather.addView(unitRow);

        Button btnRefreshWeather = createPillButton("🔄 Fetch Live Weather Now", new View.OnClickListener() {
            @Override public void onClick(View v) {
                RavenWeatherManager.refreshWeatherAsync(MainActivity.this, true);
                Toast.makeText(MainActivity.this, "Updating live weather from satellite...", Toast.LENGTH_SHORT).show();
            }
        });
        btnRefreshWeather.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(40 * density)));
        formWeather.addView(btnRefreshWeather);

        // --- SUBFORM 3: Quick App Dock (With Interactive Slot Customization) ---
        final LinearLayout formDock = new LinearLayout(this);
        formDock.setOrientation(LinearLayout.VERTICAL);
        formDock.setVisibility(View.GONE);

        formDock.addView(createFieldLabel("DOCK TITLE"));
        final EditText etDockTitle = createModernEditText(WidgetSettingsManager.getDockTitle(this));
        formDock.addView(etDockTitle);

        formDock.addView(createFieldLabel("DOCK TAG"));
        final EditText etDockTag = createModernEditText(WidgetSettingsManager.getDockTag(this));
        formDock.addView(etDockTag);

        formDock.addView(createFieldLabel("CUSTOMIZE DOCK APPS (5 SLOTS)"));
        final ImageView[] dockSlotIcons = new ImageView[5];
        final TextView[] dockSlotLabels = new TextView[5];

        for (int s = 0; s < 5; s++) {
            final int slotIndex = s;
            LinearLayout slotRow = new LinearLayout(this);
            slotRow.setOrientation(LinearLayout.HORIZONTAL);
            slotRow.setGravity(Gravity.CENTER_VERTICAL);
            slotRow.setPadding(0, (int)(5 * density), 0, (int)(5 * density));

            ImageView sIv = new ImageView(this);
            sIv.setLayoutParams(new LinearLayout.LayoutParams((int)(34 * density), (int)(34 * density)));
            sIv.setBackground(createCapsule());
            sIv.setPadding(4, 4, 4, 4);
            updateSlotIconView(slotIndex, sIv);
            dockSlotIcons[slotIndex] = sIv;
            slotRow.addView(sIv);

            TextView sTv = new TextView(this);
            sTv.setText("Slot " + (slotIndex + 1) + ": " + WidgetSettingsManager.getDockSlotLabel(this, slotIndex));
            sTv.setTextColor(Color.WHITE);
            sTv.setTextSize(12);
            sTv.setTypeface(Typeface.DEFAULT_BOLD);
            sTv.setPadding((int)(10 * density), 0, (int)(6 * density), 0);
            sTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            sTv.setSingleLine(true);
            sTv.setEllipsize(TextUtils.TruncateAt.END);
            dockSlotLabels[slotIndex] = sTv;
            slotRow.addView(sTv);

            Button btnPick = createPillButton("Change", new View.OnClickListener() {
                @Override public void onClick(View v) {
                    showAppChooserDialog(slotIndex, new Runnable() {
                        @Override public void run() {
                            dockSlotLabels[slotIndex].setText("Slot " + (slotIndex + 1) + ": " + WidgetSettingsManager.getDockSlotLabel(MainActivity.this, slotIndex));
                            updateSlotIconView(slotIndex, dockSlotIcons[slotIndex]);
                            if (updatePreviewRef[0] != null) updatePreviewRef[0].run();
                        }
                    });
                }
            });
            btnPick.setTextSize(11);
            btnPick.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(34 * density)));
            slotRow.addView(btnPick);

            formDock.addView(slotRow);
        }

        // --- SUBFORM 4: Anime Mini Clock ---
        final LinearLayout formAnime = new LinearLayout(this);
        formAnime.setOrientation(LinearLayout.VERTICAL);
        formAnime.setVisibility(View.GONE);

        formAnime.addView(createFieldLabel("ANIME CLOCK TITLE"));
        final EditText etAnimeTitle = createModernEditText(WidgetSettingsManager.getAnimeTitle(this));
        formAnime.addView(etAnimeTitle);

        formAnime.addView(createFieldLabel("ZONE TAG"));
        final EditText etAnimeTag = createModernEditText(WidgetSettingsManager.getAnimeTag(this));
        formAnime.addView(etAnimeTag);

        formCard.addView(formClock);
        formCard.addView(formMusic);
        formCard.addView(formWeather);
        formCard.addView(formShuffle);
        formCard.addView(formDock);
        formCard.addView(formAnime);

        // Form Buttons: Save All & Sync + Reset Defaults
        LinearLayout formBtnRow = new LinearLayout(this);
        formBtnRow.setOrientation(LinearLayout.HORIZONTAL);
        formBtnRow.setPadding(0, (int)(14 * density), 0, 0);

        Button btnSaveWidget = createRedButton(this, "💾 Save & Sync All Widgets");
        btnSaveWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.6f));
        btnSaveWidget.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // 1. Clock
                WidgetSettingsManager.setTitle(MainActivity.this, etClockTitle.getText().toString().trim());
                WidgetSettingsManager.setSubtitle(MainActivity.this, etClockSub.getText().toString().trim());
                WidgetSettingsManager.setTag(MainActivity.this, etClockTag.getText().toString().trim());
                WidgetSettingsManager.setShowTag(MainActivity.this, switchTag.isChecked());
                WidgetSettingsManager.setShowBattery(MainActivity.this, switchBat.isChecked());

                // 2. Music
                WidgetSettingsManager.setMusicBrand(MainActivity.this, etMusicBrand.getText().toString().trim());
                WidgetSettingsManager.setMusicDefaultTitle(MainActivity.this, etMusicTitle.getText().toString().trim());
                WidgetSettingsManager.setMusicDefaultArtist(MainActivity.this, etMusicArtist.getText().toString().trim());
                WidgetSettingsManager.setMusicTag(MainActivity.this, etMusicTag.getText().toString().trim());

                // Weather
                WidgetSettingsManager.setWeatherCity(MainActivity.this, etWeatherCity.getText().toString().trim());
                WidgetSettingsManager.setWeatherTag(MainActivity.this, etWeatherTag.getText().toString().trim());
                WidgetSettingsManager.setWeatherUnit(MainActivity.this, switchUnit.isChecked() ? "F" : "C");

                // 3. Shuffle
                WidgetSettingsManager.setShuffleTitle(MainActivity.this, etShuffleTitle.getText().toString().trim());
                WidgetSettingsManager.setShuffleSubtitle(MainActivity.this, etShuffleSub.getText().toString().trim());
                
                // 4. Dock
                WidgetSettingsManager.setDockTitle(MainActivity.this, etDockTitle.getText().toString().trim());
                WidgetSettingsManager.setDockTag(MainActivity.this, etDockTag.getText().toString().trim());

                // 5. Anime
                WidgetSettingsManager.setAnimeTitle(MainActivity.this, etAnimeTitle.getText().toString().trim());
                WidgetSettingsManager.setAnimeTag(MainActivity.this, etAnimeTag.getText().toString().trim());

                WidgetSettingsManager.updateAllWidgets(MainActivity.this);
                Toast.makeText(MainActivity.this, "✓ All widget texts saved & synced to Home Screen!", Toast.LENGTH_SHORT).show();
            }
        });
        formBtnRow.addView(btnSaveWidget);

        View fbSpacer = new View(this);
        fbSpacer.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
        formBtnRow.addView(fbSpacer);

        Button btnResetWidget = createPillButton("↺ Reset", new View.OnClickListener() {
            @Override public void onClick(View v) {
                WidgetSettingsManager.resetDefaults(MainActivity.this);
                etClockTitle.setText(WidgetSettingsManager.DEFAULT_TITLE);
                etClockSub.setText(WidgetSettingsManager.DEFAULT_SUBTITLE);
                etClockTag.setText(WidgetSettingsManager.DEFAULT_TAG);
                switchTag.setChecked(true);
                switchBat.setChecked(true);

                etMusicBrand.setText(WidgetSettingsManager.DEFAULT_MUSIC_BRAND);
                etMusicTitle.setText(WidgetSettingsManager.DEFAULT_MUSIC_TITLE);
                etMusicArtist.setText(WidgetSettingsManager.DEFAULT_MUSIC_ARTIST);
                etMusicTag.setText(WidgetSettingsManager.DEFAULT_MUSIC_TAG);

                etShuffleTitle.setText(WidgetSettingsManager.DEFAULT_SHUFFLE_TITLE);
                etShuffleSub.setText(WidgetSettingsManager.DEFAULT_SHUFFLE_SUBTITLE);
                
                etDockTitle.setText(WidgetSettingsManager.DEFAULT_DOCK_TITLE);
                etDockTag.setText(WidgetSettingsManager.DEFAULT_DOCK_TAG);

                etAnimeTitle.setText(WidgetSettingsManager.DEFAULT_ANIME_TITLE);
                etAnimeTag.setText(WidgetSettingsManager.DEFAULT_ANIME_TAG);

                Toast.makeText(MainActivity.this, "↺ Reset to Raven Shade defaults", Toast.LENGTH_SHORT).show();
            }
        });
        btnResetWidget.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(46 * density), 1.0f));
        formBtnRow.addView(btnResetWidget);

        formCard.addView(formBtnRow);
        root.addView(formCard);

        // Preview rendering function
        final Runnable updatePreview = new Runnable() {

            @Override public void run() {
                mainUpdatePreviewRunnable = this;
                dynamicPreviewCard.setBackground(createAdaptiveCard(MainActivity.this, 28 * density));
                dynamicPreviewCard.removeAllViews();
                Date now = new Date();
                SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE | d MMM", Locale.getDefault());

                if (formClock.getVisibility() == View.VISIBLE) {
                    // Clock Preview
                    LinearLayout head = new LinearLayout(MainActivity.this);
                    head.setOrientation(LinearLayout.HORIZONTAL);
                    head.setGravity(Gravity.CENTER_VERTICAL);

                    TextView t = new TextView(MainActivity.this);
                    t.setText(etClockTitle.getText().toString().isEmpty() ? "RAVEN SHADE" : etClockTitle.getText().toString());
                    t.setTextColor(0xFFFF2E4D);
                    t.setTextSize(14);
                    t.setTypeface(Typeface.DEFAULT_BOLD);
                    t.setLetterSpacing(0.10f);
                    t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                    head.addView(t);

                    TextView g = new TextView(MainActivity.this);
                    g.setText(etClockSub.getText().toString().isEmpty() ? "OBSIDIAN EDITION" : etClockSub.getText().toString());
                    g.setTextColor(Color.parseColor("#CCFFFFFF"));
                    g.setTextSize(11);
                    g.setTypeface(Typeface.DEFAULT_BOLD);
                    head.addView(g);
                    dynamicPreviewCard.addView(head);

                    LinearLayout clkRow = new LinearLayout(MainActivity.this);
                    clkRow.setOrientation(LinearLayout.HORIZONTAL);
                    clkRow.setGravity(Gravity.CENTER_VERTICAL);
                    clkRow.setPadding(0, (int)(8 * density), 0, (int)(8 * density));

                    TextView clk = new TextView(MainActivity.this);
                    clk.setText(timeFmt.format(now));
                    clk.setTextColor(Color.WHITE);
                    clk.setTextSize(32);
                    clk.setTypeface(Typeface.DEFAULT_BOLD);
                    clkRow.addView(clk);

                    LinearLayout dCol = new LinearLayout(MainActivity.this);
                    dCol.setOrientation(LinearLayout.VERTICAL);
                    dCol.setPadding((int)(16 * density), 0, 0, 0);

                    TextView dTxt = new TextView(MainActivity.this);
                    dTxt.setText(dateFmt.format(now));
                    dTxt.setTextColor(Color.parseColor("#B8B8B8"));
                    dTxt.setTextSize(12);
                    dCol.addView(dTxt);

                    if (switchTag.isChecked()) {
                        TextView tg = new TextView(MainActivity.this);
                        tg.setText(etClockTag.getText().toString().isEmpty() ? "RAVENSHADE EDITION" : etClockTag.getText().toString());
                        tg.setTextColor(Color.parseColor("#80FF5A73"));
                        tg.setTextSize(9);
                        tg.setPadding(0, 2, 0, 0);
                        dCol.addView(tg);
                    }
                    clkRow.addView(dCol);
                    dynamicPreviewCard.addView(clkRow);

                } else if (formMusic.getVisibility() == View.VISIBLE) {
                    // Music Preview: Larger art, centered text, audio wave, music timer
                    LinearLayout mBox = new LinearLayout(MainActivity.this);
                    mBox.setOrientation(LinearLayout.VERTICAL);
                    mBox.setGravity(Gravity.CENTER);

                    LinearLayout mRow = new LinearLayout(MainActivity.this);
                    mRow.setOrientation(LinearLayout.HORIZONTAL);
                    mRow.setGravity(Gravity.CENTER_VERTICAL);

                    ImageView ivArt = new ImageView(MainActivity.this);
                    int artSz = (int)(62 * density);
                    ivArt.setLayoutParams(new LinearLayout.LayoutParams(artSz, artSz));
                    Bitmap liveArt = RavenMusicManager.getAlbumArt(MainActivity.this);
                    if (liveArt != null && !liveArt.isRecycled()) {
                        ivArt.setImageBitmap(liveArt);
                    } else {
                        int musicRes = getResources().getIdentifier("ic_music_note", "drawable", getPackageName());
                        if (musicRes != 0) ivArt.setImageResource(musicRes);
                    }
                    // Background box removed for clean album art look
                    mRow.addView(ivArt);

                    LinearLayout mCol = new LinearLayout(MainActivity.this);
                    mCol.setOrientation(LinearLayout.VERTICAL);
                    mCol.setPadding((int)(12 * density), 0, 0, 0);
                    mCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                    TextView bTxt = new TextView(MainActivity.this);
                    bTxt.setText(etMusicBrand.getText().toString().isEmpty() ? "RAVEN AUDIO" : etMusicBrand.getText().toString());
                    bTxt.setTextColor(Color.parseColor("#FFDC143C"));
                    bTxt.setTextSize(10);
                    bTxt.setTypeface(Typeface.DEFAULT_BOLD);
                    mCol.addView(bTxt);

                    TextView tTxt = new TextView(MainActivity.this);
                    tTxt.setText(etMusicTitle.getText().toString().isEmpty() ? "Raven Waves" : etMusicTitle.getText().toString());
                    tTxt.setTextColor(Color.WHITE);
                    tTxt.setTextSize(14);
                    tTxt.setTypeface(Typeface.DEFAULT_BOLD);
                    tTxt.setSingleLine(true);
                    tTxt.setEllipsize(TextUtils.TruncateAt.END);
                    mCol.addView(tTxt);

                    TextView aTxt = new TextView(MainActivity.this);
                    aTxt.setText(etMusicArtist.getText().toString().isEmpty() ? "Obsidian Session" : etMusicArtist.getText().toString());
                    aTxt.setTextColor(Color.parseColor("#B8B8B8"));
                    aTxt.setTextSize(11);
                    aTxt.setSingleLine(true);
                    aTxt.setEllipsize(TextUtils.TruncateAt.END);
                    mCol.addView(aTxt);

                    // Waveform + Music Time
                    LinearLayout wRow = new LinearLayout(MainActivity.this);
                    wRow.setOrientation(LinearLayout.HORIZONTAL);
                    wRow.setGravity(Gravity.CENTER_VERTICAL);
                    wRow.setPadding(0, 4, 0, 0);

                    TextView tvCur = new TextView(MainActivity.this);
                    tvCur.setText(RavenMusicManager.getCurrentTimeFormatted(MainActivity.this));
                    tvCur.setTextColor(Color.WHITE);
                    tvCur.setTextSize(9);
                    wRow.addView(tvCur);

                    ImageView ivWave = new ImageView(MainActivity.this);
                    ivWave.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(18 * density), 1.0f));
                    ivWave.setScaleType(ImageView.ScaleType.FIT_XY);
                    ivWave.setPadding(6, 0, 6, 0);
                    int activeWaveStyle = WidgetSettingsManager.getMusicWaveStyle(MainActivity.this);
                    int activeWpColor = 0xFFFF2E4D;
                    Bitmap liveWaveBm = RavenMusicWaveRenderer.renderWaveBitmap(
                            MainActivity.this, activeWaveStyle, (int)(260 * density), (int)(36 * density), activeWpColor);
                    if (liveWaveBm != null) ivWave.setImageBitmap(liveWaveBm);
                    wRow.addView(ivWave);

                    TextView tvTot = new TextView(MainActivity.this);
                    tvTot.setText(RavenMusicManager.getTotalTimeFormatted(MainActivity.this));
                    tvTot.setTextColor(Color.parseColor("#80FFFFFF"));
                    tvTot.setTextSize(9);
                    wRow.addView(tvTot);

                    mCol.addView(wRow);
                    mRow.addView(mCol);
                    mBox.addView(mRow);
                    dynamicPreviewCard.addView(mBox);

                } else if (formWeather.getVisibility() == View.VISIBLE) {
                    // Weather Preview
                    LinearLayout wBox = new LinearLayout(MainActivity.this);
                    wBox.setOrientation(LinearLayout.VERTICAL);
                    wBox.setGravity(Gravity.CENTER);

                    LinearLayout topRow = new LinearLayout(MainActivity.this);
                    topRow.setOrientation(LinearLayout.HORIZONTAL);
                    topRow.setGravity(Gravity.CENTER_VERTICAL);

                    ImageView ivW = new ImageView(MainActivity.this);
                    int wSz = (int)(40 * density);
                    ivW.setLayoutParams(new LinearLayout.LayoutParams(wSz, wSz));
                    ivW.setBackground(createCapsule());
                    ivW.setPadding(5, 5, 5, 5);
                    int wRes = getResources().getIdentifier(RavenWeatherManager.getIconName(MainActivity.this), "drawable", getPackageName());
                    if (wRes == 0) wRes = getResources().getIdentifier("ic_weather_sunny", "drawable", getPackageName());
                    if (wRes != 0) ivW.setImageResource(wRes);
                    topRow.addView(ivW);

                    LinearLayout tCol = new LinearLayout(MainActivity.this);
                    tCol.setOrientation(LinearLayout.VERTICAL);
                    tCol.setGravity(Gravity.END);
                    tCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                    TextView tvTemp = new TextView(MainActivity.this);
                    int tmp = RavenWeatherManager.getTemperature(MainActivity.this);
                    if (switchUnit.isChecked()) tmp = (int) Math.round(tmp * 1.8 + 32);
                    tvTemp.setText(tmp + "°");
                    tvTemp.setTextColor(Color.WHITE);
                    tvTemp.setTextSize(26);
                    tvTemp.setTypeface(Typeface.DEFAULT_BOLD);
                    tCol.addView(tvTemp);

                    TextView tvUnit = new TextView(MainActivity.this);
                    tvUnit.setText(switchUnit.isChecked() ? "FAHRENHEIT" : "CELSIUS");
                    tvUnit.setTextColor(Color.parseColor("#80FFFFFF"));
                    tvUnit.setTextSize(8);
                    tvUnit.setTypeface(Typeface.DEFAULT_BOLD);
                    tCol.addView(tvUnit);

                    topRow.addView(tCol);
                    wBox.addView(topRow);

                    TextView tvCond = new TextView(MainActivity.this);
                    tvCond.setText(RavenWeatherManager.getCondition(MainActivity.this));
                    tvCond.setTextColor(Color.parseColor("#FFFF2E4D"));
                    tvCond.setTextSize(13);
                    tvCond.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCond.setPadding(0, 4, 0, 0);
                    wBox.addView(tvCond);

                    TextView tvTag = new TextView(MainActivity.this);
                    String tagVal = etWeatherTag.getText().toString().trim();
                    tvTag.setText(tagVal.isEmpty() ? "OBSIDIAN BREEZE" : tagVal);
                    tvTag.setTextColor(Color.parseColor("#B8B8B8"));
                    tvTag.setTextSize(10);
                    wBox.addView(tvTag);

                    TextView tvCity = new TextView(MainActivity.this);
                    String cityVal = etWeatherCity.getText().toString().trim();
                    tvCity.setText((cityVal.isEmpty() ? "Bengaluru" : cityVal) + " • H: " + RavenWeatherManager.getHigh(MainActivity.this) + "°  L: " + RavenWeatherManager.getLow(MainActivity.this) + "°");
                    tvCity.setTextColor(Color.WHITE);
                    tvCity.setTextSize(11);
                    tvCity.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCity.setPadding(0, 3, 0, 0);
                    wBox.addView(tvCity);

                    dynamicPreviewCard.addView(wBox);

                } else if (formShuffle.getVisibility() == View.VISIBLE) {
                    // Pop Wallpaper (Pure Text) Preview - Centered circular container
                    FrameLayout cWrap = new FrameLayout(MainActivity.this);
                    int cSz = (int)(105 * density);
                    LinearLayout.LayoutParams cwLp = new LinearLayout.LayoutParams(cSz, cSz);
                    cwLp.gravity = Gravity.CENTER;
                    cWrap.setLayoutParams(cwLp);

                    ImageView cBg = new ImageView(MainActivity.this);
                    cBg.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    GradientDrawable cCircle = new GradientDrawable(); cCircle.setShape(GradientDrawable.OVAL); cCircle.setColor(Color.parseColor("#16161A")); cCircle.setStroke(2, Color.parseColor("#2A2A30")); cBg.setBackground(cCircle);
                    cWrap.addView(cBg);

                    LinearLayout sBox = new LinearLayout(MainActivity.this);
                    sBox.setOrientation(LinearLayout.VERTICAL);
                    sBox.setGravity(Gravity.CENTER);
                    sBox.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    TextView sTitle = new TextView(MainActivity.this);
                    sTitle.setText(etShuffleTitle.getText().toString().isEmpty() ? "POP" : etShuffleTitle.getText().toString());
                    sTitle.setTextColor(Color.WHITE);
                    sTitle.setTextSize(18);
                    sTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    sTitle.setLetterSpacing(0.14f);
                    sTitle.setSingleLine(true);
                    sBox.addView(sTitle);

                    TextView sSub = new TextView(MainActivity.this);
                    sSub.setText(etShuffleSub.getText().toString().isEmpty() ? "WALLPAPER" : etShuffleSub.getText().toString());
                    sSub.setTextColor(Color.parseColor("#FFFF2E4D"));
                    sSub.setTextSize(9);
                    sSub.setTypeface(Typeface.DEFAULT_BOLD);
                    sSub.setLetterSpacing(0.12f);
                    sSub.setSingleLine(true);
                    sSub.setPadding(0, 2, 0, 0);
                    sBox.addView(sSub);

                    cWrap.addView(sBox);
                    dynamicPreviewCard.addView(cWrap);

                } else if (formDock.getVisibility() == View.VISIBLE) {
                    // Dock Preview
                    LinearLayout dBox = new LinearLayout(MainActivity.this);
                    dBox.setOrientation(LinearLayout.VERTICAL);
                    dBox.setGravity(Gravity.CENTER);

                    TextView dTitle = new TextView(MainActivity.this);
                    dTitle.setText(etDockTitle.getText().toString().isEmpty() ? "RAVEN DOCK" : etDockTitle.getText().toString());
                    dTitle.setTextColor(Color.WHITE);
                    dTitle.setTextSize(13);
                    dTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    dBox.addView(dTitle);

                    LinearLayout dockIcons = new LinearLayout(MainActivity.this);
                    dockIcons.setOrientation(LinearLayout.HORIZONTAL);
                    dockIcons.setGravity(Gravity.CENTER);
                    dockIcons.setPadding(0, 8, 0, 0);

                    for (int s = 0; s < 5; s++) {
                        ImageView iv = new ImageView(MainActivity.this);
                        int sz = (int)(32 * density);
                        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(sz, sz);
                        ivLp.setMargins(6, 0, 6, 0);
                        iv.setLayoutParams(ivLp);
                        iv.setPadding(3, 3, 3, 3);
                        iv.setBackground(createCapsule());
                        updateSlotIconView(s, iv);
                        dockIcons.addView(iv);
                    }
                    dBox.addView(dockIcons);
                    dynamicPreviewCard.addView(dBox);

                } else {
                    // Anime Clock Preview
                    LinearLayout aBox = new LinearLayout(MainActivity.this);
                    aBox.setOrientation(LinearLayout.VERTICAL);
                    aBox.setGravity(Gravity.CENTER);

                    TextView aTitle = new TextView(MainActivity.this);
                    aTitle.setText(etAnimeTitle.getText().toString().isEmpty() ? "SHADOW RAVEN" : etAnimeTitle.getText().toString());
                    aTitle.setTextColor(Color.parseColor("#DC143C"));
                    aTitle.setTextSize(12);
                    aTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    aBox.addView(aTitle);

                    TextView aClk = new TextView(MainActivity.this);
                    aClk.setText(timeFmt.format(now));
                    aClk.setTextColor(Color.WHITE);
                    aClk.setTextSize(28);
                    aClk.setTypeface(Typeface.DEFAULT_BOLD);
                    aBox.addView(aClk);

                    TextView aTag = new TextView(MainActivity.this);
                    aTag.setText(etAnimeTag.getText().toString().isEmpty() ? "ZONE ACTIVE" : etAnimeTag.getText().toString());
                    aTag.setTextColor(Color.parseColor("#80FF5A73"));
                    aTag.setTextSize(9);
                    aBox.addView(aTag);

                    dynamicPreviewCard.addView(aBox);
                }
            }
        };

        // Tab selection logic
        final View.OnClickListener tabListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                formClock.setVisibility(v == tabClock ? View.VISIBLE : View.GONE);
                formMusic.setVisibility(v == tabMusic ? View.VISIBLE : View.GONE);
                formWeather.setVisibility(v == tabWeather ? View.VISIBLE : View.GONE);
                formShuffle.setVisibility(v == tabShuffle ? View.VISIBLE : View.GONE);
                formDock.setVisibility(v == tabDock ? View.VISIBLE : View.GONE);
                formAnime.setVisibility(v == tabAnime ? View.VISIBLE : View.GONE);

                tabClock.setBackground(v == tabClock ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tabMusic.setBackground(v == tabMusic ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tabWeather.setBackground(v == tabWeather ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tabShuffle.setBackground(v == tabShuffle ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tabDock.setBackground(v == tabDock ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tabAnime.setBackground(v == tabAnime ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));

                updatePreview.run();
            }
        };

        updatePreviewRef[0] = updatePreview;
        tabClock.setOnClickListener(tabListener);
        tabMusic.setOnClickListener(tabListener);
        tabWeather.setOnClickListener(tabListener);
        tabShuffle.setOnClickListener(tabListener);
        tabDock.setOnClickListener(tabListener);
        tabAnime.setOnClickListener(tabListener);

        // TextWatcher to update live preview on type
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview.run(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etClockTitle.addTextChangedListener(tw);
        etClockSub.addTextChangedListener(tw);
        etClockTag.addTextChangedListener(tw);
        etMusicBrand.addTextChangedListener(tw);
        etMusicTitle.addTextChangedListener(tw);
        etMusicArtist.addTextChangedListener(tw);
        etMusicTag.addTextChangedListener(tw);
        etShuffleTitle.addTextChangedListener(tw);
        etShuffleSub.addTextChangedListener(tw);
        etWeatherCity.addTextChangedListener(tw);
        etWeatherTag.addTextChangedListener(tw);
        switchUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean isChecked) { updatePreview.run(); }
        });
        etDockTitle.addTextChangedListener(tw);
        etDockTag.addTextChangedListener(tw);
        etAnimeTitle.addTextChangedListener(tw);
        etAnimeTag.addTextChangedListener(tw);

        switchTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { updatePreview.run(); }
        });

        // Initial tab styling
        tabClock.setBackground(createRedGradient(20));
        updatePreview.run();

        // =========================================================================
        // 3. AUTOMATIC WALLPAPER CHANGER (GOOGLE DRIVE)
        // =========================================================================
        autoWpSectionAnchor = createSectionTitle("AUTOMATIC WALLPAPER CHANGER");
        autoWpSectionAnchor.setPadding(12, (int)(36 * density), 12, (int)(8 * density));
        root.addView(autoWpSectionAnchor);

        TextView autoWpSub = new TextView(this);
        autoWpSub.setText("Powered by your Google Drive collection • High-Res Random Shuffle");
        autoWpSub.setTextColor(Color.parseColor("#B8B8B8"));
        autoWpSub.setTextSize(12);
        autoWpSub.setPadding(16, 0, 16, (int)(16 * density));
        root.addView(autoWpSub);

        // Auto Wallpaper Master Card
        LinearLayout autoWpCard = new LinearLayout(this);
        autoWpCard.setOrientation(LinearLayout.VERTICAL);
        autoWpCard.setPadding((int)(20 * density), (int)(20 * density), (int)(20 * density), (int)(20 * density));
        autoWpCard.setBackground(createModernCard(36));
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
                ? ("● Active: Every " + AutoWallpaperManager.getIntervalMinutes(this) + " mins")
                : "○ Inactive (Manual only)");
        tvAutoWpStatus.setTextColor(AutoWallpaperManager.isAutoEnabled(this) ? Color.parseColor("#44D62C") : Color.parseColor("#8E8E93"));
        tvAutoWpStatus.setTextSize(11);
        tvAutoWpStatus.setPadding(0, 2, 0, 0);
        masterTextCol.addView(tvAutoWpStatus);
        masterRow.addView(masterTextCol);

        final Button btnToggleOn = createPillButton("● ON", null);
        final Button btnToggleOff = createPillButton("○ OFF", null);

        final Runnable updateAutoToggleUI = new Runnable() {
            @Override public void run() {
                boolean isEn = AutoWallpaperManager.isAutoEnabled(MainActivity.this);
                btnToggleOn.setBackground(isEn ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                btnToggleOff.setBackground(!isEn ? createRedGradient(20) : createModernPill("#12FFFFFF", "#28FFFFFF", 20 * density));
                tvAutoWpStatus.setText(isEn
                        ? ("● ACTIVE: Every " + AutoWallpaperManager.getIntervalMinutes(MainActivity.this) + " mins")
                        : "○ PAUSED / OFF (Manual only)");
                tvAutoWpStatus.setTextColor(isEn ? Color.parseColor("#44D62C") : Color.parseColor("#8E8E93"));
            }
        };

        btnToggleOn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AutoWallpaperManager.setAutoEnabled(MainActivity.this, true);
                updateAutoToggleUI.run();
                Toast.makeText(MainActivity.this, "✓ Auto Wallpaper Changer Enabled (ON)", Toast.LENGTH_SHORT).show();
            }
        });

        btnToggleOff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AutoWallpaperManager.setAutoEnabled(MainActivity.this, false);
                updateAutoToggleUI.run();
                Toast.makeText(MainActivity.this, "Auto Wallpaper Changer Paused (OFF)", Toast.LENGTH_SHORT).show();
            }
        });

        int toggleMargin = (int)(6 * density);
        LinearLayout.LayoutParams btnOnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(36 * density));
        btnOnLp.setMargins(0, 0, toggleMargin, 0);
        btnToggleOn.setLayoutParams(btnOnLp);
        btnToggleOff.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(36 * density)));

        updateAutoToggleUI.run();
        masterRow.addView(btnToggleOn);
        masterRow.addView(btnToggleOff);
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
                        tvAutoWpStatus.setText("● Active: Every " + interval + " mins");
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

        Button btnShuffleNow = createRedButton(this, "🔀 Change Wallpaper Now");
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

        Button btnPrevWp = createPillButton("◀", new View.OnClickListener() {
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

        Button btnNextWp = createPillButton("▶", new View.OnClickListener() {
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
        currentWpCard.setBackground(createModernPill("#12FFFFFF", "#26FFFFFF", 20 * density));
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
        Collections.shuffle(initialItems);
        driveItems.addAll(initialItems);

        LinearLayout driveSectionHeader = createGallerySubHeader("RAVEN'S GALLERY", driveItems.size(), true);
        driveSectionHeader.setPadding(12, 8, 12, (int)(14 * density));
        root.addView(driveSectionHeader);

        galleryDrive = new LinearLayout(this);
        galleryDrive.setOrientation(LinearLayout.VERTICAL);
        galleryDrive.setPadding(0, 0, 0, (int)(18 * density));
        populateDriveCards();
        root.addView(galleryDrive);

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
            galleryLocal.addView(createWallpaperCard("Wallpaper #" + index, null, fileName, null));
        }
        hsvLocal.addView(galleryLocal);
        root.addView(hsvLocal);

                // =========================================================================
        // 5. RAVEN AUDIO & RINGTONE SUITE
        // =========================================================================
        View ringtonesSectionAnchor = createSectionTitle("RAVEN AUDIO & RINGTONES");
        ringtonesSectionAnchor.setPadding(12, (int)(32 * density), 12, (int)(8 * density));
        root.addView(ringtonesSectionAnchor);

        TextView ringtonesSub = new TextView(this);
        ringtonesSub.setText("Raven Sound Studio • Set System Ringtones, Notifications & Alarms");
        ringtonesSub.setTextColor(Color.parseColor("#B8B8B8"));
        ringtonesSub.setTextSize(12);
        ringtonesSub.setPadding(16, 0, 16, (int)(14 * density));
        root.addView(ringtonesSub);

        LinearLayout audioCard = new LinearLayout(this);
        audioCard.setOrientation(LinearLayout.VERTICAL);
        audioCard.setBackground(createModernCard(32));
        audioCard.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));
        audioCard.setElevation(6);

        for (int i = 0; i < RavenRingtoneManager.BUILTIN_TONES.length; i++) {
            final int toneIdx = i;
            final RavenRingtoneManager.ToneInfo tone = RavenRingtoneManager.BUILTIN_TONES[i];
            audioCard.addView(createRingtoneRow(tone, toneIdx));
            if (i < RavenRingtoneManager.BUILTIN_TONES.length - 1) {
                View div = new View(this);
                div.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                div.setBackgroundColor(Color.parseColor("#1FFFFFFF"));
                audioCard.addView(div);
            }
        }

        LinearLayout customRow = new LinearLayout(this);
        customRow.setOrientation(LinearLayout.HORIZONTAL);
        customRow.setGravity(Gravity.CENTER_VERTICAL);
        customRow.setPadding(0, (int)(14 * density), 0, 0);

        Button btnImportTune = createPillButton("📁 Choose Custom Audio / Tune", new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Your Tune"), 701);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "File picker not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnImportTune.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(44 * density)));
        customRow.addView(btnImportTune);
        audioCard.addView(customRow);

        root.addView(audioCard);

        // =========================================================================
        // 5. HANDCRAFTED ICON VAULT (CAROUSEL + EXPLORE + SUPPORT)
        // =========================================================================
        View iconsSectionAnchor = createSectionTitle("HANDCRAFTED ICON VAULT");
        iconsSectionAnchor.setPadding(12, (int)(28 * density), 12, (int)(14 * density));
        root.addView(iconsSectionAnchor);

        LinearLayout heroCard = new LinearLayout(this);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        heroCard.setPadding((int)(20 * density), (int)(20 * density), (int)(20 * density), (int)(20 * density));
        heroCard.setBackground(createModernCard(36));
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

        // Missing Icons Request Banner Card
        LinearLayout requestCard = new LinearLayout(this);
        requestCard.setOrientation(LinearLayout.VERTICAL);
        requestCard.setPadding((int)(18 * density), (int)(18 * density), (int)(18 * density), (int)(18 * density));
        requestCard.setBackground(createModernCard(28));
        LinearLayout.LayoutParams reqLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reqLp.topMargin = (int)(14 * density);
        requestCard.setLayoutParams(reqLp);

        TextView reqTitle = new TextView(this);
        reqTitle.setText("✨ MISSING YOUR FAVORITE APPS?");
        reqTitle.setTextColor(Color.parseColor("#FFFF2E4D"));
        reqTitle.setTextSize(13);
        reqTitle.setTypeface(Typeface.DEFAULT_BOLD);
        reqTitle.setLetterSpacing(0.08f);
        requestCard.addView(reqTitle);

        TextView reqDesc = new TextView(this);
        reqDesc.setText("The built-in scanner detects unthemed apps on your device, extracts their package names and components, and sends an instant request directly to the developer.");
        reqDesc.setTextColor(Color.parseColor("#B8B8B8"));
        reqDesc.setTextSize(11);
        reqDesc.setPadding(0, (int)(4 * density), 0, (int)(14 * density));
        requestCard.addView(reqDesc);

        Button btnOpenRequest = createRedButton(this, "🚀 Request Missing Icons (Scan Apps)");
        btnOpenRequest.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                RavenIconRequestManager.showIconRequestDialog(MainActivity.this);
            }
        });
        requestCard.addView(btnOpenRequest);
        root.addView(requestCard);

        activityContainer.addView(mainScrollView);
        setContentView(activityContainer);

        // Register wallpaper & battery receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoWallpaperManager.ACTION_WALLPAPER_CHANGED);
        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(eventReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(eventReceiver, filter);
        }

        // Live system wallpaper colors listener (Android 8.1+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                WallpaperManager wm = WallpaperManager.getInstance(this);
                wm.addOnColorsChangedListener(new WallpaperManager.OnColorsChangedListener() {
                    @Override
                    public void onColorsChanged(WallpaperColors colors, int which) {
                        if ((which & WallpaperManager.FLAG_SYSTEM) != 0 && colors != null) {
                            int accent = 0;
                            if (colors.getPrimaryColor() != null) accent = colors.getPrimaryColor().toArgb();
                            else if (colors.getSecondaryColor() != null) accent = colors.getSecondaryColor().toArgb();
                            if (accent != 0) {
                                // Accent cached
                                WidgetSettingsManager.updateAllWidgets(MainActivity.this);
                                if (mainUpdatePreviewRunnable != null) mainUpdatePreviewRunnable.run();
                            }
                        }
                    }
                }, new Handler(Looper.getMainLooper()));
            } catch (Throwable ignored) {}
        }

        syncLiveDriveWallpapers(false);
    }

    private void updateBatteryPreview(Intent batteryStatus) {
        if (batteryStatus == null || prevBatPct == null || prevBatBar == null) return;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int pct = 85;
        if (level >= 0 && scale > 0) {
            pct = (int) ((level / (float) scale) * 100);
        }
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                              status == BatteryManager.BATTERY_STATUS_FULL);

        prevBatPct.setText(isCharging ? ("⚡ " + pct + "% CHARGING") : (pct + "%"));
        prevBatBar.setProgress(pct);
        prevBatBar.setIndeterminate(isCharging);
    }

    
    private View createRingtoneRow(final RavenRingtoneManager.ToneInfo tone, final int index) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, (int)(10 * density), 0, (int)(10 * density));

        LinearLayout infoRow = new LinearLayout(this);
        infoRow.setOrientation(LinearLayout.HORIZONTAL);
        infoRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(tone.title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        infoRow.addView(tvTitle);

        TextView tvCat = new TextView(this);
        tvCat.setText(tone.category);
        tvCat.setTextColor(Color.parseColor("#FFDC143C"));
        tvCat.setTextSize(9);
        tvCat.setTypeface(Typeface.DEFAULT_BOLD);
        tvCat.setLetterSpacing(0.08f);
        infoRow.addView(tvCat);
        row.addView(infoRow);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(tone.description);
        tvDesc.setTextColor(Color.parseColor("#B8B8B8"));
        tvDesc.setTextSize(10);
        tvDesc.setPadding(0, 2, 0, (int)(8 * density));
        row.addView(tvDesc);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER_VERTICAL);

        final Button btnPlay = createPillButton("▶ Play", null);
        btnPlay.setTextSize(11);
        btnPlay.setLayoutParams(new LinearLayout.LayoutParams((int)(72 * density), (int)(36 * density)));
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (RavenRingtoneManager.isPlaying(index)) {
                    RavenRingtoneManager.stopTone();
                    btnPlay.setText("▶ Play");
                } else {
                    RavenRingtoneManager.playTone(MainActivity.this, index, new Runnable() {
                        @Override public void run() {
                            btnPlay.setText(RavenRingtoneManager.isPlaying(index) ? "⏹ Stop" : "▶ Play");
                        }
                    });
                    btnPlay.setText("⏹ Stop");
                }
            }
        });
        btnRow.addView(btnPlay);

        View s1 = new View(this);
        s1.setLayoutParams(new LinearLayout.LayoutParams((int)(6 * density), 1));
        btnRow.addView(s1);

        Button btnSetRing = createRedButton(this, "🔔 Ringtone");
        btnSetRing.setTextSize(10);
        btnSetRing.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(36 * density), 1.0f));
        btnSetRing.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                applyRingtoneWithCheck(index, RingtoneManager.TYPE_RINGTONE, "Phone Ringtone");
            }
        });
        btnRow.addView(btnSetRing);

        View s2 = new View(this);
        s2.setLayoutParams(new LinearLayout.LayoutParams((int)(6 * density), 1));
        btnRow.addView(s2);

        Button btnSetNotif = createPillButton("💬 Notify", new View.OnClickListener() {
            @Override public void onClick(View v) {
                applyRingtoneWithCheck(index, RingtoneManager.TYPE_NOTIFICATION, "Notification Tone");
            }
        });
        btnSetNotif.setTextSize(10);
        btnSetNotif.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(36 * density), 0.9f));
        btnRow.addView(btnSetNotif);

        View s3 = new View(this);
        s3.setLayoutParams(new LinearLayout.LayoutParams((int)(6 * density), 1));
        btnRow.addView(s3);

        Button btnSetAlarm = createPillButton("⏰ Alarm", new View.OnClickListener() {
            @Override public void onClick(View v) {
                applyRingtoneWithCheck(index, RingtoneManager.TYPE_ALARM, "Alarm Tone");
            }
        });
        btnSetAlarm.setTextSize(10);
        btnSetAlarm.setLayoutParams(new LinearLayout.LayoutParams(0, (int)(36 * density), 0.8f));
        btnRow.addView(btnSetAlarm);

        row.addView(btnRow);
        return row;
    }

    private void applyRingtoneWithCheck(int index, int type, String typeLabel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Toast.makeText(this, "Please enable 'Modify System Settings' to apply " + typeLabel, Toast.LENGTH_LONG).show();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
            return;
        }

        boolean ok = RavenRingtoneManager.applyTone(this, index, type);
        if (ok) {
            Toast.makeText(this, "✓ Applied " + RavenRingtoneManager.BUILTIN_TONES[index].title + " as " + typeLabel + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not set " + typeLabel, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 701 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final Uri uri = data.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
                Toast.makeText(this, "Please enable 'Modify System Settings' to apply custom ringtones", Toast.LENGTH_LONG).show();
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception ignored) {}
                return;
            }
            boolean ok = RavenRingtoneManager.applyCustomAudioUri(this, uri, "Raven Custom Tune", RingtoneManager.TYPE_RINGTONE);
            if (ok) {
                Toast.makeText(this, "✓ Applied your custom tune as Phone Ringtone!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to apply custom tune", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNotificationListenerEnabled() {
        try {
            String pkgName = getPackageName();
            final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
            if (flat != null && !flat.isEmpty()) {
                final String[] names = flat.split(":");
                for (String name : names) {
                    final ComponentName cn = ComponentName.unflattenFromString(name);
                    if (cn != null && TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(eventReceiver); } catch (Exception ignored) {}
    }


    private void setupAmbientFrostedBackdrop(FrameLayout container) {
        ambientBackdropView = new ImageView(this);
        ambientBackdropView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ambientBackdropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(ambientBackdropView);

        ambientScrimView = new View(this);
        ambientScrimView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GradientDrawable scrim = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        0xCC050508,
                        0xDB07070C,
                        0xEB050508
                });
        ambientScrimView.setBackground(scrim);
        container.addView(ambientScrimView);

        updateAmbientBackdrop();
    }

    private void updateAmbientBackdrop() {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap blurred = RavenGlassThemeManager.getFrostedWallpaperBackdrop(MainActivity.this, 360, 640);
                if (blurred != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ambientBackdropView != null) {
                                ambientBackdropView.setImageBitmap(blurred);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        updateAmbientBackdrop();
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
                    updateAmbientBackdrop();
                }
                if (ivCurrentWpThumb != null && thumbUrl != null) {
                    loadThumbnailInto(thumbUrl, ivCurrentWpThumb);
                }
            }
        });
    }

    private View createModernBadge(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(10);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.06f);
        tv.setPadding((int)(12 * density), (int)(6 * density), (int)(12 * density), (int)(6 * density));
        tv.setBackground(createModernPill("#18FFFFFF", "#3DFFFFFF", 18 * density));
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

    private EditText createModernEditText(String initialText) {
        EditText et = new EditText(this);
        et.setText(initialText);
        et.setTextColor(Color.WHITE);
        et.setTextSize(13);
        et.setSingleLine(true);
        et.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(44 * density));
        lp.setMargins(0, 0, 0, (int)(8 * density));
        et.setLayoutParams(lp);
        et.setPadding((int)(14 * density), (int)(10 * density), (int)(14 * density), (int)(10 * density));
        et.setBackground(createModernPill("#14FFFFFF", "#3DFFFFFF", 14 * density));
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
        tv.setTextSize(13);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLetterSpacing(0.08f);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        row.addView(tv);

        if (isCloud) {
            Button btnShuffle = createPillButton("🔀 Shuffle Gallery", new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Collections.shuffle(driveItems);
                    populateDriveCards();
                    Toast.makeText(MainActivity.this, "🔀 Shuffled Raven's Gallery!", Toast.LENGTH_SHORT).show();
                }
            });
            btnShuffle.setTextSize(11);
            btnShuffle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(34 * density)));
            btnShuffle.setPadding((int)(12 * density), 0, (int)(12 * density), 0);
            row.addView(btnShuffle);
        }

        TextView badge = new TextView(this);
        badge.setText(isCloud ? "9 OF " + count : count + " ITEMS");
        badge.setTextColor(Color.parseColor("#B8AAAA"));
        badge.setTextSize(10);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setPadding((int)(8 * density), 0, 0, 0);
        row.addView(badge);
        return row;
    }

    private void populateDriveCards() {
        if (galleryDrive == null) return;
        galleryDrive.removeAllViews();
        if (driveItems.isEmpty()) return;

        int totalToDisplay = Math.min(9, driveItems.size());
        int columns = 3;
        int rows = (int) Math.ceil((double) totalToDisplay / columns);

        for (int r = 0; r < rows; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 0, 0, (int)(6 * density));

            for (int c = 0; c < columns; c++) {
                int index = r * columns + c;
                if (index < totalToDisplay) {
                    final CloudWallpaper item = driveItems.get(index);
                    View card = createWallpaperGridCard(item.title, item.thumbUrl, null, item.fullUrl);
                    row.addView(card);
                } else {
                    View empty = new View(this);
                    empty.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
                    row.addView(empty);
                }
            }
            galleryDrive.addView(row);
        }
    }

    private View createWallpaperGridCard(final String title, final String thumbUrl, final String localAsset, final String fullUrl) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int h = (int)(165 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, h, 1.0f);
        lp.setMargins((int)(3 * density), (int)(3 * density), (int)(3 * density), (int)(3 * density));
        card.setLayoutParams(lp);
        card.setBackground(createModernCard(20));
        card.setPadding(2, 2, 2, 2);

        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setClipToOutline(true);
            iv.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 18 * density);
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
                if (localAsset != null) {
                    intent.putExtra("fileName", localAsset);
                    intent.putExtra("local_asset", localAsset);
                }
                String targetUrl = (fullUrl != null && !fullUrl.isEmpty()) ? fullUrl : thumbUrl;
                if (targetUrl != null) {
                    intent.putExtra("fileUrl", targetUrl);
                    intent.putExtra("full_url", targetUrl);
                }
                startActivity(intent);
            }
        });
        return card;
    }

    private View createWallpaperCard(final String title, final String thumbUrl, final String localAsset, final String fullUrl) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int w = (int)(110 * density);
        int h = (int)(170 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        lp.rightMargin = (int)(12 * density);
        card.setLayoutParams(lp);
        card.setBackground(createModernCard(24));
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
                if (localAsset != null) {
                    intent.putExtra("fileName", localAsset);
                    intent.putExtra("local_asset", localAsset);
                }
                String targetUrl = (fullUrl != null && !fullUrl.isEmpty()) ? fullUrl : thumbUrl;
                if (targetUrl != null) {
                    intent.putExtra("fileUrl", targetUrl);
                    intent.putExtra("full_url", targetUrl);
                }
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
        card.setBackground(createModernCard(32));
        card.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));

        LinearLayout preview = new LinearLayout(this);
        preview.setOrientation(LinearLayout.VERTICAL);
        preview.setPadding((int)(14 * density), (int)(14 * density), (int)(14 * density), (int)(14 * density));
        preview.setBackground(createModernPill("#12FFFFFF", "#38FFFFFF", 20 * density));
        preview.setGravity(Gravity.CENTER);

        Date now = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (type == 1) {
            // Black Raven Clock
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
            // Quick App Dock
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
        } else if (type == 4) {
            // Raven Music Player
            LinearLayout mRow = new LinearLayout(this);
            mRow.setOrientation(LinearLayout.HORIZONTAL);
            mRow.setGravity(Gravity.CENTER_VERTICAL);

            ImageView ivArt = new ImageView(this);
            int artSz = (int)(36 * density);
            ivArt.setLayoutParams(new LinearLayout.LayoutParams(artSz, artSz));
            int musicRes = getResources().getIdentifier("com_convx_music", "drawable", getPackageName());
            if (musicRes != 0) ivArt.setImageResource(musicRes);
            mRow.addView(ivArt);

            LinearLayout mTextCol = new LinearLayout(this);
            mTextCol.setOrientation(LinearLayout.VERTICAL);
            mTextCol.setPadding((int)(8 * density), 0, (int)(8 * density), 0);
            mTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

            TextView mTitle = new TextView(this);
            mTitle.setText(RavenMusicManager.getTitle(this));
            mTitle.setTextColor(Color.WHITE);
            mTitle.setTextSize(12);
            mTitle.setTypeface(Typeface.DEFAULT_BOLD);
            mTextCol.addView(mTitle);

            TextView mArtist = new TextView(this);
            mArtist.setText(RavenMusicManager.getArtist(this));
            mArtist.setTextColor(Color.parseColor("#B8B8B8"));
            mArtist.setTextSize(10);
            mTextCol.addView(mArtist);
            mRow.addView(mTextCol);

            // Miniature Play Circle
            ImageView btnP = new ImageView(this);
            btnP.setLayoutParams(new LinearLayout.LayoutParams((int)(28 * density), (int)(28 * density)));
            int icPlay = getResources().getIdentifier("ic_play_arrow", "drawable", getPackageName());
            if (icPlay != 0) btnP.setImageResource(icPlay);
            int bgProg = getResources().getIdentifier("bg_battery_progress", "drawable", getPackageName());
            if (bgProg != 0) btnP.setBackgroundResource(bgProg);
            btnP.setPadding(4, 4, 4, 4);
            mRow.addView(btnP);
            preview.addView(mRow);

        } else if (type == 5) {
            // Pop Wallpaper (1x1) - Pure Text ONLY
            LinearLayout sBox = new LinearLayout(this);
            sBox.setOrientation(LinearLayout.VERTICAL);
            sBox.setGravity(Gravity.CENTER);
            sBox.setPadding(0, (int)(12 * density), 0, (int)(12 * density));

            TextView sTxt = new TextView(this);
            sTxt.setText(WidgetSettingsManager.getShuffleTitle(this));
            sTxt.setTextColor(Color.WHITE);
            sTxt.setTextSize(18);
            sTxt.setTypeface(Typeface.DEFAULT_BOLD);
            sTxt.setLetterSpacing(0.16f);
            sBox.addView(sTxt);

            TextView sSub = new TextView(this);
            sSub.setText(WidgetSettingsManager.getShuffleSubtitle(this));
            sSub.setTextColor(Color.parseColor("#FFFF2E4D"));
            sSub.setTextSize(9);
            sSub.setTypeface(Typeface.DEFAULT_BOLD);
            sSub.setLetterSpacing(0.14f);
            sSub.setPadding(0, 3, 0, 0);
            sBox.addView(sSub);

            preview.addView(sBox);

        } else if (type == 6) {
            // Raven Music Mini (2x2)
            LinearLayout m2Box = new LinearLayout(this);
            m2Box.setOrientation(LinearLayout.VERTICAL);
            m2Box.setGravity(Gravity.CENTER);

            ImageView iv2Art = new ImageView(this);
            int artSz2 = (int)(38 * density);
            iv2Art.setLayoutParams(new LinearLayout.LayoutParams(artSz2, artSz2));
            int musicRes2 = getResources().getIdentifier("ic_music_note", "drawable", getPackageName());
            if (musicRes2 != 0) iv2Art.setImageResource(musicRes2);
            // Background box removed for clean album art look
            m2Box.addView(iv2Art);

            TextView m2Title = new TextView(this);
            m2Title.setText(RavenMusicManager.getTitle(this));
            m2Title.setTextColor(Color.WHITE);
            m2Title.setTextSize(11);
            m2Title.setTypeface(Typeface.DEFAULT_BOLD);
            m2Title.setMaxLines(1);
            m2Title.setPadding(0, 4, 0, 0);
            m2Box.addView(m2Title);

            TextView m2Artist = new TextView(this);
            m2Artist.setText(RavenMusicManager.getArtist(this));
            m2Artist.setTextColor(Color.parseColor("#B8B8B8"));
            m2Artist.setTextSize(9);
            m2Artist.setMaxLines(1);
            m2Box.addView(m2Artist);

            preview.addView(m2Box);

        } else if (type == 7) {
            // Weather Widget Preview
            LinearLayout wBox = new LinearLayout(this);
            wBox.setOrientation(LinearLayout.VERTICAL);
            wBox.setGravity(Gravity.CENTER);

            ImageView ivW = new ImageView(this);
            int wSz = (int)(32 * density);
            ivW.setLayoutParams(new LinearLayout.LayoutParams(wSz, wSz));
            int wRes = getResources().getIdentifier(RavenWeatherManager.getIconName(this), "drawable", getPackageName());
            if (wRes == 0) wRes = getResources().getIdentifier("ic_weather_sunny", "drawable", getPackageName());
            if (wRes != 0) ivW.setImageResource(wRes);
            wBox.addView(ivW);

            TextView tvTemp = new TextView(this);
            tvTemp.setText(RavenWeatherManager.getTemperature(this) + "°");
            tvTemp.setTextColor(Color.WHITE);
            tvTemp.setTextSize(22);
            tvTemp.setTypeface(Typeface.DEFAULT_BOLD);
            wBox.addView(tvTemp);

            TextView tvCond = new TextView(this);
            tvCond.setText(RavenWeatherManager.getCondition(this));
            tvCond.setTextColor(Color.parseColor("#FFFF2E4D"));
            tvCond.setTextSize(10);
            tvCond.setTypeface(Typeface.DEFAULT_BOLD);
            wBox.addView(tvCond);

            preview.addView(wBox);
        } else if (type == 8) {
            // Music Large 2 (4x2 Minimalist Glass Suite)
            LinearLayout m2Box = new LinearLayout(this);
            m2Box.setOrientation(LinearLayout.VERTICAL);
            m2Box.setGravity(Gravity.CENTER);

            // Row 1: Album art circle + volume pill
            LinearLayout r1 = new LinearLayout(this);
            r1.setOrientation(LinearLayout.HORIZONTAL);
            r1.setGravity(Gravity.CENTER);

            ImageView ivArt = new ImageView(this);
            int szArt = (int)(32 * density);
            ivArt.setLayoutParams(new LinearLayout.LayoutParams(szArt, szArt));
            int musicRes = getResources().getIdentifier("ic_music_note", "drawable", getPackageName());
            if (musicRes != 0) ivArt.setImageResource(musicRes);
            int artBgRes = getResources().getIdentifier("bg_music_circle_art", "drawable", getPackageName());
            if (artBgRes != 0) ivArt.setBackgroundResource(artBgRes);
            r1.addView(ivArt);

            View sp1 = new View(this);
            sp1.setLayoutParams(new LinearLayout.LayoutParams((int)(12 * density), 1));
            r1.addView(sp1);

            LinearLayout vPill = new LinearLayout(this);
            vPill.setOrientation(LinearLayout.HORIZONTAL);
            vPill.setLayoutParams(new LinearLayout.LayoutParams((int)(64 * density), (int)(32 * density)));

            ImageView ivMinus = new ImageView(this);
            ivMinus.setLayoutParams(new LinearLayout.LayoutParams((int)(32 * density), ViewGroup.LayoutParams.MATCH_PARENT));
            int minusRes = getResources().getIdentifier("ic_volume_minus", "drawable", getPackageName());
            if (minusRes != 0) ivMinus.setImageResource(minusRes);
            int minusBg = getResources().getIdentifier("bg_volume_pill_minus", "drawable", getPackageName());
            if (minusBg != 0) ivMinus.setBackgroundResource(minusBg);
            ivMinus.setScaleType(ImageView.ScaleType.CENTER);
            vPill.addView(ivMinus);

            ImageView ivPlus = new ImageView(this);
            ivPlus.setLayoutParams(new LinearLayout.LayoutParams((int)(32 * density), ViewGroup.LayoutParams.MATCH_PARENT));
            int plusRes = getResources().getIdentifier("ic_volume_plus", "drawable", getPackageName());
            if (plusRes != 0) ivPlus.setImageResource(plusRes);
            int plusBg = getResources().getIdentifier("bg_volume_pill_plus", "drawable", getPackageName());
            if (plusBg != 0) ivPlus.setBackgroundResource(plusBg);
            ivPlus.setScaleType(ImageView.ScaleType.CENTER);
            vPill.addView(ivPlus);

            r1.addView(vPill);
            m2Box.addView(r1);

            // Row 2: 3 circular buttons (Prev, Next, Play)
            LinearLayout r2 = new LinearLayout(this);
            r2.setOrientation(LinearLayout.HORIZONTAL);
            r2.setGravity(Gravity.CENTER);
            r2.setPadding(0, (int)(6 * density), 0, 0);

            int btnSz = (int)(30 * density);
            ImageView bPrev = new ImageView(this);
            bPrev.setLayoutParams(new LinearLayout.LayoutParams(btnSz, btnSz));
            int prevRes = getResources().getIdentifier("ic_skip_previous_dark", "drawable", getPackageName());
            if (prevRes != 0) bPrev.setImageResource(prevRes);
            int prevBg = getResources().getIdentifier("bg_music_circle_white", "drawable", getPackageName());
            if (prevBg != 0) bPrev.setBackgroundResource(prevBg);
            bPrev.setScaleType(ImageView.ScaleType.CENTER);
            r2.addView(bPrev);

            View sp2 = new View(this);
            sp2.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
            r2.addView(sp2);

            ImageView bNext = new ImageView(this);
            bNext.setLayoutParams(new LinearLayout.LayoutParams(btnSz, btnSz));
            int nextRes = getResources().getIdentifier("ic_skip_next_dark", "drawable", getPackageName());
            if (nextRes != 0) bNext.setImageResource(nextRes);
            bNext.setBackgroundResource(prevBg);
            bNext.setScaleType(ImageView.ScaleType.CENTER);
            r2.addView(bNext);

            View sp3 = new View(this);
            sp3.setLayoutParams(new LinearLayout.LayoutParams((int)(8 * density), 1));
            r2.addView(sp3);

            ImageView bPlay = new ImageView(this);
            bPlay.setLayoutParams(new LinearLayout.LayoutParams(btnSz, btnSz));
            int playRes = getResources().getIdentifier("ic_play_arrow", "drawable", getPackageName());
            if (playRes != 0) bPlay.setImageResource(playRes);
            int redBg = getResources().getIdentifier("bg_music_circle_red", "drawable", getPackageName());
            if (redBg != 0) bPlay.setBackgroundResource(redBg);
            bPlay.setScaleType(ImageView.ScaleType.CENTER);
            r2.addView(bPlay);

            m2Box.addView(r2);
            preview.addView(m2Box);
        } else {
            // Anime Mini Clock
            TextView b = new TextView(this);
            b.setText(WidgetSettingsManager.getAnimeTitle(this));
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
                .setMessage("1. Touch and hold any empty area on your Home Screen.\n2. Tap 'Widgets'.\n3. Select 'Raven Shade' and place your widget!")
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
        box.setBackground(createModernCard(32));

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
        box.setBackground(createModernCard(36));

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

    private void showAppMenuDialog() {
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding((int)(20 * density), (int)(20 * density), (int)(20 * density), (int)(16 * density));
        box.setBackground(createModernCard(32));

        TextView title = new TextView(this);
        title.setText("RAVEN SHADE MENU");
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.12f);
        title.setPadding(0, 0, 0, (int)(16 * density));
        box.addView(title);

        box.addView(createDialogOptionButton("🚀 Request Missing Icons", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                RavenIconRequestManager.showIconRequestDialog(MainActivity.this);
            }
        }));

        box.addView(createDialogOptionButton("⚡ Apply Icon Pack", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                showApplyLauncherChooser();
            }
        }));

        box.addView(createDialogOptionButton("🕒 Widget Studio", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                if (widgetsSectionAnchor != null) mainScrollView.smoothScrollTo(0, widgetsSectionAnchor.getTop() - 20);
            }
        }));

        box.addView(createDialogOptionButton("🖼 Auto Wallpapers", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                if (autoWpSectionAnchor != null) mainScrollView.smoothScrollTo(0, autoWpSectionAnchor.getTop() - 20);
            }
        }));

        box.addView(createDialogOptionButton("ℹ️ How to Apply Guide", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                showApplyGuideDialog();
            }
        }));

        box.addView(createDialogOptionButton("🔗 Share App", new View.OnClickListener() {
            @Override public void onClick(View v) {
                d.dismiss();
                shareApp();
            }
        }));

        d.setContentView(box);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
    }

    private void showApplyGuideDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("How to Apply Raven Shade")
                .setMessage("1. Long press an empty area on your Home Screen.\n2. Tap 'Customisation' → 'Icon pack'.\n3. Select 'Raven Shade' from the list.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private Button createDialogOptionButton(String text, View.OnClickListener listener) {
        Button btn = createRedButton(this, text);
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
        intent.putExtra(Intent.EXTRA_TEXT, "Check out Raven Shade - Crimson & Obsidian customization app for Android!");
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
                        String[] parts = line.split("\\|", 2);
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
                        Pattern pattern = Pattern.compile("https://drive\\.google\\.com/file/d/([a-zA-Z0-9_-]+)/view");
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

    private void showAppChooserDialog(final int slotIndex, final Runnable onSelected) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                String lA = a.loadLabel(pm).toString();
                String lB = b.loadLabel(pm).toString();
                return lA.compareToIgnoreCase(lB);
            }
        });

        final List<String> labels = new ArrayList<>();
        final List<String> pkgs = new ArrayList<>();
        final List<android.graphics.drawable.Drawable> icons = new ArrayList<>();

        for (ResolveInfo ri : activities) {
            labels.add(ri.loadLabel(pm).toString());
            pkgs.add(ri.activityInfo.packageName);
            icons.add(ri.loadIcon(pm));
        }

        BaseAdapter adapter = new BaseAdapter() {
            @Override public int getCount() { return labels.size(); }
            @Override public Object getItem(int position) { return labels.get(position); }
            @Override public long getItemId(int position) { return position; }
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout row = new LinearLayout(MainActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding((int)(14 * density), (int)(10 * density), (int)(14 * density), (int)(10 * density));

                ImageView iv = new ImageView(MainActivity.this);
                iv.setLayoutParams(new LinearLayout.LayoutParams((int)(36 * density), (int)(36 * density)));
                iv.setImageDrawable(icons.get(position));
                row.addView(iv);

                TextView tv = new TextView(MainActivity.this);
                tv.setText(labels.get(position));
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(14);
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setPadding((int)(12 * density), 0, 0, 0);
                row.addView(tv);

                return row;
            }
        };

        ListView listView = new ListView(this);
        listView.setBackgroundColor(Color.parseColor("#121216"));
        listView.setAdapter(adapter);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(listView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.90), (int)(getResources().getDisplayMetrics().heightPixels * 0.70));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chosenPkg = pkgs.get(position);
                String chosenLabel = labels.get(position);
                WidgetSettingsManager.setDockSlotPackage(MainActivity.this, slotIndex, chosenPkg, chosenLabel);
                WidgetSettingsManager.updateAllWidgets(MainActivity.this);
                Toast.makeText(MainActivity.this, "✓ Slot " + (slotIndex + 1) + " set to " + chosenLabel, Toast.LENGTH_SHORT).show();
                if (onSelected != null) onSelected.run();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateSlotIconView(int slotIndex, ImageView iv) {
        String pkg = WidgetSettingsManager.getDockSlotPackage(this, slotIndex);
        if (pkg != null && !pkg.isEmpty()) {
            try {
                PackageManager pm = getPackageManager();
                android.graphics.drawable.Drawable d = pm.getApplicationIcon(pkg);
                if (d != null) {
                    iv.setImageDrawable(d);
                    return;
                }
            } catch (Throwable ignored) {}
        }
        String[] defaultDrawables = {
                "com_google_android_dialer",
                "com_whatsapp",
                "com_vivaldi_browser",
                "com_convx_music",
                "com_nothing_camera"
        };
        if (slotIndex >= 0 && slotIndex < defaultDrawables.length) {
            int res = getResources().getIdentifier(defaultDrawables[slotIndex], "drawable", getPackageName());
            if (res != 0) iv.setImageResource(res);
        }
    }
}
