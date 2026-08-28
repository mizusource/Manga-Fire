import re

with open('app/src/main/res/layout/item_comment.xml', 'r') as f:
    content = f.read()

like_button = '''                    <ImageView
                        android:id="@+id/ivLike"
                        android:layout_width="18dp"
                        android:layout_height="18dp"
                        android:src="@drawable/ic_favorite_border"
                        app:tint="?attr/colorOnSurfaceVariant" />'''
content = re.sub(r'<ImageView[^>]*android:id="@+id/ivLike".*?/>', like_button, content, flags=re.DOTALL)

# Hide reply button
content = content.replace('android:id="@+id/btnReply"', 'android:id="@+id/btnReply"\n                    android:visibility="gone"')

with open('app/src/main/res/layout/item_comment.xml', 'w') as f:
    f.write(content)
