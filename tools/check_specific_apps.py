import re

with open(r'E:\Android\app\src\main\res\xml\appfilter.xml', 'r', encoding='utf-8') as f:
    text = f.read()

apps = [
    'dialer', 'vending', 'spotify', 'netflix', 'mihon', 'nothing.camera',
    'vivaldi', 'youtube.music', 'stremio', 'quettabrowser', 'chrome', 'whatsapp'
]

for a in apps:
    matches = re.findall(rf'<item component="[^"]*{a}[^"]*" [^/]*/>', text, re.IGNORECASE)
    print(f"=== {a} ({len(matches)} matches) ===")
    for m in matches:
        print(" ", m)
