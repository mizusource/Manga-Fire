package com.fire.mangareader.presentation.activity;
import com.fire.mangareader.data.network.SupabaseManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.fire.mangareader.data.parser.ParserConfigManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.fire.mangareader.domain.usecase.user.EnableChapterNotificationUseCase;

import com.fire.mangareader.R;
import com.fire.mangareader.util.LocaleHelper;
import com.fire.mangareader.util.PreferenceManager;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        android.view.View btnBack = findViewById(R.id.btnBackSettings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private androidx.activity.result.ActivityResultLauncher<String> exportLauncher;
        private androidx.activity.result.ActivityResultLauncher<String[]> importLauncher;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            exportLauncher = registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json"),
                    uri -> {
                        if (uri != null) {
                            com.fire.mangareader.util.BackupManager.exportBackup(requireContext(), uri, new com.fire.mangareader.util.BackupManager.BackupCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
            );

            importLauncher = registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            com.fire.mangareader.util.BackupManager.importBackup(requireContext(), uri, new com.fire.mangareader.util.BackupManager.BackupCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
            );
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            PreferenceManager prefs = new PreferenceManager(requireContext());

            // 1. تغيير الثيم
            ListPreference themePref = findPreference("app_theme");
            if (themePref != null) {
                themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext());
                    sp.edit().putString("app_theme", (String) newValue).apply();
                    
                    Intent intent = new Intent(requireContext(), MainComposeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                });
            }
            ListPreference langPref = findPreference("language");
            if (langPref != null) {
                langPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    prefs.setLanguage((String) newValue);
                    LocaleHelper.setLocale(requireContext(), (String) newValue);
                    
                    Intent intent = new Intent(requireContext(), MainComposeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                });
            }

            Preference storageManager = findPreference("storage_manager");
            if (storageManager != null) {
                storageManager.setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(requireContext(), StorageManagerActivity.class));
                    return true;
                });
            }

            // 🧹 تنظيف الذاكرة المؤقتة (Cache Manager)
            Preference clearCachePref = findPreference("clear_cache");
            if (clearCachePref != null) {
                String formattedSize = com.fire.mangareader.util.CacheManager.getFormattedCacheSize(requireContext());
                clearCachePref.setSummary("الحجم المستهلك حالياً: " + formattedSize);
                clearCachePref.setOnPreferenceClickListener(preference -> {
                    boolean success = com.fire.mangareader.util.CacheManager.clearAppCache(requireContext());
                    if (success) {
                        Toast.makeText(requireContext(), "تم تنظيف الذاكرة المؤقتة وتسريع التطبيق بنجاح ✨", Toast.LENGTH_SHORT).show();
                        clearCachePref.setSummary("الحجم المستهلك حالياً: " + com.fire.mangareader.util.CacheManager.getFormattedCacheSize(requireContext()));
                    } else {
                        Toast.makeText(requireContext(), "الذاكرة المؤقتة فارغة بالفعل", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
            }

            // 📥 جودة التنزيل
            ListPreference downloadQualityPref = findPreference("download_quality");
            if (downloadQualityPref != null) {
                com.fire.mangareader.domain.model.DownloadQuality currentQuality = com.fire.mangareader.domain.model.DownloadQuality.fromPreferences(requireContext());
                String summaryText = currentQuality.getArabicName() + " (" + currentQuality.getDescription() + ")";
                downloadQualityPref.setSummary(summaryText.replace("%", "%%"));
                downloadQualityPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    com.fire.mangareader.domain.model.DownloadQuality q = com.fire.mangareader.domain.model.DownloadQuality.valueOf((String) newValue);
                    String newSummary = q.getArabicName() + " (" + q.getDescription() + ")";
                    downloadQualityPref.setSummary(newSummary.replace("%", "%%"));
                    Toast.makeText(requireContext(), "تم ضبط جودة التنزيل على: " + q.getArabicName(), Toast.LENGTH_SHORT).show();
                    return true;
                });
            }

            // ✈️ تليجرام
            Preference telegramChannelPref = findPreference("telegram_channel");
            if (telegramChannelPref != null) {
                telegramChannelPref.setOnPreferenceClickListener(preference -> {
                    com.fire.mangareader.util.TelegramManager.openTelegramChannel(requireContext(), "wv_sj");
                    return true;
                });
            }

            Preference telegramTestPref = findPreference("telegram_test");
            if (telegramTestPref != null) {
                telegramTestPref.setOnPreferenceClickListener(preference -> {
                    com.fire.mangareader.util.TelegramManager tm = new com.fire.mangareader.util.TelegramManager(requireContext());
                    Toast.makeText(requireContext(), "توكن البوت: " + (tm.getBotToken().isEmpty() ? "غير مضبوط" : "متصل ✔️"), Toast.LENGTH_LONG).show();
                    return true;
                });
            }

            SwitchPreferenceCompat notifPref = findPreference("notifications_enabled");
            if (notifPref != null) {
                notifPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    new EnableChapterNotificationUseCase(requireContext()).enableNewChapterNotification(enabled, new EnableChapterNotificationUseCase.Callback() {
                        @Override
                        public void onSuccess(String token) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), enabled ? "تم تفعيل الإشعارات بنجاح 🔔" : "تم إيقاف الإشعارات 🔕", Toast.LENGTH_SHORT).show();
                            });
                        }
                        @Override
                        public void onError(String error) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                                notifPref.setChecked(!enabled); // revert
                            });
                        }
                    });
                    return true;
                });
            }

            SwitchPreferenceCompat wifiPref = findPreference("wifi_only");
            if (wifiPref != null) {
                wifiPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    prefs.setWifiOnly((Boolean) newValue);
                    return true;
                });
            }

            // 🗜️ 4. إعداد جودة الصور (توفير البيانات)
            Preference imageQualityPref = findPreference("image_quality");
            if (imageQualityPref == null) {
                imageQualityPref = new Preference(requireContext());
                imageQualityPref.setKey("image_quality");
                imageQualityPref.setTitle("جودة صور المانجا");
                imageQualityPref.setSummary("تقليل الجودة لتوفير باقة الإنترنت");
                getPreferenceScreen().addPreference(imageQualityPref);
            }
            
            imageQualityPref.setOnPreferenceClickListener(preference -> {
                showImageQualityDialog();
                return true;
            });

            
            Preference parserSyncPref = findPreference("parser_sync_url");
            if (parserSyncPref != null) {
                parserSyncPref.setOnPreferenceClickListener(preference -> {
                    showParserSyncDialog();
                    return true;
                });
            }
            Preference backupExport = findPreference("backup_export");
            if (backupExport != null) {
                backupExport.setOnPreferenceClickListener(preference -> {
                    exportLauncher.launch("mangafire_backup_" + System.currentTimeMillis() + ".json");
                    return true;
                });
            }

            Preference backupImport = findPreference("backup_import");
            if (backupImport != null) {
                backupImport.setOnPreferenceClickListener(preference -> {
                    importLauncher.launch(new String[]{"application/json", "text/*", "*/*"});
                    return true;
                });
            }

            Preference logout = findPreference("logout");
            if (logout != null) {
                logout.setOnPreferenceClickListener(preference -> {
                    SupabaseManager.getInstance(requireContext()).signOut();
                    prefs.clearUser();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                });
            }
        }

        // نافذة اختيار الجودة
        
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

        private void showImageQualityDialog() {
            String[] options = {"الأصلية (100%)", "عالية (80%)", "متوسطة (60%)", "منخفضة (40%)"};
            int[] values = {100, 80, 60, 40};

            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("MangaFirePrefs", android.content.Context.MODE_PRIVATE);
            int currentQuality = prefs.getInt("image_quality_value", 100);

            int selectedIndex = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == currentQuality) {
                    selectedIndex = i;
                    break;
                }
            }

            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("اختر دقة صور المانجا")
                    .setSingleChoiceItems(options, selectedIndex, (dialog, which) -> {
                        int selectedQuality = values[which];
                        prefs.edit().putInt("image_quality_value", selectedQuality).apply();
                        Toast.makeText(requireContext(), "تم تعيين الجودة على " + options[which], Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .show();
        }
    }
}
