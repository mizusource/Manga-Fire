with open('build.gradle', 'r') as f:
    root_gradle = f.read()
if 'com.google.gms:google-services' not in root_gradle:
    root_gradle = root_gradle.replace(
        "classpath 'com.android.tools.build:gradle:9.1.1'",
        "classpath 'com.android.tools.build:gradle:9.1.1'\n        classpath 'com.google.gms:google-services:4.4.0'"
    )
    with open('build.gradle', 'w') as f:
        f.write(root_gradle)

with open('app/build.gradle', 'r') as f:
    app_gradle = f.read()
if 'com.google.gms.google-services' not in app_gradle:
    app_gradle = app_gradle.replace(
        "id 'com.android.application'",
        "id 'com.android.application'\n    id 'com.google.gms.google-services'"
    )
    app_gradle = app_gradle.replace(
        "dependencies {",
        "dependencies {\n    implementation platform('com.google.firebase:firebase-bom:32.7.1')\n    implementation 'com.google.firebase:firebase-messaging'"
    )
    with open('app/build.gradle', 'w') as f:
        f.write(app_gradle)
