import re

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "r") as f:
    content = f.read()

# I want to update CommentAdapter to populate replies_count.
# The user wants likes, dislikes, replies, spoilers using the CommentDto conceptually. 
# We already have likes and spoilers. Let's add dislikes and replies if they exist, or just replies.
# Currently item_comment has btnReply and tvReplyCount.
bind_reply_count = """
        holder.tvReplyCount.setText(comment.replies_count > 0 ? comment.replies_count + " ردود" : "رد");
"""
content = content.replace('holder.tvLikeCount.setText(String.valueOf(comment.likes));', 
    'holder.tvLikeCount.setText(String.valueOf(comment.likes));\n' + bind_reply_count)

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "w") as f:
    f.write(content)

