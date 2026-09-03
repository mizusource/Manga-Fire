import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/SettingsActivity.java", "r") as f:
    content = f.read()

import_stmt = """import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.fire.mangareader.data.parser.ParserConfigManager;
"""

if "ParserConfigManager" not in content:
    content = content.replace("import android.widget.Toast;", "import android.widget.Toast;\n" + import_stmt)
    
    click_listener = """
            Preference parserSyncPref = findPreference("parser_sync_url");
            if (parserSyncPref != null) {
                parserSyncPref.setOnPreferenceClickListener(preference -> {
                    showParserSyncDialog();
                    return true;
                });
            }
"""
    content = content.replace("Preference backupExport", click_listener + "            Preference backupExport")
    
    dialog_method_clean = """
        private void showParserSyncDialog() {
            final EditText input = new EditText(requireContext());
            input.setHint("https://.../config.json");
            input.setText(ParserConfigManager.INSTANCE.getSyncUrl(requireContext()));
            
            new AlertDialog.Builder(requireContext())
                    .setTitle("رابط إعدادات المحرك الديناميكي")
                    .setMessage("أدخل رابط ملف الـ JSON الخاص بإعدادات المواقع:")
                    .setView(input)
                    .setPositiveButton("تحديث", (dialog, which) -> {
                        String url = input.getText().toString().trim();
                        if (!url.isEmpty()) {
                            android.widget.Toast.makeText(requireContext(), "جاري التحديث...", android.widget.Toast.LENGTH_SHORT).show();
                            new Thread(() -> {
                                try {
                                    okhttp3.Request req = new okhttp3.Request.Builder().url(url).build();
                                    okhttp3.Response res = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(req).execute();
                                    if(res.isSuccessful() && res.body() != null) {
                                        String json = res.body().string();
                                        requireContext().getSharedPreferences("parser_config_prefs", android.content.Context.MODE_PRIVATE)
                                            .edit()
                                            .putString("config_json", json)
                                            .putString("sync_url", url)
                                            .apply();
                                        ParserConfigManager.INSTANCE.init(requireContext());
                                        requireActivity().runOnUiThread(() -> {
                                            android.widget.Toast.makeText(requireContext(), "تم التحديث بنجاح!", android.widget.Toast.LENGTH_LONG).show();
                                        });
                                    } else {
                                        requireActivity().runOnUiThread(() -> {
                                            android.widget.Toast.makeText(requireContext(), "فشل التحديث: خطأ بالاتصال", android.widget.Toast.LENGTH_LONG).show();
                                        });
                                    }
                                } catch (Exception e) {
                                    requireActivity().runOnUiThread(() -> {
                                        android.widget.Toast.makeText(requireContext(), "فشل التحديث: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                                    });
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        }
"""
    content = content.replace("private void showImageQualityDialog() {", dialog_method_clean + "\n        private void showImageQualityDialog() {")
    
    with open("app/src/main/java/com/fire/mangareader/presentation/activity/SettingsActivity.java", "w") as f:
        f.write(content)
    print("Patched SettingsActivity.java")
