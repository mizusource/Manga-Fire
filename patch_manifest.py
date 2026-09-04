import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    text = f.read()

# Make sure INTERNET permission is there
if "android.permission.INTERNET" not in text:
    text = text.replace("<application", "<uses-permission android:name=\"android.permission.INTERNET\" />\n    <application")

# Make sure ACCESS_NETWORK_STATE permission is there
if "android.permission.ACCESS_NETWORK_STATE" not in text:
    text = text.replace("<application", "<uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n    <application")

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(text)
