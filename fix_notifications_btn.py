with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

old_code = """        android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
        
        if (supabase.isLoggedIn()) {"""

new_code = """        android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
        android.view.View btnNotifications = headerView.findViewById(R.id.btnNotifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, com.fire.mangareader.activity.NotificationsActivity.class)));
        }
        
        if (supabase.isLoggedIn()) {"""

if old_code in content:
    with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
        f.write(content.replace(old_code, new_code))
    print("Patched MainActivity.java for Notifications button")
else:
    print("Could not find the target code in MainActivity.java")
