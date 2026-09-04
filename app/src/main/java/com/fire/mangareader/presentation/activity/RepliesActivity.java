package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.ReplyAdapter;
import com.fire.mangareader.domain.model.Comment;
import com.fire.mangareader.data.network.SupabaseManager;
import com.fire.mangareader.util.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RepliesActivity extends AppCompatActivity {
    private String mangaUrl;
    private String parentId;
    private String parentUserId;
    private RecyclerView rvReplies;
    private ReplyAdapter adapter;
    private List<Comment> repliesList;
    private EditText etReply;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SupabaseManager supabaseManager;
    private PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        parentId = getIntent().getStringExtra("parentId");
        parentUserId = getIntent().getStringExtra("parentUserId");

        if (parentId == null) {
            finish();
            return;
        }

        supabaseManager = SupabaseManager.getInstance(this);
        prefManager = new PreferenceManager(this);

        rvReplies = findViewById(R.id.rvReplies);
        etReply = findViewById(R.id.etCommentInput);
        btnSend = findViewById(R.id.btnSendComment);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        repliesList = new ArrayList<>();
        adapter = new ReplyAdapter(this, repliesList);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));
        rvReplies.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendReply());

        loadReplies();
    }

    private void loadReplies() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        supabaseManager.getReplies(parentId, new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    repliesList.clear();

                    if (data == null || data.length() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        try {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Comment reply = new Comment();
                                reply.id = obj.optString("id");
                                reply.mangaUrl = obj.optString("manga_url");
                                reply.username = obj.optString("username");
                                reply.text = obj.optString("text");
                                reply.created_at = obj.optString("created_at");
                                reply.likes = obj.optInt("likes");
                                reply.user_id = obj.optString("user_id");
                                repliesList.add(reply);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RepliesActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendReply() {
        String text = etReply.getText().toString().trim();
        if (text.isEmpty()) return;

        btnSend.setEnabled(false);
        String userName = prefManager.getUserName();

        supabaseManager.addReply(mangaUrl, parentId, text, false, userName, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    etReply.setText("");
                    loadReplies();
                    
                    if (parentUserId != null && !parentUserId.isEmpty() && !parentUserId.equals(supabaseManager.getCurrentUserId())) {
                        String senderName = prefManager.getUserName();
                        if (senderName == null || senderName.isEmpty()) senderName = "مستخدم";
                        supabaseManager.sendNotification(parentUserId, senderName, "قام بالرد على تعليقك", mangaUrl, null);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    Toast.makeText(RepliesActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
