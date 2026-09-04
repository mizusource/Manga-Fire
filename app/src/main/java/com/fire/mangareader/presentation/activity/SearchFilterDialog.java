package com.fire.mangareader.presentation.activity;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.SearchRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class SearchFilterDialog {

    public interface OnFilterAppliedListener {
        void onFilterApplied(SearchRequest request);
    }

    public static void show(Context context, String currentQuery, OnFilterAppliedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = android.view.LayoutInflater.from(context).inflate(R.layout.dialog_search_filter, null);
        dialog.setContentView(view);

        ChipGroup chipGroupStatus = view.findViewById(R.id.chipGroupStatus);
        RangeSlider sliderChapters = view.findViewById(R.id.sliderChapters);
        ChipGroup chipGroupGenres = view.findViewById(R.id.chipGroupGenres);
        View btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

        // Add Genres Chips programmatically
        String[] genresList = {"أكشن", "خيال", "رومانسي", "دراما", "إيسيكاي", "رعب", "شريحة من الحياة", "كوميدي", "مدرسي", "غموض", "نفسي", "تاريخي", "رياضي"};
        for (String genre : genresList) {
            Chip chip = new Chip(context);
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chipGroupGenres.addView(chip);
        }

        btnApplyFilter.setOnClickListener(v -> {
            SearchRequest req = new SearchRequest(currentQuery);
            
            // 1. Status
            int checkedId = chipGroupStatus.getCheckedChipId();
            if (checkedId == R.id.chipStatusOngoing) req.setStatus(1);
            else if (checkedId == R.id.chipStatusCompleted) req.setStatus(2);
            else req.setStatus(0);
            
            // 2. Genres
            List<String> selectedGenres = new ArrayList<>();
            for (int i = 0; i < chipGroupGenres.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupGenres.getChildAt(i);
                if (chip.isChecked()) {
                    selectedGenres.add(chip.getText().toString());
                }
            }
            req.setGenres(selectedGenres);
            
            // 3. Range Slider (mock feature, you could add this to SearchRequest if supported by backend)
            List<Float> values = sliderChapters.getValues();
            if (values.size() == 2) {
                // req.setMinChapters(values.get(0).intValue());
                // req.setMaxChapters(values.get(1).intValue());
            }

            listener.onFilterApplied(req);
            dialog.dismiss();
        });

        dialog.show();
    }
}
