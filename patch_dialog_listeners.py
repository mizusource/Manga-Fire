import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "r") as f:
    text = f.read()

# Find onViewCreated and add attach listener
pattern = r'(btnSendComment\.setOnClickListener\(v -> sendComment\(\)\);)'

replacement = r'''\1
        
        View btnAttach = view.findViewById(R.id.btnAttach);
        if (btnAttach != null) {
            btnAttach.setOnClickListener(v -> {
                Toast.makeText(getContext(), "قريباً: إضافة صور ومرفقات للتعليق!", Toast.LENGTH_SHORT).show();
            });
        }
'''

new_text = re.sub(pattern, replacement, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "w") as f:
    f.write(new_text)
