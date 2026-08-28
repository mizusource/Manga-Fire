with open('app/src/main/res/layout/item_comment.xml', 'r') as f:
    content = f.read()

content = content.replace('android:id="@+id/btnReply"\n                    android:visibility="gone"', 'android:id="@+id/btnReply"')

with open('app/src/main/res/layout/item_comment.xml', 'w') as f:
    f.write(content)
