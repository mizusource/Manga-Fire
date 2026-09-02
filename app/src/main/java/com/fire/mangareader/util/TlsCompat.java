package com.fire.mangareader.util;

import android.util.Log;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public final class TlsCompat {
    private static final String TAG = "TlsCompat";

    public static OkHttpClient.Builder enableTls12And13(OkHttpClient.Builder builder) {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager x509TrustManager = null;
            if (trustManagers != null) {
                for (TrustManager tm : trustManagers) {
                    if (tm instanceof X509TrustManager) {
                        x509TrustManager = (X509TrustManager) tm;
                        break;
                    }
                }
            }

            if (x509TrustManager != null) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
                builder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);
            }

            builder.connectionSpecs(Arrays.asList(
                    new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).allEnabledTlsVersions().allEnabledCipherSuites().build(),
                    new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS).allEnabledTlsVersions().allEnabledCipherSuites().build(),
                    ConnectionSpec.CLEARTEXT
            ));
        } catch (Exception e) {
            Log.e(TAG, "Error applying TLS compatibility", e);
        }
        return builder;
    }
}
