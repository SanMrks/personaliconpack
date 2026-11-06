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

    public static boolean isLightWallpaper(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                WallpaperManager wm = WallpaperManager.getInstance(context);
                WallpaperColors colors = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
                if (colors != null) {
                    if ((colors.getColorHints() & WallpaperColors.HINT_SUPPORTS_DARK_TEXT) != 0) {
                        return true;
                    }
                    if (colors.getPrimaryColor() != null) {
                        int c = colors.getPrimaryColor().toArgb();
                        double lum = (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c)) / 255.0;
                        return lum > 0.55;
                    }
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }

    /**
     * Procedurally renders an authentic Frosted Liquid Glass surface Bitmap for RemoteViews widgets.
     * Incorporates:
     * 1. Luminous ultra-clear liquid glass body (crystal-clear optical transmission, wallpaper 100% visible)
     * 2. Curved liquid meniscus highlight (fluid light glaze across the top surface)
     * 3. Wallpaper-adaptive caustic refraction glow along lower curves
     * 4. Inner specular lip highlight for tangible 3D glass slab thickness
     * 5. Polished outer specular bevel rim (1.35dp) with dual-angle light reflection
     */
    public static Bitmap createLiquidGlassBitmap(Context context, int widthPx, int heightPx, float cornerRadiusDp) {
        if (widthPx <= 0) widthPx = 540;
        if (heightPx <= 0) heightPx = 270;

        float density = context.getResources().getDisplayMetrics().density;
        // Rectangular curve squircle (18dp)
        float radiusPx = 18f * density;

        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float strokeWidth = 1.35f * density;
        float halfStroke = strokeWidth / 2.0f;
        RectF rect = new RectF(halfStroke, halfStroke, widthPx - halfStroke, heightPx - halfStroke);
        Path clipPath = new Path();
        clipPath.addRoundRect(rect, radiusPx, radiusPx, Path.Direction.CW);

        // 1. Luminous Liquid Glass Sheen (Adapts contrast to light/dark wallpaper)
        boolean isLightWp = isLightWallpaper(context);
        int topSheen = isLightWp ? 0x55101018 : 0x22FFFFFF;
        int centerSheen = isLightWp ? 0x3E0A0A10 : 0x03FFFFFF;
        int bottomSheen = isLightWp ? 0x4812121A : 0x10FFFFFF;

        Paint glassSheenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glassSheenPaint.setShader(new LinearGradient(
                0, 0, widthPx * 0.7f, heightPx,
                new int[]{ topSheen, centerSheen, bottomSheen },
                new float[]{0.0f, 0.45f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, glassSheenPaint);

        // 2. 3D Water Droplet Surface Tension Shelf (The Liquid Meniscus Refraction Boundary)
        float shelfInset = 1.3f * density;
        RectF shelfRect = new RectF(shelfInset, shelfInset, widthPx - shelfInset, heightPx - shelfInset);
        Path shelfPath = new Path();
        shelfPath.addRoundRect(shelfRect, Math.max(0, radiusPx - shelfInset), Math.max(0, radiusPx - shelfInset), Path.Direction.CW);

        Paint shelfShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shelfShadowPaint.setStyle(Paint.Style.STROKE);
        shelfShadowPaint.setStrokeWidth(2.6f * density);
        shelfShadowPaint.setShader(new LinearGradient(
                0, 0, 0, heightPx,
                new int[]{ 0x22000000, 0x55000000 },
                new float[]{ 0.0f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(shelfPath, shelfShadowPaint);

        // Inner luminous refraction contour line
        float innerRimInset = 3.2f * density;
        RectF innerRimRect = new RectF(innerRimInset, innerRimInset, widthPx - innerRimInset, heightPx - innerRimInset);
        Path innerRimPath = new Path();
        innerRimPath.addRoundRect(innerRimRect, Math.max(0, radiusPx - innerRimInset), Math.max(0, radiusPx - innerRimInset), Path.Direction.CW);

        Paint innerRimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerRimPaint.setStyle(Paint.Style.STROKE);
        innerRimPaint.setStrokeWidth(1.1f * density);
        innerRimPaint.setShader(new LinearGradient(
                0, 0, widthPx * 0.6f, heightPx,
                new int[]{ 0x55FFFFFF, 0x12FFFFFF, 0x00FFFFFF },
                new float[]{ 0.0f, 0.45f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(innerRimPath, innerRimPaint);

        // 3. Convex Liquid Dome Highlight (Exponential curved gloss reflecting on water droplet surface)
        canvas.save();
        canvas.clipPath(clipPath);
        float domeH = heightPx * 0.44f;
        Paint domePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        domePaint.setShader(new LinearGradient(
                0, 0, 0, domeH,
                new int[]{
                        0x80FFFFFF, // Brilliant glossy liquid surface reflection
                        0x20FFFFFF, // Mid fluid sheen
                        0x00FFFFFF  // Smooth meniscus dissipation
                },
                new float[]{ 0.0f, 0.38f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, widthPx, domeH, domePaint);
        canvas.restore();

        // 4. Primary Specular Fluid Glaze (Floating elongated gloss reflection)
        canvas.save();
        canvas.clipPath(clipPath);
        float streakLeft = radiusPx * 0.85f;
        float streakTop = 3.2f * density;
        float streakRight = widthPx * 0.60f;
        float streakHeight = Math.max(10f * density, heightPx * 0.16f);
        RectF streakRect = new RectF(streakLeft, streakTop, streakRight, streakTop + streakHeight);
        Paint streakPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        streakPaint.setShader(new LinearGradient(
                streakLeft, streakTop, streakRight, streakTop + streakHeight,
                new int[]{ 0x85FFFFFF, 0x25FFFFFF, 0x00FFFFFF },
                new float[]{ 0.0f, 0.42f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawRoundRect(streakRect, streakHeight * 0.5f, streakHeight * 0.5f, streakPaint);
        canvas.restore();

        // 4b. Secondary Droplet Glint Bead (Tiny optical water droplet cue)
        canvas.save();
        canvas.clipPath(clipPath);
        float beadX = radiusPx * 0.70f;
        float beadY = radiusPx * 0.70f;
        float beadR = 4.0f * density;
        Paint beadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        beadPaint.setShader(new RadialGradient(
                beadX, beadY, beadR,
                new int[]{ 0xB0FFFFFF, 0x30FFFFFF, 0x00FFFFFF },
                new float[]{ 0.0f, 0.50f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(beadX, beadY, beadR, beadPaint);
        canvas.restore();

        // 5. Bottom Water Caustic Focus (With Wallpaper-Adaptive Prismatic Refraction)
        canvas.save();
        canvas.clipPath(clipPath);
        int wpAccent = isAdaptiveEnabled(context) ? getWallpaperAccentColor(context) : DEFAULT_CRIMSON;
        int r = Color.red(wpAccent);
        int g = Color.green(wpAccent);
        int b = Color.blue(wpAccent);
        int causticFluid = Color.argb(0x3C, r, g, b);
        int causticCrimson = Color.argb(0x28, 0xDC, 0x14, 0x3C);

        float causticTop = heightPx * 0.68f;
        Paint causticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        causticPaint.setShader(new LinearGradient(
                widthPx * 0.2f, causticTop, widthPx * 0.8f, heightPx,
                new int[]{ 0x00000000, causticFluid, 0x48FFFFFF, causticCrimson },
                new float[]{ 0.0f, 0.40f, 0.78f, 1.0f },
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, causticTop, widthPx, heightPx, causticPaint);
        canvas.restore();

        // 6. Polished Specular Outer Bevel Rim (1.35dp)
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setShader(new LinearGradient(
                0, 0, widthPx, heightPx,
                new int[]{
                        0xEBFFFFFF, // Crisp luminous white specular highlight at top-left
                        0x48FFFFFF, // Fluid side transmission
                        causticFluid // Caustic reflection bounce along bottom-right edge
                },
                new float[]{0.0f, 0.46f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(clipPath, strokePaint);

        return bitmap;
    }

    /**
     * Completely transparent rectangular curved squircle glass for 1x1 widgets
     */
    public static Bitmap createLiquidGlassCircle(Context context, int sizePx) {
        if (sizePx <= 0) sizePx = 240;
        return createLiquidGlassBitmap(context, sizePx, sizePx, 18f);
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
                        0x32FFFFFF, // Luminous specular light dispersion
                        0x1007070C, // Translucent obsidian core
                        causticColor // Translucent caustic depth
                }
        );
        baseBody.setCornerRadius(radiusPx);
        baseBody.setStroke((int) (1.4f * density), 0x7EFFFFFF);

        // Layer 1: Top Glaze Meniscus (Curved liquid glass reflection)
        GradientDrawable topGlaze = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x45FFFFFF, 0x10FFFFFF, 0x00FFFFFF}
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
