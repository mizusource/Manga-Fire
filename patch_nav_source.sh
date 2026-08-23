sed -i '/if (id == R.id.nav_home)/i \
        if (id == R.id.nav_source) {\n            showSourceSelectionDialog();\n            drawerLayout.closeDrawers();\n            return true;\n        }' app/src/main/java/com/fire/mangareader/activity/MainActivity.java
