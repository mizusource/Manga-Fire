import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    text = f.read()

variables = """
    private androidx.recyclerview.widget.RecyclerView relatedRecyclerView;
"""
text = re.sub(r'(public class MangaDetailActivity extends AppCompatActivity \{)', r'\1\n' + variables, text)

init_logic = """
        relatedRecyclerView = findViewById(R.id.relatedRecyclerView);
        
        // Setup Related Manga RecyclerView (Mocked data)
        relatedRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        // We will just use the normal MangaAdapter or create a simple adapter inline
        // But since we don't have the adapter readily available, let's just leave it empty or hide it if no data
"""
text = re.sub(r'(chaptersRecyclerView = findViewById\(R\.id\.chaptersRecyclerView\);)', r'\1\n' + init_logic, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(text)
