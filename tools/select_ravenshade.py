import subprocess
import time
import re

def adb(cmd):
    return subprocess.run(f"adb -s 7b5334c5 {cmd}", shell=True, capture_output=True, text=True)

# 1. Close any overlay and open IconPackPickerActivity
adb("shell input keyevent 4")
time.sleep(0.5)
adb("shell input keyevent 3")
time.sleep(0.5)

print("Starting IconPackPickerActivity...")
adb("shell am start -n com.nothing.launcher/.setting.iconpack.IconPackPickerActivity")
time.sleep(1.5)

# 2. Dump hierarchy
adb("shell uiautomator dump /sdcard/window_dump.xml")
res = adb("shell cat /sdcard/window_dump.xml")

# Look for Ravenshade or Custom
print("Looking for Ravenshade in picker...")
m = re.search(r'text="([^"]*Ravenshade[^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', res.stdout, re.IGNORECASE)
if m:
    name, x1, y1, x2, y2 = m.group(1), int(m.group(2)), int(m.group(3)), int(m.group(4)), int(m.group(5))
    cx, cy = (x1 + x2) // 2, (y1 + y2) // 2
    print(f"Found {name} at ({cx}, {cy}). Clicking...")
    adb(f"shell input tap {cx} {cy}")
    time.sleep(1.0)
else:
    print("Ravenshade text node not directly matched. Checking all text entries:")
    all_texts = re.findall(r'text="([^"]+)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', res.stdout)
    for t, x1, y1, x2, y2 in all_texts:
        print(f"  '{t}' at [{(int(x1)+int(x2))//2}, {(int(y1)+int(y2))//2}]")
        if "raven" in t.lower() or "custom" in t.lower():
            cx, cy = (int(x1) + int(x2)) // 2, (int(y1) + int(y2)) // 2
            print(f"Clicking candidate {t} at ({cx}, {cy})...")
            adb(f"shell input tap {cx} {cy}")
            time.sleep(1.0)
            break

# 3. Commit by clicking back / navigate up
print("Committing selection...")
# Tap top left 'Navigate up' or Back
adb("shell input tap 72 198")
time.sleep(0.5)
adb("shell input keyevent 4")
time.sleep(0.5)
adb("shell input keyevent 3")
time.sleep(1.0)

# 4. Take a screenshot
print("Capturing screenshot of home screen...")
adb("shell screencap -p /sdcard/homescreen_v41.png")
adb("pull /sdcard/homescreen_v41.png output/homescreen_v41.png")
print("Saved screenshot to output/homescreen_v41.png!")
