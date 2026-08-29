import re

with open('app/src/main/res/values/styles.xml', 'r') as f:
    content = f.read()

font_item = '\n        <item name="android:fontFamily">@font/ios_arabic</item>'

# Add to Theme.Animeista.Dark
content = content.replace('<style name="Theme.Animeista.Dark" parent="Theme.Material3.DayNight.NoActionBar">', '<style name="Theme.Animeista.Dark" parent="Theme.Material3.DayNight.NoActionBar">' + font_item)
# Add to Theme.Animeista.ClassicWhite
content = content.replace('<style name="Theme.Animeista.ClassicWhite" parent="Theme.Material3.DayNight.NoActionBar">', '<style name="Theme.Animeista.ClassicWhite" parent="Theme.Material3.DayNight.NoActionBar">' + font_item)

with open('app/src/main/res/values/styles.xml', 'w') as f:
    f.write(content)
