import re

with open("settings.gradle.kts", "r") as f:
    text = f.read()

if "jitpack.io" not in text:
    text = text.replace("mavenCentral()", "mavenCentral()\n        maven { url = uri(\"https://jitpack.io\") }")

with open("settings.gradle.kts", "w") as f:
    f.write(text)
