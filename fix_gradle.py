import re

# 1. Fix build.gradle
with open('build.gradle', 'r') as f:
    content = f.read()

content = content.replace("classpath 'com.google.gms:google-services:4.4.1'", "")

with open('build.gradle', 'w') as f:
    f.write(content)

# 2. Fix app/build.gradle
with open('app/build.gradle', 'r') as f:
    content = f.read()

content = content.replace("id 'com.google.gms.google-services'", "")

firebase_pattern = re.compile(r"// Firebase\s*implementation platform\('com\.google\.firebase:firebase-bom:32\.7\.0'\)\s*implementation 'com\.google\.firebase:firebase-auth'\s*implementation 'com\.google\.firebase:firebase-firestore'\s*implementation 'com\.google\.firebase:firebase-database'\s*implementation 'com\.google\.firebase:firebase-storage'", re.MULTILINE)
content = firebase_pattern.sub('', content)

# Google Sign In - we might also want to remove play-services-auth since we are using Supabase?
# Supabase email/password was implemented. The user didn't say to remove Google sign in, but it usually requires google-services.
# However, play-services-auth doesn't strictly break if google-services is absent as long as we don't call it. Let's leave it for now.

with open('app/build.gradle', 'w') as f:
    f.write(content)
