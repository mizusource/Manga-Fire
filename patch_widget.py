import re

with open("app/src/main/res/layout/widget_comment_action.xml", "r") as f:
    text = f.read()

text = text.replace('android:id="@+id/tvActionCount"', 'android:id="@+id/tvLikeCount"')
with open("app/src/main/res/layout/widget_comment_action.xml", "w") as f:
    f.write(text)
