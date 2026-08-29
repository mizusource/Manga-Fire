import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

new_onPageFinished = """
            boolean[] isProcessed = {false};
            public void onPageFinished(android.webkit.WebView view, String url) {
                if (isProcessed[0]) return;
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                if (cookies != null) { com.fire.mangareader.network.MangaScraper.globalCookies = cookies; getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("cloudflare_cookies", cookies).apply(); }
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { 
                    try {
                        view.evaluateJavascript("(function() { try { if (document.getElementById(\\"manga-chapters-holder\\") && document.getElementById(\\"manga-chapters-holder\\").innerHTML.trim() === \\"\\") { var req = new XMLHttpRequest(); req.open(\\"POST\\", window.location.href + (window.location.href.endsWith(\\"/\\") ? \\"\\" : \\"/\\") + \\"ajax/chapters/\\", false); req.send(); if (req.status === 200) { document.getElementById(\\"manga-chapters-holder\\").innerHTML = req.responseText; } } } catch(e){} return document.documentElement.outerHTML; })();", html -> {
                            if (isProcessed[0]) return;
                            if (html == null || html.equals("null")) {
                                if (!isSilentBackgroundFetch) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MangaDetailActivity.this, "فشل جلب محتوى الصفحة", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                    });
                                }
                                return;
                            }

                            if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) { 
                                runOnUiThread(() -> { 
                                    progressBar.setVisibility(View.GONE); 
                                    webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); 
                                    webView.setAlpha(0.01f); 
                                    Toast.makeText(MangaDetailActivity.this, "يرجى الانتظار لتخطي حماية Cloudflare...", Toast.LENGTH_LONG).show(); 
                                });
                                return; 
                            }
                            
                            isProcessed[0] = true;
                            rootView.removeView(webView); 
                            try { 
                                webView.stopLoading(); 
                                webView.loadUrl("about:blank"); 
                                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { 
                                    try { webView.destroy(); } catch (Exception ignored2) {} 
                                }, 1500); 
                            } catch (Exception ignored) {} 
                            
                            String cleanHtml = html.replaceAll("^\\"|\\"$", "").replace("\\\\u003C", "<").replace("\\\\u003E", ">").replace("\\\\\\"", "\\"").replace("\\\\n", " ").replace("\\\\t", " ").replace("\\\\r", " ").replace("\\\\\\\\", "");
                            
                            parseHtmlLocally(cleanHtml, isSilentBackgroundFetch);
                        }); 
                    } catch (Exception ignored) {}
                }, 2500);
            }
"""

content = re.sub(r'public void onPageFinished\(android\.webkit\.WebView view, String url\) \{[\s\S]*?\}\); \}, 2500\);\n            \}', new_onPageFinished.strip(), content)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
