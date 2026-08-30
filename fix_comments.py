with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import_str = 'import java.text.SimpleDateFormat;\nimport java.util.Date;\nimport java.util.TimeZone;\nimport java.text.ParseException;\n'
if 'java.text.SimpleDateFormat' not in content:
    content = content.replace('import java.util.List;', import_str + 'import java.util.List;')

# update viewholder
if 'tvDate = itemView.findViewById(R.id.tvDate);' not in content:
    content = content.replace('tvUsername = itemView.findViewById(R.id.tvUsername);', 
                            'tvUsername = itemView.findViewById(R.id.tvUsername);\n            tvDate = itemView.findViewById(R.id.tvDate);')
    content = content.replace('public TextView tvUsername, tvCommentText, tvLikeCount;', 
                            'public TextView tvUsername, tvDate, tvCommentText, tvLikeCount;')

# update bind
time_ago_func = """
        // Parse time and calculate time ago
        if (comment.created_at != null && !comment.created_at.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(comment.created_at);
                if (date != null) {
                    long timeInMillis = date.getTime();
                    long now = System.currentTimeMillis();
                    long diff = now - timeInMillis;
                    
                    String timeAgo;
                    if (diff < 60000) {
                        timeAgo = "الآن";
                    } else if (diff < 3600000) {
                        long mins = diff / 60000;
                        timeAgo = "منذ " + mins + " دقيقة";
                    } else if (diff < 86400000) {
                        long hours = diff / 3600000;
                        timeAgo = "منذ " + hours + " ساعة";
                    } else {
                        long days = diff / 86400000;
                        timeAgo = "منذ " + days + " يوم";
                    }
                    holder.tvDate.setText(timeAgo);
                }
            } catch (ParseException e) {
                holder.tvDate.setText("");
            }
        } else {
            holder.tvDate.setText("");
        }
"""
if 'SimpleDateFormat sdf = new SimpleDateFormat' not in content:
    content = content.replace('holder.tvCommentText.setText(comment.text != null ? comment.text : "");',
                            'holder.tvCommentText.setText(comment.text != null ? comment.text : "");\n' + time_ago_func)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
