with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'r') as f:
    content = f.read()

import re
pattern = re.compile(r'private void saveSettings\(\) \{.*?\n    \}', re.DOTALL)
match = pattern.search(content)

if match:
    new_save = """private void saveSettings() {
        btnSave.setEnabled(false);
        btnSave.setText("جاري الحفظ...");
        
        AppAdminSettings.commentsEnabled = switchComments.isChecked();
        AppAdminSettings.repliesEnabled = switchReplies.isChecked();
        AppAdminSettings.spoilersEnabled = switchSpoilers.isChecked();
        AppAdminSettings.profanityFilterEnabled = switchProfanity.isChecked();
        
        AppAdminSettings.announcementEnabled = switchBanner.isChecked();
        AppAdminSettings.announcementText = etBannerText.getText() != null ? etBannerText.getText().toString() : "";
        
        AppAdminSettings.maintenanceMode = switchMaintenance.isChecked();
        AppAdminSettings.maintenanceMessage = etMaintenanceMessage.getText() != null ? etMaintenanceMessage.getText().toString() : "";
        
        android.content.SharedPreferences prefs = getApplicationContext().getSharedPreferences("speed_manga_admin_settings", android.content.Context.MODE_PRIVATE);
        prefs.edit()
            .putBoolean("comments_enabled", AppAdminSettings.commentsEnabled)
            .putBoolean("replies_enabled", AppAdminSettings.repliesEnabled)
            .putBoolean("spoilers_enabled", AppAdminSettings.spoilersEnabled)
            .putBoolean("profanity_filter_enabled", AppAdminSettings.profanityFilterEnabled)
            .putBoolean("announcement_enabled", AppAdminSettings.announcementEnabled)
            .putString("announcement_text", AppAdminSettings.announcementText)
            .putBoolean("maintenance_mode", AppAdminSettings.maintenanceMode)
            .putString("maintenance_message", AppAdminSettings.maintenanceMessage)
            .apply();
            
        Toast.makeText(this, "تم حفظ الإعدادات بنجاح!", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(true);
        btnSave.setText("حفظ الإعدادات");
        finish();
    }"""
    content = content.replace(match.group(0), new_save)
    with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'w') as f:
        f.write(content)
    print("Fixed AdminDashboardActivity saveSettings")
else:
    print("Could not find saveSettings")
