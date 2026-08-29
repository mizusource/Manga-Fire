package com.fire.mangareader.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.fire.mangareader.R;
import com.fire.mangareader.utils.AppAdminSettings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private MaterialSwitch switchComments, switchReplies, switchSpoilers, switchProfanity;
    private MaterialSwitch switchBanner, switchMaintenance;
    private TextInputEditText etBannerText, etMaintenanceMessage;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        switchComments = findViewById(R.id.switchComments);
        switchReplies = findViewById(R.id.switchReplies);
        switchSpoilers = findViewById(R.id.switchSpoilers);
        switchProfanity = findViewById(R.id.switchProfanity);
        switchBanner = findViewById(R.id.switchBanner);
        switchMaintenance = findViewById(R.id.switchMaintenance);
        etBannerText = findViewById(R.id.etBannerText);
        etMaintenanceMessage = findViewById(R.id.etMaintenanceMessage);
        btnSave = findViewById(R.id.btnSave);

        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        switchComments.setChecked(AppAdminSettings.commentsEnabled);
        switchReplies.setChecked(AppAdminSettings.repliesEnabled);
        switchSpoilers.setChecked(AppAdminSettings.spoilersEnabled);
        switchProfanity.setChecked(AppAdminSettings.profanityFilterEnabled);
        
        switchBanner.setChecked(AppAdminSettings.announcementEnabled);
        etBannerText.setText(AppAdminSettings.announcementText);
        
        switchMaintenance.setChecked(AppAdminSettings.maintenanceMode);
        etMaintenanceMessage.setText(AppAdminSettings.maintenanceMessage);
    }

    private void saveSettings() {
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
    }
}
