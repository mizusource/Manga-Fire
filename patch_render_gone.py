import re
import os

files_to_patch = [
    'app/src/main/java/com/fire/mangareader/activity/MainActivity.java',
    'app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java',
    'app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java',
    'app/src/main/java/com/fire/mangareader/utils/MangaDownloader.java',
    'app/src/main/java/com/fire/mangareader/network/CloudflareBypassDialog.java'
]

render_gone_code = """
            @Override
            public boolean onRenderProcessGone(android.webkit.WebView view, android.webkit.RenderProcessGoneDetail detail) {
                if (view != null) {
                    view.destroy();
                }
                return true;
            }
"""

for filepath in files_to_patch:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
            
        if "onRenderProcessGone" not in content and "WebViewClient" in content:
            # Insert right after `new WebViewClient() {`
            content = re.sub(r'(new\s+(?:android\.webkit\.)?WebViewClient\s*\(\)\s*\{)', r'\1' + render_gone_code, content, count=0)
            
            with open(filepath, 'w') as f:
                f.write(content)
            print(f"Patched {filepath}")
