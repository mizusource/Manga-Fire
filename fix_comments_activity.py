with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()

if 'comment.created_at = obj.optString("created_at");' not in content:
    content = content.replace('comment.text = obj.optString("text");', 
                              'comment.text = obj.optString("text");\n                            comment.created_at = obj.optString("created_at");\n                            comment.id = obj.optString("id");')
    with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
        f.write(content)
