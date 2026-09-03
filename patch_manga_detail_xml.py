import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

# 1. Add alternative titles
alt_titles_xml = """
                    <!-- Alternative Titles -->
                    <LinearLayout
                        android:id="@+id/altTitlesContainer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="20dp"
                        android:layout_marginTop="16dp"
                        android:orientation="vertical"
                        android:visibility="gone">
                        
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
                            android:lineSpacingExtra="4dp" />
                    </LinearLayout>
"""

# Find where to insert alt_titles_xml, maybe after genres row? Let's insert it before the Description.
# I need to see where Description is.
