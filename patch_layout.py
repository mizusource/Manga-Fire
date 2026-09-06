import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

target = """                        <ImageView
                            android:id="@+id/btnFavorite"
                            android:layout_width="44dp"
                            android:layout_height="44dp"
                            android:layout_marginStart="12dp"
                            android:padding="10dp"
                            android:src="@drawable/ic_favorite_border"
                            android:background="@drawable/bg_glass_icon"
                            app:tint="#FFFFFF" />"""

replacement = """                        <ImageView
                            android:id="@+id/btnCustomList"
                            android:layout_width="44dp"
                            android:layout_height="44dp"
                            android:layout_marginStart="12dp"
                            android:padding="10dp"
                            android:src="@drawable/ic_view_list"
                            android:background="@drawable/bg_glass_icon"
                            app:tint="#FFFFFF" />

                        <ImageView
                            android:id="@+id/btnFavorite"
                            android:layout_width="44dp"
                            android:layout_height="44dp"
                            android:layout_marginStart="12dp"
                            android:padding="10dp"
                            android:src="@drawable/ic_favorite_border"
                            android:background="@drawable/bg_glass_icon"
                            app:tint="#FFFFFF" />"""

content = content.replace(target, replacement)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(content)

