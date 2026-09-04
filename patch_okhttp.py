import re

with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "r") as f:
    text = f.read()

# Add imports
imports = """
import com.fire.mangareader.data.network.cookie.AndroidCookieJar;
import com.fire.mangareader.data.network.ssl.SSLHelper;
import com.fire.mangareader.data.network.interceptor.IgnoreGzipInterceptor;
import okhttp3.brotli.BrotliInterceptor;
"""
text = text.replace("import okhttp3.OkHttpClient;", imports + "import okhttp3.OkHttpClient;")

# Replace PreferencesCookieJar with AndroidCookieJar
text = text.replace("new PreferencesCookieJar(appContext)", "new AndroidCookieJar()")

# Add Brotli and IgnoreGzip interceptors
interceptors = """
                    .addInterceptor(new IgnoreGzipInterceptor())
                    .addInterceptor(BrotliInterceptor.INSTANCE)
"""
text = text.replace(".addInterceptor(new RateLimitInterceptor())", ".addInterceptor(new RateLimitInterceptor())" + interceptors)

# Replace TlsCompat with SSLHelper
tls_pattern = r'client = TlsCompat\.enableTls12And13\(builder\)\.build\(\);'
ssl_logic = """
            try {
                SSLHelper.SSLContextResult sslResult = SSLHelper.getSslContext();
                builder.sslSocketFactory(sslResult.socketFactory, sslResult.trustManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client = builder.build();
"""
text = re.sub(tls_pattern, ssl_logic, text)

with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "w") as f:
    f.write(text)
