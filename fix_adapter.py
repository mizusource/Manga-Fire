with open('app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java', 'r') as f:
    content = f.read()

limit_field = """
    private List<String> downloadedChapters = new ArrayList<>();
    private int displayLimit = 50;

    public void setDisplayLimit(int limit) {
        this.displayLimit = limit;
        notifyDataSetChanged();
    }
"""

content = content.replace('private List<String> downloadedChapters = new ArrayList<>();', limit_field.strip())

count_method = """
    @Override
    public int getItemCount() {
        return chapters != null ? Math.min(chapters.size(), displayLimit) : 0;
    }
"""
content = content.replace("""    @Override
    public int getItemCount() {
        return chapters != null ? chapters.size() : 0;
    }""", count_method.strip())

with open('app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java', 'w') as f:
    f.write(content)
