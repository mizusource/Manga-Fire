import re

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

new_error_handling = """
                    try {
                        String errorBody = response.body().string();
                        org.json.JSONObject errorObj = new org.json.JSONObject(errorBody);
                        String msg = errorObj.optString("error_description", errorObj.optString("msg", "البريد أو كلمة المرور غير صحيحة"));
                        
                        if (msg.contains("Email not confirmed") || msg.contains("verify")) {
                            msg = "يرجى تأكيد بريدك الإلكتروني (تأكد من صندوق الوارد أو البريد المزعج)، أو قم بتعطيل 'Confirm email' من إعدادات Supabase.";
                        } else if (msg.contains("Invalid login credentials")) {
                            msg = "البريد أو كلمة المرور غير صحيحة.";
                        }
                        
                        postAuthCallback(callback, false, msg);
                    } catch (Exception ex) {
                        postAuthCallback(callback, false, "البريد أو كلمة المرور غير صحيحة");
                    }
"""

content = re.sub(r'postAuthCallback\(callback, false, "البريد أو كلمة المرور غير صحيحة"\);', new_error_handling.strip(), content)

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(content)

