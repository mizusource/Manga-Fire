import re
with open("app/build.gradle.kts", "r") as f:
    content = f.read()

if "org.jsoup:jsoup" not in content:
    content = content.replace("dependencies {", "dependencies {\n    implementation(\"org.jsoup:jsoup:1.17.2\")")
    with open("app/build.gradle.kts", "w") as f:
        f.write(content)
        print("Injected jsoup to build.gradle.kts")
else:
    print("Jsoup already exists")
