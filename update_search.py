import re

with open('app/src/main/java/com/fire/mangareader/activity/SearchActivity.java', 'r') as f:
    content = f.read()

imports = """
import com.fire.mangareader.network.CloudflareBypassDialog;
"""
content = content.replace('import java.util.List;', 'import java.util.List;\n' + imports)

# We will add a method that wraps searchAllSources and checks for error. If error contains 403 or 503, show bypass dialog
search_all_replacement = """        if (chipGlobalSearch.isChecked()) {
            MangaScraper.searchAllSources(query, new MangaScraper.ScrapingCallback() {
                @Override
                public void onSuccess(List<Manga> mangas) {
                    progressBar.setVisibility(View.GONE);
                    if (mangas != null && !mangas.isEmpty()) {
                        searchResults.addAll(mangas);
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    if (errorMessage.contains("403") || errorMessage.contains("503") || errorMessage.contains("Cloudflare")) {
                        new CloudflareBypassDialog(SearchActivity.this, MangaScraper.BASE_URL, new CloudflareBypassDialog.BypassCallback() {
                            @Override
                            public void onSuccess(String cookies, String userAgent) {
                                performSearch(query); // Retry search
                            }
                            @Override
                            public void onFailed() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SearchActivity.this, "فشل تجاوز الحماية", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            MangaScraper.searchManga(query, new MangaScraper.ScrapingCallback() {
                @Override
                public void onSuccess(List<Manga> mangas) {
                    progressBar.setVisibility(View.GONE);
                    if (mangas != null && !mangas.isEmpty()) {
                        searchResults.addAll(mangas);
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    if (errorMessage.contains("403") || errorMessage.contains("503") || errorMessage.contains("Cloudflare")) {
                        new CloudflareBypassDialog(SearchActivity.this, MangaScraper.BASE_URL, new CloudflareBypassDialog.BypassCallback() {
                            @Override
                            public void onSuccess(String cookies, String userAgent) {
                                performSearch(query); // Retry search
                            }
                            @Override
                            public void onFailed() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SearchActivity.this, "فشل تجاوز الحماية", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }"""

# I need to match the if (chipGlobalSearch.isChecked()) block up to the end of performSearch
# Since I know the exact code, I will use regex or find and replace from "if (chipGlobalSearch.isChecked()) {" to the end of performSearch method

content = re.sub(r'if \(chipGlobalSearch\.isChecked\(\)\) \{.*\}\n\s*\}\n', search_all_replacement + '\n    }\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/SearchActivity.java', 'w') as f:
    f.write(content)
