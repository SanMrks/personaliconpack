import subprocess
import re

subprocess.run(["adb", "-s", "7b5334c5", "shell", "uiautomator", "dump", "/sdcard/window_dump.xml"], check=True)
res = subprocess.run(["adb", "-s", "7b5334c5", "shell", "cat", "/sdcard/window_dump.xml"], capture_output=True, text=True)

matches = re.findall(r'text="([^"]+)"[^>]*bounds="([^"]+)"', res.stdout)
print("Visible items on screen:")
for text, bounds in matches:
    print(f"  '{text}' at {bounds}")

if not matches:
    print("No text nodes found. Full XML snippet:")
    print(res.stdout[:1000])
