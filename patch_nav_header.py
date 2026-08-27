import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

content = content.replace('btnMenuToggle.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));',
'''btnMenuToggle.setOnClickListener(v -> {
            updateNavHeader();
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
        });''')

new_method = '''
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
}'''

if 'private void updateNavHeader()' not in content:
    content = content.rsplit('}', 1)[0] + new_method

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
