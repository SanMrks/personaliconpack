import subprocess
import time
import shutil

subprocess.run(["adb", "connect", "192.168.220.4:38665"], check=True)
time.sleep(0.5)

# Tap 'Add to home screen' button on the dialog (around x=800, y=940 of the bottom sheet or y=2300 of screen)
# Let's dump uiautomator to get exact bounds of "Add to home screen"
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "uiautomator", "dump", "/sdcard/window_dump.xml"])
res = subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "cat", "/sdcard/window_dump.xml"], capture_output=True, text=True)

import re
matches = re.findall(r'text="([^"]*Add to home screen[^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', res.stdout, re.IGNORECASE)
for t, x1, y1, x2, y2 in matches:
    cx, cy = (int(x1) + int(x2)) // 2, (int(y1) + int(y2)) // 2
    print(f"Tapping '{t}' at ({cx}, {cy})...")
    subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "input", "tap", str(cx), str(cy)])
    break

time.sleep(1.0)
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "input", "keyevent", "3"])
time.sleep(1.5)

# Capture home screen with widget
with open("output/homescreen_with_widget.png", "wb") as f:
    subprocess.run(["adb", "-s", "192.168.220.4:38665", "exec-out", "screencap", "-p"], stdout=f, check=True)
shutil.copy2("output/homescreen_with_widget.png", r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\homescreen_with_widget.png")
print("Captured screenshot to homescreen_with_widget.png!")
