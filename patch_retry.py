with open("app/src/main/java/com/fire/mangareader/data/network/interceptor/RetryInterceptor.java", "r") as f:
    content = f.read()

new_content = """package com.fire.mangareader.data.network.interceptor;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private final int maxRetries = 3;
    private final long initialDelayMs = 1000L;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException exception = null;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                if (response != null) {
                    response.close();
                }
                response = chain.proceed(request);
                // Return if successful or if it's an error we shouldn't retry (like 404 Not Found)
                if (response.isSuccessful() || response.code() == 404) {
                    return response;
                }
            } catch (IOException e) {
                exception = e;
            }
            
            retryCount++;
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep(initialDelayMs * (1 << (retryCount - 1))); // Exponential backoff: 1s, 2s, 4s...
                } catch (InterruptedException ignored) {}
            }
        }
        
        if (response != null) {
            return response;
        }
        if (exception != null) {
            throw exception;
        }
        throw new IOException("Failed to execute request after " + maxRetries + " retries");
    }
}
"""

with open("app/src/main/java/com/fire/mangareader/data/network/interceptor/RetryInterceptor.java", "w") as f:
    f.write(new_content)
