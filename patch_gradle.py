import re

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

if 'id("com.google.gms.google-services")' not in content:
    content = content.replace('alias(libs.plugins.ksp)', 'alias(libs.plugins.ksp)\n    id("com.google.gms.google-services") version "4.4.1"')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched app/build.gradle.kts")
