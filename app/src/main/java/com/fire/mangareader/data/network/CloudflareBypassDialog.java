package com.fire.mangareader.data.network;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fire.mangareader.R;

public class CloudflareBypassDialog extends Dialog {

    private String url;
    private WebView webView;
    private BypassCallback callback;
    private TextView tvStatus;

    public interface BypassCallback {
        void onSuccess(String cookies, String userAgent);
        void onFailed();
    }

    public CloudflareBypassDialog(@NonNull Context context, String url, BypassCallback callback) {
        super(context);
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple layout for the dialog
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        tvStatus = new TextView(getContext());
        tvStatus.setText("جاري التحقق من Cloudflare، يرجى الانتظار...");
        tvStatus.setTextSize(16);
        tvStatus.setTextColor(android.graphics.Color.BLACK);
        layout.addView(tvStatus);
        
        webView = new WebView(getContext()); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);
        webView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 800));
        layout.addView(webView);
        
        setContentView(layout);
        setCancelable(false);
        setupWebView();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        
        String defaultUA = WebSettings.getDefaultUserAgent(getContext());
        settings.setUserAgentString(defaultUA);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean onRenderProcessGone(android.webkit.WebView view, android.webkit.RenderProcessGoneDetail detail) {
                if (view != null) {
                    view.destroy();
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // let webview handle it
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // ignore SSL errors to bypass strict blocks
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                tvStatus.setText("جاري التحميل...");
            }

            @Override
            public void onPageFinished(WebView view, String pageUrl) {
                super.onPageFinished(view, pageUrl);
                
                final String cookies = CookieManager.getInstance().getCookie(pageUrl) != null ? CookieManager.getInstance().getCookie(pageUrl) : "";
                
                
                Log.d("CF_BYPASS", "Cookies: " + cookies);
                
                if (cookies.contains("cf_clearance") || !pageUrl.contains("just_a_moment")) {
                    tvStatus.setText("تم التحقق بنجاح!");
                    MangaScraper.globalCookies = cookies;
                    MangaScraper.globalUserAgent = view.getSettings().getUserAgentString();
                    
                    getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("cloudflare_cookies", cookies)
                            .putString("user_agent", MangaScraper.globalUserAgent)
                            .apply();
                            
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (callback != null) callback.onSuccess(cookies, MangaScraper.globalUserAgent);
                        dismiss();
                    }, 1000);
                }
            }
        });

        webView.loadUrl(url);
    }
}
