import re

def replace_input_area(file_path):
    with open(file_path, "r") as f:
        text = f.read()

    # Find the input LinearLayout and replace it with include
    pattern = r'<LinearLayout[^>]*?>\s*<ImageView.*?</LinearLayout>' if 'activity_comments' in file_path else r'<LinearLayout[^>]*?>\s*<com.google.android.material.textfield.TextInputLayout.*?</LinearLayout>'
    
    # Just simpler regex: from <LinearLayout to </LinearLayout> at the end of the file.
    # We know the input area is the last child in the main LinearLayout.
    # Let's use a simpler approach:
    
    parts = text.split("</FrameLayout>")
    if len(parts) == 2:
        new_text = parts[0] + "</FrameLayout>\n    <include android:id=\"@+id/widgetComposer\" layout=\"@layout/layout_widget_composer\" />\n</LinearLayout>"
        with open(file_path, "w") as f:
            f.write(new_text)

replace_input_area("app/src/main/res/layout/activity_comments.xml")
replace_input_area("app/src/main/res/layout/activity_replies.xml")
