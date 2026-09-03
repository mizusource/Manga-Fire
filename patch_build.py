import re

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('implementation(libs.androidx.lifecycle.runtime.compose)', 
                          'implementation(libs.androidx.lifecycle.runtime.compose)\n    implementation("androidx.navigation:navigation-compose:2.7.7")')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched build.gradle.kts")
