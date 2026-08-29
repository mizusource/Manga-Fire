import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

nav_logic = """
    private void updateNavHeader() {
        com.fire.mangareader.network.SupabaseManager supabase = com.fire.mangareader.network.SupabaseManager.getInstance(this);
        com.fire.mangareader.utils.PreferenceManager prefs = new com.fire.mangareader.utils.PreferenceManager(this);
        
        com.google.android.material.navigation.NavigationView navView = findViewById(R.id.nav_view);
        android.view.View headerView = navView.getHeaderView(0);
        android.widget.TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
        android.widget.TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
        android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
        
        if (supabase.isLoggedIn()) {
            String name = prefs.getUserName();
            navHeaderName.setText(name != null && !name.isEmpty() ? name : "مستخدم");
            String email = prefs.getUserEmail();
            navHeaderEmail.setText(email != null ? email : "");
            headerView.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, com.fire.mangareader.activity.ProfileActivity.class)));
            
            // Check admin
            if ("mstfybdwy633@gmail.com".equals(email)) {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(true);
            } else {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(false);
            }
        } else {
            navHeaderName.setText("تسجيل الدخول");
            navHeaderEmail.setText("انقر هنا لتسجيل الدخول");
            navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
            headerView.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, com.fire.mangareader.activity.LoginActivity.class)));
            navView.getMenu().findItem(R.id.nav_admin).setVisible(false);
        }
    }
"""

content = re.sub(r'private void updateNavHeader\(\) \{\}', nav_logic.strip(), content)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
