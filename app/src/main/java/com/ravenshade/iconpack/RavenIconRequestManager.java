package com.ravenshade.iconpack;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RavenIconRequestManager
 * Scans installed user apps, compares against Raven Shade's active appfilter.xml,
 * detects unthemed/missing apps, fetches complete package & component metadata,
 * and generates developer-ready requests and appfilter XML snippets.
 */
public class RavenIconRequestManager {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static class AppInfo {
        public final String label;
        public final String packageName;
        public final String activityName;
        public final String componentInfo;
        public final String proposedDrawable;
        public final boolean isMissing;
        public boolean isSelected;
        public Drawable icon;

        public AppInfo(String label, String packageName, String activityName,
                       String componentInfo, String proposedDrawable, boolean isMissing, Drawable icon) {
            this.label = label;
            this.packageName = packageName;
            this.activityName = activityName;
            this.componentInfo = componentInfo;
            this.proposedDrawable = proposedDrawable;
            this.isMissing = isMissing;
            this.isSelected = isMissing; // Default selected if missing
            this.icon = icon;
        }
    }

    public interface ScanCallback {
        void onCompleted(List<AppInfo> apps, int totalInstalled, int missingCount, int themedCount);
    }

    /**
     * Loads active themed packages and components from assets/appfilter.xml
     */
    public static Set<String> loadThemedPackages(Context context) {
        Set<String> set = new HashSet<>();
        try {
            InputStream is = context.getAssets().open("appfilter.xml");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                    String component = parser.getAttributeValue(null, "component");
                    if (component != null) {
                        // Extract component: ComponentInfo{pkg/activity} or plain pkg
                        set.add(component.trim().toLowerCase(Locale.ROOT));
                        if (component.contains("{") && component.contains("}")) {
                            int start = component.indexOf('{');
                            int slash = component.indexOf('/', start);
                            if (slash > start) {
                                String pkg = component.substring(start + 1, slash).trim();
                                set.add(pkg.toLowerCase(Locale.ROOT));
                            }
                        } else if (!component.contains("/")) {
                            set.add(component.trim().toLowerCase(Locale.ROOT));
                        }
                    }
                }
                eventType = parser.next();
            }
            is.close();
        } catch (Throwable ignored) {}
        return set;
    }

    /**
     * Asynchronously scans all installed user launcher applications.
     */
    public static void scanInstalledApps(final Context context, final ScanCallback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = context.getPackageManager();
                Set<String> themedSet = loadThemedPackages(context);

                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> activities = pm.queryIntentActivities(mainIntent, 0);
                final List<AppInfo> appList = new ArrayList<>();
                int missing = 0;
                int themed = 0;

                Set<String> processedComponents = new HashSet<>();
                String ourPkg = context.getPackageName();

                for (ResolveInfo ri : activities) {
                    if (ri.activityInfo == null) continue;
                    String pkg = ri.activityInfo.packageName;
                    String act = ri.activityInfo.name;
                    if (ourPkg.equals(pkg)) continue;

                    String compStr = "ComponentInfo{" + pkg + "/" + act + "}";
                    if (processedComponents.contains(compStr)) continue;
                    processedComponents.add(compStr);

                    CharSequence labelSeq = ri.loadLabel(pm);
                    String label = labelSeq != null ? labelSeq.toString().trim() : pkg;
                    if (TextUtils.isEmpty(label)) label = pkg;

                    Drawable icon = null;
                    try {
                        icon = ri.loadIcon(pm);
                    } catch (Throwable ignored) {}

                    boolean isThemed = themedSet.contains(compStr.toLowerCase(Locale.ROOT)) ||
                                       themedSet.contains(pkg.toLowerCase(Locale.ROOT));

                    // Generate proposed drawable identifier, e.g. "com_whatsapp"
                    String cleanPkg = pkg.replace('.', '_').replace('-', '_').toLowerCase(Locale.ROOT);
                    String proposedDrawable = cleanPkg;

                    if (isThemed) {
                        themed++;
                    } else {
                        missing++;
                    }

                    appList.add(new AppInfo(label, pkg, act, compStr, proposedDrawable, !isThemed, icon));
                }

                // Sort: Missing first (A-Z), then Themed (A-Z)
                Collections.sort(appList, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        if (o1.isMissing != o2.isMissing) {
                            return o1.isMissing ? -1 : 1;
                        }
                        return o1.label.compareToIgnoreCase(o2.label);
                    }
                });

                final int finalTotal = appList.size();
                final int finalMissing = missing;
                final int finalThemed = themed;

                MAIN_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onCompleted(appList, finalTotal, finalMissing, finalThemed);
                        }
                    }
                });
            }
        });
    }

    /**
     * Formats the user's selected missing apps into a professional report
     * including device details, app package list, and ready-to-paste appfilter XML.
     */
    public static String buildRequestReport(Context context, List<AppInfo> selectedApps) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("   RAVEN SHADE - MISSING ICONS REQUEST   \n");
        sb.append("=========================================\n");
        sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        sb.append("Android: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("Requested Apps: ").append(selectedApps.size()).append("\n");
        sb.append("Date: ").append(new Date().toString()).append("\n");
        sb.append("=========================================\n\n");

        sb.append("--- REQUESTED APPLICATIONS LIST ---\n");
        for (int i = 0; i < selectedApps.size(); i++) {
            AppInfo app = selectedApps.get(i);
            sb.append(i + 1).append(". ").append(app.label).append("\n");
            sb.append("   Package: ").append(app.packageName).append("\n");
            sb.append("   Activity: ").append(app.activityName).append("\n");
            sb.append("   Component: ").append(app.componentInfo).append("\n\n");
        }

        sb.append("--- APPFILTER.XML SNIPPETS (READY TO PASTE) ---\n");
        for (AppInfo app : selectedApps) {
            sb.append("  <item component=\"").append(app.componentInfo).append("\" ")
              .append("drawable=\"").append(app.proposedDrawable).append("\" />\n");
        }

        sb.append("\n--- THEME_RESOURCES.XML SNIPPETS ---\n");
        for (AppInfo app : selectedApps) {
            sb.append("  <AppIcon selector=\"").append(app.proposedDrawable).append("\" />\n");
        }

        sb.append("\n=========================================\n");
        return sb.toString();
    }

    /**
     * Sends the missing icons request via Email / Share Intent.
     */
    public static void sendRequest(Context context, List<AppInfo> selectedApps) {
        if (selectedApps.isEmpty()) {
            Toast.makeText(context, "Please select at least 1 app to request.", Toast.LENGTH_SHORT).show();
            return;
        }

        String report = buildRequestReport(context, selectedApps);
        String subject = "[Raven Shade] Missing Icon Request (" + selectedApps.size() + " apps)";

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, report);

        try {
            context.startActivity(Intent.createChooser(sendIntent, "Send Icon Request via"));
        } catch (Exception e) {
            copyToClipboard(context, report, selectedApps.size());
        }
    }

    /**
     * Copies request to clipboard with instant confirmation toast.
     */
    public static void copyToClipboard(Context context, String report, int count) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData clip = ClipData.newPlainText("Raven Shade Icon Request", report);
            cm.setPrimaryClip(clip);
            Toast.makeText(context, "✓ Copied request for " + count + " apps & XML to clipboard!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Renders a Raven Dark Card Drawable for dialogs.
     */
    public static GradientDrawable createCard(int bgColor, int strokeColor, float radiusDp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable g = new GradientDrawable();
        g.setColor(bgColor);
        g.setCornerRadius(radiusDp * density);
        if (strokeColor != 0) {
            g.setStroke((int)(1.2f * density), strokeColor);
        }
        return g;
    }

    /**
     * Displays the interactive full-featured Raven Dark Missing Icon Request Dialog.
     */
    public static void showIconRequestDialog(final Activity activity) {
        final float density = activity.getResources().getDisplayMetrics().density;

        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFA060608));
        }

        final FrameLayout root = new FrameLayout(activity);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Loading Screen
        final LinearLayout loadingBox = new LinearLayout(activity);
        loadingBox.setOrientation(LinearLayout.VERTICAL);
        loadingBox.setGravity(Gravity.CENTER);
        loadingBox.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ProgressBar pb = new ProgressBar(activity);
        loadingBox.addView(pb);

        TextView tvLoading = new TextView(activity);
        tvLoading.setText("Scanning installed apps & fetching packages...");
        tvLoading.setTextColor(Color.WHITE);
        tvLoading.setTextSize(13);
        tvLoading.setPadding(0, (int)(16 * density), 0, 0);
        loadingBox.addView(tvLoading);
        root.addView(loadingBox);

        dialog.setContentView(root);
        dialog.show();

        // Scan apps asynchronously
        scanInstalledApps(activity, new ScanCallback() {
            @Override
            public void onCompleted(final List<AppInfo> apps, int totalInstalled, int missingCount, int themedCount) {
                root.removeView(loadingBox);

                // Build full interactive request view
                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                // --- 1. TOP HEADER BAR ---
                LinearLayout topBar = new LinearLayout(activity);
                topBar.setOrientation(LinearLayout.HORIZONTAL);
                topBar.setGravity(Gravity.CENTER_VERTICAL);
                topBar.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(12 * density));
                topBar.setBackground(createCard(0x4D101018, 0x33FFFFFF, 0, activity));

                // Close Button
                TextView btnClose = new TextView(activity);
                btnClose.setText("✕");
                btnClose.setTextColor(Color.WHITE);
                btnClose.setTextSize(20);
                btnClose.setGravity(Gravity.CENTER);
                int btnSz = (int)(38 * density);
                LinearLayout.LayoutParams bcLp = new LinearLayout.LayoutParams(btnSz, btnSz);
                btnClose.setLayoutParams(bcLp);
                btnClose.setBackground(createCard(0x33FFFFFF, 0x55FFFFFF, 19, activity));
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) { dialog.dismiss(); }
                });
                topBar.addView(btnClose);

                LinearLayout titleCol = new LinearLayout(activity);
                titleCol.setOrientation(LinearLayout.VERTICAL);
                titleCol.setPadding((int)(14 * density), 0, 0, 0);
                titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                TextView tvHeader = new TextView(activity);
                tvHeader.setText("REQUEST MISSING ICONS");
                tvHeader.setTextColor(Color.WHITE);
                tvHeader.setTextSize(15);
                tvHeader.setTypeface(Typeface.DEFAULT_BOLD);
                tvHeader.setLetterSpacing(0.12f);
                titleCol.addView(tvHeader);

                TextView tvSubHeader = new TextView(activity);
                tvSubHeader.setText(missingCount + " Missing • " + themedCount + " Themed • " + totalInstalled + " Total");
                tvSubHeader.setTextColor(Color.parseColor("#B8B8B8"));
                tvSubHeader.setTextSize(11);
                titleCol.addView(tvSubHeader);

                topBar.addView(titleCol);
                mainLayout.addView(topBar);

                // --- 2. SEARCH & FILTER CONTROLS ---
                LinearLayout filterCard = new LinearLayout(activity);
                filterCard.setOrientation(LinearLayout.VERTICAL);
                filterCard.setPadding((int)(16 * density), (int)(10 * density), (int)(16 * density), (int)(8 * density));

                final EditText etSearch = new EditText(activity);
                etSearch.setHint("🔍 Search installed apps or package names...");
                etSearch.setHintTextColor(Color.parseColor("#777777"));
                etSearch.setTextColor(Color.WHITE);
                etSearch.setTextSize(13);
                etSearch.setSingleLine(true);
                etSearch.setPadding((int)(14 * density), (int)(10 * density), (int)(14 * density), (int)(10 * density));
                etSearch.setBackground(createCard(0x28FFFFFF, 0x44FFFFFF, 20, activity));
                filterCard.addView(etSearch);

                // Filter tabs & selection controls
                LinearLayout filterRow = new LinearLayout(activity);
                filterRow.setOrientation(LinearLayout.HORIZONTAL);
                filterRow.setGravity(Gravity.CENTER_VERTICAL);
                filterRow.setPadding(0, (int)(10 * density), 0, 0);

                final boolean[] filterMissingOnly = new boolean[]{ true }; // default show missing

                final TextView tabMissing = new TextView(activity);
                tabMissing.setText("Missing (" + missingCount + ")");
                tabMissing.setTextSize(11);
                tabMissing.setTypeface(Typeface.DEFAULT_BOLD);
                tabMissing.setPadding((int)(12 * density), (int)(6 * density), (int)(12 * density), (int)(6 * density));
                tabMissing.setBackground(createCard(0xFFDC143C, 0xFFFF2E4D, 16, activity));
                tabMissing.setTextColor(Color.WHITE);
                filterRow.addView(tabMissing);

                final TextView tabAll = new TextView(activity);
                tabAll.setText("All (" + totalInstalled + ")");
                tabAll.setTextSize(11);
                tabAll.setTypeface(Typeface.DEFAULT_BOLD);
                tabAll.setPadding((int)(12 * density), (int)(6 * density), (int)(12 * density), (int)(6 * density));
                tabAll.setBackground(createCard(0x22FFFFFF, 0x44FFFFFF, 16, activity));
                tabAll.setTextColor(Color.parseColor("#BBBBBB"));
                LinearLayout.LayoutParams taLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                taLp.leftMargin = (int)(8 * density);
                tabAll.setLayoutParams(taLp);
                filterRow.addView(tabAll);

                View spacer = new View(activity);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
                filterRow.addView(spacer);

                final TextView btnSelectToggle = new TextView(activity);
                btnSelectToggle.setText("Select None");
                btnSelectToggle.setTextSize(11);
                btnSelectToggle.setTypeface(Typeface.DEFAULT_BOLD);
                btnSelectToggle.setTextColor(Color.parseColor("#4DA8DA"));
                btnSelectToggle.setPadding((int)(8 * density), (int)(4 * density), (int)(8 * density), (int)(4 * density));
                filterRow.addView(btnSelectToggle);

                filterCard.addView(filterRow);
                mainLayout.addView(filterCard);

                // --- 3. SCROLLABLE APPS LIST ---
                final ScrollView scrollView = new ScrollView(activity);
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

                final LinearLayout listContainer = new LinearLayout(activity);
                listContainer.setOrientation(LinearLayout.VERTICAL);
                listContainer.setPadding((int)(16 * density), (int)(4 * density), (int)(16 * density), (int)(80 * density));
                scrollView.addView(listContainer);
                mainLayout.addView(scrollView);

                // --- 4. BOTTOM ACTION BAR ---
                LinearLayout bottomBar = new LinearLayout(activity);
                bottomBar.setOrientation(LinearLayout.HORIZONTAL);
                bottomBar.setGravity(Gravity.CENTER_VERTICAL);
                bottomBar.setPadding((int)(16 * density), (int)(12 * density), (int)(16 * density), (int)(16 * density));
                bottomBar.setBackground(createCard(0xEE0B0B10, 0x4DFFFFFF, 0, activity));

                final TextView tvSelectedCount = new TextView(activity);
                tvSelectedCount.setTextColor(Color.WHITE);
                tvSelectedCount.setTextSize(12);
                tvSelectedCount.setTypeface(Typeface.DEFAULT_BOLD);
                tvSelectedCount.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                bottomBar.addView(tvSelectedCount);

                Button btnCopy = new Button(activity);
                btnCopy.setText("📋 Copy");
                btnCopy.setTextColor(Color.WHITE);
                btnCopy.setTextSize(11);
                btnCopy.setTypeface(Typeface.DEFAULT_BOLD);
                btnCopy.setBackground(createCard(0x33FFFFFF, 0x66FFFFFF, 20, activity));
                LinearLayout.LayoutParams cpLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, (int)(44 * density));
                cpLp.rightMargin = (int)(8 * density);
                btnCopy.setLayoutParams(cpLp);
                bottomBar.addView(btnCopy);

                Button btnSend = new Button(activity);
                btnSend.setText("✉ Send Request");
                btnSend.setTextColor(Color.WHITE);
                btnSend.setTextSize(12);
                btnSend.setTypeface(Typeface.DEFAULT_BOLD);
                btnSend.setBackground(createCard(0xFFDC143C, 0xFFFF2E4D, 20, activity));
                btnSend.setLayoutParams(new LinearLayout.LayoutParams(
                        (int)(140 * density), (int)(44 * density)));
                bottomBar.addView(btnSend);

                mainLayout.addView(bottomBar);
                root.addView(mainLayout);

                // Live list population and refresh handler
                final Runnable refreshSelectedCount = new Runnable() {
                    @Override
                    public void run() {
                        int selCount = 0;
                        for (AppInfo a : apps) {
                            if (a.isSelected) selCount++;
                        }
                        tvSelectedCount.setText("✓ " + selCount + " Selected");
                        btnSelectToggle.setText(selCount > 0 ? "Select None" : "Select All");
                    }
                };

                final Runnable populateList = new Runnable() {
                    @Override
                    public void run() {
                        listContainer.removeAllViews();
                        String query = etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
                        boolean onlyMissing = filterMissingOnly[0];

                        for (final AppInfo app : apps) {
                            if (onlyMissing && !app.isMissing) continue;

                            if (!query.isEmpty()) {
                                boolean matchName = app.label.toLowerCase(Locale.ROOT).contains(query);
                                boolean matchPkg = app.packageName.toLowerCase(Locale.ROOT).contains(query);
                                if (!matchName && !matchPkg) continue;
                            }

                            // App Item Row
                            final LinearLayout row = new LinearLayout(activity);
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            row.setGravity(Gravity.CENTER_VERTICAL);
                            row.setPadding((int)(12 * density), (int)(10 * density), (int)(12 * density), (int)(10 * density));
                            row.setBackground(createCard(0x1F202030, 0x24FFFFFF, 16, activity));
                            LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            rLp.bottomMargin = (int)(6 * density);
                            row.setLayoutParams(rLp);

                            // App Icon
                            ImageView ivIcon = new ImageView(activity);
                            int icSz = (int)(40 * density);
                            ivIcon.setLayoutParams(new LinearLayout.LayoutParams(icSz, icSz));
                            if (app.icon != null) {
                                ivIcon.setImageDrawable(app.icon);
                            }
                            row.addView(ivIcon);

                            // Text details
                            LinearLayout textCol = new LinearLayout(activity);
                            textCol.setOrientation(LinearLayout.VERTICAL);
                            textCol.setPadding((int)(12 * density), 0, (int)(8 * density), 0);
                            textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                            LinearLayout labelRow = new LinearLayout(activity);
                            labelRow.setOrientation(LinearLayout.HORIZONTAL);
                            labelRow.setGravity(Gravity.CENTER_VERTICAL);

                            TextView tvName = new TextView(activity);
                            tvName.setText(app.label);
                            tvName.setTextColor(Color.WHITE);
                            tvName.setTextSize(13);
                            tvName.setTypeface(Typeface.DEFAULT_BOLD);
                            tvName.setSingleLine(true);
                            tvName.setEllipsize(TextUtils.TruncateAt.END);
                            labelRow.addView(tvName);

                            // Missing or Themed Pill
                            TextView pill = new TextView(activity);
                            pill.setText(app.isMissing ? " MISSING " : " THEMED ");
                            pill.setTextSize(8);
                            pill.setTypeface(Typeface.DEFAULT_BOLD);
                            pill.setPadding((int)(6 * density), (int)(2 * density), (int)(6 * density), (int)(2 * density));
                            pill.setTextColor(Color.WHITE);
                            pill.setBackground(createCard(
                                    app.isMissing ? 0x99DC143C : 0x8810B981, 0, 10, activity));
                            LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            pLp.leftMargin = (int)(6 * density);
                            pill.setLayoutParams(pLp);
                            labelRow.addView(pill);
                            textCol.addView(labelRow);

                            TextView tvPkg = new TextView(activity);
                            tvPkg.setText(app.packageName);
                            tvPkg.setTextColor(Color.parseColor("#9E9E9E"));
                            tvPkg.setTextSize(10);
                            tvPkg.setTypeface(Typeface.MONOSPACE);
                            tvPkg.setSingleLine(true);
                            tvPkg.setEllipsize(TextUtils.TruncateAt.END);
                            tvPkg.setPadding(0, 2, 0, 0);
                            textCol.addView(tvPkg);

                            row.addView(textCol);

                            // Checkbox
                            final CheckBox cb = new CheckBox(activity);
                            cb.setChecked(app.isSelected);
                            cb.setClickable(false); // row handles click
                            row.addView(cb);

                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    app.isSelected = !app.isSelected;
                                    cb.setChecked(app.isSelected);
                                    refreshSelectedCount.run();
                                }
                            });

                            listContainer.addView(row);
                        }
                        refreshSelectedCount.run();
                    }
                };

                // Initial populate
                populateList.run();

                // Search watcher
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        populateList.run();
                    }
                    @Override public void afterTextChanged(Editable s) {}
                });

                // Tab Missing click
                tabMissing.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        filterMissingOnly[0] = true;
                        tabMissing.setBackground(createCard(0xFFDC143C, 0xFFFF2E4D, 16, activity));
                        tabMissing.setTextColor(Color.WHITE);
                        tabAll.setBackground(createCard(0x22FFFFFF, 0x44FFFFFF, 16, activity));
                        tabAll.setTextColor(Color.parseColor("#BBBBBB"));
                        populateList.run();
                    }
                });

                // Tab All click
                tabAll.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        filterMissingOnly[0] = false;
                        tabAll.setBackground(createCard(0xFFDC143C, 0xFFFF2E4D, 16, activity));
                        tabAll.setTextColor(Color.WHITE);
                        tabMissing.setBackground(createCard(0x22FFFFFF, 0x44FFFFFF, 16, activity));
                        tabMissing.setTextColor(Color.parseColor("#BBBBBB"));
                        populateList.run();
                    }
                });

                // Select All / None toggle
                btnSelectToggle.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        int selCount = 0;
                        for (AppInfo a : apps) {
                            if (a.isSelected) selCount++;
                        }
                        boolean newState = selCount == 0;
                        for (AppInfo a : apps) {
                            if (filterMissingOnly[0]) {
                                if (a.isMissing) a.isSelected = newState;
                            } else {
                                a.isSelected = newState;
                            }
                        }
                        populateList.run();
                    }
                });

                // Bottom button Copy
                btnCopy.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        List<AppInfo> selected = new ArrayList<>();
                        for (AppInfo a : apps) {
                            if (a.isSelected) selected.add(a);
                        }
                        if (selected.isEmpty()) {
                            Toast.makeText(activity, "Select at least 1 app to copy.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String report = buildRequestReport(activity, selected);
                        copyToClipboard(activity, report, selected.size());
                    }
                });

                // Bottom button Send
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        List<AppInfo> selected = new ArrayList<>();
                        for (AppInfo a : apps) {
                            if (a.isSelected) selected.add(a);
                        }
                        sendRequest(activity, selected);
                    }
                });
            }
        });
    }
}
