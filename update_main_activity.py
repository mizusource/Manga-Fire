import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

imports = """
import androidx.viewpager2.widget.ViewPager2;
import com.fire.mangareader.adapter.HeroBannerAdapter;
"""
content = content.replace('import com.fire.mangareader.adapter.MangaAdapter;', 'import com.fire.mangareader.adapter.MangaAdapter;\n' + imports)

vars_replacement = """
    private RecyclerView rvLatestUpdates;
    private ViewPager2 vpHeroBanner;
"""
content = content.replace('private RecyclerView rvLatestUpdates;', vars_replacement)

init_replacement = """
        rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        vpHeroBanner = findViewById(R.id.vpHeroBanner);
"""
content = content.replace('rvLatestUpdates = findViewById(R.id.rvLatestUpdates);', init_replacement)

parse_html_replacement = """
                    runOnUiThread(() -> {
                        mainProgressBar.setVisibility(View.GONE);
                        swipeRefreshMain.setRefreshing(false);
                        
                        // تخصيص المانجات المميزة (Hero Banner) من أول 5 مانجات
                        if (fetchedList.size() >= 5) {
                            java.util.List<Manga> heroList = new ArrayList<>(fetchedList.subList(0, 5));
                            HeroBannerAdapter heroAdapter = new HeroBannerAdapter(MainActivity.this, heroList);
                            vpHeroBanner.setAdapter(heroAdapter);
                            
                            // إزالة المانجات التي ظهرت في البانر من قائمة التحديثات العادية لتجنب التكرار
                            fetchedList.subList(0, 5).clear();
                        }
                        
                        mangaList.clear();
                        mangaList.addAll(fetchedList);
                        adapter.notifyDataSetChanged();
                    });
"""
content = re.sub(r'runOnUiThread\(\(\) -> \{\s*mainProgressBar.setVisibility\(View.GONE\);\s*swipeRefreshMain.setRefreshing\(false\);\s*mangaList.clear\(\);\s*mangaList.addAll\(fetchedList\);\s*adapter.notifyDataSetChanged\(\);\s*\}\);', parse_html_replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
