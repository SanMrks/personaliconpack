path = "app/src/main/java/com/ravenshade/iconpack/MainActivity.java"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()

# 1. Update subtitle
c = c.replace(
    'availableWidgetsSub.setText("6 Interactive Widgets • 1-Tap Add to Home Screen");',
    'availableWidgetsSub.setText("8 Interactive Widgets • 1-Tap Add to Home Screen");'
)

# 2. Add to widgetsRow
target_row = """        // 7. Raven Weather (2x2)
        widgetsRow.addView(createWidgetPreviewCard(
            "Raven Weather (2x2)",
            "Live Weather • Obsidian Dark Edition",
            WeatherWidgetProvider.class,
            7
        ));"""

new_row = """        // 7. Raven Weather (2x2)
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
        ));"""

if target_row in c:
    c = c.replace(target_row, new_row)
    print("Added MusicLarge2 to widgetsRow")

# 3. Add type 8 preview
target_type7 = """            TextView tvCond = new TextView(this);
            tvCond.setText(RavenWeatherManager.getCondition(this));
            tvCond.setTextColor(Color.parseColor("#FFFF2E4D"));
            tvCond.setTextSize(10);
            tvCond.setTypeface(Typeface.DEFAULT_BOLD);
            wBox.addView(tvCond);

            preview.addView(wBox);
        } else {"""

new_type8 = """            TextView tvCond = new TextView(this);
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
        } else {"""

if target_type7 in c:
    c = c.replace(target_type7, new_type8)
    print("Added type 8 preview to createWidgetPreviewCard")

with open(path, "w", encoding="utf-8") as f:
    f.write(c)

print("Updated MainActivity.java successfully")
