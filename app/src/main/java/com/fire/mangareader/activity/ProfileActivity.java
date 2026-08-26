package com.fire.mangareader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail, tvFavCount, tvReadCount, tvDownloadsCount;
    private ImageView profileImage, profileBanner;
    private PreferenceManager prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> bannerPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfilePic(imageUri.toString());
                            Glide.with(this).load(imageUri).circleCrop().into(profileImage);
                            Toast.makeText(this, "تم تحديث الصورة الشخصية", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        bannerPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfileBanner(imageUri.toString());
                            Glide.with(this).load(imageUri).centerCrop().into(profileBanner);
                            Toast.makeText(this, "تم تحديث الغلاف", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        prefs = new PreferenceManager(this);
        profileImage = findViewById(R.id.profileImage);
        profileBanner = findViewById(R.id.profileBanner);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvFavCount = findViewById(R.id.tvFavCount);
        tvReadCount = findViewById(R.id.tvReadCount);
        tvDownloadsCount = findViewById(R.id.tvDownloadsCount);

        MaterialButton btnSettings = findViewById(R.id.btnSettings);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            tvUserName.setText("قارئ المانجا (حساب زائر)");
            tvUserEmail.setText("سجل الدخول لحفظ ومزامنة بياناتك");
            btnSettings.setText("تسجيل الدخول");
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
            btnLogout.setVisibility(View.GONE);
            
            profileImage.setOnClickListener(v -> Toast.makeText(this, "سجل الدخول لتعديل الصورة", Toast.LENGTH_SHORT).show());
            profileBanner.setOnClickListener(v -> Toast.makeText(this, "سجل الدخول لتعديل الغلاف", Toast.LENGTH_SHORT).show());
        } else {
            tvUserName.setText(prefs.getUserName());
            tvUserEmail.setText(prefs.getUserEmail());

            btnSettings.setOnClickListener(v -> showEditProfileDialog());
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("تسجيل الخروج")
                        .setMessage("هل أنت متأكد من تسجيل الخروج؟")
                        .setPositiveButton("نعم", (d, w) -> {
                            FirebaseAuth.getInstance().signOut();
                            prefs.clearUser();
                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        })
                        .setNegativeButton("إلغاء", null)
                        .show();
            });

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
            });
        }).start();
    }

    private void showEditProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etEditName = dialogView.findViewById(R.id.etEditName);
        if(etEditName != null) {
            etEditName.setText(prefs.getUserName());
        }

        new AlertDialog.Builder(this)
                .setTitle("تعديل الحساب")
                .setView(dialogView)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    if (etEditName != null) {
                        String newName = etEditName.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            prefs.setUserName(newName);
                            tvUserName.setText(newName);
                            Toast.makeText(this, "تم تحديث الاسم", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}
