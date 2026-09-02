package com.fire.mangareader.data.network.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PreferencesCookieJar implements CookieJar {
    private final Map<String, CookieWrapper> cache = new HashMap<>();
    private final SharedPreferences prefs;
    private boolean loaded = false;

    public PreferencesCookieJar(Context context) {
        this.prefs = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
    }

    private synchronized void loadFromPrefs() {
        if (loaded) return;
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            try {
                CookieWrapper wrapper = CookieWrapper.decodeFromString((String) entry.getValue());
                cache.put(entry.getKey(), wrapper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loaded = true;
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        loadFromPrefs();
        List<Cookie> validCookies = new ArrayList<>();
        Set<String> expiredCookies = new HashSet<>();

        for (Map.Entry<String, CookieWrapper> entry : cache.entrySet()) {
            CookieWrapper wrapper = entry.getValue();
            if (wrapper.cookie.expiresAt() < System.currentTimeMillis()) {
                expiredCookies.add(entry.getKey());
            } else if (wrapper.cookie.matches(url)) {
                validCookies.add(wrapper.cookie);
            }
        }

        if (!expiredCookies.isEmpty()) {
            SharedPreferences.Editor editor = prefs.edit();
            for (String key : expiredCookies) {
                cache.remove(key);
                editor.remove(key);
            }
            editor.apply();
        }
        return validCookies;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        loadFromPrefs();
        SharedPreferences.Editor editor = prefs.edit();
        for (Cookie cookie : cookies) {
            CookieWrapper wrapper = new CookieWrapper(cookie);
            String key = wrapper.getUniqueKey();
            cache.put(key, wrapper);
            if (cookie.persistent()) {
                try {
                    editor.putString(key, wrapper.encodeToString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        editor.apply();
    }
}
