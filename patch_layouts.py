import re

def replace_bottom_nav(file_path):
    with open(file_path, 'r') as f:
        content = f.read()
    
    # regex to find the MaterialCardView containing bottom_navigation
    pattern = r'<com\.google\.android\.material\.card\.MaterialCardView[^>]*>.*?<com\.google\.android\.material\.bottomnavigation\.BottomNavigationView.*?/>\s*</com\.google\.android\.material\.card\.MaterialCardView>'
    
    if re.search(pattern, content, re.DOTALL):
        content = re.sub(pattern, '<include layout="@layout/layout_bottom_nav" />', content, flags=re.DOTALL)
        with open(file_path, 'w') as f:
            f.write(content)
            print(f"Patched {file_path}")

replace_bottom_nav('app/src/main/res/layout/activity_main.xml')
