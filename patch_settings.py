import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/SettingsActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import androidx.preference.SwitchPreferenceCompat;', 'import androidx.preference.SwitchPreferenceCompat;\nimport com.fire.mangareader.domain.usecase.user.EnableChapterNotificationUseCase;')

target = """            SwitchPreferenceCompat wifiPref = findPreference("wifi_only");"""

replacement = """            SwitchPreferenceCompat notifPref = findPreference("notifications_enabled");
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

            SwitchPreferenceCompat wifiPref = findPreference("wifi_only");"""

content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched SettingsActivity.java")
