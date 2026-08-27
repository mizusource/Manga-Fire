#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        Map<String, Object> updates = new HashMap<>();
        updates.put("comments_enabled", switchComments.isChecked());
        updates.put("replies_enabled", switchReplies.isChecked());
        updates.put("spoilers_enabled", switchSpoilers.isChecked());
        updates.put("profanity_filter_enabled", switchProfanity.isChecked());
        
        updates.put("announcement_enabled", switchBanner.isChecked());
        updates.put("announcement_text", etBannerText.getText() != null ? etBannerText.getText().toString() : "");
        
        updates.put("maintenance_mode", switchMaintenance.isChecked());
        updates.put("maintenance_message", etMaintenanceMessage.getText() != null ? etMaintenanceMessage.getText().toString() : "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("settings");
        ref.updateChildren(updates).addOnCompleteListener(task -> {
            btnSave.setEnabled(true);
            btnSave.setText("حفظ الإعدادات والتطبيق");
            if (task.isSuccessful()) {
                Toast.makeText(this, "✅ تم تطبيق الإعدادات وحفظها في السيرفر", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ فشل حفظ الإعدادات", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
INNER_EOF
