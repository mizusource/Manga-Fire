import re

with open('app/src/main/java/com/fire/mangareader/activity/RegisterActivity.java', 'r') as f:
    content = f.read()

content = content.replace(
    'Toast.makeText(RegisterActivity.this, "تم التسجيل بنجاح، يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();',
    'Toast.makeText(RegisterActivity.this, "لتفعيل حسابك قم بالدخول الى بريدك الالكتروني واضغط تأكيد", Toast.LENGTH_LONG).show();'
)

with open('app/src/main/java/com/fire/mangareader/activity/RegisterActivity.java', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

content = content.replace(
    'msg = "يرجى تأكيد بريدك الإلكتروني (تأكد من صندوق الوارد أو البريد المزعج)، أو قم بتعطيل \'Confirm email\' من إعدادات Supabase.";',
    'msg = "لتفعيل حسابك قم بالدخول الى بريدك الالكتروني واضغط تأكيد";'
)

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(content)
