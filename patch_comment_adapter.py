with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import re
bind_logic = '''    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.username.setText(comment.username != null ? comment.username : comment.user_name);
        holder.commentText.setText(comment.text);
        
        holder.date.setText(com.fire.mangareader.utils.CommentUtils.getRelativeTime(comment.timestamp));
        
        holder.tvLikeCount.setText(String.valueOf(comment.likes));
        holder.tvReplyCount.setText("رد");

        // Load avatar if available
        if (comment.user_avatar != null && !comment.user_avatar.isEmpty()) {
            holder.avatar.setColorFilter(null);
            com.bumptech.glide.Glide.with(context).load(comment.user_avatar).circleCrop().into(holder.avatar);
        } else {
            holder.avatar.setImageResource(com.fire.mangareader.R.drawable.ic_person);
            holder.avatar.setColorFilter(android.graphics.Color.GRAY);
        }

        if (comment.isSpoiler || comment.is_spoiler) {'''

content = re.sub(r'    public void onBindViewHolder\(\@NonNull ViewHolder holder, int position\) \{.*?if \(comment\.isSpoiler \|\| comment\.is_spoiler\) \{', bind_logic, content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
