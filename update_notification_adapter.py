import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/adapter/NotificationAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

import_statements = """import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;"""
content = content.replace('import java.util.List;', 'import java.util.List;\n' + import_statements)

bind_logic = """        holder.tvMessage.setText(model.message != null ? model.message : "");
        
        if (model.createdAt != null && !model.createdAt.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(model.createdAt);
                if (date != null) {
                    holder.tvDate.setText(com.fire.mangareader.util.TimeUtils.getTimeAgo(date.getTime()));
                }
            } catch (ParseException e) {
                holder.tvDate.setText("");
            }
        } else {
            holder.tvDate.setText("");
        }

        holder.itemView.setBackgroundColor(model.isRead ? Color.TRANSPARENT : Color.parseColor("#1A00E5FF"));"""
content = content.replace('holder.tvMessage.setText(model.message != null ? model.message : "");\n        holder.itemView.setBackgroundColor(model.isRead ? Color.TRANSPARENT : Color.parseColor("#1A00E5FF"));', bind_logic)

viewholder_logic = """    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSender, tvMessage, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }"""
content = re.sub(r'public static class ViewHolder extends RecyclerView\.ViewHolder \{.*?\}', viewholder_logic, content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated NotificationAdapter.java")
