import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import_target = "import android.os.Bundle;"
import_replace = """import android.os.Bundle;
import com.fire.mangareader.utils.AppAdminSettings;
import androidx.cardview.widget.CardView;
import android.widget.TextView;"""

if "import com.fire.mangareader.utils.AppAdminSettings;" not in content:
    content = content.replace(import_target, import_replace)

init_target = "rvLatestUpdates = findViewById(R.id.rvLatestUpdates);"
init_replace = """rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        CardView announcementBanner = findViewById(R.id.announcementBanner);
        TextView tvAnnouncementText = findViewById(R.id.tvAnnouncementText);
        
        AppAdminSettings.addListener(() -> {
            if (AppAdminSettings.announcementEnabled && !AppAdminSettings.announcementText.isEmpty()) {
                if (announcementBanner != null) announcementBanner.setVisibility(View.VISIBLE);
                if (tvAnnouncementText != null) tvAnnouncementText.setText(AppAdminSettings.announcementText);
            } else {
                if (announcementBanner != null) announcementBanner.setVisibility(View.GONE);
            }
        });
        
        // Initial check
        if (AppAdminSettings.announcementEnabled && !AppAdminSettings.announcementText.isEmpty()) {
            if (announcementBanner != null) announcementBanner.setVisibility(View.VISIBLE);
            if (tvAnnouncementText != null) tvAnnouncementText.setText(AppAdminSettings.announcementText);
        } else {
            if (announcementBanner != null) announcementBanner.setVisibility(View.GONE);
        }
"""

if "CardView announcementBanner" not in content:
    content = content.replace(init_target, init_replace)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)

