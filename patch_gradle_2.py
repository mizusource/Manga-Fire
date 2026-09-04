import re

with open("app/build.gradle.kts", "r") as f:
    text = f.read()

# We need to remove org.jetbrains.kotlin.com.intellij.util.io caches
# We'll just run gradle clean again and make sure we build after
