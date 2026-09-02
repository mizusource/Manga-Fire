package com.fire.mangareader.data.network.interceptor;

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
        
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_config_preferences", Context.MODE_PRIVATE);
        String userAgent = sharedPreferences.getString("cloudflare_user_agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent);
        
        builder.header("User-Agent", userAgent != null ? userAgent : "");
        builder.header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
        builder.header("Sec-Fetch-Dest", "image");
        builder.header("Sec-Fetch-Mode", "no-cors");
        builder.header("Sec-Fetch-Site", "cross-site");

        return chain.proceed(builder.build());
    }
}
