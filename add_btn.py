with open('app/src/main/res/layout/activity_login.xml', 'r') as f:
    content = f.read()

btn_forgot = """    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnForgotPassword"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="نسيت كلمة المرور؟"
        style="@style/Widget.Material3.Button.TextButton" />
"""

content = content.replace('</LinearLayout>', btn_forgot + '</LinearLayout>')

with open('app/src/main/res/layout/activity_login.xml', 'w') as f:
    f.write(content)
