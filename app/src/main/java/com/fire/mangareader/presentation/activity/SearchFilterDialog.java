package com.fire.mangareader.presentation.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.SearchRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SearchFilterDialog {

    public interface OnFilterAppliedListener {
        void onFilterApplied(SearchRequest request);
    }

    public static void show(Context context, String currentQuery, OnFilterAppliedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        // Create simple layout programmatically for the bottom sheet
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        // Status
        android.widget.TextView tvStatus = new android.widget.TextView(context);
        tvStatus.setText("حالة المانجا:");
        tvStatus.setTextSize(16);
        tvStatus.setPadding(0, 0, 0, 16);
        layout.addView(tvStatus);

        RadioGroup rgStatus = new RadioGroup(context);
        rgStatus.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbAll = new RadioButton(context); rbAll.setText("الكل"); rbAll.setId(View.generateViewId()); rbAll.setChecked(true);
        RadioButton rbOngoing = new RadioButton(context); rbOngoing.setText("مستمرة"); rbOngoing.setId(View.generateViewId());
        RadioButton rbCompleted = new RadioButton(context); rbCompleted.setText("مكتملة"); rbCompleted.setId(View.generateViewId());
        rgStatus.addView(rbAll); rgStatus.addView(rbOngoing); rgStatus.addView(rbCompleted);
        layout.addView(rgStatus);

        // Genres
        android.widget.TextView tvGenres = new android.widget.TextView(context);
        tvGenres.setText("التصنيفات:");
        tvGenres.setTextSize(16);
        tvGenres.setPadding(0, 32, 0, 16);
        layout.addView(tvGenres);

        String[] genresList = {"أكشن", "خيال", "رومانسي", "دراما", "إيسيكاي", "رعب"};
        List<CheckBox> checkBoxes = new ArrayList<>();
        
        // Simple Grid using multiple LinearLayouts
        LinearLayout grid = new LinearLayout(context);
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout row = null;
        for (int i = 0; i < genresList.length; i++) {
            if (i % 3 == 0) {
                row = new LinearLayout(context);
                row.setOrientation(LinearLayout.HORIZONTAL);
                grid.addView(row);
            }
            CheckBox cb = new CheckBox(context);
            cb.setText(genresList[i]);
            cb.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            checkBoxes.add(cb);
            if (row != null) row.addView(cb);
        }
        layout.addView(grid);

        Button btnApply = new Button(context);
        btnApply.setText("تطبيق الفلاتر 🔍");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 40, 0, 0);
        btnApply.setLayoutParams(params);
        btnApply.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
        btnApply.setTextColor(android.graphics.Color.WHITE);
        layout.addView(btnApply);

        dialog.setContentView(layout);

        btnApply.setOnClickListener(v -> {
            SearchRequest req = new SearchRequest(currentQuery);
            
            int checkedId = rgStatus.getCheckedRadioButtonId();
            if (checkedId == rbOngoing.getId()) req.setStatus(1);
            else if (checkedId == rbCompleted.getId()) req.setStatus(2);
            else req.setStatus(0);

            List<String> selectedGenres = new ArrayList<>();
            for (CheckBox cb : checkBoxes) {
                if (cb.isChecked()) {
                    selectedGenres.add(cb.getText().toString());
                }
            }
            req.setGenres(selectedGenres);

            listener.onFilterApplied(req);
            dialog.dismiss();
        });

        dialog.show();
    }
}
