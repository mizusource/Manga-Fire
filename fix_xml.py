with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

target = """                    <TextView
                        android:id="@+id/mangaTitleDetail"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:textColor="#FFFFFF"
                        android:textSize="22sp"
                        android:textStyle="bold"
                        android:maxLines="3"
                        android:ellipsize="end" />"""

replacement = """                    <TextView
                        android:id="@+id/mangaTitleDetail"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:textColor="#FFFFFF"
                        android:textSize="22sp"
                        android:textStyle="bold"
                        android:maxLines="3"
                        android:textAlignment="viewStart"
                        android:ellipsize="end" />"""

content = content.replace(target, replacement)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(content)
