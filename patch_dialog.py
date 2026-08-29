with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

import re

old = '''        adapter = new CommentAdapter(getContext(), commentList);
        adapter.setMangaDocId(mangaUrl.replaceAll("[^a-zA-Z0-9]", "_"));'''

new = '''        adapter = new CommentAdapter(getContext(), commentList);
        adapter.setMangaDocId(mangaUrl.replaceAll("[^a-zA-Z0-9]", "_"));
        adapter.setOnReplyClickListener(comment -> {
            String username = comment.username != null ? comment.username : comment.user_name;
            etComment.setText("@" + username + " ");
            etComment.setSelection(etComment.getText().length());
            etComment.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });'''
content = content.replace(old, new)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
