with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

old_error = 'postAuthCallback(callback, false, "فشل إضافة التعليق: " + errorBody);'
new_error = '''if (errorBody.contains("PGRST303")) {
                        postAuthCallback(callback, false, "انتهت صلاحية الجلسة، يرجى تسجيل الخروج ثم الدخول مجدداً");
                    } else {
                        postAuthCallback(callback, false, "فشل إضافة التعليق: " + errorBody);
                    }'''

if old_error in content:
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content.replace(old_error, new_error))
    print("Patched SupabaseManager.java for comments")
else:
    print("Could not find code to patch")
