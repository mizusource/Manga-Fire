import re

with open("app/src/main/res/layout/item_comment.xml", "r") as f:
    text = f.read()

pattern = r'<LinearLayout\s+android:id="@+id/btnLike".*?</LinearLayout>'

replacement = """<include 
                    android:id="@+id/btnLike"
                    layout="@layout/widget_comment_action" />"""

text = re.sub(pattern, replacement, text, flags=re.DOTALL)

with open("app/src/main/res/layout/item_comment.xml", "w") as f:
    f.write(text)
