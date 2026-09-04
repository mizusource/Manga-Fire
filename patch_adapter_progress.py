import re

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/ChapterAdapter.java", "r") as f:
    text = f.read()

# Add method and field
if "private java.util.Map<String, Integer> downloadProgresses" not in text:
    field_code = """
    private java.util.Map<String, Integer> downloadProgresses = new java.util.HashMap<>();
    
    public void setDownloadProgresses(java.util.Map<String, Integer> progresses) {
        this.downloadProgresses = progresses;
        notifyDataSetDataSetChanged(); // Simple refresh for now, or could use DiffUtil
    }
    
    private void notifyDataSetDataSetChanged() {
        notifyDataSetChanged();
    }
"""
    class_pattern = r'public class ChapterAdapter extends RecyclerView\.Adapter<ChapterAdapter\.ChapterViewHolder> \{'
    text = text.replace("public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {", "public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {" + field_code)

# Bind progress
bind_pattern = r'holder\.downloadProgress\.setVisibility\(View\.GONE\);[\s]*if \(downloadedChapters \!= null && downloadedChapters\.contains\(chapter\.getUrl\(\)\)\) \{'
bind_code = """
        if (downloadProgresses != null && downloadProgresses.containsKey(chapter.getUrl())) {
            holder.downloadProgress.setVisibility(View.VISIBLE);
            holder.downloadProgress.setProgress(downloadProgresses.get(chapter.getUrl()));
            holder.ivDownload.setVisibility(View.GONE);
        } else {
            holder.downloadProgress.setVisibility(View.GONE);
        }

        if (downloadedChapters != null && downloadedChapters.contains(chapter.getUrl())) {
"""
text = re.sub(bind_pattern, bind_code, text)

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/ChapterAdapter.java", "w") as f:
    f.write(text)
