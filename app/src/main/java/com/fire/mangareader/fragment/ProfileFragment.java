package com.fire.mangareader.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import com.fire.mangareader.activity.LoginActivity;
import com.fire.mangareader.activity.SettingsActivity;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    
    private TextView tvUserName, tvUserEmail, tvBio, tvFavCount, tvReadCount, tvDownloadsCount, tvRank;
    private ImageView profileImage, profileBanner;
    private PreferenceManager prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> bannerPickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. ميزة التقاط الصورة الشخصية
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            requireContext().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfilePic(imageUri.toString());
                            
                            // قص الصورة الشخصية بشكل دائري
                            Glide.with(this)
                                 .load(imageUri)
                                 .circleCrop()
                                 .into(profileImage);
                        }
                    }
                }
        );

        // 2. ميزة التقاط صورة الغلاف (Banner)
        bannerPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            requireContext().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.setProfileBanner(imageUri.toString());
                            
                            // تعبئة الغلاف بالكامل
                            Glide.with(this)
                                 .load(imageUri)
                                 .centerCrop()
                                 .into(profileBanner);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        prefs = new PreferenceManager(requireContext());

        // ربط العناصر بالتصميم
        profileImage = view.findViewById(R.id.profileImage);
        profileBanner = view.findViewById(R.id.profileBanner);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvBio = view.findViewById(R.id.tvBio);
        tvRank = view.findViewById(R.id.tvRank); 
        tvFavCount = view.findViewById(R.id.tvFavCount);
        tvReadCount = view.findViewById(R.id.tvReadCount);
        tvDownloadsCount = view.findViewById(R.id.tvDownloadsCount);
        
        MaterialButton btnSettings = view.findViewById(R.id.btnSettings);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        
        // تعيين النصوص المحفوظة
        tvUserName.setText(prefs.getUserName() != null ? prefs.getUserName() : "Mostafa");
        tvUserEmail.setText(prefs.getUserEmail() != null ? prefs.getUserEmail() : "mostafayano681@gmail.com");
        
        if (tvBio != null) {
            tvBio.setText(prefs.getUserBio() != null ? prefs.getUserBio() : "قارئ شغوف بالمانها والمانجا ⚡");
        }

        // تحميل الصورة الشخصية إن وجدت
        String savedPic = prefs.getProfilePic();
        if (savedPic != null) {
            Glide.with(this)
                 .load(Uri.parse(savedPic))
                 .circleCrop()
                 .into(profileImage);
        }

        // تحميل صورة الغلاف إن وجدت
        String savedBanner = prefs.getProfileBanner();
        if (savedBanner != null) {
            Glide.with(this)
                 .load(Uri.parse(savedBanner))
                 .centerCrop()
                 .into(profileBanner);
        }

        // جلب الإحصائيات من قاعدة البيانات وتحديث الرتبة
        loadUserStatistics();

        // أوامر النقر (Clicks)
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

        tvUserName.setOnClickListener(v -> showEditDialog("تغيير الاسم", tvUserName.getText().toString(), newValue -> {
            prefs.saveUser(prefs.getUserUid(), prefs.getUserEmail(), newValue, prefs.isGuest());
            tvUserName.setText(newValue);
        }));

        if (tvBio != null) {
            tvBio.setOnClickListener(v -> showEditDialog("تعديل البايو", tvBio.getText().toString(), newValue -> {
                prefs.setUserBio(newValue);
                tvBio.setText(newValue);
            }));
        }

        btnSettings.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
        
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            prefs.clearUser();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
        
        return view;
    }

    // دالة لإظهار مربع حوار (Dialog) لتعديل النصوص
    private void showEditDialog(String title, String currentValue, OnTextSavedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setText(currentValue);
        input.setTextColor(Color.WHITE);
        builder.setView(input);

        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                listener.onSaved(newText);
            } else {
                Toast.makeText(requireContext(), "لا يمكن ترك الحقل فارغاً", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private interface OnTextSavedListener {
        void onSaved(String newValue);
    }

    // دالة جلب إحصائيات المستخدم
    private void loadUserStatistics() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                int favCount = db.mangaDao().getAllFavorites() != null ? db.mangaDao().getAllFavorites().size() : 0;
                int downloadCount = db.downloadDao().getAllDownloads() != null ? db.downloadDao().getAllDownloads().size() : 0;
                
                // جلب الفصول المقروءة 
                int readCount = db.chapterStateDao().getAllReadStates() != null ? db.chapterStateDao().getAllReadStates().size() : 0; 

                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        tvFavCount.setText(String.valueOf(favCount));
                        tvDownloadsCount.setText(String.valueOf(downloadCount));
                        tvReadCount.setText(String.valueOf(readCount));
                        
                        // تفعيل خوارزمية الرتبة
                        updateRank(readCount);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // خوارزمية تحديد الرتبة (Rank Leveling System)
    private void updateRank(int readChaptersCount) {
        String rankName;
        int rankColor;

        if (readChaptersCount <= 10) {
            rankName = "صياد مبتدئ (E-Rank)";
            rankColor = Color.parseColor("#9E9E9E"); // رمادي
        } else if (readChaptersCount <= 50) {
            rankName = "متدرب (D-Rank)";
            rankColor = Color.parseColor("#4CAF50"); // أخضر
        } else if (readChaptersCount <= 150) {
            rankName = "مغامر (C-Rank)";
            rankColor = Color.parseColor("#2196F3"); // أزرق
        } else if (readChaptersCount <= 300) {
            rankName = "خبير (B-Rank)";
            rankColor = Color.parseColor("#9C27B0"); // بنفسجي
        } else if (readChaptersCount <= 600) {
            rankName = "نخبة (A-Rank)";
            rankColor = Color.parseColor("#FF9800"); // برتقالي
        } else {
            rankName = "أسطورة (S-Rank) 👑";
            rankColor = Color.parseColor("#FFD700"); // ذهبي
        }

        if (tvRank != null) {
            tvRank.setText(rankName);
            tvRank.setTextColor(rankColor);
        }
    }
}
