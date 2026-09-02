import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Let's replace the Toasts with safeToast in a few spots to demonstrate the integration
content = content.replace('Toast.makeText(this, "يجب تسجيل الدخول لإضافة المانجا للمكتبة", Toast.LENGTH_SHORT).show();', 'com.fire.mangareader.util.SystemUtils.safeToast(this, "يجب تسجيل الدخول لإضافة المانجا للمكتبة");')
content = content.replace('Toast.makeText(MangaDetailActivity.this, "تم تسجيل تقييمك بنجاح! شكرًا لك ⭐", Toast.LENGTH_SHORT).show();', 'com.fire.mangareader.util.SystemUtils.safeToast(MangaDetailActivity.this, "تم تسجيل تقييمك بنجاح! شكرًا لك ⭐");')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDetailActivity.java")
