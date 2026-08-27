import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

old_buttons = '''            <ImageView
                android:id="@+id/btnComments"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:padding="12dp"
                android:src="@drawable/ic_comments"
                android:background="?attr/selectableItemBackgroundBorderless"
                app:tint="?attr/colorOnSurfaceVariant" />

            <ImageView
                android:id="@+id/btnFavorite"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:padding="12dp"
                android:layout_marginEnd="8dp"
                android:src="@drawable/ic_favorite_border"
                android:background="?attr/selectableItemBackgroundBorderless"
                app:tint="?attr/colorOnSurfaceVariant" />'''

new_buttons = '''            <LinearLayout
                android:id="@+id/btnCommentsContainer"
                android:layout_width="wrap_content"
                android:layout_height="36dp"
                android:orientation="horizontal"
                android:background="@drawable/bg_tonal_button"
                android:gravity="center"
                android:paddingHorizontal="12dp"
                android:layout_marginEnd="8dp"
                android:clickable="true"
                android:focusable="true">
                <ImageView
                    android:id="@+id/btnComments"
                    android:layout_width="18dp"
                    android:layout_height="18dp"
                    android:src="@drawable/ic_comments"
                    app:tint="?attr/colorPrimary" />
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="التعليقات"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:textColor="?attr/colorPrimary"
                    android:layout_marginStart="6dp"/>
            </LinearLayout>

            <LinearLayout
                android:id="@+id/btnFavoriteContainer"
                android:layout_width="wrap_content"
                android:layout_height="36dp"
                android:orientation="horizontal"
                android:background="@drawable/bg_tonal_button"
                android:gravity="center"
                android:paddingHorizontal="12dp"
                android:layout_marginEnd="8dp"
                android:clickable="true"
                android:focusable="true">
                <ImageView
                    android:id="@+id/btnFavorite"
                    android:layout_width="18dp"
                    android:layout_height="18dp"
                    android:src="@drawable/ic_favorite_border"
                    app:tint="?attr/colorPrimary" />
                <TextView
                    android:id="@+id/tvFavoriteText"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="حفظ"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:textColor="?attr/colorPrimary"
                    android:layout_marginStart="6dp"/>
            </LinearLayout>'''

content = content.replace(old_buttons, new_buttons)

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)
