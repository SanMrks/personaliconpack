package com.ravenshade.iconpack;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WallpaperViewerActivity extends Activity {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);

    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap currentBitmap;
    private String wallpaperName;
    private Uri savedImageUri;

    // Liquid Glass Card / Pill Helper
    public static GradientDrawable createLiquidGlass(int bgColor, int strokeColor, float radius, int strokeWidth) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(bgColor);
        g.setCornerRadius(radius);
        if (strokeWidth > 0) {
            g.setStroke(strokeWidth, strokeColor);
        }
        return g;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fn = getIntent().getStringExtra("fileName");
        if (fn == null || fn.isEmpty()) fn = getIntent().getStringExtra("local_asset");
        final String fileName = fn;

        String fu = getIntent().getStringExtra("fileUrl");
        if (fu == null || fu.isEmpty()) fu = getIntent().getStringExtra("full_url");
        final String fileUrl = fu;

        wallpaperName = getIntent().getStringExtra("title");
        if (wallpaperName == null) wallpaperName = "Wallpaper";

        // Root container
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#060608"));

        // Fullscreen Image View
        imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(imageView);

        // Ambient Bottom & Top Gradient Scrims for readable glass controls
        View topScrim = new View(this);
        GradientDrawable topGrad = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#99050507"), Color.TRANSPARENT});
        topScrim.setBackground(topGrad);
        FrameLayout.LayoutParams topScrimParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 260);
        topScrimParams.gravity = Gravity.TOP;
        topScrim.setLayoutParams(topScrimParams);
        root.addView(topScrim);

        View bottomScrim = new View(this);
        GradientDrawable botGrad = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.parseColor("#E6050507"), Color.parseColor("#66050507"), Color.TRANSPARENT});
        bottomScrim.setBackground(botGrad);
        FrameLayout.LayoutParams botScrimParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 560);
        botScrimParams.gravity = Gravity.BOTTOM;
        bottomScrim.setLayoutParams(botScrimParams);
        root.addView(bottomScrim);

        // Loading Progress Bar
        progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pbParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(pbParams);
        root.addView(progressBar);

        // --- 1. SLEEK TOP-LEFT CIRCULAR BACK BUTTON (MATCHING SCREENSHOT) ---
        FrameLayout backBtn = new FrameLayout(this);
        backBtn.setClickable(true);
        backBtn.setFocusable(true);

        GradientDrawable backBg = new GradientDrawable();
        backBg.setShape(GradientDrawable.OVAL);
        backBg.setColor(Color.parseColor("#73150608"));
        backBg.setStroke(2, Color.parseColor("#4DFFFFFF"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#33FFFFFF")), backBg, null));
        } else {
            backBtn.setBackground(backBg);
        }

        ImageView backIcon = new ImageView(this);
        int arrowRes = getResources().getIdentifier("ic_arrow_back", "drawable", getPackageName());
        if (arrowRes != 0) {
            backIcon.setImageResource(arrowRes);
        }
        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(56, 56);
        iconLp.gravity = Gravity.CENTER;
        backIcon.setLayoutParams(iconLp);
        backBtn.addView(backIcon);

        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(114, 114);
        backParams.gravity = Gravity.TOP | Gravity.START;
        backParams.setMargins(48, 100, 0, 0);
        backBtn.setLayoutParams(backParams);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(backBtn);

        // --- 2. FLOATING BOTTOM CONTROLS (FROSTED DOCK + SET AS WALLPAPER PILL) ---
        LinearLayout bottomContainer = new LinearLayout(this);
        bottomContainer.setOrientation(LinearLayout.VERTICAL);
        bottomContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        FrameLayout.LayoutParams botContainerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        botContainerParams.gravity = Gravity.BOTTOM;
        botContainerParams.setMargins(44, 0, 44, 72);
        bottomContainer.setLayoutParams(botContainerParams);

        // --- FROSTED GLASS ACTION DOCK (DOWNLOAD, SHARE, WHATSAPP, INSTAGRAM) ---
        LinearLayout actionDock = new LinearLayout(this);
        actionDock.setOrientation(LinearLayout.HORIZONTAL);
        actionDock.setGravity(Gravity.CENTER_VERTICAL);
        actionDock.setPadding(16, 20, 16, 20);

        GradientDrawable dockBg = new GradientDrawable();
        dockBg.setColor(Color.parseColor("#80150709")); // Translucent frosted obsidian crimson
        dockBg.setCornerRadius(60);
        dockBg.setStroke(2, Color.parseColor("#40FFFFFF")); // Specular light rim
        actionDock.setBackground(dockBg);
        actionDock.setElevation(14);

        LinearLayout.LayoutParams dockParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionDock.setLayoutParams(dockParams);

        // 1. Download
        actionDock.addView(createDockItem("ic_download", "Download", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadWallpaper();
            }
        }));

        // 2. Share
        actionDock.addView(createDockItem("ic_share", "Share", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWallpaper(null);
            }
        }));

        // 3. WhatsApp
        actionDock.addView(createDockItem("ic_whatsapp", "WhatsApp", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWallpaper("com.whatsapp");
            }
        }));

        // 4. Instagram
        actionDock.addView(createDockItem("ic_instagram", "Instagram", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWallpaper("com.instagram.android");
            }
        }));

        bottomContainer.addView(actionDock);

        // --- "SET AS WALLPAPER" SOLID WHITE PILL BUTTON (MATCHING SCREENSHOT) ---
        LinearLayout setBtn = new LinearLayout(this);
        setBtn.setOrientation(LinearLayout.HORIZONTAL);
        setBtn.setGravity(Gravity.CENTER);
        setBtn.setClickable(true);
        setBtn.setFocusable(true);

        GradientDrawable setBtnBg = new GradientDrawable();
        setBtnBg.setColor(Color.WHITE);
        setBtnBg.setCornerRadius(60);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setColor(Color.WHITE);
            mask.setCornerRadius(60);
            setBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#33000000")), setBtnBg, mask));
        } else {
            setBtn.setBackground(setBtnBg);
        }
        setBtn.setElevation(16);

        LinearLayout.LayoutParams setBtnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 126);
        setBtnParams.topMargin = 22;
        setBtn.setLayoutParams(setBtnParams);

        ImageView setIcon = new ImageView(this);
        int wallRes = getResources().getIdentifier("ic_wallpaper", "drawable", getPackageName());
        if (wallRes != 0) {
            setIcon.setImageResource(wallRes);
        }
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(54, 54);
        iconParams.rightMargin = 18;
        setIcon.setLayoutParams(iconParams);
        setBtn.addView(setIcon);

        TextView setText = new TextView(this);
        setText.setText("Set as wallpaper");
        setText.setTextColor(Color.parseColor("#111111"));
        setText.setTextSize(15);
        setText.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        setBtn.addView(setText);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBitmap != null) {
                    showCustomLiquidApplyDialog();
                } else {
                    Toast.makeText(WallpaperViewerActivity.this, "Loading wallpaper...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomContainer.addView(setBtn);
        root.addView(bottomContainer);

        setContentView(root);

        // Load wallpaper in background
        loadWallpaper(fileName, fileUrl);
    }

    private View createDockItem(String iconName, String label, View.OnClickListener listener) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setClickable(true);
        item.setFocusable(true);
        item.setPadding(0, 10, 0, 10);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#33FFFFFF")), null, null));
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        item.setLayoutParams(lp);

        ImageView iv = new ImageView(this);
        int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (resId != 0) {
            iv.setImageResource(resId);
        }
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(58, 58);
        iv.setLayoutParams(ivParams);
        item.addView(iv);

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(11);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.topMargin = 8;
        tv.setLayoutParams(tvParams);
        item.addView(tv);

        item.setOnClickListener(listener);
        return item;
    }

    private void loadWallpaper(final String fileName, final String fileUrl) {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = null;
                try {
                    if (fileName != null && !fileName.isEmpty()) {
                        InputStream is = getAssets().open("wallpapers/" + fileName);
                        bmp = BitmapFactory.decodeStream(is);
                        is.close();
                    } else if (fileUrl != null && !fileUrl.isEmpty()) {
                        String targetUrl = fileUrl;
                        HttpURLConnection conn = null;
                        for (int redirect = 0; redirect < 5; redirect++) {
                            URL u = new URL(targetUrl);
                            conn = (HttpURLConnection) u.openConnection();
                            conn.setConnectTimeout(15000);
                            conn.setReadTimeout(15000);
                            conn.setInstanceFollowRedirects(true);
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)");
                            int status = conn.getResponseCode();
                            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == 307 || status == 308) {
                                String newUrl = conn.getHeaderField("Location");
                                if (newUrl != null && !newUrl.isEmpty()) {
                                    targetUrl = newUrl;
                                    conn.disconnect();
                                    continue;
                                }
                            }
                            break;
                        }
                        if (conn != null && conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            bmp = BitmapFactory.decodeStream(is);
                            is.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final Bitmap loadedBmp = bmp;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (loadedBmp != null) {
                            currentBitmap = loadedBmp;
                            imageView.setImageBitmap(currentBitmap);
                        } else {
                            Toast.makeText(WallpaperViewerActivity.this, "Failed to load wallpaper", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    // --- DOWNLOAD WALLPAPER TO GALLERY ---
    private void downloadWallpaper() {
        if (currentBitmap == null) {
            Toast.makeText(this, "Wallpaper is still downloading...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "\u2935 Downloading wallpaper...", Toast.LENGTH_SHORT).show();
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    String cleanName = (wallpaperName != null ? wallpaperName.replaceAll("[^a-zA-Z0-9_-]", "_") : "wallpaper");
                    String fileName = "Ravenshade_" + cleanName + "_" + System.currentTimeMillis() + ".jpg";
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ravenshade");
                        values.put(MediaStore.Images.Media.IS_PENDING, 1);
                    }
                    final Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream os = getContentResolver().openOutputStream(uri);
                        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            getContentResolver().update(uri, values, null, null);
                        }
                        savedImageUri = uri;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WallpaperViewerActivity.this, "\u2728 Saved to Pictures/Ravenshade", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WallpaperViewerActivity.this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // --- SHARE WALLPAPER (SYSTEM, WHATSAPP, INSTAGRAM) ---
    private void shareWallpaper(final String targetPackage) {
        if (currentBitmap == null) {
            Toast.makeText(this, "Wallpaper is still downloading...", Toast.LENGTH_SHORT).show();
            return;
        }
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                Uri shareUri = savedImageUri;
                if (shareUri == null) {
                    try {
                        ContentValues values = new ContentValues();
                        String fileName = "share_" + System.currentTimeMillis() + ".jpg";
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ravenshade");
                        }
                        shareUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        if (shareUri != null) {
                            OutputStream os = getContentResolver().openOutputStream(shareUri);
                            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 95, os);
                            os.flush();
                            os.close();
                            savedImageUri = shareUri;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                final Uri finalUri = shareUri;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalUri == null) {
                            Toast.makeText(WallpaperViewerActivity.this, "Unable to prepare image for sharing", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_STREAM, finalUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (targetPackage != null) {
                            intent.setPackage(targetPackage);
                            try {
                                startActivity(intent);
                                return;
                            } catch (ActivityNotFoundException e) {
                                String appName = targetPackage.contains("whatsapp") ? "WhatsApp" : "Instagram";
                                Toast.makeText(WallpaperViewerActivity.this, appName + " is not installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        startActivity(Intent.createChooser(intent, "Share Wallpaper"));
                    }
                });
            }
        });
    }

    // Custom Liquid Glass Apply Modal Dialog in Red & Black
    private void showCustomLiquidApplyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout dialogBox = new LinearLayout(this);
        dialogBox.setOrientation(LinearLayout.VERTICAL);
        dialogBox.setPadding(52, 48, 52, 48);
        dialogBox.setBackground(createLiquidGlass(
                Color.parseColor("#F2150608"),
                Color.parseColor("#60FF3322"),
                40, 3));

        TextView title = new TextView(this);
        title.setText("\u2726 SET WALLPAPER");
        title.setTextColor(Color.parseColor("#FF5449"));
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.12f);
        title.setGravity(Gravity.CENTER);
        dialogBox.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(wallpaperName);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(16);
        subtitle.setTypeface(Typeface.DEFAULT_BOLD);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 10, 0, 36);
        dialogBox.addView(subtitle);

        // 3 Glass Option Buttons
        dialogBox.addView(createDialogOptionButton("\u2726  HOME SCREEN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                applyWallpaper(WallpaperManager.FLAG_SYSTEM);
            }
        }));

        dialogBox.addView(createDialogOptionButton("\u2726  LOCK SCREEN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                applyWallpaper(WallpaperManager.FLAG_LOCK);
            }
        }));

        dialogBox.addView(createDialogOptionButton("\u2726  BOTH (HOME & LOCK)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                applyWallpaper(WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
            }
        }));

        // Cancel Button
        Button cancelBtn = new Button(this);
        cancelBtn.setText("CANCEL");
        cancelBtn.setTextColor(Color.parseColor("#999999"));
        cancelBtn.setTextSize(12);
        cancelBtn.setBackground(null);
        cancelBtn.setPadding(0, 20, 0, 0);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBox.addView(cancelBtn);

        dialog.setContentView(dialogBox);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.88),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
    }

    private View createDialogOptionButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.08f);

        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setColor(Color.parseColor("#26FF3322"));
        pillBg.setCornerRadius(28);
        pillBg.setStroke(2, Color.parseColor("#55FF3322"));
        btn.setBackground(pillBg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 100);
        lp.bottomMargin = 20;
        btn.setLayoutParams(lp);
        btn.setOnClickListener(listener);
        return btn;
    }

    private void applyWallpaper(final int flags) {
        Toast.makeText(this, "\u2726 Setting liquid wallpaper...", Toast.LENGTH_SHORT).show();
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WallpaperManager wm = WallpaperManager.getInstance(WallpaperViewerActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wm.setBitmap(currentBitmap, null, true, flags);
                    } else {
                        wm.setBitmap(currentBitmap);
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WallpaperViewerActivity.this, "\u2728 Wallpaper applied successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WallpaperViewerActivity.this, "Error applying: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
