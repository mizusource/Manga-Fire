package com.fire.mangareader.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvBio, tvFavCount, tvReadCount, tvDownloadsCount, tvRank;
    private ImageView profileImage, profileBanner;
    private PreferenceManager prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> bannerPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            this.getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfilePic(imageUri.toString());
                            Glide.with(this).load(imageUri).circleCrop().into(profileImage);
                        }
                    }
                }
        );

        bannerPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            this.getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfileBanner(imageUri.toString());
                            Glide.with(this).load(imageUri).centerCrop().into(profileBanner);
                        }
                    }
                }
        );

        setContentView(R.layout.fragment_profile);
        View view = findViewById(android.R.id.content);

        prefs = new PreferenceManager(this);

        profileImage = view.findViewById(R.id.profileImage);
        profileBanner = view.findViewById(R.id.profileBanner);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvBio = view.findViewById(R.id.tvBio);
        tvFavCount = view.findViewById(R.id.tvFavCount);
        tvReadCount = view.findViewById(R.id.tvReadCount);
        tvDownloadsCount = view.findViewById(R.id.tvDownloadsCount);
        tvRank = view.findViewById(R.id.tvRank);
        MaterialButton btnEditProfile = view.findViewById(R.id.btnSettings);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            tvUserName.setText("Guest");
            tvUserEmail.setText("Login to edit profile");
            btnEditProfile.setText("Login");
            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
            profileImage.setOnClickListener(v -> Toast.makeText(this, "Login to edit", Toast.LENGTH_SHORT).show());
            profileBanner.setOnClickListener(v -> Toast.makeText(this, "Login to edit", Toast.LENGTH_SHORT).show());
        } else {
            tvUserName.setText(prefs.getUserName());
            tvUserEmail.setText(prefs.getUserEmail());
            tvBio.setText(prefs.getUserBio());
            
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

            profileImage.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            });

            profileBanner.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                bannerPickerLauncher.launch(intent);
            });
        }

        String savedPic = prefs.getProfilePic();
        if (savedPic != null && !savedPic.isEmpty()) {
            Glide.with(this).load(Uri.parse(savedPic)).circleCrop().into(profileImage);
        }

        String savedBanner = prefs.getProfileBanner();
        if (savedBanner != null && !savedBanner.isEmpty()) {
            Glide.with(this).load(Uri.parse(savedBanner)).centerCrop().into(profileBanner);
        }

        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int favCount = db.mangaDao().getAllFavorites().size();
            int readCount = db.chapterStateDao().getAllReadStates().size();
            int downCount = db.downloadDao().getAllDownloads().size();
            
            runOnUiThread(() -> {
                tvFavCount.setText(String.valueOf(favCount));
                tvReadCount.setText(String.valueOf(readCount));
                tvDownloadsCount.setText(String.valueOf(downCount));
                
                if (readCount > 100) tvRank.setText("Otaku Emperor 👑");
                else if (readCount > 50) tvRank.setText("Veteran Reader ⚔️");
                else if (readCount > 10) tvRank.setText("Manga Explorer 🧭");
                else tvRank.setText("Beginner 👶");
            });
        }).start();
    }

    private void showEditProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText etEditName = dialogView.findViewById(R.id.etEditName);
        EditText etEditBio = dialogView.findViewById(R.id.etEditBio);

        etEditName.setText(prefs.getUserName());
        etEditBio.setText(prefs.getUserBio());

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etEditName.getText().toString().trim();
                    String newBio = etEditBio.getText().toString().trim();
                    
                    if (!newName.isEmpty()) {
                        prefs.setUserName(newName);
                        tvUserName.setText(newName);
                    }
                    if (!newBio.isEmpty()) {
                        prefs.setUserBio(newBio);
                        tvBio.setText(newBio);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
