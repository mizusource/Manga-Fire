import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MainActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add Handler and Runnable
fields = """    private android.os.Handler sliderHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpHeroBanner != null && vpHeroBanner.getAdapter() != null && vpHeroBanner.getAdapter().getItemCount() > 0) {
                int currentItem = vpHeroBanner.getCurrentItem();
                int nextItem = (currentItem + 1) % vpHeroBanner.getAdapter().getItemCount();
                vpHeroBanner.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3500);
            }
        }
    };"""

if 'sliderHandler' not in content:
    content = content.replace('private ViewPager2 vpHeroBanner;', 'private ViewPager2 vpHeroBanner;\n' + fields)

pause_resume = """
    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
"""

if 'protected void onResume()' not in content:
    content = content.replace('protected void onCreate(Bundle savedInstanceState) {', pause_resume + '\n    @Override\n    protected void onCreate(Bundle savedInstanceState) {')

# Enable auto-scroll upon adapter set
callback = """                            vpHeroBanner.setAdapter(bannerAdapter);
                            sliderHandler.removeCallbacks(sliderRunnable);
                            if (bannerList.size() > 1) {
                                sliderHandler.postDelayed(sliderRunnable, 3500);
                            }"""
content = content.replace('vpHeroBanner.setAdapter(bannerAdapter);', callback)

with open(filepath, 'w') as f:
    f.write(content)
print("Added AutoScroll to MainActivity")
