import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/fragment/HomeFragment.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add Handler and Runnable
fields = """    private android.os.Handler sliderHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (heroViewPager != null && heroAdapter != null && heroAdapter.getItemCount() > 0) {
                int currentItem = heroViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % heroAdapter.getItemCount();
                heroViewPager.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };"""

if 'sliderHandler' not in content:
    content = content.replace('private List<Manga> heroList;', 'private List<Manga> heroList;\n' + fields)

pause_resume = """
    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
"""

if 'public void onResume()' not in content:
    content = content.replace('private void loadMangas() {', pause_resume + '\n    private void loadMangas() {')

callback = """                
                heroAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                
                sliderHandler.removeCallbacks(sliderRunnable);
                if (heroList.size() > 1) {
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
"""
content = content.replace('heroAdapter.notifyDataSetChanged();\n                adapter.notifyDataSetChanged();', callback)

with open(filepath, 'w') as f:
    f.write(content)
print("Added AutoScroll")
