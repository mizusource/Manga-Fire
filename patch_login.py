import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/LoginActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Forgot password check
content = content.replace('if (email.isEmpty()) {\n            Toast.makeText(this, "يرجى كتابة بريدك الإلكتروني في الحقل المخصص أولاً", Toast.LENGTH_SHORT).show();\n            return;\n        }', 'if (email.isEmpty()) {\n            Toast.makeText(this, "يرجى كتابة بريدك الإلكتروني في الحقل المخصص أولاً", Toast.LENGTH_SHORT).show();\n            return;\n        }\n        if (!com.fire.mangareader.util.ValidationUtils.isValidEmail(email)) {\n            Toast.makeText(this, "يرجى إدخال بريد إلكتروني صالح", Toast.LENGTH_SHORT).show();\n            return;\n        }')

# Login check
content = content.replace('if (email.isEmpty() || password.isEmpty()) {\n            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();\n            return;\n        }', 'if (email.isEmpty() || password.isEmpty()) {\n            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();\n            return;\n        }\n        if (!com.fire.mangareader.util.ValidationUtils.isValidEmail(email)) {\n            Toast.makeText(this, "يرجى إدخال بريد إلكتروني صالح", Toast.LENGTH_SHORT).show();\n            return;\n        }')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched LoginActivity")
