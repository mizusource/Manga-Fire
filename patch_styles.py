import re

with open('app/src/main/res/values/styles.xml', 'r') as f:
    content = f.read()

# Add font family
if "android:fontFamily" not in content:
    content = content.replace(
        '<item name="android:windowAnimationStyle">@style/WindowAnimationTransition</item>',
        '<item name="android:windowAnimationStyle">@style/WindowAnimationTransition</item>\n        <item name="android:fontFamily">@font/ios_font</item>\n        <item name="fontFamily">@font/ios_font</item>'
    )
    content = content.replace(
        '<item name="android:navigationBarColor">@color/cl_white_background</item>',
        '<item name="android:navigationBarColor">@color/cl_white_background</item>\n        <item name="android:fontFamily">@font/ios_font</item>\n        <item name="fontFamily">@font/ios_font</item>\n        <item name="colorSurfaceVariant">#F5F5F5</item>\n        <item name="colorOutline">#E0E0E0</item>'
    )
    
    # Add neon surface variants
    content = content.replace(
        '<item name="android:navigationBarColor">@color/dn_red_background</item>',
        '<item name="android:navigationBarColor">@color/dn_red_background</item>\n        <item name="colorSurfaceVariant">#1A1A1A</item>\n        <item name="colorOutline">#333333</item>'
    )
    content = content.replace(
        '<item name="android:navigationBarColor">@color/dn_purple_background</item>',
        '<item name="android:navigationBarColor">@color/dn_purple_background</item>\n        <item name="colorSurfaceVariant">#1A1A1A</item>\n        <item name="colorOutline">#333333</item>'
    )
    content = content.replace(
        '<item name="android:navigationBarColor">@color/dn_yellow_background</item>',
        '<item name="android:navigationBarColor">@color/dn_yellow_background</item>\n        <item name="colorSurfaceVariant">#1A1A1A</item>\n        <item name="colorOutline">#333333</item>'
    )
    content = content.replace(
        '<item name="android:navigationBarColor">@color/cl_navy_background</item>',
        '<item name="android:navigationBarColor">@color/cl_navy_background</item>\n        <item name="colorSurfaceVariant">#1C2331</item>\n        <item name="colorOutline">#2B3548</item>'
    )

with open('app/src/main/res/values/styles.xml', 'w') as f:
    f.write(content)
