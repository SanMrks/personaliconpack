package com.ravenshade.iconpack;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;

/**
 * RavenGlassThemeManager
 * Procedurally generates Apple iOS 26 style Liquid Glass surfaces
 * dynamically adapting optical refraction and caustic gradients to match the active wallpaper.
 */
public class RavenGlassThemeManager {

    private static final String PREF_NAME = "raven_glass_theme";
    private static final String KEY_ADAPTIVE_ENABLED = "adaptive_glass_enabled";

    // Fallback signature Raven crimson accent
    private static final int DEFAULT_CRIMSON = 0xFFDC143C;
    private static final int DEFAULT_DEEP_CRIMSON = 0xFF8B0000;

    public static boolean isAdaptiveEnabled(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ADAPTIVE_ENABLED, true);
    }

    public static void setAdaptiveEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ADAPTIVE_ENABLED, enabled).apply();
    }

    /**
     * Extracts active wallpaper accent color.
     * Uses WallpaperColors on API 27+, falls back to cached wallpaper analysis or signature crimson.
     */
    public static int getWallpaperAccentColor(Context context) {
        int accent = DEFAULT_CRIMSON;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                WallpaperManager wm = WallpaperManager.getInstance(context);
                WallpaperColors colors = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
                if (colors != null) {
                    if (colors.getSecondaryColor() != null) {
                        accent = colors.getSecondaryColor().toArgb();
                    } else if (colors.getPrimaryColor() != null) {
                        accent = colors.getPrimaryColor().toArgb();
                    }
                }
            } catch (Throwable ignored) {}
        }

        // Ensure accent has decent saturation and brightness for glass refraction
        float[] hsv = new float[3];
        Color.colorToHSV(accent, hsv);
        if (hsv[1] < 0.25f) hsv[1] = 0.45f; // boost saturation slightly if too washed out
        if (hsv[2] < 0.35f) hsv[2] = 0.65f; // boost brightness so glass caustics glow subtly
        return Color.HSVToColor(hsv);
    }

    /**
     * Procedurally renders a high-fidelity Liquid Glass surface Bitmap.
     * Incorporates:
     * 1. Ultra-transparent obsidian glass body (wallpaper clearly visible)
     * 2. Specular light refraction top-left to center
     * 3. Wallpaper-adaptive caustic glow at curves blended with Raven crimson
     * 4. Delicate 1.2dp specular outer bevel rim
     */
    public static Bitmap createLiquidGlassBitmap(Context context, int widthPx, int heightPx, float cornerRadiusDp) {
        if (widthPx <= 0) widthPx = 500;
        if (heightPx <= 0) heightPx = 250;

        float density = context.getResources().getDisplayMetrics().density;
        float radiusPx = cornerRadiusDp * density;

        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        RectF rect = new RectF(1.5f, 1.5f, widthPx - 1.5f, heightPx - 1.5f);
        Path clipPath = new Path();
        clipPath.addRoundRect(rect, radiusPx, radiusPx, Path.Direction.CW);

        // 1. Base Liquid Obsidian Glass Tint (88% transparent)
        Paint baseGlassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseGlassPaint.setShader(new LinearGradient(
                0, 0, widthPx, heightPx,
                new int[]{
                        0x24FFFFFF, // Very subtle top sheen
                        0x1208080C, // Deep obsidian transparent body
                        0x1E050508  // Bottom subtle depth
                },
                new float[]{0.0f, 0.45f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, baseGlassPaint);

        // 2. Wallpaper-Adaptive Caustic Refraction along Bottom & Edges
        int wpAccent = isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int causticStart = Color.argb(0x40, Color.red(wpAccent), Color.green(wpAccent), Color.blue(wpAccent));
        int causticCrimson = Color.argb(0x35, 0xDC, 0x14, 0x3C); // Signature Raven blend

        Paint causticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        causticPaint.setShader(new LinearGradient(
                widthPx * 0.2f, heightPx * 0.1f, widthPx, heightPx,
                new int[]{
                        0x00000000,
                        0x08000000,
                        causticStart,
                        causticCrimson
                },
                new float[]{0.0f, 0.50f, 0.82f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, causticPaint);

        // 3. Top Specular Glaze Highlight (iOS 26 Liquid Glass Edge)
        Paint specularPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        specularPaint.setShader(new LinearGradient(
                0, 0, widthPx * 0.6f, heightPx * 0.4f,
                new int[]{0x4DFFFFFF, 0x1AFFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.35f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, specularPaint);

        // 4. Polished Glass Bevel Rim (1.2dp Stroke)
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.2f * density);
        strokePaint.setShader(new LinearGradient(
                0, 0, widthPx, heightPx,
                new int[]{
                        0x66FFFFFF, // Crisp specular reflection at top-left
                        0x24FFFFFF, // Subtle side transmission
                        causticStart // Refraction bounce at bottom-right
                },
                new float[]{0.0f, 0.55f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, strokePaint);

        return bitmap;
    }

    /**
     * Circular Liquid Glass badge for 1x1 Pop widget
     */
    public static Bitmap createLiquidGlassCircle(Context context, int sizePx) {
        if (sizePx <= 0) sizePx = 250;
        float density = context.getResources().getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float center = sizePx / 2.0f;
        float radius = center - 2.0f;

        // Base glass
        Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setShader(new RadialGradient(
                center, center, radius,
                new int[]{0x22FFFFFF, 0x14050508, 0x220A0507},
                new float[]{0.0f, 0.70f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, basePaint);

        // Wallpaper caustic reflection
        int wpAccent = isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int causticColor = Color.argb(0x48, Color.red(wpAccent), Color.green(wpAccent), Color.blue(wpAccent));

        Paint causticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        causticPaint.setShader(new LinearGradient(
                0, 0, sizePx, sizePx,
                new int[]{0x00000000, 0x15DC143C, causticColor},
                new float[]{0.0f, 0.60f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, causticPaint);

        // Specular stroke
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.4f * density);
        strokePaint.setShader(new LinearGradient(
                0, 0, sizePx, sizePx,
                new int[]{0x70FFFFFF, 0x26FFFFFF, causticColor},
                new float[]{0.0f, 0.60f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, strokePaint);

        return bitmap;
    }
}
