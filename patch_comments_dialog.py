import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add CheckBox import and declaration
content = content.replace('import android.widget.EditText;', 'import android.widget.EditText;\nimport android.widget.CheckBox;')
content = content.replace('private android.widget.ImageView btnSendComment;', 'private android.widget.ImageView btnSendComment;\n    private CheckBox cbSpoiler;')
content = content.replace('btnSendComment = view.findViewById(R.id.btnSendComment);', 'btnSendComment = view.findViewById(R.id.btnSendComment);\n        cbSpoiler = view.findViewById(R.id.cbSpoiler);')

# Update sendComment()
old_send = """    private void sendComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;
        
        btnSendComment.setEnabled(false);
        String userName = prefManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "مستخدم";
        }
        
        supabaseManager.addComment(mangaUrl, text, false, userName, new SupabaseManager.AuthCallback() {"""

new_send = """    private void sendComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;
        
        btnSendComment.setEnabled(false);
        String userName = prefManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "مستخدم";
        }
        
        boolean isSpoiler = cbSpoiler != null && cbSpoiler.isChecked();
        
        supabaseManager.addComment(mangaUrl, text, isSpoiler, userName, new SupabaseManager.AuthCallback() {"""

content = content.replace(old_send, new_send)

# Reset checkbox on success
old_success = """                btnSendComment.setEnabled(true);
                etCommentInput.setText("");
                loadComments();"""

new_success = """                btnSendComment.setEnabled(true);
                etCommentInput.setText("");
                if (cbSpoiler != null) cbSpoiler.setChecked(false);
                loadComments();"""
content = content.replace(old_success, new_success)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched CommentsBottomSheetDialog.java")
