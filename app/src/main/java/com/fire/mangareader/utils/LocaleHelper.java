package com.fire.mangareader.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {
    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public static void applyLocale(Context context) {
        PreferenceManager prefs = new PreferenceManager(context);
        String lang = prefs.getLanguage();
        setLocale(context, lang);
    }
}
