package com.fire.mangareader.data.network.cookie;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class AndroidCookieJar implements CookieJar {
    private final CookieManager cookieManager = CookieManager.getInstance();

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String urlString = url.toString();
        String cookieString = cookieManager.getCookie(urlString);
        
        if (cookieString == null || cookieString.isEmpty()) {
            return Collections.emptyList();
        }
        
        String[] cookieHeaders = cookieString.split(";");
        List<Cookie> cookies = new ArrayList<>(cookieHeaders.length);
        
        for (String header : cookieHeaders) {
            Cookie parsed = Cookie.parse(url, header);
            if (parsed != null) {
                cookies.add(parsed);
            }
        }
        
        return cookies;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies.isEmpty()) return;
        
        String urlString = url.toString();
        for (Cookie cookie : cookies) {
            cookieManager.setCookie(urlString, cookie.toString());
        }
        cookieManager.flush();
    }
}
