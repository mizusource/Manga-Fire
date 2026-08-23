package com.fire.mangareader.utils;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MangaOkHttp {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            CookieManager cookieManager = CookieManager.getInstance();
                            for (Cookie cookie : cookies) {
                                cookieManager.setCookie(url.toString(), cookie.toString());
                            }
                            cookieManager.flush();
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            CookieManager cookieManager = CookieManager.getInstance();
                            String cookiesStr = cookieManager.getCookie(url.toString());
                            List<Cookie> cookies = new ArrayList<>();
                            if (cookiesStr != null && !cookiesStr.isEmpty()) {
                                String[] cookieHeaders = cookiesStr.split(";");
                                for (String header : cookieHeaders) {
                                    Cookie c = Cookie.parse(url, header.trim());
                                    if (c != null) cookies.add(c);
                                }
                            }
                            return cookies;
                        }
                    })
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();

                        // 1. تطبيق خدعة IgnoreGzipInterceptor (إلغاء Gzip اليدوي لتجنب حظر Cloudflare)
                        if ("gzip".equals(original.header("Accept-Encoding"))) {
                            requestBuilder.removeHeader("Accept-Encoding");
                        }

                        // 2. تطبيق خدعة UserAgentInterceptor (تأمين هوية المتصفح لكل طلب)
                        if (original.header("User-Agent") == null) {
                            requestBuilder.header("User-Agent", "Mozilla/5.0 (Linux; Android 14; SM-A366B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");
                        }

                        // 3. خداع السيرفر (Chrome Mimicry)
                        requestBuilder.header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                                .header("Sec-Fetch-Dest", "image")
                                .header("Sec-Fetch-Mode", "no-cors")
                                .header("Sec-Fetch-Site", "cross-site");

                        return chain.proceed(requestBuilder.build());
                    })
                    .build();
        }
        return client;
    }
}
