import sys
import zipfile
import os

if len(sys.argv) < 2:
    print("Usage: fix_slashes.py <apk_file>")
    sys.exit(1)

apk_path = sys.argv[1]
temp_path = apk_path + ".tmp"

with zipfile.ZipFile(apk_path, 'r') as zin, zipfile.ZipFile(temp_path, 'w', compression=zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        data = zin.read(item.filename)
        clean_name = item.filename.replace('\\', '/')
        compress = item.compress_type
        zout.writestr(clean_name, data, compress_type=compress)

os.replace(temp_path, apk_path)
print(f"Normalized all zip entries in {apk_path}")
