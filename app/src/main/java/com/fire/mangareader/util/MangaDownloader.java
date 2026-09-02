package com.fire.mangareader.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.domain.usecase.downloads.AddMangaDownloadUseCase;
import com.fire.mangareader.data.database.DownloadedChapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class MangaDownloader {
    public static volatile boolean isCancelled = false;


    public interface DownloadListener {
        void onProgressUpdate(int current, int total);
        void onSuccess();
        void onError(String errorMessage);
    }

    public static void downloadChapter(Context context, String mangaUrl, String chapterUrl, String chapterTitle, DownloadListener listener) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        
        mainHandler.post(() -> {
            WebView webView = new WebView(context); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true); settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);
            
            // استخدام الهوية الحقيقية للهاتف لكي لا يكتشفنا Cloudflare
            String agent = WebSettings.getDefaultUserAgent(context);
            settings.setUserAgentString(agent);

            CookieManager.getInstance().setAcceptCookie(true);

            // مؤقت لإنهاء العملية إذا علق السيرفر
            Runnable timeoutTask = () -> {
                if (listener != null) listener.onError("انتهى الوقت. السيرفر يرفض الاتصال.");
                try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
            };
            mainHandler.postDelayed(timeoutTask, 40000);

            webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean onRenderProcessGone(android.webkit.WebView view, android.webkit.RenderProcessGoneDetail detail) {
                if (view != null) {
                    view.destroy();
                }
                return true;
            }

                boolean isProcessing = false;

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (isProcessing) return;
                    
                    // نعطي السيرفر مهلة لمعالجة حماية Adscore وجلب الصور المخفية
                    mainHandler.postDelayed(() -> {
                        if (isProcessing) return;
                        
                        view.evaluateJavascript("window.scrollTo(0, document.body.scrollHeight);", null);
                        
                        view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
                            if (html == null || html.equals("null")) return;
                            
                            // التحقق مما إذا كانت الحماية لا تزال تعمل
                            if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("adscore")) {
                                return; 
                            }

                            isProcessing = true;
                            mainHandler.removeCallbacks(timeoutTask); 

                            // حفظ الكوكيز الجديدة التي أصدرتها الحماية
                            CookieManager.getInstance().flush();

                            String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\\\", "");
                            
                            new Thread(() -> {
                                try {
                                    Document doc = Jsoup.parse(cleanHtml, chapterUrl);
                                    Elements imgs = doc.select(".reading-content img, .page-break img, #readerarea img, .image-container img, .blocks-gallery-item img");
                                    List<String> imageUrls = new ArrayList<>();
                                    
                                    for (Element img : imgs) {
                                        String src = img.attr("data-src");
                                        if (src.isEmpty() || src.startsWith("data:image")) src = img.attr("data-lazy-src");
                                        if (src.isEmpty() || src.startsWith("data:image")) src = img.attr("src");
                                        
                                        src = src.trim();
                                        if (!src.isEmpty() && !src.startsWith("data:image") && !src.contains("logo") && !src.contains("spinner") && !src.contains("loader")) {
                                            if (src.startsWith("//")) src = "https:" + src;
                                            imageUrls.add(src);
                                        }
                                    }

                                    if (imageUrls.isEmpty()) {
                                        mainHandler.post(() -> {
                                            if (listener != null) listener.onError("الروابط مشفرة أو فارغة.");
                                            try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
                                        });
                                        return;
                                    }

                                    File mangaFolder = new File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
                                    File chapterFolder = new File(mangaFolder, String.valueOf(chapterUrl.hashCode()));
                                    if (!chapterFolder.exists() && !chapterFolder.mkdirs()) {
                                        throw new Exception("لا يمكن إنشاء مجلد الحفظ");
                                    }

                                    java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4); java.util.List<java.util.concurrent.Future<?>> futures = new java.util.ArrayList<>(); java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0); for (int i = 0; i < imageUrls.size(); i++) { final int index = i; futures.add(executor.submit(() -> {
                                        if (isCancelled) return;

                                        // 🚀 نعتمد على OkHttp للتنزيل 
                                        try { downloadImageFile(context, imageUrls.get(index), chapterUrl, new File(chapterFolder, index + ".jpg")); } catch(Exception e) { e.printStackTrace(); }
                                        
                                        int currentProgress = count.incrementAndGet();
                                        int total = imageUrls.size();
                                        if (listener != null) {
                                            mainHandler.post(() -> listener.onProgressUpdate(currentProgress, total));
                                        }
                                    })); } for (java.util.concurrent.Future<?> f : futures) { try { f.get(); } catch (Exception ignored) {} } executor.shutdown();

                                    DownloadedChapter downloaded = new DownloadedChapter();
                                    downloaded.chapterUrl = chapterUrl;
                                    downloaded.mangaUrl = mangaUrl;
                                    downloaded.chapterTitle = chapterTitle;
                                    downloaded.localFolderPath = chapterFolder.getAbsolutePath();
                                    
                                    new AddMangaDownloadUseCase(context).execute(downloaded, new AddMangaDownloadUseCase.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(String error) {
                                        }
                                    });

                                    mainHandler.post(() -> {
                                        if (listener != null) listener.onSuccess();
                                        Toast.makeText(context, "تم تنزيل: " + chapterTitle, Toast.LENGTH_SHORT).show();
                                        try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mainHandler.post(() -> {
                                        if (listener != null) listener.onError("فشل: " + e.getMessage());
                                        try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
                                    });
                                }
                            }).start();
                        });
                    }, 2500); 
                }
            });
            webView.loadUrl(chapterUrl);
        });
    }

    // 🚀 دالة التحميل تعتمد على محرك OkHttp القوي المجهز مسبقاً مع تطبيق خيارات DownloadQuality
    private static void downloadImageFile(Context context, String fileUrl, String chapterUrl, java.io.File outputFile) throws Exception {
        fileUrl = fileUrl.trim().replace(" ", "%20");
        if (fileUrl.startsWith("//")) {
            fileUrl = "https:" + fileUrl;
        }
        
        android.content.SharedPreferences defaultPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String qualityKey = defaultPrefs.getString("download_quality", "MEDIUM");
        com.fire.mangareader.domain.model.DownloadQuality qualityEnum;
        try {
            qualityEnum = com.fire.mangareader.domain.model.DownloadQuality.valueOf(qualityKey);
        } catch (Exception e) {
            qualityEnum = com.fire.mangareader.domain.model.DownloadQuality.MEDIUM;
        }

        // استخدام ملف مؤقت لمنع حفظ صور تالفة عند انقطاع الاتصال
        java.io.File tmpFile = new java.io.File(outputFile.getAbsolutePath() + ".tmp");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(fileUrl)
                .header("Referer", chapterUrl) // مهم جداً لتخطي حظر الصور
                .build();
        okhttp3.Response response = MangaOkHttp.getClient().newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code());
        }

        byte[] imageBytes = response.body().bytes();

        if (qualityEnum == com.fire.mangareader.domain.model.DownloadQuality.HIGH) {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
            fos.write(imageBytes);
            fos.flush();
            fos.close();
        } else {
            try {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    int originalWidth = bitmap.getWidth();
                    int originalHeight = bitmap.getHeight();
                    int maxW = qualityEnum.getMaxPixelWidth();
                    
                    if (originalWidth > maxW) {
                        int targetHeight = (int) (((float) originalHeight / originalWidth) * maxW);
                        android.graphics.Bitmap scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, maxW, targetHeight, true);
                        if (scaled != bitmap) {
                            bitmap.recycle();
                            bitmap = scaled;
                        }
                    }
                    
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, qualityEnum.getCompressionQuality(), fos);
                    fos.flush();
                    fos.close();
                    bitmap.recycle();
                } else {
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                    fos.write(imageBytes);
                    fos.flush();
                    fos.close();
                }
            } catch (Throwable t) {
                java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                fos.write(imageBytes);
                fos.flush();
                fos.close();
            }
        }
        
        // بمجرد التأكد من كتابة الملف بنجاح دون أخطاء، نقوم بإعادة تسميته إلى الاسم الأصلي (.jpg)
        if (tmpFile.exists()) {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            tmpFile.renameTo(outputFile);
        }
    }
}

