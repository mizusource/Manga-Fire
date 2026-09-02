import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/fragment/HomeFragment.java'
with open(filepath, 'r') as f:
    content = f.read()

# Imports to add
imports = """import androidx.viewpager2.widget.ViewPager2;
import com.fire.mangareader.presentation.adapter.HeroBannerAdapter;"""

if 'import androidx.viewpager2.widget.ViewPager2;' not in content:
    content = content.replace('import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;', 'import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;\n' + imports)

# Fields to add
fields = """    private ViewPager2 heroViewPager;
    private View heroContainer;
    private HeroBannerAdapter heroAdapter;
    private List<Manga> heroList;"""

if 'private ViewPager2 heroViewPager;' not in content:
    content = content.replace('private SwipeRefreshLayout swipeRefresh;', fields + '\n    private SwipeRefreshLayout swipeRefresh;')

# Init logic
init_logic = """        heroViewPager = view.findViewById(R.id.heroViewPager);
        heroContainer = view.findViewById(R.id.heroContainer);

        heroList = new ArrayList<>();
        heroAdapter = new HeroBannerAdapter(getContext(), heroList);
        heroViewPager.setAdapter(heroAdapter);"""

if 'heroViewPager = view.findViewById(R.id.heroViewPager);' not in content:
    content = content.replace('swipeRefresh = view.findViewById(R.id.swipeRefresh);', init_logic + '\n        swipeRefresh = view.findViewById(R.id.swipeRefresh);')

# Data loading logic
data_logic = """
            @Override
            public void onSuccess(List<Manga> mangas) {
                mangaList.clear();
                heroList.clear();
                
                if (mangas.size() > 5) {
                    heroList.addAll(mangas.subList(0, 5));
                    mangaList.addAll(mangas.subList(5, mangas.size()));
                    heroContainer.setVisibility(View.VISIBLE);
                } else {
                    mangaList.addAll(mangas);
                    heroContainer.setVisibility(View.GONE);
                }
                
                heroAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
"""
old_onSuccess = """            @Override
            public void onSuccess(List<Manga> mangas) {
                mangaList.clear();
                mangaList.addAll(mangas);
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }"""

content = content.replace(old_onSuccess, data_logic)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated HomeFragment.java")
