import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "r") as f:
    text = f.read()

pattern = r'(cbSpoiler = view\.findViewById\(R\.id\.cbSpoiler\);)'
replacement = r'\1\n        if (cbSpoiler != null) cbSpoiler.setVisibility(View.VISIBLE);'
new_text = re.sub(pattern, replacement, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "w") as f:
    f.write(new_text)
