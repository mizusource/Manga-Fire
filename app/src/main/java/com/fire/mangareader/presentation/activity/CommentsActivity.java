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
import com.fire.mangareader.presentation.adapter.CommentAdapter;
import com.fire.mangareader.domain.model.Comment;
import com.fire.mangareader.data.network.SupabaseManager;
import com.fire.mangareader.util.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private String mangaUrl;
    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> commentsList;
    private EditText etComment;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SupabaseManager supabaseManager;
    private PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        if (mangaUrl == null) {
            finish();
            return;
        }

        supabaseManager = SupabaseManager.getInstance(this);
        prefManager = new PreferenceManager(this);

        rvComments = findViewById(R.id.commentsRecyclerView);
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSendComment);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmptyComments);

        commentsList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentsList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendComment());

        loadComments();
    }

    private void loadComments() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        supabaseManager.getComments(mangaUrl, new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                progressBar.setVisibility(View.GONE);
                commentsList.clear();
                if (data == null || data.length() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    try {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Comment comment = new Comment();
                            comment.mangaUrl = obj.optString("manga_url");
                            comment.username = obj.optString("username");
                            comment.text = obj.optString("text");
                            comment.created_at = obj.optString("created_at");
                            comment.id = obj.optString("id");
                            comment.isSpoiler = obj.optBoolean("is_spoiler");
                            comment.likes = obj.optInt("likes");
                            commentsList.add(comment);
                        }
                    } catch (Exception e) {}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CommentsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        String text = etComment.getText().toString().trim();
        if (text.isEmpty()) return;

        btnSend.setEnabled(false);
        String userName = prefManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "مستخدم";
        }

        supabaseManager.addComment(mangaUrl, text, false, userName, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnSend.setEnabled(true);
                etComment.setText("");
                loadComments();
            }
            @Override
            public void onError(String error) {
                btnSend.setEnabled(true);
                Toast.makeText(CommentsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
