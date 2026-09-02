import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/SplashActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.util.PreferenceManager;', 'import com.fire.mangareader.util.PreferenceManager;\nimport com.fire.mangareader.domain.usecase.update.UpdateUseCase;\nimport com.fire.mangareader.domain.usecase.update.DownloadUpdateUseCase;\nimport com.fire.mangareader.domain.model.remote.Update;\nimport android.app.AlertDialog;\nimport android.app.ProgressDialog;\nimport android.widget.Toast;\nimport java.io.File;\nimport android.net.Uri;\nimport androidx.core.content.FileProvider;')

old_logic = """        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PreferenceManager prefs = new PreferenceManager(this);
            Intent intent;
            if (prefs.isFirstLaunch() || !prefs.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1500);"""

new_logic = """        checkAppUpdate();
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
                .setMessage(update.getMessage() + "\\n" + update.getChangeLog())
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
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 500);"""

content = content.replace(old_logic, new_logic)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched SplashActivity.java")
