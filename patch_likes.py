with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

old_code = """        holder.tvCommentText.setText(comment.text != null ? comment.text : "");
        holder.tvLikeCount.setText(String.valueOf(comment.likes));
    }"""

new_code = """        holder.tvCommentText.setText(comment.text != null ? comment.text : "");
        holder.tvLikeCount.setText(String.valueOf(comment.likes));
        holder.btnLike.setOnClickListener(v -> android.widget.Toast.makeText(context, "ميزة الإعجاب والردود قيد التطوير وسيتم تفعيلها قريباً", android.widget.Toast.LENGTH_SHORT).show());
    }"""

if old_code in content:
    with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
        f.write(content.replace(old_code, new_code))
    print("Patched CommentAdapter.java")
else:
    print("Could not find the target code in CommentAdapter.java")
