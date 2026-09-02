package com.fire.mangareader.presentation.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.DownloadQuality;

public class DownloadQualityDialog {

    public interface QualityCallback {
        void onQualitySelected(DownloadQuality quality);
    }

    public static void show(Context context, QualityCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("اختر جودة التحميل");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentQuality = prefs.getString("download_quality", "MEDIUM");

        String[] options = {"عالية (أفضل جودة - حجم كبير)", "متوسطة (توازن جيد)", "ضعيفة (توفير البيانات)"};
        DownloadQuality[] values = {DownloadQuality.HIGH, DownloadQuality.MEDIUM, DownloadQuality.LOW};
        
        int checkedItem = 1;
        if (currentQuality.equals("HIGH")) checkedItem = 0;
        else if (currentQuality.equals("LOW")) checkedItem = 2;

        builder.setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
            DownloadQuality selected = values[which];
            prefs.edit().putString("download_quality", selected.name()).apply();
            
            if (callback != null) {
                callback.onQualitySelected(selected);
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
