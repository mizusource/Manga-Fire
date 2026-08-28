with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

settings_listener = '''
        loadAdminSettings();
    }

    private void loadAdminSettings() {
        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("settings")
            .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        View maintenanceLayout = findViewById(R.id.maintenanceLayout);
                        android.widget.TextView tvMaintenanceMessage = findViewById(R.id.tvMaintenanceMessage);
                        View announcementBanner = findViewById(R.id.announcementBanner);
                        android.widget.TextView tvAnnouncementText = findViewById(R.id.tvAnnouncementText);

                        Boolean maintenanceMode = snapshot.child("maintenance_mode").getValue(Boolean.class);
                        String maintenanceMessage = snapshot.child("maintenance_message").getValue(String.class);
                        if (maintenanceMode != null && maintenanceMode) {
                            if (maintenanceLayout != null) maintenanceLayout.setVisibility(View.VISIBLE);
                            if (tvMaintenanceMessage != null && maintenanceMessage != null && !maintenanceMessage.isEmpty()) {
                                tvMaintenanceMessage.setText(maintenanceMessage);
                            }
                        } else {
                            if (maintenanceLayout != null) maintenanceLayout.setVisibility(View.GONE);
                        }

                        Boolean announcementEnabled = snapshot.child("announcement_enabled").getValue(Boolean.class);
                        String announcementText = snapshot.child("announcement_text").getValue(String.class);
                        if (announcementEnabled != null && announcementEnabled) {
                            if (announcementBanner != null) announcementBanner.setVisibility(View.VISIBLE);
                            if (tvAnnouncementText != null && announcementText != null && !announcementText.isEmpty()) {
                                tvAnnouncementText.setText(announcementText);
                            }
                        } else {
                            if (announcementBanner != null) announcementBanner.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) { }
            });
    }

    private void fetchLatestUpdates() {'''
content = content.replace('    private void fetchLatestUpdates() {', settings_listener)

edit_profile_logic = '''
                android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);
                if (btnEditProfile != null) {
                    btnEditProfile.setVisibility(View.VISIBLE);
                    btnEditProfile.setOnClickListener(v -> {
                        startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                    });
                }
                
                android.view.Menu menu = navView.getMenu();'''
content = content.replace('                android.view.Menu menu = navView.getMenu();', edit_profile_logic, 1) # Only first match (user != null)

edit_profile_logic_gone = '''
                android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);
                if (btnEditProfile != null) btnEditProfile.setVisibility(View.GONE);
                
                android.view.Menu menu = navView.getMenu();'''
content = content.replace('                android.view.Menu menu = navView.getMenu();', edit_profile_logic_gone, 1) # Second match (user == null)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
