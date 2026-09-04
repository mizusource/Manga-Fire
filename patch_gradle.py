import re

with open("app/build.gradle.kts", "r") as f:
    text = f.read()

# Add MPAndroidChart repository if not there
if "jitpack.io" not in text:
    pass # Actually, jitpack is usually in settings.gradle.kts. Let's check settings.gradle.kts later.

# Add dependencies
deps = """
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.l4digital:FastScroll:3.0.0") // Or similar if available, but let's just use standard fast scroll properties in XML
"""
if "MPAndroidChart" not in text:
    text = text.replace('implementation("org.jsoup:jsoup:1.17.2")', 'implementation("org.jsoup:jsoup:1.17.2")' + deps)

with open("app/build.gradle.kts", "w") as f:
    f.write(text)
