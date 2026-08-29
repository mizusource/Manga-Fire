import re

def fix_cloudflare(file_path):
    with open(file_path, 'r') as f:
        content = f.read()

    # Find the cloudflare block
    old_cf = """                                    webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); 
                                    webView.setAlpha(0.01f); 
                                    Toast.makeText"""
                                    
    new_cf = """                                    webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT, 
                                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT)); 
                                    webView.setAlpha(1.0f); 
                                    Toast.makeText"""
    
    if old_cf in content:
        content = content.replace(old_cf, new_cf)
        content = content.replace('"يرجى الانتظار لتخطي حماية Cloudflare..."', '"يرجى حل اختبار التحقق (Cloudflare) للمتابعة"')
        with open(file_path, 'w') as f:
            f.write(content)
        print("Fixed", file_path)
    else:
        print("Not found in", file_path)

fix_cloudflare('app/src/main/java/com/fire/mangareader/activity/MainActivity.java')
fix_cloudflare('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java')
