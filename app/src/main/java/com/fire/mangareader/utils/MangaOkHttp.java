package com.fire.mangareader.utils;

import android.webkit.CookieManager;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Dispatcher;
import okhttp3.dnsoverhttps.DnsOverHttps;
import okhttp3.brotli.BrotliInterceptor;

public class MangaOkHttp {

    private static OkHttpClient client;

    public static synchronized OkHttpClient getClient() {
        if (client == null) {
            // 1. Create a bootstrap client for DoH
            OkHttpClient bootstrapClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();

            // 2. Configure Cloudflare DNS over HTTPS (DoH) to bypass blocks
            DnsOverHttps dns = new DnsOverHttps.Builder()
                    .client(bootstrapClient)
                    .url(HttpUrl.get("https://cloudflare-dns.com/dns-query"))
                    .bootstrapDnsHosts(InetAddress.getLoopbackAddress())
                    .build();

            // 3. Configure Dispatcher for high concurrency
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(64);
            dispatcher.setMaxRequestsPerHost(16);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .dns(dns)
                    .dispatcher(dispatcher)
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(45, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .connectionPool(new ConnectionPool(32, 5L, TimeUnit.MINUTES))
                    .addInterceptor(BrotliInterceptor.INSTANCE)
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
                    // 🔄 Rate-limit & 429/403 Backoff Retry Interceptor
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        int retryCount = 0;
                        while ((response.code() == 429 || response.code() == 403) && retryCount < 3) {
                            response.close();
                            retryCount++;
                            try {
                                Thread.sleep(retryCount * 1000L);
                            } catch (InterruptedException ignored) {}
                            response = chain.proceed(request);
                        }
                        return response;
                    })
                    // 🌐 Request Headers & User-Agent Interceptor
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();

                        if ("gzip".equals(original.header("Accept-Encoding"))) {
                            requestBuilder.removeHeader("Accept-Encoding");
                        }

                        if (original.header("User-Agent") == null) {
                            requestBuilder.header("User-Agent", com.fire.mangareader.network.MangaScraper.globalUserAgent);
                        }

                        requestBuilder.header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                                .header("Sec-Fetch-Dest", "image")
                                .header("Sec-Fetch-Mode", "no-cors")
                                .header("Sec-Fetch-Site", "cross-site");

                        return chain.proceed(requestBuilder.build());
                    });
            client = TlsCompat.enableTls12And13(builder).build();
        }
        return client;
    }
}
