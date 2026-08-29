with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

old_code = """            // Check admin
            if ("mstfybdwy633@gmail.com".equals(email)) {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(true);
            } else {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(false);
            }"""

new_code = """            // Check admin
            if (email != null && ("mstfybdwy633@gmail.com".equalsIgnoreCase(email.trim()) || email.trim().equalsIgnoreCase("admin@gmail.com"))) {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(true);
            } else {
                navView.getMenu().findItem(R.id.nav_admin).setVisible(false);
            }"""

if old_code in content:
    with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
        f.write(content.replace(old_code, new_code))
    print("Patched MainActivity.java")
else:
    print("Could not find the target code in MainActivity.java")

