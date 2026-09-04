import re

with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    text = f.read()

text = text.replace(
    '''<androidx.recyclerview.widget.RecyclerView
                    android:id="@+id/chaptersRecyclerView"''',
    '''<com.l4digital.fastscroll.FastScrollRecyclerView
                    android:id="@+id/chaptersRecyclerView"
                    app:bubbleColor="?attr/colorPrimary"
                    app:handleColor="?attr/colorPrimary"
                    app:trackColor="?attr/colorSurfaceVariant"
                    app:showTrack="true"'''
)
text = text.replace(
    '''</androidx.recyclerview.widget.RecyclerView>
            </LinearLayout>''',
    '''</com.l4digital.fastscroll.FastScrollRecyclerView>
            </LinearLayout>'''
)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(text)
