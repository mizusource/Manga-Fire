import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/SearchActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

target = """updateSourceStatusText();"""

replacement = """updateSourceStatusText();
        
        com.google.android.material.chip.Chip chipFilter = findViewById(R.id.chipFilter);
        if (chipFilter != null) {
            chipFilter.setOnClickListener(v -> {
                String currentQuery = searchView.getQuery() != null ? searchView.getQuery().toString() : "";
                SearchFilterDialog.show(SearchActivity.this, currentQuery, request -> {
                    Toast.makeText(SearchActivity.this, "تم تطبيق الفلاتر: " + request.getGenres().size() + " تصنيفات", Toast.LENGTH_SHORT).show();
                    // In a real scenario, this would format the advanced search URL and pass it to MangaScraper.
                    if (!currentQuery.isEmpty()) {
                        performSearch(currentQuery);
                    }
                });
            });
        }"""

content = content.replace(target, replacement, 1) # Replace only the first occurrence in onCreate

with open(filepath, 'w') as f:
    f.write(content)
print("Patched SearchActivity.java")
