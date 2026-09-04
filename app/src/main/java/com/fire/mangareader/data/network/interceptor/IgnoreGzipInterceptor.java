package com.fire.mangareader.data.network.interceptor;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IgnoreGzipInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if ("gzip".equals(request.header("Accept-Encoding"))) {
            Request.Builder builder = request.newBuilder();
            builder.removeHeader("Accept-Encoding");
            request = builder.build();
        }
        return chain.proceed(request);
    }
}
