import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# We will extract imports, fields, and the parts after onCreate.
# Let's just do a clean replacement of everything between `protected void onCreate(Bundle savedInstanceState) {`
# and `rvLatestUpdates.setLayoutManager(new GridLayoutManager(this, 3));`

start_marker = "protected void onCreate(Bundle savedInstanceState) {"
end_marker = "rvLatestUpdates.setLayoutManager(new GridLayoutManager(this, 3));"

start_idx = content.find(start_marker)
end_idx = content.find(end_marker)

if start_idx != -1 and end_idx != -1:
    before = content[:start_idx]
    after = content[end_idx:]
    
    new_on_create = """protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        com.fire.mangareader.utils.DisplayUtils.optimizeRefreshRate(this);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();

        com.fire.mangareader.network.MangaScraper.globalCookies = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("cloudflare_cookies", "");
        BASE_URL = com.fire.mangareader.network.SourceManager.getActiveSource(this);
        com.fire.mangareader.network.MangaScraper.BASE_URL = BASE_URL;

        rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        vpHeroBanner = findViewById(R.id.vpHeroBanner);
        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        mainShimmerView = findViewById(R.id.mainShimmerView);
        btnToggleView = findViewById(R.id.btnToggleView);

        android.widget.ImageView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, SearchActivity.class)));

        """
    
    with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
        f.write(before + new_on_create + after)

