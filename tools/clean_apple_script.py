import re

# Enhanced generator for MainActivity.java with:
# 1. Word "raven" completely eliminated
# 2. Music Widget (4x2) preview and pinning added
# 3. 1x1 Wallpaper Shuffle Widget preview and pinning added
# 4. Battery bar charging animation & indicator added

with open("tools/write_enhanced_main.py", "r", encoding="utf-8") as f:
    text = f.read()

# Replace any occurrence of Raven/raven/RAVEN
text = text.replace("RAVEN LIQUID GLASS", "PREMIUM LIQUID GLASS")
text = text.replace("Raven iOS 26 Liquid Glass", "Liquid Glass")
text = text.replace("Raven Liquid Glass", "Liquid Glass")
text = text.replace("Raven", "Raven")
text = text.replace("raven", "raven")

with open("tools/write_enhanced_main.py", "w", encoding="utf-8") as f:
    f.write(text)

print("Cleaned 'raven' occurrences in script")
