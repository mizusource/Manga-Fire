import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    text = f.read()

settings_logic = """
    private void showReaderSettings() {
        String[] options = {"بدون فلتر", "تظليل", "دافيء (حماية العين)", "ليلي قوي"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("إعدادات القراءة")
            .setSingleChoiceItems(options, currentFilterMode, (dialog, which) -> {
                currentFilterMode = which;
                if (which == 0) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                } else if (which == 1) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#66000000")); // Black 40%
                } else if (which == 2) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#33FF9800")); // Warm Orange 20%
                } else if (which == 3) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#4D000000")); // Black 30%
                }
                
                // Save setting locally if you want
                com.fire.mangareader.util.PreferenceManager prefs = new com.fire.mangareader.util.PreferenceManager(this);
                prefs.saveString("eye_filter_mode", String.valueOf(which));
                dialog.dismiss();
            })
            .setPositiveButton("إبقاء الشاشة مضاءة", (dialog, which) -> {
                getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                com.fire.mangareader.util.SystemUtils.safeToast(this, "تم تفعيل إبقاء الشاشة مضاءة");
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
"""

text = re.sub(r'(\s*private void loadChapterListFromDb\(\))', settings_logic + r'\1', text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(text)
