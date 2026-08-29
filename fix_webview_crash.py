import os

for filename in ['app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java']:
    with open(filename, 'r') as f:
        content = f.read()
    
    old_error = "if(!request.isForMainFrame()) return; runOnUiThread"
    new_error = "if(!request.isForMainFrame()) return; isProcessed[0] = true; runOnUiThread"
    
    if old_error in content:
        content = content.replace(old_error, new_error)
        with open(filename, 'w') as f:
            f.write(content)
        print(f"Fixed {filename}")
    else:
        print(f"Not found in {filename}")
