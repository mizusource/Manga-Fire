package com.fire.mangareader.data.network.interceptor;

import com.fire.mangareader.data.network.exception.RateLimitException;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class RateLimitInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 429) {
            String retryAfterStr = response.header("Retry-After");
            int retryAfter = 0;
            if (retryAfterStr != null) {
                try {
                    retryAfter = Integer.parseInt(retryAfterStr);
                } catch (NumberFormatException ignored) {}
            }
            response.close();
            String message = "تم تجاوز الحد المسموح. حاول مرة أخرى بعد " + (retryAfter / 60) + " دقيقة و " + (retryAfter % 60) + " ثانية.";
            throw new RateLimitException(message);
        }
        return response;
    }
}
