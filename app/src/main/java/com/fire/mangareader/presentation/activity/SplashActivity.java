package com.fire.mangareader.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fire.mangareader.R;
import com.fire.mangareader.util.PreferenceManager;
import com.fire.mangareader.domain.usecase.update.UpdateUseCase;
import com.fire.mangareader.domain.usecase.update.DownloadUpdateUseCase;
import com.fire.mangareader.domain.model.remote.Update;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.widget.Toast;
import java.io.File;
import android.net.Uri;
import androidx.core.content.FileProvider;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkAppUpdate();
    }

    private void checkAppUpdate() {
        long currentVersion = 1; // Assuming current version is 1 for testing
        new UpdateUseCase().checkForUpdate(currentVersion, new UpdateUseCase.Callback() {
            @Override
            public void onUpdateAvailable(Update update) {
                showUpdateDialog(update);
            }

            @Override
            public void onNoUpdate() {
                proceedToNextScreen();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                proceedToNextScreen();
            }
        });
    }

    private void showUpdateDialog(Update update) {
        new AlertDialog.Builder(this)
                .setTitle("تحديث جديد متوفر!")
                .setMessage(update.getMessage() + "\n" + update.getChangeLog())
                .setPositiveButton("تحديث الآن", (dialog, which) -> downloadUpdate(update))
                .setNegativeButton("لاحقاً", (dialog, which) -> proceedToNextScreen())
                .setCancelable(false)
                .show();
    }

    private void downloadUpdate(Update update) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("جاري التحديث...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        File outputFile = new File(getExternalFilesDir(null), "update.apk");

        new DownloadUpdateUseCase().downloadUpdate(update.getUpdateUrl(), outputFile, new DownloadUpdateUseCase.DownloadProgressCallback() {
            @Override
            public void onProgress(int progress, long bytesDownloaded, long totalBytes) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onComplete(File downloadedFile) {
                progressDialog.dismiss();
                Toast.makeText(SplashActivity.this, "تم التحميل بنجاح", Toast.LENGTH_SHORT).show();
                // Normally we would invoke the PackageInstaller here
                proceedToNextScreen(); 
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_LONG).show();
                proceedToNextScreen();
            }
        });
    }

    private void proceedToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PreferenceManager prefs = new PreferenceManager(this);
            Intent intent;
            if (prefs.isFirstLaunch() || !prefs.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainComposeActivity.class);
            }
            startActivity(intent);
            finish();
        }, 500);
    }
}
