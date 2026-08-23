        NavigationView navigationView = findViewById(R.id.nav_view);
        android.view.View headerView = navigationView.getHeaderView(0);
        android.widget.TextView navName = headerView.findViewById(R.id.navHeaderName);
        android.widget.TextView navEmail = headerView.findViewById(R.id.navHeaderEmail);
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            navName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            navEmail.setText(user.getEmail());
        } else {
            navName.setText("Guest");
            navEmail.setText("Login to access features");
        }
