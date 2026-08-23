package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fire.mangareader.R;
import com.fire.mangareader.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PreferenceManager prefs = new PreferenceManager(this);
            Intent intent;
            if (prefs.isFirstLaunch() || !prefs.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1500);
    }
}
