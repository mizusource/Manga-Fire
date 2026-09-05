import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    text = f.read()

# Add Trailer and Related sections before the Chapters Tab
trailer_and_related = """
                <!-- العرض الدعائي (Trailer) -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp"
                    android:layout_marginTop="8dp">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="العرض الدعائي"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:textColor="@android:color/white"
                        android:layout_marginBottom="12dp"/>
                    <FrameLayout
                        android:layout_width="match_parent"
                        android:layout_height="180dp"
                        android:background="#222"
                        android:layout_marginBottom="16dp">
                        <ImageView
                            android:id="@+id/ivTrailerThumbnail"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:scaleType="centerCrop"
                            android:alpha="0.7"/>
                        <ImageView
                            android:layout_width="48dp"
                            android:layout_height="48dp"
                            android:layout_gravity="center"
                            android:src="@android:drawable/ic_media_play"/>
                    </FrameLayout>
                </LinearLayout>

                <!-- أعمال مشابهة (Related Manga) -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:paddingTop="16dp"
                    android:paddingBottom="16dp">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="أعمال مشابهة"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:textColor="@android:color/white"
                        android:layout_marginStart="16dp"
                        android:layout_marginBottom="12dp"/>
                    
                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/relatedRecyclerView"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:clipToPadding="false"
                        android:paddingHorizontal="16dp" />
                </LinearLayout>

                <!-- Chapters Tab -->
"""

text = text.replace("<!-- Chapters Tab -->", trailer_and_related)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(text)
