package com.ravenshade.iconpack;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

import java.io.InputStream;

/**
 * RavenGlassThemeManager
 * Procedurally generates high-fidelity Frosted Liquid Glass surfaces,
 * dynamically adapting optical refraction and caustic gradients to match the active wallpaper.
 */
public class RavenGlassThemeManager {

    private static final String PREF_NAME = "raven_glass_theme";
    private static final String KEY_ADAPTIVE_ENABLED = "adaptive_glass_enabled";

    // Signature Raven Crimson accents
    public static final int DEFAULT_CRIMSON = 0xFFDC143C;
    public static final int DEFAULT_DEEP_CRIMSON = 0xFF8B0000;

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
     * Uses WallpaperColors on API 27+, falls back to signature crimson.
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

        // Boost saturation and brightness so glass caustics glow subtly
        float[] hsv = new float[3];
        Color.colorToHSV(accent, hsv);
        if (hsv[1] < 0.28f) hsv[1] = 0.50f;
        if (hsv[2] < 0.38f) hsv[2] = 0.70f;
        return Color.HSVToColor(hsv);
    }

    /**
     * Procedurally renders a high-fidelity Frosted Liquid Glass surface Bitmap for RemoteViews widgets.
     * Incorporates:
     * 1. Ultra-translucent obsidian glass body (wallpaper clearly visible through widget)
     * 2. Specular light refraction top-left to center
     * 3. Wallpaper-adaptive caustic glow at curves blended with Raven crimson
     * 4. Curved horizon specular glaze highlight (liquid glass meniscus)
     * 5. Polished specular outer bevel rim (1.4dp)
     */
    public static Bitmap createLiquidGlassBitmap(Context context, int widthPx, int heightPx, float cornerRadiusDp) {
        if (widthPx <= 0) widthPx = 750;
        if (heightPx <= 0) heightPx = 360;

        float density = context.getResources().getDisplayMetrics().density;
        float radiusPx = cornerRadiusDp * density;

        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        RectF rect = new RectF(1.5f, 1.5f, widthPx - 1.5f, heightPx - 1.5f);
        Path clipPath = new Path();
        clipPath.addRoundRect(rect, radiusPx, radiusPx, Path.Direction.CW);

        // 1. Base Frosted Liquid Obsidian Glass Body (Translucent ~86% optical clarity)
        Paint baseGlassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseGlassPaint.setShader(new LinearGradient(
                0, 0, widthPx, heightPx,
                new int[]{
                        0x2EFFFFFF, // Top-left specular sheen
                        0x1406060B, // Translucent obsidian glass core
                        0x24160408  // Subtle crimson-obsidian depth
                },
                new float[]{0.0f, 0.42f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, baseGlassPaint);

        // 2. Wallpaper-Adaptive Caustic Refraction along Bottom & Edges
        int wpAccent = isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int causticStart = Color.argb(0x48, Color.red(wpAccent), Color.green(wpAccent), Color.blue(wpAccent));
        int causticCrimson = Color.argb(0x40, 0xDC, 0x14, 0x3C);

        Paint causticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        causticPaint.setShader(new LinearGradient(
                widthPx * 0.15f, heightPx * 0.10f, widthPx, heightPx,
                new int[]{
                        0x00000000,
                        0x0A000000,
                        causticStart,
                        causticCrimson
                },
                new float[]{0.0f, 0.48f, 0.80f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, causticPaint);

        // 3. Curved Horizon Specular Glaze (Liquid Glass Meniscus)
        Paint horizonGlazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        horizonGlazePaint.setShader(new LinearGradient(
                0, 0, widthPx * 0.7f, heightPx * 0.45f,
                new int[]{0x4AFFFFFF, 0x18FFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.35f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, horizonGlazePaint);

        // 4. Polished Specular Glass Bevel Rim (1.4dp Stroke)
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.4f * density);
        strokePaint.setShader(new LinearGradient(
                0, 0, widthPx, heightPx,
                new int[]{
                        0x80FFFFFF, // Crisp polished specular reflection at top-left
                        0x2AFFFFFF, // Subtle side transmission
                        causticStart // Caustic refraction bounce along bottom-right
                },
                new float[]{0.0f, 0.52f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, strokePaint);

        return bitmap;
    }

    /**
     * Circular Frosted Liquid Glass badge for 1x1 Pop widget
     */
    public static Bitmap createLiquidGlassCircle(Context context, int sizePx) {
        if (sizePx <= 0) sizePx = 280;
        float density = context.getResources().getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float center = sizePx / 2.0f;
        float radius = center - 2.0f;

        // Base frosted glass
        Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setShader(new RadialGradient(
                center, center, radius,
                new int[]{0x2CFFFFFF, 0x14050509, 0x24120408},
                new float[]{0.0f, 0.65f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, basePaint);

        // Wallpaper caustic reflection
        int wpAccent = isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int causticColor = Color.argb(0x50, Color.red(wpAccent), Color.green(wpAccent), Color.blue(wpAccent));

        Paint causticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        causticPaint.setShader(new LinearGradient(
                0, 0, sizePx, sizePx,
                new int[]{0x00000000, 0x1EDC143C, causticColor},
                new float[]{0.0f, 0.55f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, causticPaint);

        // Top specular glaze
        Paint glazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glazePaint.setShader(new LinearGradient(
                0, 0, sizePx * 0.6f, sizePx * 0.4f,
                new int[]{0x4DFFFFFF, 0x14FFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.40f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, glazePaint);

        // Specular stroke
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.5f * density);
        strokePaint.setShader(new LinearGradient(
                0, 0, sizePx, sizePx,
                new int[]{0x85FFFFFF, 0x2AFFFFFF, causticColor},
                new float[]{0.0f, 0.55f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(center, center, radius, strokePaint);

        return bitmap;
    }

    /**
     * Creates an authentic Frosted Liquid Glass Drawable for in-app cards and containers.
     * Uses a multi-layer composition:
     * - Layer 0: Base translucent obsidian glass gradient with specular rim
     * - Layer 1: Inset top specular meniscus glaze
     */
    public static Drawable createFrostedGlassDrawable(Context context, float cornerRadiusDp, boolean withCaustic) {
        float density = context.getResources().getDisplayMetrics().density;
        float radiusPx = cornerRadiusDp * density;

        int wpAccent = withCaustic && isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int causticColor = Color.argb(0x28, Color.red(wpAccent), Color.green(wpAccent), Color.blue(wpAccent));

        // Layer 0: Base Obsidian Translucent Body with Specular Rim
        GradientDrawable baseBody = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        0x26FFFFFF, // Specular light dispersion
                        0x1407070C, // Deep translucent obsidian core
                        causticColor // Translucent caustic depth
                }
        );
        baseBody.setCornerRadius(radiusPx);
        baseBody.setStroke((int) (1.4f * density), 0x5EFFFFFF);

        // Layer 1: Top Glaze Meniscus (Curved liquid glass reflection)
        GradientDrawable topGlaze = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x30FFFFFF, 0x08FFFFFF, 0x00FFFFFF}
        );
        topGlaze.setCornerRadius(radiusPx);

        LayerDrawable layers = new LayerDrawable(new Drawable[]{baseBody, topGlaze});
        // Inset the top glaze so it acts as an optical top highlight
        layers.setLayerInset(1, (int)(2 * density), (int)(2 * density), (int)(2 * density), (int)(32 * density));

        return layers;
    }

    /**
     * Frosted Glass Pill for chips and small action controls
     */
    public static GradientDrawable createFrostedPillDrawable(Context context, float radiusDp) {
        float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable pill = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0x2AFFFFFF, 0x1608080E}
        );
        pill.setCornerRadius(radiusDp * density);
        pill.setStroke((int)(1.2f * density), 0x60FFFFFF);
        return pill;
    }

    /**
     * Frosted Crimson Liquid Glass Drawable for Primary Buttons
     */
    public static GradientDrawable createFrostedCrimsonDrawable(Context context, float radiusDp) {
        float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable crimson = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        0xE6DC143C, // Radiant Crimson Top
                        0xCC8B0000  // Deep Crimson Base
                }
        );
        crimson.setCornerRadius(radiusDp * density);
        crimson.setStroke((int)(1.5f * density), 0x99FFA3B2);
        return crimson;
    }

    /**
     * Loads the active wallpaper bitmap downscaled and heavily blurred with dark obsidian frosting
     * for the in-app ambient background.
     */
    public static Bitmap getFrostedWallpaperBackdrop(Context context, int targetWidth, int targetHeight) {
        try {
            Bitmap src = null;
            WallpaperManager wm = WallpaperManager.getInstance(context);
            Drawable d = wm.getDrawable();
            if (d != null) {
                int w = Math.min(240, d.getIntrinsicWidth() > 0 ? d.getIntrinsicWidth() : 240);
                int h = Math.min(480, d.getIntrinsicHeight() > 0 ? d.getIntrinsicHeight() : 480);
                src = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(src);
                d.setBounds(0, 0, w, h);
                d.draw(c);
            }

            // Fallback to local wallpaper asset if system wallpaper was null
            if (src == null) {
                AssetManager am = context.getAssets();
                InputStream is = am.open("wallpapers/wallpaper_01.jpg");
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 8;
                src = BitmapFactory.decodeStream(is, null, opts);
                is.close();
            }

            if (src != null) {
                Bitmap blurred = stackBlur(src, 26);
                return blurred;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * High-performance StackBlur algorithm for ultra-smooth frosted glass diffusion.
     */
    public static Bitmap stackBlur(Bitmap sentBitmap, int radius) {
        if (radius < 1) return sentBitmap;
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }

        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve full opacity for backdrop
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
