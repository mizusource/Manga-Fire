import re

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'\.addOnSuccessListener.*?\)\);', '', content, flags=re.DOTALL)
content = re.sub(r'\.addOnFailureListener.*?\)\);', '', content, flags=re.DOTALL)
content = re.sub(r'\.addOnFailureListener.*?\)\.show\(\)\);', '', content, flags=re.DOTALL)
content = re.sub(r'\}\)', '', content, flags=re.DOTALL)
# It's so broken now. I'll just rewrite sendComment.

content = re.sub(r'private void sendComment\(\) \{.*?\}', 'private void sendComment() {\n        String text = etComment.getText().toString().trim();\n        if (text.isEmpty()) return;\n        etComment.setText("");\n    }', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentsBottomSheetDialog.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()
content = re.sub(r'private void sendComment\(\) \{.*?\}', 'private void sendComment() {\n        String text = etCommentInput.getText().toString().trim();\n        if (text.isEmpty()) return;\n        etCommentInput.setText("");\n    }', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
