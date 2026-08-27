#!/bin/bash
sed -i 's/btnMenuToggle.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));/btnMenuToggle.setOnClickListener(v -> {\n            updateNavHeader();\n            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);\n        });/g' app/src/main/java/com/fire/mangareader/activity/MainActivity.java

cat << 'INNER_EOF' >> app/src/main/java/com/fire/mangareader/activity/MainActivity.java
    private void updateNavHeader() {
        android.view.View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            android.widget.TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            android.widget.TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
            android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                navHeaderName.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "المستخدم");
                navHeaderEmail.setText(user.getEmail());
                headerView.setOnClickListener(null);
                
                if (user.getPhotoUrl() != null) {
                    com.bumptech.glide.Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navHeaderImage);
                } else {
                    navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                }
                
                android.view.Menu menu = navView.getMenu();
                android.view.MenuItem adminItem = menu.findItem(R.id.nav_admin);
                if (adminItem != null) {
                    adminItem.setVisible(user.getEmail() != null && user.getEmail().equals("mstfybdwy633@gmail.com"));
                }
            } else {
                navHeaderName.setText("تسجيل الدخول");
                navHeaderEmail.setText("انقر هنا لتسجيل الدخول");
                navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                headerView.setOnClickListener(v -> {
                    startActivity(new android.content.Intent(MainActivity.this, LoginActivity.class));
                });
                
                android.view.Menu menu = navView.getMenu();
                android.view.MenuItem adminItem = menu.findItem(R.id.nav_admin);
                if (adminItem != null) adminItem.setVisible(false);
            }
        }
    }
INNER_EOF
# Let's fix the closing brace we just appended after MainActivity
sed -i '/private void updateNavHeader()/,$d' app/src/main/java/com/fire/mangareader/activity/MainActivity.java
