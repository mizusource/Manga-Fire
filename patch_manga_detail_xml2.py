import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

# Add Alt Titles and Advanced Stats before mangaDescription
stats_xml = """
                    <!-- Advanced Stats -->
                    <LinearLayout
                        android:id="@+id/advancedStatsContainer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="24dp"
                        android:layout_marginTop="16dp"
                        android:orientation="vertical">
                        
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="تقييمات مفصلة"
                            android:textColor="#FFFFFF"
                            android:textSize="16sp"
                            android:textStyle="bold" />
                            
                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:orientation="horizontal"
                            android:weightSum="3">
                            
                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:gravity="center"
                                android:orientation="vertical">
                                <TextView
                                    android:id="@+id/tvStatStory"
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="-"
                                    android:textColor="#39FF14"
                                    android:textSize="16sp"
                                    android:textStyle="bold" />
                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="القصة"
                                    android:textColor="#B3FFFFFF"
                                    android:textSize="12sp" />
                            </LinearLayout>
                            
                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:gravity="center"
                                android:orientation="vertical">
                                <TextView
                                    android:id="@+id/tvStatCharacters"
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="-"
                                    android:textColor="#39FF14"
                                    android:textSize="16sp"
                                    android:textStyle="bold" />
                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="الشخصيات"
                                    android:textColor="#B3FFFFFF"
                                    android:textSize="12sp" />
                            </LinearLayout>
                            
                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:gravity="center"
                                android:orientation="vertical">
                                <TextView
                                    android:id="@+id/tvStatDrawing"
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="-"
                                    android:textColor="#39FF14"
                                    android:textSize="16sp"
                                    android:textStyle="bold" />
                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="الرسم"
                                    android:textColor="#B3FFFFFF"
                                    android:textSize="12sp" />
                            </LinearLayout>
                            
                        </LinearLayout>
                    </LinearLayout>
                    
                    <!-- Alternative Titles -->
                    <LinearLayout
                        android:id="@+id/altTitlesContainer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="24dp"
                        android:layout_marginTop="16dp"
                        android:orientation="vertical">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="الأسماء البديلة"
                            android:textColor="#FFFFFF"
                            android:textSize="16sp"
                            android:textStyle="bold" />
                        <TextView
                            android:id="@+id/tvAltTitles"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:textColor="#B3FFFFFF"
                            android:textSize="14sp"
                            android:text="غير متوفر" />
                    </LinearLayout>
                    
"""

desc_tag = 'android:id="@+id/mangaDescription"'
# Insert before Description TextView
if desc_tag in content:
    idx = content.find('<TextView\n                        android:id="@+id/mangaDescription"')
    content = content[:idx] + stats_xml + content[idx:]
    with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
        f.write(content)
    print("Manga Details XML Updated successfully.")
else:
    print("Could not find mangaDescription in XML.")

