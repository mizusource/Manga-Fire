sed -i '/Preference clearCache = findPreference("clear_cache");/,/});/d' app/src/main/java/com/fire/mangareader/activity/SettingsActivity.java
sed -i '/Preference clearDownloads = findPreference("clear_downloads");/,/});/d' app/src/main/java/com/fire/mangareader/activity/SettingsActivity.java
sed -i '/SwitchPreferenceCompat wifiPref = findPreference("wifi_only");/i \
            Preference storageManager = findPreference("storage_manager");\
            if (storageManager != null) {\
                storageManager.setOnPreferenceClickListener(preference -> {\
                    startActivity(new Intent(requireContext(), StorageManagerActivity.class));\
                    return true;\
                });\
            }' app/src/main/java/com/fire/mangareader/activity/SettingsActivity.java
