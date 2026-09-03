import re

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "r") as f:
    content = f.read()

content = content.replace("public TextView tvUsername, tvDate, tvCommentText, tvLikeCount;", 
                          "public TextView tvUsername, tvDate, tvCommentText, tvLikeCount, tvReplyCount;")

content = content.replace("tvLikeCount = itemView.findViewById(R.id.tvLikeCount);",
                          "tvLikeCount = itemView.findViewById(R.id.tvLikeCount);\n            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);")

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "w") as f:
    f.write(content)
