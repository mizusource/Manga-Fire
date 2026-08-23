package com.fire.mangareader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.fire.mangareader.R;

public class ThemeHelper {
    public static void applyTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString("app_theme", "dark_neon_blue");
        switch (theme) {
            case "dark_neon_red":
                context.setTheme(R.style.Theme_Animeista_DarkNeonRed);
                break;
            case "dark_neon_purple":
                context.setTheme(R.style.Theme_Animeista_DarkNeonPurple);
                break;
            case "dark_neon_yellow":
                context.setTheme(R.style.Theme_Animeista_DarkNeonYellow);
                break;
            case "classic_white":
                context.setTheme(R.style.Theme_Animeista_ClassicWhite);
                break;
            case "classic_red":
                context.setTheme(R.style.Theme_Animeista_ClassicRed);
                break;
            case "classic_blue":
                context.setTheme(R.style.Theme_Animeista_ClassicBlue);
                break;
            case "classic_purple":
                context.setTheme(R.style.Theme_Animeista_ClassicPurple);
                break;
            case "classic_navy":
                context.setTheme(R.style.Theme_Animeista_ClassicNavy);
                break;
            case "dark_neon_blue":
            default:
                context.setTheme(R.style.Theme_Animeista_Dark);
                break;
        }
    }
}
