with open("app/build.gradle", "r") as f:
    content = f.read()

shimmer = "    implementation 'com.facebook.shimmer:shimmer:0.5.0'\n"
if "com.facebook.shimmer" not in content:
    content = content.replace("dependencies {", "dependencies {\n" + shimmer)
    
with open("app/build.gradle", "w") as f:
    f.write(content)
