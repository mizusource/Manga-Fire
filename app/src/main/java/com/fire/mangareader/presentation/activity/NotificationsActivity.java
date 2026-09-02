package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.NotificationAdapter;
import com.fire.mangareader.domain.model.NotificationModel;
import com.fire.mangareader.data.network.SupabaseManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rvNotifications;
    private TextView tvEmpty;
    private NotificationAdapter adapter;
    private List<NotificationModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, list);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        SupabaseManager.getInstance(this).getNotifications(new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                runOnUiThread(() -> {
                    list.clear();
                    if (data != null && data.length() > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                        try {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                NotificationModel model = new NotificationModel();
                                model.id = obj.optString("id");
                                model.senderName = obj.optString("sender_name");
                                model.message = obj.optString("message");
                                model.mangaUrl = obj.optString("manga_url");
                                model.type = obj.optString("type");
                                model.isRead = obj.optBoolean("is_read");
                                model.createdAt = obj.optString("created_at");
                                list.add(model);
                            }
                        } catch (Exception e) {}
                        adapter.notifyDataSetChanged();
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(NotificationsActivity.this, "خطأ: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
