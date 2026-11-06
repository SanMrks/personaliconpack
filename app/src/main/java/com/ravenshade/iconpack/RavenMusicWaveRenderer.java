package com.ravenshade.iconpack;

import android.content.Context;
import android.graphics.*;

/**
 * RavenMusicWaveRenderer
 * Procedurally renders 5 distinct audio visualization waveforms and line styles:
 * 0 - Spectrum Equalizer Bars
 * 1 - Smooth Harmonic Sine Wave
 * 2 - Cyber Pulse / Neon ECG Line
 * 3 - Digital Matrix Equalizer Dots
 * 4 - Minimalist Scrubber Line
 */
public class RavenMusicWaveRenderer {

    public static final int STYLE_SPECTRUM_BARS = 0;
    public static final int STYLE_SMOOTH_SINE = 1;
    public static final int STYLE_CYBER_PULSE = 2;
    public static final int STYLE_DIGITAL_DOTS = 3;
    public static final int STYLE_MINIMAL_LINE = 4;

    public static final String[] STYLE_NAMES = {
            "📊 Spectrum Bars",
            "〰️ Fluid Sine",
            "⚡ Cyber Pulse",
            "•• Digital Dots",
            "━ Minimal Line"
    };

    public static final String[] STYLE_DESCRIPTIONS = {
            "Rhythmic frequency equalizer soundwave bars",
            "Smooth continuous harmonic acoustic wave",
            "Futuristic neon cyber / ECG heartbeat line",
            "Digital matrix illuminated LED dots",
            "Clean modern scrubber progress line"
    };

    public static String getStyleName(int style) {
        if (style >= 0 && style < STYLE_NAMES.length) {
            return STYLE_NAMES[style];
        }
        return STYLE_NAMES[0];
    }

    public static Bitmap renderWaveBitmap(Context context, int style, int widthPx, int heightPx, int accentColor) {
        if (widthPx <= 0) widthPx = 280;
        if (heightPx <= 0) heightPx = 36;
        if (accentColor == 0) accentColor = 0xFFDC143C;

        float density = context != null ? context.getResources().getDisplayMetrics().density : 2.5f;

        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        switch (style) {
            case STYLE_SMOOTH_SINE:
                drawSmoothSine(canvas, widthPx, heightPx, accentColor, density);
                break;
            case STYLE_CYBER_PULSE:
                drawCyberPulse(canvas, widthPx, heightPx, accentColor, density);
                break;
            case STYLE_DIGITAL_DOTS:
                drawDigitalDots(canvas, widthPx, heightPx, accentColor, density);
                break;
            case STYLE_MINIMAL_LINE:
                drawMinimalLine(canvas, widthPx, heightPx, accentColor, density);
                break;
            case STYLE_SPECTRUM_BARS:
            default:
                drawSpectrumBars(canvas, widthPx, heightPx, accentColor, density);
                break;
        }

        return bitmap;
    }

    /**
     * 1. SPECTRUM EQUALIZER BARS
     */
    private static void drawSpectrumBars(Canvas canvas, int widthPx, int heightPx, int color, float density) {
        float centerY = heightPx / 2.0f;
        int barCount = 26;
        float barWidth = 3.2f * density;
        float spacing = (widthPx - (barCount * barWidth)) / (barCount + 1);

        // Pre-defined rhythmic normalized height factors (0.15 to 0.95)
        float[] rhythm = {
                0.25f, 0.45f, 0.70f, 0.35f, 0.85f, 0.55f, 0.95f, 0.40f,
                0.65f, 0.90f, 0.30f, 0.80f, 0.50f, 1.00f, 0.60f, 0.85f,
                0.40f, 0.70f, 0.95f, 0.45f, 0.75f, 0.35f, 0.60f, 0.40f,
                0.25f, 0.15f
        };

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(barWidth);

        float maxHalfHeight = (heightPx * 0.46f);

        for (int i = 0; i < barCount; i++) {
            float x = spacing + i * (barWidth + spacing) + (barWidth / 2.0f);
            float hFactor = rhythm[i % rhythm.length];
            float halfH = Math.max(3.0f * density, maxHalfHeight * hFactor);

            // Gradient: luminous accent color to lighter core
            LinearGradient shader = new LinearGradient(
                    x, centerY - halfH, x, centerY + halfH,
                    new int[]{
                            Color.argb(0x80, Color.red(color), Color.green(color), Color.blue(color)),
                            color,
                            Color.argb(0xCC, Color.red(color), Color.green(color), Color.blue(color))
                    },
                    new float[]{ 0.0f, 0.5f, 1.0f },
                    Shader.TileMode.CLAMP
            );
            paint.setShader(shader);
            canvas.drawLine(x, centerY - halfH, x, centerY + halfH, paint);
        }
    }

    /**
     * 2. SMOOTH HARMONIC SINE WAVE
     */
    private static void drawSmoothSine(Canvas canvas, int widthPx, int heightPx, int color, float density) {
        float centerY = heightPx / 2.0f;
        float maxAmplitude = heightPx * 0.40f;

        // Background subtle ghost harmonic wave
        Paint ghostPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ghostPaint.setStyle(Paint.Style.STROKE);
        ghostPaint.setStrokeCap(Paint.Cap.ROUND);
        ghostPaint.setStrokeWidth(1.4f * density);
        ghostPaint.setColor(Color.argb(0x45, Color.red(color), Color.green(color), Color.blue(color)));

        Path ghostPath = new Path();
        ghostPath.moveTo(0, centerY);
        for (int x = 0; x <= widthPx; x += 3) {
            float normX = (float) x / widthPx;
            // Phase-shifted secondary frequency
            float y = centerY + (float) Math.sin(normX * Math.PI * 5.0 + 1.2) * (maxAmplitude * 0.65f);
            ghostPath.lineTo(x, y);
        }
        canvas.drawPath(ghostPath, ghostPaint);

        // Foreground primary glowing harmonic sine wave
        Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeCap(Paint.Cap.ROUND);
        wavePaint.setStrokeWidth(2.6f * density);
        wavePaint.setColor(color);

        Path wavePath = new Path();
        wavePath.moveTo(0, centerY);
        for (int x = 0; x <= widthPx; x += 2) {
            float normX = (float) x / widthPx;
            // Envelope damping at edges for clean fluid look
            float envelope = (float) Math.sin(normX * Math.PI);
            float y = centerY + (float) Math.sin(normX * Math.PI * 4.0) * (maxAmplitude * envelope);
            wavePath.lineTo(x, y);
        }
        canvas.drawPath(wavePath, wavePaint);
    }

    /**
     * 3. CYBER PULSE / NEON ECG LINE
     */
    private static void drawCyberPulse(Canvas canvas, int widthPx, int heightPx, int color, float density) {
        float centerY = heightPx / 2.0f;

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeWidth(2.2f * density);
        linePaint.setColor(color);

        Path path = new Path();
        path.moveTo(0, centerY);

        // Pulse points definitions (normalized x, normalized y offset from -1 to 1)
        float[][] points = {
                { 0.00f, 0.00f },
                { 0.15f, 0.00f },
                { 0.19f, -0.25f },
                { 0.23f, 0.15f },
                { 0.27f, 0.00f },
                { 0.35f, 0.00f },
                // Big Pulse 1
                { 0.39f, -0.85f },
                { 0.44f, 0.75f },
                { 0.48f, -0.30f },
                { 0.52f, 0.00f },
                { 0.62f, 0.00f },
                // Medium Pulse 2
                { 0.66f, -0.65f },
                { 0.71f, 0.50f },
                { 0.75f, -0.15f },
                { 0.79f, 0.00f },
                { 0.88f, 0.00f },
                { 0.91f, -0.25f },
                { 0.94f, 0.15f },
                { 1.00f, 0.00f }
        };

        float maxAmp = heightPx * 0.44f;
        for (float[] pt : points) {
            float x = pt[0] * widthPx;
            float y = centerY + (pt[1] * maxAmp);
            path.lineTo(x, y);
        }

        canvas.drawPath(path, linePaint);

        // Glowing nodes at the two main peaks
        Paint nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setColor(Color.WHITE);

        float peak1X = 0.39f * widthPx;
        float peak1Y = centerY - (0.85f * maxAmp);
        canvas.drawCircle(peak1X, peak1Y, 3.2f * density, linePaint);
        canvas.drawCircle(peak1X, peak1Y, 2.0f * density, nodePaint);

        float peak2X = 0.66f * widthPx;
        float peak2Y = centerY - (0.65f * maxAmp);
        canvas.drawCircle(peak2X, peak2Y, 2.8f * density, linePaint);
        canvas.drawCircle(peak2X, peak2Y, 1.8f * density, nodePaint);
    }

    /**
     * 4. DIGITAL MATRIX EQUALIZER DOTS
     */
    private static void drawDigitalDots(Canvas canvas, int widthPx, int heightPx, int color, float density) {
        float centerY = heightPx / 2.0f;
        int cols = 22;
        int maxRowsPerSide = 3; // total up to 7 vertical dots per col
        float dotRadius = 1.8f * density;
        float rowSpacing = 4.5f * density;
        float colSpacing = (float) widthPx / (cols + 1);

        int[] dotCounts = {
                1, 2, 4, 3, 5, 4, 6, 3, 5, 6, 2, 5, 4, 7, 5, 6, 3, 5, 6, 4, 3, 2
        };

        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);

        for (int c = 0; c < cols; c++) {
            float x = (c + 1) * colSpacing;
            int dotsInCol = dotCounts[c % dotCounts.length];
            int halfDots = dotsInCol / 2;

            for (int r = -halfDots; r <= halfDots; r++) {
                float y = centerY + (r * rowSpacing);
                int distFromCenter = Math.abs(r);
                int alpha = distFromCenter == 0 ? 0xFF : Math.max(0x55, 0xFF - (distFromCenter * 50));

                dotPaint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
                canvas.drawCircle(x, y, dotRadius, dotPaint);
            }
        }
    }

    /**
     * 5. MINIMALIST SCRUBBER LINE
     */
    private static void drawMinimalLine(Canvas canvas, int widthPx, int heightPx, int color, float density) {
        float centerY = heightPx / 2.0f;

        // 1. Inactive background track (translucent white line)
        Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(2.2f * density);
        trackPaint.setColor(0x38FFFFFF);
        canvas.drawLine(4 * density, centerY, widthPx - (4 * density), centerY, trackPaint);

        // 2. Active played progress bar (48% width)
        float progressX = widthPx * 0.48f;
        Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setStrokeCap(Paint.Cap.ROUND);
        activePaint.setStrokeWidth(3.0f * density);
        activePaint.setColor(color);
        canvas.drawLine(4 * density, centerY, progressX, centerY, activePaint);

        // 3. Glowing Scrubber Node at current position
        Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        haloPaint.setStyle(Paint.Style.FILL);
        haloPaint.setColor(Color.argb(0x40, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(progressX, centerY, 6.5f * density, haloPaint);

        Paint nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setColor(color);
        canvas.drawCircle(progressX, centerY, 4.0f * density, nodePaint);

        Paint centerCore = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCore.setStyle(Paint.Style.FILL);
        centerCore.setColor(Color.WHITE);
        canvas.drawCircle(progressX, centerY, 2.0f * density, centerCore);
    }
}
