import sys
import re

path = "app/src/main/java/com/ravenshade/iconpack/MainActivity.java"
with open(path, "r", encoding="utf-8") as f:
    code = f.read()

# 1. Imports
if "import android.graphics.drawable.LayerDrawable;" not in code:
    code = code.replace(
        "import android.graphics.drawable.GradientDrawable;",
        "import android.graphics.drawable.Drawable;\nimport android.graphics.drawable.GradientDrawable;\nimport android.graphics.drawable.LayerDrawable;"
    )

# 2. Add fields
fields_search = "    // Broadcast receiver for live wallpaper and battery changes"
fields_replace = """    // Ambient Frosted Liquid Glass Backdrop
    private ImageView ambientBackdropView;
    private View ambientScrimView;
    private FrameLayout activityContainer;

    // Broadcast receiver for live wallpaper and battery changes"""

if "private ImageView ambientBackdropView;" not in code:
    code = code.replace(fields_search, fields_replace)

# 3. Update UI helpers
helpers_pattern = re.compile(
    r"    // --- MODERN OBSIDIAN & CRIMSON UI HELPERS ---.*?(?=    @Override\s+protected void onCreate)",
    re.DOTALL
)

new_helpers = """    // --- FROSTED LIQUID GLASS UI HELPERS (OBSIDIAN & CRIMSON) ---

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

"""

code = helpers_pattern.sub(new_helpers, code)

# 4. In onCreate, initialize activityContainer and setup ambient frosted backdrop
on_create_target = """        mainScrollView = new ScrollView(this);
        mainScrollView.setBackground(createScreenBackground());
        mainScrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);"""

on_create_replace = """        activityContainer = new FrameLayout(this);
        activityContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setupAmbientFrostedBackdrop(activityContainer);

        mainScrollView = new ScrollView(this);
        mainScrollView.setBackgroundColor(Color.TRANSPARENT);
        mainScrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);"""

if on_create_target in code:
    code = code.replace(on_create_target, on_create_replace)

# 5. Replace setContentView(mainScrollView); with activityContainer
if "setContentView(mainScrollView);" in code:
    code = code.replace("setContentView(mainScrollView);", "activityContainer.addView(mainScrollView);\n        setContentView(activityContainer);")

# 6. Add setupAmbientFrostedBackdrop and updateAmbientBackdrop methods
backdrop_methods = """
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
"""

if "private void setupAmbientFrostedBackdrop" not in code:
    # insert before onResume
    code = code.replace("    @Override\n    protected void onResume() {", backdrop_methods + "\n    @Override\n    protected void onResume() {\n        updateAmbientBackdrop();")

# 7. In updateCurrentWallpaperUI, call updateAmbientBackdrop
if "tvCurrentWpTitle.setText(title);" in code:
    code = code.replace("tvCurrentWpTitle.setText(title);", "tvCurrentWpTitle.setText(title);\n                    updateAmbientBackdrop();")

with open(path, "w", encoding="utf-8") as f:
    f.write(code)

print("Successfully applied Frosted Liquid Glass UI to MainActivity.java")
