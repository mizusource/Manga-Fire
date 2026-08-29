with open('app/src/main/res/layout/dialog_edit_profile.xml', 'r') as f:
    content = f.read()

content = content.replace('<com.google.android.material.textfield.TextInputLayout', '<com.google.android.material.textfield.TextInputLayout\n        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"\n        app:boxCornerRadiusTopStart="16dp"\n        app:boxCornerRadiusTopEnd="16dp"\n        app:boxCornerRadiusBottomStart="16dp"\n        app:boxCornerRadiusBottomEnd="16dp"\n        xmlns:app="http://schemas.android.com/apk/res-auto"')

with open('app/src/main/res/layout/dialog_edit_profile.xml', 'w') as f:
    f.write(content)
