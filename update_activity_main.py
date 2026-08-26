import re

with open('app/src/main/res/layout/activity_main.xml', 'r') as f:
    content = f.read()

hero_banner_xml = """                <View 
                    android:layout_width="match_parent" 
                    android:layout_height="match_parent" 
                    android:background="@drawable/bg_appbar" 
                    android:alpha="0.3"
                    app:layout_collapseMode="parallax" />

                <androidx.viewpager2.widget.ViewPager2
                    android:id="@+id/vpHeroBanner"
                    android:layout_width="match_parent"
                    android:layout_height="220dp"
                    android:layout_marginTop="?attr/actionBarSize"
                    app:layout_collapseMode="parallax" />
"""

content = re.sub(r'<View\s+android:layout_width="match_parent"\s+android:layout_height="match_parent"\s+android:background="@drawable/bg_appbar"\s+android:alpha="0.3"\s+app:layout_collapseMode="parallax"\s*/>', hero_banner_xml, content)

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(content)
