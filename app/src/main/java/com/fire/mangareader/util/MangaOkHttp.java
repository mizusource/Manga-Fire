package com.fire.mangareader.util;

import android.content.Context;
import com.fire.mangareader.data.network.FastDns;
import com.fire.mangareader.data.network.cookie.PreferencesCookieJar;
import com.fire.mangareader.data.network.interceptor.CommonHeadersInterceptor;
import com.fire.mangareader.data.network.interceptor.RateLimitInterceptor;
import com.fire.mangareader.data.network.interceptor.RetryInterceptor;
import com.fire.mangareader.data.network.interceptor.DirectIpInterceptor;

import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;

import com.fire.mangareader.data.network.cookie.AndroidCookieJar;
import com.fire.mangareader.data.network.ssl.SSLHelper;
import com.fire.mangareader.data.network.interceptor.IgnoreGzipInterceptor;
import okhttp3.brotli.BrotliInterceptor;
import okhttp3.OkHttpClient;

public class MangaOkHttp {
        public static DirectIpInterceptor directIpInterceptor = new DirectIpInterceptor(null);
    private static OkHttpClient client;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static synchronized OkHttpClient getClient() {
        if (client == null) {
            if (appContext == null) {
                throw new IllegalStateException("MangaOkHttp must be initialized with init(Context) before calling getClient()");
            }
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .dns(FastDns.INSTANCE)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(8, 5L, TimeUnit.MINUTES))
                    .cookieJar(new AndroidCookieJar())
                    .addInterceptor(new CommonHeadersInterceptor(appContext))
                    .addInterceptor(new RateLimitInterceptor())
                    .addInterceptor(new IgnoreGzipInterceptor())
                    .addInterceptor(BrotliInterceptor.INSTANCE)

                                        .addInterceptor(new RetryInterceptor())
                    .addInterceptor(directIpInterceptor);
            
            
            try {
                SSLHelper.SSLContextResult sslResult = SSLHelper.getSslContext();
                builder.sslSocketFactory(sslResult.socketFactory, sslResult.trustManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client = builder.build();

        }
        return client;
    }
}
