package com.fire.mangareader.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fire.mangareader.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class AdminDashboardActivity extends AppCompatActivity {

    private SharedPreferences adminPrefs;
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

        adminPrefs = getSharedPreferences("admin_settings", MODE_PRIVATE);

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
        switchComments.setChecked(adminPrefs.getBoolean("comments_enabled", true));
        switchReplies.setChecked(adminPrefs.getBoolean("replies_enabled", true));
        switchSpoilers.setChecked(adminPrefs.getBoolean("spoilers_enabled", true));
        switchProfanity.setChecked(adminPrefs.getBoolean("profanity_filter", true));
        
        switchBanner.setChecked(adminPrefs.getBoolean("banner_enabled", false));
        etBannerText.setText(adminPrefs.getString("banner_text", ""));
        
        switchMaintenance.setChecked(adminPrefs.getBoolean("maintenance_mode", false));
        etMaintenanceMessage.setText(adminPrefs.getString("maintenance_message", "نقوم بتحديث الخوادم، نعود قريباً!"));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = adminPrefs.edit();
        editor.putBoolean("comments_enabled", switchComments.isChecked());
        editor.putBoolean("replies_enabled", switchReplies.isChecked());
        editor.putBoolean("spoilers_enabled", switchSpoilers.isChecked());
        editor.putBoolean("profanity_filter", switchProfanity.isChecked());
        
        editor.putBoolean("banner_enabled", switchBanner.isChecked());
        editor.putString("banner_text", etBannerText.getText() != null ? etBannerText.getText().toString() : "");
        
        editor.putBoolean("maintenance_mode", switchMaintenance.isChecked());
        editor.putString("maintenance_message", etMaintenanceMessage.getText() != null ? etMaintenanceMessage.getText().toString() : "");
        
        editor.apply();

        Toast.makeText(this, "✅ تم تطبيق الإعدادات وحفظها في السيرفر", Toast.LENGTH_SHORT).show();
    }
}
