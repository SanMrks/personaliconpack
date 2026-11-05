import re

MAIN_JAVA = r"E:\Android\app\src\main\java\com\ravenshade\iconpack\MainActivity.java"

with open(MAIN_JAVA, 'r', encoding='utf-8') as f:
    code = f.read()

# 1. Add missing imports
needed_imports = [
    "import android.appwidget.AppWidgetManager;",
    "import android.content.ComponentName;",
    "import java.text.SimpleDateFormat;",
    "import java.util.Date;",
    "import java.util.Locale;"
]
import_block = ""
for imp in needed_imports:
    if imp not in code:
        import_block += imp + "\n"

if import_block:
    code = code.replace("import android.app.Activity;", "import android.app.Activity;\n" + import_block)

# 2. Add WIDGETS section right before // --- 6. SECTION: WALLPAPERS VAULT ---
section_code = """
        // --- 5.5. SECTION: USABLE WIDGETS ---
        View widgetsSectionAnchor = createSectionTitle("USABLE WIDGETS");
        widgetsSectionAnchor.setPadding(12, 54, 12, 16);
        root.addView(widgetsSectionAnchor);

        TextView widgetsSub = new TextView(this);
        widgetsSub.setText("Tap to add matching Obsidian & Crimson widgets directly to your Home Screen");
        widgetsSub.setTextColor(Color.parseColor("#B8AAAA"));
        widgetsSub.setTextSize(12);
        widgetsSub.setPadding(16, 0, 16, 24);
        root.addView(widgetsSub);

        HorizontalScrollView hsvWidgets = new HorizontalScrollView(this);
        hsvWidgets.setHorizontalScrollBarEnabled(false);
        hsvWidgets.setPadding(0, 0, 0, 24);

        LinearLayout widgetsRow = new LinearLayout(this);
        widgetsRow.setOrientation(LinearLayout.HORIZONTAL);

        // Widget 1: Black Raven Clock (4x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Black Raven Clock (4x2)",
            "Digital Clock \u2022 Date \u2022 Live Battery Bar",
            BlackRavenWidgetProvider.class,
            1
        ));

        // Widget 2: Quick App Dock (4x1)
        widgetsRow.addView(createWidgetPreviewCard(
            "Quick App Dock (4x1)",
            "Obsidian Dock \u2022 5 Quick Launch Shortcuts",
            QuickDockWidgetProvider.class,
            2
        ));

        // Widget 3: Anime Mini Clock (2x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Anime Mini Clock (2x2)",
            "Compact Obsidian & Crimson Square Clock",
            AnimeClockWidgetProvider.class,
            3
        ));

        hsvWidgets.addView(widgetsRow);
        root.addView(hsvWidgets);
"""

target_marker = "// --- 6. SECTION: WALLPAPERS VAULT (LIVE CLOUD + BUNDLED) ---"
if target_marker in code and "createWidgetPreviewCard" not in code:
    code = code.replace(target_marker, section_code + "\n        " + target_marker)

# 3. Add helper methods createWidgetPreviewCard and pinWidget
helpers = """
    // --- USABLE WIDGET HELPERS ---

    private View createWidgetPreviewCard(final String title, final String subtitle, final Class<?> providerClass, int type) {
        float density = getResources().getDisplayMetrics().density;
        int cardWidth = (int) (density * 310);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(14, 0, 14, 0);
        card.setLayoutParams(cardLp);
        card.setBackground(createLiquidGlassCard(36));
        card.setPadding((int)(density * 18), (int)(density * 18), (int)(density * 18), (int)(density * 18));
        card.setElevation(8);

        // Preview Container
        LinearLayout preview = new LinearLayout(this);
        preview.setOrientation(LinearLayout.VERTICAL);
        preview.setPadding((int)(density * 14), (int)(density * 14), (int)(density * 14), (int)(density * 14));
        preview.setBackground(createLiquidGlassCard(24));

        Date now = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE | d MMM", Locale.getDefault());

        if (type == 1) {
            // Black Raven 4x2 preview
            LinearLayout hdr = new LinearLayout(this);
            hdr.setOrientation(LinearLayout.HORIZONTAL);
            hdr.setGravity(Gravity.CENTER_VERTICAL);

            TextView tTag = new TextView(this);
            tTag.setText("BLACK RAVEN");
            tTag.setTextColor(Color.parseColor("#FF2E4D"));
            tTag.setTextSize(12);
            tTag.setTypeface(Typeface.DEFAULT_BOLD);
            LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            tTag.setLayoutParams(tagLp);
            hdr.addView(tTag);

            TextView tWea = new TextView(this);
            tWea.setText("OBSIDIAN");
            tWea.setTextColor(Color.parseColor("#CCFFFFFF"));
            tWea.setTextSize(10);
            tWea.setTypeface(Typeface.DEFAULT_BOLD);
            hdr.addView(tWea);
            preview.addView(hdr);

            LinearLayout mid = new LinearLayout(this);
            mid.setOrientation(LinearLayout.HORIZONTAL);
            mid.setGravity(Gravity.CENTER_VERTICAL);
            mid.setPadding(0, 12, 0, 12);

            TextView tTime = new TextView(this);
            tTime.setText(timeFmt.format(now));
            tTime.setTextColor(Color.WHITE);
            tTime.setTextSize(26);
            tTime.setTypeface(Typeface.DEFAULT_BOLD);
            mid.addView(tTime);

            LinearLayout dBox = new LinearLayout(this);
            dBox.setOrientation(LinearLayout.VERTICAL);
            dBox.setPadding(20, 0, 0, 0);

            TextView tDate = new TextView(this);
            tDate.setText(dateFmt.format(now));
            tDate.setTextColor(Color.parseColor("#E6FFFFFF"));
            tDate.setTextSize(11);
            tDate.setTypeface(Typeface.DEFAULT_BOLD);
            dBox.addView(tDate);

            TextView tSub = new TextView(this);
            tSub.setText("RAVENSHADE EDITION");
            tSub.setTextColor(Color.parseColor("#80FF5A73"));
            tSub.setTextSize(8);
            tSub.setLetterSpacing(0.04f);
            dBox.addView(tSub);

            mid.addView(dBox);
            preview.addView(mid);

            // Battery bar
            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(100);
            pb.setProgress(85);
            pb.setProgressDrawable(getResources().getDrawable(R.drawable.bg_battery_progress));
            pb.setBackground(getResources().getDrawable(R.drawable.bg_battery_track));
            LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(density * 5));
            pbLp.topMargin = 8;
            pb.setLayoutParams(pbLp);
            preview.addView(pb);

        } else if (type == 2) {
            // Quick Dock preview
            LinearLayout dock = new LinearLayout(this);
            dock.setOrientation(LinearLayout.HORIZONTAL);
            dock.setGravity(Gravity.CENTER);
            dock.setPadding(0, 8, 0, 8);

            String[] dockDrawables = {
                "com_google_android_dialer",
                "com_whatsapp",
                "com_vivaldi_browser",
                "com_convx_music",
                "com_nothing_camera"
            };

            for (String dName : dockDrawables) {
                int resId = getResources().getIdentifier(dName, "drawable", getPackageName());
                if (resId != 0) {
                    ImageView iv = new ImageView(this);
                    int sz = (int) (density * 36);
                    LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(sz, sz);
                    ivLp.setMargins((int)(density * 4), 0, (int)(density * 4), 0);
                    iv.setLayoutParams(ivLp);
                    iv.setImageResource(resId);
                    dock.addView(iv);
                }
            }
            preview.addView(dock);

        } else {
            // Anime Mini Clock preview
            LinearLayout miniBox = new LinearLayout(this);
            miniBox.setOrientation(LinearLayout.VERTICAL);
            miniBox.setGravity(Gravity.CENTER);

            TextView bTag = new TextView(this);
            bTag.setText("RAVENSHADE");
            bTag.setTextColor(Color.parseColor("#FF2E4D"));
            bTag.setTextSize(10);
            bTag.setTypeface(Typeface.DEFAULT_BOLD);
            bTag.setLetterSpacing(0.12f);
            miniBox.addView(bTag);

            TextView mTime = new TextView(this);
            mTime.setText(timeFmt.format(now));
            mTime.setTextColor(Color.WHITE);
            mTime.setTextSize(24);
            mTime.setTypeface(Typeface.DEFAULT_BOLD);
            miniBox.addView(mTime);

            TextView mDate = new TextView(this);
            mDate.setText(dateFmt.format(now));
            mDate.setTextColor(Color.parseColor("#CCFFFFFF"));
            mDate.setTextSize(10);
            miniBox.addView(mDate);

            preview.addView(miniBox);
        }

        card.addView(preview);

        // Titles below preview
        TextView wTitle = new TextView(this);
        wTitle.setText(title);
        wTitle.setTextColor(Color.WHITE);
        wTitle.setTextSize(14);
        wTitle.setTypeface(Typeface.DEFAULT_BOLD);
        wTitle.setPadding(0, (int)(density * 12), 0, (int)(density * 2));
        card.addView(wTitle);

        TextView wSub = new TextView(this);
        wSub.setText(subtitle);
        wSub.setTextColor(Color.parseColor("#B8AAAA"));
        wSub.setTextSize(11);
        wSub.setPadding(0, 0, 0, (int)(density * 12));
        card.addView(wSub);

        // "+ Add to Home Screen" Pill Button
        Button addBtn = createSmallPillButton("+ Add to Home Screen", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinWidget(providerClass);
            }
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
                new int[]{Color.parseColor("#FF2E4D"), Color.parseColor("#FF4364")}
        );
        bg.setCornerRadius(24);
        btn.setBackground(bg);
        btn.setElevation(6);
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
                .setMessage("1. Touch and hold any empty area on your Home Screen.\\n2. Tap 'Widgets'.\\n3. Select 'Ravenshade' and place your widget!")
                .setPositiveButton("Got it", null)
                .show();
    }
"""

# Insert helpers right before the end of the class
last_brace = code.rfind("}")
if last_brace != -1 and "createWidgetPreviewCard" not in code:
    code = code[:last_brace] + helpers + "\n}\n"

with open(MAIN_JAVA, 'w', encoding='utf-8') as f:
    f.write(code)

print("Updated MainActivity.java with Usable Widgets section and direct Pinning support!")
