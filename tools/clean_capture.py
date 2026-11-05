import subprocess
import time
import shutil

subprocess.run(["adb", "connect", "192.168.220.4:38665"], check=True)
time.sleep(0.5)

# Tap X button on the floating window (top right of window: x=800, y=248)
subprocess.run(["adb", "-s", "192.168.220.4:38665", "shell", "input", "tap", "800", "248"])
time.sleep(1.0)

# Capture clean home screen
with open("output/clean_homescreen_with_black_raven.png", "wb") as f:
    subprocess.run(["adb", "-s", "192.168.220.4:38665", "exec-out", "screencap", "-p"], stdout=f, check=True)
shutil.copy2("output/clean_homescreen_with_black_raven.png", r"C:\Users\sanil\.gemini\antigravity\brain\b2d0eb3f-d9d7-4f4e-9a72-fea47f8f65ec\clean_homescreen_with_black_raven.png")
print("Captured clean screenshot!")
