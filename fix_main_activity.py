import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# Remove drawer layout variables and setup
content = re.sub(r'private androidx\.drawerlayout\.widget\.DrawerLayout drawerLayout;', '', content)
content = re.sub(r'private com\.google\.android\.material\.navigation\.NavigationView navView;', '', content)
content = re.sub(r'drawerLayout = findViewById\(R\.id\.drawer_layout\);', '', content)
content = re.sub(r'navView = findViewById\(R\.id\.nav_view\);', '', content)
content = re.sub(r'android\.widget\.ImageView btnMenuToggle = findViewById\(R\.id\.btnMenuToggle\);', '', content)
content = re.sub(r'btnMenuToggle\.setOnClickListener[^;]+;', '', content)
content = re.sub(r'navView\.setNavigationItemSelectedListener[^;]+;', '', content)
content = re.sub(r'updateNavHeader\(\);', 'updateProfileAvatar();', content)

# Remove the entire updateNavHeader method
content = re.sub(r'private void updateNavHeader\(\) \{.*?(?=^    private void startBannerAutoScroll|^\})', '', content, flags=re.MULTILINE|re.DOTALL)

# Add updateProfileAvatar and BottomNavigationView logic
bottom_nav_logic = """
    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
                return true;
            } else if (id == R.id.nav_library) {
                startActivity(new android.content.Intent(MainActivity.this, LibraryActivity.class));
                return false;
            } else if (id == R.id.nav_downloads) {
                startActivity(new android.content.Intent(MainActivity.this, DownloadsActivity.class));
                return false;
            } else if (id == R.id.nav_profile) {
                startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                return false;
            }
            return false;
        });
        
        android.widget.ImageView btnProfileAvatar = findViewById(R.id.btnProfileAvatar);
        if (btnProfileAvatar != null) {
            btnProfileAvatar.setOnClickListener(v -> {
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    startActivity(new android.content.Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

    private void updateProfileAvatar() {
        android.widget.ImageView btnProfileAvatar = findViewById(R.id.btnProfileAvatar);
        if (btnProfileAvatar == null) return;
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            com.fire.mangareader.utils.PreferenceManager pm = new com.fire.mangareader.utils.PreferenceManager(this);
            if (pm.getProfilePic() != null && !pm.getProfilePic().isEmpty()) {
                com.bumptech.glide.Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(btnProfileAvatar);
            } else if (user.getPhotoUrl() != null) {
                com.bumptech.glide.Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(btnProfileAvatar);
            }
        } else {
            btnProfileAvatar.setImageResource(R.drawable.ic_drawer_profile);
        }
    }
"""

content = content.replace("public class MainActivity extends androidx.appcompat.app.AppCompatActivity {", "public class MainActivity extends androidx.appcompat.app.AppCompatActivity {\n" + bottom_nav_logic)

# Replace the call to parse html locally update header with updateProfileAvatar
content = content.replace("updateNavHeader();", "updateProfileAvatar();")

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
