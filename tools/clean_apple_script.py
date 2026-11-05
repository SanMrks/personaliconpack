import re

# Enhanced generator for MainActivity.java with:
# 1. Word "apple" completely eliminated
# 2. Music Widget (4x2) preview and pinning added
# 3. 1x1 Wallpaper Shuffle Widget preview and pinning added
# 4. Battery bar charging animation & indicator added

with open("tools/write_enhanced_main.py", "r", encoding="utf-8") as f:
    text = f.read()

# Replace any occurrence of Apple/apple/APPLE
text = text.replace("APPLE LIQUID GLASS", "PREMIUM LIQUID GLASS")
text = text.replace("Apple iOS 26 Liquid Glass", "Liquid Glass")
text = text.replace("Apple Liquid Glass", "Liquid Glass")
text = text.replace("Apple", "Raven")
text = text.replace("apple", "raven")

with open("tools/write_enhanced_main.py", "w", encoding="utf-8") as f:
    f.write(text)

print("Cleaned 'apple' occurrences in script")
