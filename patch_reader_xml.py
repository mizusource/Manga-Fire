import re

with open('app/src/main/res/layout/activity_chapter_reader.xml', 'r') as f:
    content = f.read()

# I will add the missing WebView to the layout, hidden, since it's probably for scraping only.
if "scraperWebView" not in content:
    content = content.replace(
        '<androidx.recyclerview.widget.RecyclerView',
        '<WebView\n        android:id="@+id/scraperWebView"\n        android:layout_width="1dp"\n        android:layout_height="1dp"\n        android:visibility="gone" />\n\n    <androidx.recyclerview.widget.RecyclerView'
    )

with open('app/src/main/res/layout/activity_chapter_reader.xml', 'w') as f:
    f.write(content)
