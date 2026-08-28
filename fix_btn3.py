with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# I will just insert the declaration at the beginning of updateNavHeader
new_method = '''    private void updateNavHeader() {
        android.view.View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);'''

content = content.replace('''    private void updateNavHeader() {
        android.view.View headerView = navView.getHeaderView(0);
        if (headerView != null) {''', new_method)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
