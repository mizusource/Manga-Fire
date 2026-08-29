package com.fire.mangareader.activity;
import com.fire.mangareader.network.SupabaseManager;

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
import com.fire.mangareader.utils.DonutChartView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUserName, tvFullName;
    private ImageView profileImage;
    private PreferenceManager prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> bannerPickerLauncher;

    private DonutChartView donutChart;
    private TextView legendFav, legendRead, legendPlan, legendComp, legendDrop, tvTotalChapters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfilePic(imageUri.toString());
                            profileImage.setColorFilter(null);
                            Glide.with(this).load(imageUri).circleCrop().into(profileImage);
                            Toast.makeText(this, "تم تحديث الصورة الشخصية", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        prefs = new PreferenceManager(this);
        profileImage = findViewById(R.id.profileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvFullName = findViewById(R.id.tvFullName);
        
        donutChart = findViewById(R.id.donutChart);
        legendFav = findViewById(R.id.legendFav);
        legendRead = findViewById(R.id.legendRead);
        legendPlan = findViewById(R.id.legendPlan);
        legendComp = findViewById(R.id.legendComp);
        legendDrop = findViewById(R.id.legendDrop);
        tvTotalChapters = findViewById(R.id.tvTotalChapters);

        ImageView btnSettings = findViewById(R.id.btnSettings);
        ImageView btnLogout = findViewById(R.id.btnLogout);
        ImageView btnChangeBanner = findViewById(R.id.btnChangeBanner);

        if (!SupabaseManager.getInstance(this).isLoggedIn()) {
            tvUserName.setText("تسجيل الدخول");
            tvFullName.setText("زائر");
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
            btnLogout.setVisibility(View.GONE);
            btnChangeBanner.setVisibility(View.GONE);
            profileImage.setOnClickListener(v -> Toast.makeText(this, "سجل الدخول لتعديل الصورة", Toast.LENGTH_SHORT).show());
        } else {
            tvUserName.setText(prefs.getUserName());
            tvFullName.setText(prefs.getUserName());
            btnSettings.setOnClickListener(v -> showEditProfileDialog());
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("تسجيل الخروج")
                        .setMessage("هل أنت متأكد من تسجيل الخروج؟")
                        .setPositiveButton("نعم", (d, w) -> {
                            SupabaseManager.getInstance(this).signOut();
                            prefs.clearUser();
                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        })
                        .setNegativeButton("إلغاء", null)
                        .show();
            });
            View.OnClickListener pickImage = v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            };
            profileImage.setOnClickListener(pickImage);
            btnChangeBanner.setOnClickListener(pickImage); // just use same logic for now
        }

        String savedPic = prefs.getProfilePic();
        if (savedPic != null && !savedPic.isEmpty()) {
            profileImage.setColorFilter(null);
            Glide.with(this).load(Uri.parse(savedPic)).circleCrop().into(profileImage);
        } else if (false) {
            profileImage.setColorFilter(null);
            
        }

        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int favCount = db.mangaDao().getAllFavorites().size();
            int readCount = db.chapterStateDao().getAllReadStates().size();
            
            // Just simulate other list categories for now since we don't have them in db
            int planCount = 0;
            int compCount = 0;
            int dropCount = 0;

            runOnUiThread(() -> {
                legendFav.setText("■ المفضلة : " + favCount);
                legendRead.setText("■ اشاهدها حاليا : " + (readCount > 0 ? 1 : 0)); // Fake manga count based on read chapters
                legendPlan.setText("■ ارغب بمشاهدتها : " + planCount);
                legendComp.setText("■ تم مشاهدتها : " + compCount);
                legendDrop.setText("■ لا ارغب بمشاهدتها : " + dropCount);
                
                tvTotalChapters.setText("عدد الفصول التي تم مشاهدتها: " + readCount);
                
                donutChart.setData(favCount, (readCount > 0 ? 1 : 0), planCount, compCount, dropCount);
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
                            tvFullName.setText(newName);
                            Toast.makeText(this, "تم تحديث الاسم", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}