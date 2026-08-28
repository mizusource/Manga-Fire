import re

for filename in ['app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java']:
    with open(filename, 'r') as f:
        content = f.read()
    
    # Check if etComment is declared as etCommentInput
    if 'etCommentInput =' in content:
        content = content.replace('etComment.setText', 'etCommentInput.setText')
        content = content.replace('etComment.setSelection', 'etCommentInput.setSelection')
        content = content.replace('etComment.getText', 'etCommentInput.getText')
        content = content.replace('etComment.requestFocus', 'etCommentInput.requestFocus')
        content = content.replace('imm.showSoftInput(etComment', 'imm.showSoftInput(etCommentInput')

    with open(filename, 'w') as f:
        f.write(content)
