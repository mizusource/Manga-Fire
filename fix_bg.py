import re

with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'r') as f:
    content = f.read()

content = content.replace('@drawable/bg_search', '?attr/colorSurface')

with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'w') as f:
    f.write(content)
