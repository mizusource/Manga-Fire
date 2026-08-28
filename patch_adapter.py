with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import re

# Add OnReplyClickListener
content = content.replace('public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {',
'''public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }
    private OnReplyClickListener replyListener;
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyListener = listener;
    }''')

old_reply = '''        holder.btnReply.setOnClickListener(v -> {
            Toast.makeText(context, "ميزة الردود قادمة قريباً", Toast.LENGTH_SHORT).show();
        });'''

new_reply = '''        holder.btnReply.setOnClickListener(v -> {
            if (replyListener != null) {
                replyListener.onReplyClick(comment);
            } else {
                Toast.makeText(context, "الرد: @" + (comment.username != null ? comment.username : comment.user_name), Toast.LENGTH_SHORT).show();
            }
        });'''
content = content.replace(old_reply, new_reply)

old_report = '''                if (item.getTitle().equals("إبلاغ عن التعليق")) {
                    Toast.makeText(context, "تم الإبلاغ بنجاح", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("حذف التعليق")) {'''

new_report = '''                if (item.getTitle().equals("إبلاغ عن التعليق")) {
                    if (comment.id != null) {
                        java.util.Map<String, Object> report = new java.util.HashMap<>();
                        report.put("commentId", comment.id);
                        report.put("commentText", comment.text);
                        report.put("reportedBy", user != null ? user.getUid() : "anonymous");
                        FirebaseFirestore.getInstance().collection("reports").add(report);
                    }
                    Toast.makeText(context, "تم رفع البلاغ للإدارة لمراجعته، شكراً لك.", Toast.LENGTH_LONG).show();
                } else if (item.getTitle().equals("حذف التعليق")) {'''
content = content.replace(old_report, new_report)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
