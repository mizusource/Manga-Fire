import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    text = f.read()

text = text.replace("<com.l4digital.fastscroll.FastScrollRecyclerView", "<androidx.recyclerview.widget.RecyclerView")
text = text.replace("</com.l4digital.fastscroll.FastScrollRecyclerView>", "</androidx.recyclerview.widget.RecyclerView>")
text = re.sub(r'app:bubbleColor="[^"]*"', '', text)
text = re.sub(r'app:handleColor="[^"]*"', '', text)
text = re.sub(r'app:trackColor="[^"]*"', '', text)
text = re.sub(r'app:showTrack="true"', 'app:fastScrollEnabled="true"', text)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(text)

with open("app/build.gradle.kts", "r") as f:
    gradle = f.read()

gradle = gradle.replace('implementation("com.github.l4digital:FastScroll:3.0.0")', '')

with open("app/build.gradle.kts", "w") as f:
    f.write(gradle)
