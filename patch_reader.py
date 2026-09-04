import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r", encoding="utf-8") as f:
    content = f.read()

replacement = """
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                if (url != null && url.contains("dilar.tube")) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        view.evaluateJavascript(
                            "(function() { " +
                            "  try { " +
                            "    if (window.__PRELOADED_STATE__) { " +
                            "       var data = window.__PRELOADED_STATE__.readerDataAction.readerData; " +
                            "       var result = { " +
                            "          storage_key: data.release.storage_key, " +
                            "          pages: data.release.pages " +
                            "       }; " +
                            "       return JSON.stringify(result); " +
                            "    } " +
                            "  } catch(e) {} " +
                            "  return null; " +
                            "})();",
                            jsonResult -> {
                                if (jsonResult != null && !jsonResult.equals("null") && !jsonResult.equals("\\"null\\"")) {
                                    try {
                                        String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\\\"", "\\"").replace("\\\\\\\\", "\\\\");
                                        org.json.JSONObject obj = new org.json.JSONObject(unescaped);
                                        String storageKey = obj.getString("storage_key");
                                        org.json.JSONArray pagesArray = obj.getJSONArray("pages");
                                        
                                        java.util.List<String> pagesList = new java.util.ArrayList<>();
                                        for (int i = 0; i < pagesArray.length(); i++) {
                                            String pageFileName = pagesArray.getString(i);
                                            String fullImgUrl = "https://dilar.tube/cdn-cgi/image/format=webp,width=700/" + storageKey + "/" + pageFileName;
                                            pagesList.add(fullImgUrl);
                                        }
                                        
                                        runOnUiThread(() -> {
                                            loadingProgressBar.setVisibility(View.GONE);
                                            scraperWebView.setVisibility(View.GONE);
                                            
                                            java.util.List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                                            for (int i = 0; i < pagesList.size(); i++) {
                                                pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, pagesList.get(i), pagesList.get(i), null));
                                            }
                                            adapter.setPages(pageList, null, url);
                                            
                                            int total = pagesList.size();
                                            tvPageIndicator.setText("1 / " + total);
                                            pageSeekBar.setMax(total - 1);
                                            tvTotalPagesSeek.setText(String.valueOf(total));
                                        });
                                        return; 
                                    } catch (Exception ignored) {}
                                }
                                checkAndParseStandardHtml(view, url);
                            }
                        );
                    }, 2500);
                    return;
                }
                
                checkAndParseStandardHtml(view, url);
            }

            private void checkAndParseStandardHtml(WebView view, String url) {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { view.evaluateJavascript(
"""

content = re.sub(r'public void onPageFinished\(WebView view, String url\) \{.*?new android\.os\.Handler\(android\.os\.Looper\.getMainLooper\(\)\)\.postDelayed\(\(\) -> \{ view\.evaluateJavascript\(', replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w", encoding="utf-8") as f:
    f.write(content)
print("patched")
