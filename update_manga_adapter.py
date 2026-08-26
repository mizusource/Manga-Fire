with open("app/src/main/java/com/fire/mangareader/adapter/MangaAdapter.java", "r") as f:
    content = f.read()

# Add isListView field
content = content.replace("private List<Manga> mangaList;", "private List<Manga> mangaList;\n    private boolean isListView = false;\n\n    public void setListView(boolean isListView) {\n        this.isListView = isListView;\n        notifyDataSetChanged();\n    }")

# update onCreateViewHolder
old_oncreate = """        View view = LayoutInflater.from(context).inflate(R.layout.item_manga_grid, parent, false);
        return new MangaViewHolder(view);"""

new_oncreate = """        int layoutId = isListView ? R.layout.item_manga_list : R.layout.item_manga_grid;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MangaViewHolder(view);"""
content = content.replace(old_oncreate, new_oncreate)

with open("app/src/main/java/com/fire/mangareader/adapter/MangaAdapter.java", "w") as f:
    f.write(content)
