import os

icons = [f[:-4] for f in os.listdir(r'E:\Android\icons') if f.endswith('.png')]
appfilter_txt = open(r'E:\Android\app\src\main\res\xml\appfilter.xml', encoding='utf-8').read()
drawable_txt = open(r'E:\Android\app\src\main\res\xml\drawable.xml', encoding='utf-8').read()
theme_txt = open(r'E:\Android\app\src\main\res\xml\theme_resources.xml', encoding='utf-8').read()
iconpack_txt = open(r'E:\Android\app\src\main\res\xml\iconpack.xml', encoding='utf-8').read()

missing_appfilter = [i for i in icons if f'drawable="{i}"' not in appfilter_txt]
missing_drawable = [i for i in icons if f'drawable="{i}"' not in drawable_txt]
missing_theme = [i for i in icons if f'drawable="{i}"' not in theme_txt]
missing_iconpack = [i for i in icons if f'"{i}"' not in iconpack_txt]

print('Total icons:', len(icons))
print('Missing from appfilter:', len(missing_appfilter), missing_appfilter)
print('Missing from drawable:', len(missing_drawable), missing_drawable)
print('Missing from theme_resources:', len(missing_theme), missing_theme)
print('Missing from iconpack:', len(missing_iconpack), missing_iconpack)
