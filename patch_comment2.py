with open('app/src/main/res/layout/item_comment.xml', 'r') as f:
    content = f.read()

target = '''                    <ImageView
                        android:id="@+id/ivLike"
                        android:layout_width="20dp"
                        android:layout_height="20dp"
                        android:src="@android:drawable/ic_menu_upload"
                        android:rotation="90"
                        app:tint="?attr/colorOnSurfaceVariant" />'''

like_button = '''                    <ImageView
                        android:id="@+id/ivLike"
                        android:layout_width="20dp"
                        android:layout_height="20dp"
                        android:src="@drawable/ic_favorite_border"
                        app:tint="?attr/colorOnSurfaceVariant" />'''

content = content.replace(target, like_button)

with open('app/src/main/res/layout/item_comment.xml', 'w') as f:
    f.write(content)
