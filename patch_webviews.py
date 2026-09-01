import os

files_to_patch = {
    'app/src/main/java/com/fire/mangareader/activity/MainActivity.java': [
        ('android.webkit.WebView webView = new android.webkit.WebView(this);', 'android.webkit.WebView webView = new android.webkit.WebView(this); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);')
    ],
    'app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java': [
        ('scraperWebView = findViewById(R.id.scraperWebView);', 'scraperWebView = findViewById(R.id.scraperWebView); if(scraperWebView != null) scraperWebView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);')
    ],
    'app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java': [
        ('android.webkit.WebView webView = new android.webkit.WebView(this);', 'android.webkit.WebView webView = new android.webkit.WebView(this); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);')
    ],
    'app/src/main/java/com/fire/mangareader/utils/MangaDownloader.java': [
        ('WebView webView = new WebView(context);', 'WebView webView = new WebView(context); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);')
    ],
    'app/src/main/java/com/fire/mangareader/network/CloudflareBypassDialog.java': [
        ('webView = new WebView(getContext());', 'webView = new WebView(getContext()); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);')
    ]
}

for filepath, patches in files_to_patch.items():
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        for search, replace in patches:
            if search in content and replace not in content:
                content = content.replace(search, replace)
                
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Patched {filepath}")
