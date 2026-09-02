import re

filepath = 'app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java'
with open(filepath, 'r') as f:
    content = f.read()

new_content = """package com.fire.mangareader.util;

import android.content.Context;
import com.fire.mangareader.data.network.FastDns;
import com.fire.mangareader.data.network.cookie.PreferencesCookieJar;
import com.fire.mangareader.data.network.interceptor.CommonHeadersInterceptor;
import com.fire.mangareader.data.network.interceptor.RateLimitInterceptor;
import com.fire.mangareader.data.network.interceptor.RetryInterceptor;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class MangaOkHttp {
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
                    .cookieJar(new PreferencesCookieJar(appContext))
                    .addInterceptor(new CommonHeadersInterceptor(appContext))
                    .addInterceptor(new RateLimitInterceptor())
                    .addInterceptor(new RetryInterceptor());
            
            client = TlsCompat.enableTls12And13(builder).build();
        }
        return client;
    }
}
"""

with open(filepath, 'w') as f:
    f.write(new_content)
print("Patched MangaOkHttp.java")
