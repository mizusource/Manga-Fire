#!/bin/bash
sed -i '/setContentView/a\
        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();\
        if (currentUser == null || currentUser.getEmail() == null || !currentUser.getEmail().equals("mstfybdwy633@gmail.com")) {\
            Toast.makeText(this, "عذراً، هذه الصفحة للمشرفين فقط", Toast.LENGTH_SHORT).show();\
            finish();\
            return;\
        }' app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java
