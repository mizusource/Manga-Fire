package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.local.entity.CustomListEntity;
import com.fire.mangareader.data.local.entity.CustomListMangaCrossRef;
import com.fire.mangareader.presentation.adapter.CustomListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CustomListManagerActivity extends AppCompatActivity {
    private RecyclerView rvLists;
    private CustomListAdapter adapter;
    private List<CustomListEntity> listEntities = new ArrayList<>();
    private LinearLayout emptyStateLayout;
    
    private String selectedMangaUrl;
    private String selectedMangaTitle;
    private String selectedMangaCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_manager);

        if (getIntent() != null) {
            selectedMangaUrl = getIntent().getStringExtra("mangaUrl");
            selectedMangaTitle = getIntent().getStringExtra("mangaTitle");
            selectedMangaCover = getIntent().getStringExtra("mangaCover");
        }

        rvLists = findViewById(R.id.rvCustomLists);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        FloatingActionButton fabAddList = findViewById(R.id.fabAddList);

        adapter = new CustomListAdapter(listEntities, list -> {
            if (selectedMangaUrl != null && !selectedMangaUrl.isEmpty()) {
                addMangaToList(list);
            } else {
                // Open list details (Optional)
                Toast.makeText(this, "Opening list: " + list.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        rvLists.setLayoutManager(new LinearLayoutManager(this));
        rvLists.setAdapter(adapter);

        fabAddList.setOnClickListener(v -> showCreateListDialog());
        
        loadLists();
    }

    private void loadLists() {
        new Thread(() -> {
            try {
                List<CustomListEntity> dbLists = com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao().getAllCustomLists();
                runOnUiThread(() -> {
                    listEntities.clear();
                    listEntities.addAll(dbLists);
                    adapter.notifyDataSetChanged();
                    
                    if (listEntities.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        rvLists.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        rvLists.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showCreateListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إنشاء قائمة جديدة");

        final EditText input = new EditText(this);
        input.setHint("اسم القائمة");
        builder.setView(input);

        builder.setPositiveButton("إنشاء", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (!listName.isEmpty()) {
                createNewList(listName);
            }
        });
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createNewList(String listName) {
        new Thread(() -> {
            try {
                String id = java.util.UUID.randomUUID().toString();
                CustomListEntity list = new CustomListEntity(id, listName, "", "");
                com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao().insertCustomList(list);
                loadLists();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addMangaToList(CustomListEntity list) {
        new Thread(() -> {
            try {
                com.fire.mangareader.data.database.LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(selectedMangaUrl);
                if (item == null) {
                    item = new com.fire.mangareader.data.database.LibraryItem();
                    item.setMangaId(selectedMangaUrl);
                    item.setTitle(selectedMangaTitle);
                    item.setCoverUrl(selectedMangaCover);
                    item.setAddedTime(System.currentTimeMillis());
                    AppDatabase.getInstance(this).mangaDao().insert(item);
                }
                
                CustomListMangaCrossRef crossRef = new CustomListMangaCrossRef(list.getListId(), selectedMangaUrl, System.currentTimeMillis());
                com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao().insertMangaToList(crossRef);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "تمت الإضافة إلى " + list.getName(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
