import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

recs_xml = """
                    <!-- Recommendations -->
                    <LinearLayout
                        android:id="@+id/recommendationsContainer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="20dp"
                        android:layout_marginBottom="20dp"
                        android:orientation="vertical">
                        
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginHorizontal="24dp"
                            android:text="أعمال مشابهة"
                            android:textColor="#FFFFFF"
                            android:textSize="18sp"
                            android:textStyle="bold" />
                            
                        <androidx.recyclerview.widget.RecyclerView
                            android:id="@+id/recommendationsRecyclerView"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="12dp"
                            android:clipToPadding="false"
                            android:paddingHorizontal="16dp" />
                    </LinearLayout>
"""

# Insert after mangaDescription
desc_end = 'android:textSize="15sp" />\n'
if desc_end in content:
    idx = content.find(desc_end) + len(desc_end)
    content = content[:idx] + recs_xml + content[idx:]
    with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
        f.write(content)
    print("Recommendations section added.")
else:
    print("Could not find end of mangaDescription.")

