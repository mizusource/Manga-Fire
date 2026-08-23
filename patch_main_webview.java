            @Override
            public void onReceivedError(android.webkit.WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) {
                rootView.removeView(webView);
                webView.destroy();
            }
