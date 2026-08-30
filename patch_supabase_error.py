with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

old_code = """                    postAuthCallback(callback, false, "فشل إضافة التعليق");"""
new_code = """                    String errorBody = response.body() != null ? response.body().string() : "";
                    postAuthCallback(callback, false, "فشل إضافة التعليق: " + errorBody);"""

if old_code in content:
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content.replace(old_code, new_code))
    print("Patched SupabaseManager.java")
else:
    print("Could not find code to patch")
