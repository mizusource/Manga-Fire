with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

content = content.replace('ImageView ivLike, btnMore;', 'ImageView ivAvatar, ivLike, btnMore;')
content = content.replace('super(itemView);', 'super(itemView);\n            ivAvatar = itemView.findViewById(com.fire.mangareader.R.id.ivAvatar);')
content = content.replace('holder.avatar', 'holder.ivAvatar')

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
