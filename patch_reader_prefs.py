import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Setup default layout manager logic during initialization
init_pattern = r'layoutManager = new LinearLayoutManager\(this, LinearLayoutManager\.VERTICAL, false\);.*?recyclerView\.setLayoutManager\(layoutManager\);'

new_init = """
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int readingMode = prefs.getInt("reading_mode", 0); // 0=Vert, 1=LTR, 2=RTL
        if (readingMode == 1) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            snapHelper.attachToRecyclerView(recyclerView);
        } else if (readingMode == 2) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            snapHelper.attachToRecyclerView(recyclerView);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        recyclerView.setLayoutManager(layoutManager);
"""
content = re.sub(init_pattern, new_init, content, flags=re.DOTALL)

# Add saving logic to the AlertDialog
save_pattern = r'if \(which == 0\) \{ // Vertical'
new_save = """
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("reading_mode", which).apply();
                    if (which == 0) { // Vertical"""
content = content.replace(save_pattern, new_save)

with open(filepath, 'w') as f:
    f.write(content)
print("ChapterReaderActivity prefs patched.")
