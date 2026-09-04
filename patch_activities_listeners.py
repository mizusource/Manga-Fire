import re

def update_activity(file_path):
    with open(file_path, "r") as f:
        text = f.read()

    pattern = r'(btnSend\.setOnClickListener\(v -> send.*?;\))'
    replacement = r'''\1
        
        android.view.View btnAttach = findViewById(R.id.btnAttach);
        if (btnAttach != null) {
            btnAttach.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "قريباً: إضافة صور ومرفقات!", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
'''
    new_text = re.sub(pattern, replacement, text)

    with open(file_path, "w") as f:
        f.write(new_text)

update_activity("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsActivity.java")
update_activity("app/src/main/java/com/fire/mangareader/presentation/activity/RepliesActivity.java")
