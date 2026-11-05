import os
import shutil

src_dir = r"E:\Android\app\src\main\java\com\custom\iconpack"
dst_dir = r"E:\Android\app\src\main\java\com\ravenshade\iconpack"

os.makedirs(dst_dir, exist_ok=True)

# 1. Update and move MainActivity.java
with open(os.path.join(src_dir, "MainActivity.java"), "r", encoding="utf-8") as f:
    main_code = f.read()

main_code = main_code.replace("package com.custom.iconpack;", "package com.ravenshade.iconpack;")
main_code = main_code.replace('"com.custom.iconpack"', '"com.ravenshade.iconpack"')

with open(os.path.join(dst_dir, "MainActivity.java"), "w", encoding="utf-8") as f:
    f.write(main_code)
print("Updated and wrote MainActivity.java")

# 2. Update and move WallpaperViewerActivity.java
with open(os.path.join(src_dir, "WallpaperViewerActivity.java"), "r", encoding="utf-8") as f:
    viewer_code = f.read()

viewer_code = viewer_code.replace("package com.custom.iconpack;", "package com.ravenshade.iconpack;")
viewer_code = viewer_code.replace('"com.custom.iconpack"', '"com.ravenshade.iconpack"')

with open(os.path.join(dst_dir, "WallpaperViewerActivity.java"), "w", encoding="utf-8") as f:
    f.write(viewer_code)
print("Updated and wrote WallpaperViewerActivity.java")

# 3. Update and move MainActivity.kt if exists
kt_path = os.path.join(src_dir, "MainActivity.kt")
if os.path.exists(kt_path):
    with open(kt_path, "r", encoding="utf-8") as f:
        kt_code = f.read()
    kt_code = kt_code.replace("package com.custom.iconpack", "package com.ravenshade.iconpack")
    with open(os.path.join(dst_dir, "MainActivity.kt"), "w", encoding="utf-8") as f:
        f.write(kt_code)
    print("Updated and wrote MainActivity.kt")

# 4. Remove old src directory
shutil.rmtree(src_dir)
print(f"Removed old directory {src_dir}")

# 5. Remove old bin classes if exist
old_bin = r"E:\Android\tools\bin\com\custom"
if os.path.exists(old_bin):
    shutil.rmtree(old_bin)
    print(f"Removed old compiled classes {old_bin}")
