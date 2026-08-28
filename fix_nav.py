with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import re

new_method = '''    private void updateNavHeader() {
        android.view.View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);
            android.widget.TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            android.widget.TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
            android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
            
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                navHeaderName.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "المستخدم");
                navHeaderEmail.setText(user.getEmail());
                headerView.setOnClickListener(null);
                
                com.fire.mangareader.utils.PreferenceManager pm = new com.fire.mangareader.utils.PreferenceManager(this);
                if (pm.getProfilePic() != null && !pm.getProfilePic().isEmpty()) {
                    navHeaderImage.setColorFilter(null);
                    com.bumptech.glide.Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(navHeaderImage);
                } else if (user.getPhotoUrl() != null) {
                    navHeaderImage.setColorFilter(null);
                    com.bumptech.glide.Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navHeaderImage);
                } else {
                    navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                    navHeaderImage.setColorFilter(android.graphics.Color.GRAY);
                }
                
                if (btnEditProfile != null) {
                    btnEditProfile.setVisibility(android.view.View.VISIBLE);
                    btnEditProfile.setOnClickListener(v -> {
                        startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                    });
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
                navHeaderImage.setColorFilter(android.graphics.Color.GRAY);
                headerView.setOnClickListener(v -> {
                    startActivity(new android.content.Intent(MainActivity.this, LoginActivity.class));
                });
                
                if (btnEditProfile != null) {
                    btnEditProfile.setVisibility(android.view.View.GONE);
                }
                
                android.view.Menu menu = navView.getMenu();
                android.view.MenuItem adminItem = menu.findItem(R.id.nav_admin);
                if (adminItem != null) adminItem.setVisible(false);
            }
        }
    }
}'''

content = re.sub(r'    private void updateNavHeader\(\) \{.*$', new_method, content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
