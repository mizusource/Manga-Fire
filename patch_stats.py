import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

old_stats = re.search(r'<!-- Stats Row -->.*?<!-- AniList Metadata Card -->', content, re.DOTALL).group(0)

new_stats = '''<!-- Stats Row (Manga Slayer Style) -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="24dp"
                    android:orientation="horizontal"
                    android:weightSum="4"
                    android:paddingHorizontal="12dp">

                    <!-- Global Rating -->
                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:gravity="center"
                        android:orientation="vertical">
                        <ImageView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@drawable/ic_star"
                            app:tint="?attr/colorOnSurfaceVariant" />
                        <TextView
                            android:id="@+id/tvGlobalRating"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="9.6/10"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="14sp"
                            android:textStyle="bold" />
                        <TextView
                            android:id="@+id/tvGlobalRatingCount"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="3673"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>

                    <!-- AL Rating -->
                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:gravity="center"
                        android:orientation="vertical">
                        <TextView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:text="AL"
                            android:gravity="center"
                            android:textStyle="bold"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:background="@drawable/circle_bg_gray"
                            android:textSize="12sp" />
                        <TextView
                            android:id="@+id/tvALRating"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="8.2/10"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="14sp"
                            android:textStyle="bold" />
                        <TextView
                            android:id="@+id/tvALRatingCount"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="19K"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>

                    <!-- User Rating -->
                    <LinearLayout
                        android:id="@+id/btnUserRating"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:clickable="true"
                        android:focusable="true">
                        <ImageView
                            android:id="@+id/ivUserRatingStar"
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@drawable/ic_star_outline"
                            app:tint="#FF9800" />
                        <TextView
                            android:id="@+id/tvUserRating"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="-/10"
                            android:textColor="#FF9800"
                            android:textSize="14sp"
                            android:textStyle="bold" />
                        <TextView
                            android:id="@+id/tvUserRatingLabel"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="تقييمك"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>

                    <!-- My List -->
                    <LinearLayout
                        android:id="@+id/btnMyList"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:clickable="true"
                        android:focusable="true">
                        <ImageView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@drawable/ic_list"
                            app:tint="?attr/colorOnSurfaceVariant" />
                        <TextView
                            android:id="@+id/tvMyListTitle"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="قائمتي"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="14sp"
                            android:textStyle="bold" />
                        <TextView
                            android:id="@+id/tvMyListStatus"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="غير مضاف"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>
                </LinearLayout>

                <!-- AniList Metadata Card -->'''

content = content.replace(old_stats, new_stats)

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)

