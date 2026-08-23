    private void postComment() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Login is required to post comments.", Toast.LENGTH_LONG).show();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            return;
        }

        String text = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String username = currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty() 
                ? currentUser.getDisplayName() : "User";

        boolean isSpoiler = cbSpoiler.isChecked();
        long timestamp = System.currentTimeMillis();

        Comment newComment = new Comment(mangaUrl, username, text, timestamp, isSpoiler);

        db.collection("comments").add(newComment)
                .addOnSuccessListener(documentReference -> {
                    etComment.setText("");
                    cbSpoiler.setChecked(false);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send comment", Toast.LENGTH_SHORT).show());
    }
