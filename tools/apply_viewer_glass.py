import sys
import re

path = "app/src/main/java/com/ravenshade/iconpack/WallpaperViewerActivity.java"
with open(path, "r", encoding="utf-8") as f:
    code = f.read()

# 1. Imports
if "import android.graphics.drawable.LayerDrawable;" not in code:
    code = code.replace(
        "import android.graphics.drawable.GradientDrawable;",
        "import android.graphics.drawable.Drawable;\nimport android.graphics.drawable.GradientDrawable;\nimport android.graphics.drawable.LayerDrawable;"
    )

# 2. Modern Card / Pill Helper
old_card = """    // Modern Card / Pill Helper
    public static GradientDrawable createModernCard(int bgColor, int strokeColor, float radius, int strokeWidth) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(bgColor);
        g.setCornerRadius(radius);
        if (strokeWidth > 0) {
            g.setStroke(strokeWidth, strokeColor);
        }
        return g;
    }"""

new_card = """    // Frosted Liquid Glass Card Helper
    public static Drawable createModernCard(int bgColor, int strokeColor, float radius, int strokeWidth) {
        GradientDrawable base = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    0x2EFFFFFF,
                    bgColor != 0 ? bgColor : 0x1407070C,
                    0x24180408
                });
        base.setCornerRadius(radius);
        base.setStroke(strokeWidth > 0 ? strokeWidth : 3, strokeColor != 0 ? strokeColor : 0x60FFFFFF);

        GradientDrawable glaze = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ 0x33FFFFFF, 0x08FFFFFF, 0x00FFFFFF });
        glaze.setCornerRadius(radius);

        LayerDrawable ld = new LayerDrawable(new Drawable[]{base, glaze});
        ld.setLayerInset(1, 4, 4, 4, 40);
        return ld;
    }"""

if old_card in code:
    code = code.replace(old_card, new_card)

# 3. actionDock background -> Frosted Liquid Glass
old_dock = """        GradientDrawable dockBg = new GradientDrawable();
        dockBg.setColor(Color.parseColor("#80150709")); // Translucent frosted obsidian crimson
        dockBg.setCornerRadius(60);
        dockBg.setStroke(2, Color.parseColor("#40FFFFFF")); // Specular light rim"""

new_dock = """        GradientDrawable dockBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ 0x33FFFFFF, 0x1607070D, 0x26180408 });
        dockBg.setCornerRadius(60);
        dockBg.setStroke(2, 0x66FFFFFF); // Polished specular glass bevel rim"""

if old_dock in code:
    code = code.replace(old_dock, new_dock)

with open(path, "w", encoding="utf-8") as f:
    f.write(code)

print("Updated WallpaperViewerActivity.java with Frosted Liquid Glass")
