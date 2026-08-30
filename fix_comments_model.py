with open('app/src/main/java/com/fire/mangareader/model/Comment.java', 'r') as f:
    content = f.read()

if 'public String created_at;' not in content:
    content = content.replace('public long timestamp;', 'public long timestamp;\n    public String created_at;')
    with open('app/src/main/java/com/fire/mangareader/model/Comment.java', 'w') as f:
        f.write(content)
