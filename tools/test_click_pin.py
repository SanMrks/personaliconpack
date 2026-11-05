import subprocess
import re
import time
import shutil

subprocess.run(["adb", "connect", "192.168.220.4:38665"], check=True)
time.sleep(0.5)

# 1. Bring MainActivity to front
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "am", "start", "-n", "com.ravenshade.iconpack/.MainActivity"])
time.sleep(1.0)

# 2. Swipe up to reveal the widgets section
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "input", "swipe", "540", "1600", "540", "600"])
time.sleep(1.0)

# 3. Dump UI hierarchy
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "uiautomator", "dump", "/sdcard/window_dump.xml"])
res = subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "cat", "/sdcard/window_dump.xml"], capture_output=True, text=True)

matches = re.findall(r'text="([^"]*Add to Home[^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', res.stdout)
for t, x1, y1, x2, y2 in matches:
    cx, cy = (int(x1) + int(x2)) // 2, (int(y1) + int(y2)) // 2
    print(f"Found button '{t}' at ({cx}, {cy}). Clicking...")
    subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "input", "tap", str(cx), str(cy)])
    time.sleep(1.5)
    break

# 4. Capture screenshot
with open("output/pin_widget_dialog.png", "wb") as f:
    subprocess.run(["adb", "-s", "192.168.220.4:38665", "exec-out", "screencap", "-p"], stdout=f, check=True)
shutil.copy2("output/pin_widget_dialog.png", r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\pin_widget_dialog.png")
print("Captured screenshot to pin_widget_dialog.png!")
