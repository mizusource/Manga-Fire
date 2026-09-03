with open("app/src/main/java/com/fire/mangareader/data/network/interceptor/CommonHeadersInterceptor.java", "r") as f:
    content = f.read()

new_content = """package com.fire.mangareader.data.network.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CommonHeadersInterceptor implements Interceptor {
    private final Context context;

    public CommonHeadersInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userAgent = sharedPreferences.getString("user_agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent);
        String cookies = sharedPreferences.getString("cloudflare_cookies", com.fire.mangareader.data.network.MangaScraper.globalCookies);
        
        // Use android's CookieManager to ensure we always get the absolute latest if possible
        String webViewCookies = android.webkit.CookieManager.getInstance().getCookie(original.url().toString());
        if (webViewCookies != null && !webViewCookies.isEmpty()) {
            cookies = webViewCookies;
        }

        builder.header("User-Agent", userAgent != null ? userAgent : "");
        if (cookies != null && !cookies.isEmpty()) {
            builder.header("Cookie", cookies);
        }
        
        builder.header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
        builder.header("Sec-Fetch-Dest", "image");
        builder.header("Sec-Fetch-Mode", "no-cors");
        builder.header("Sec-Fetch-Site", "cross-site");

        return chain.proceed(builder.build());
    }
}
"""

with open("app/src/main/java/com/fire/mangareader/data/network/interceptor/CommonHeadersInterceptor.java", "w") as f:
    f.write(new_content)
