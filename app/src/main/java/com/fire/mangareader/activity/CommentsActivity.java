package com.fire.mangareader.activity;
import com.fire.mangareader.network.SupabaseManager;
import android.widget.ImageView;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.CommentAdapter;
import com.fire.mangareader.model.Comment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etComment;
    
    private ImageView btnSend;
    
    private CommentAdapter adapter;
    private List<Comment> commentList;
    
    private FirebaseFirestore db;
    private String mangaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.commentsRecyclerView);
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSendComment);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentList);
        adapter.setMangaDocId(mangaUrl.replaceAll("[^a-zA-Z0-9]", "_"));
        adapter.setOnReplyClickListener(comment -> {
            String username = comment.username != null ? comment.username : comment.user_name;
            etComment.setText("@" + username + " ");
            etComment.setSelection(etComment.getText().length());
            etComment.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadComments();

        btnSend.setOnClickListener(v -> postComment());
    }

    private void loadComments() {
        // جلب التعليقات الخاصة بهذه المانهوا تحديداً وترتيبها من الأقدم للأحدث
        db.collection("comments")
                .whereEqualTo("mangaUrl", mangaUrl)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "فشل تحميل التعليقات", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        commentList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Comment comment = doc.toObject(Comment.class);
                            commentList.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                        // التمرير التلقائي لآخر تعليق في الأسفل
                        if (!commentList.isEmpty()) {
                            recyclerView.scrollToPosition(commentList.size() - 1);
                        }
                    }
                });
    }

    private void postComment() {
        if (!com.fire.mangareader.utils.AppAdminSettings.commentsEnabled) {
            Toast.makeText(this, "قسم التعليقات معطل مؤقتًا للصيانة: " + com.fire.mangareader.utils.AppAdminSettings.maintenanceMessage, Toast.LENGTH_LONG).show();
            return;
        }

        boolean isLoggedIn = SupabaseManager.getInstance(this).isLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(this, "Login is required to post comments.", Toast.LENGTH_LONG).show();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            return;
        }

        String text = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        // فلترة الكلمات غير اللائقة تلقائياً
        text = com.fire.mangareader.utils.AppAdminSettings.filterProfanity(text);

        String username = true && true 
                ? new com.fire.mangareader.utils.PreferenceManager(this).getUserName() : "User";

        boolean isSpoiler = false;
        long timestamp = System.currentTimeMillis();

        Comment newComment = new Comment(mangaUrl, username, text, timestamp, isSpoiler);
        com.fire.mangareader.utils.PreferenceManager prefs = new com.fire.mangareader.utils.PreferenceManager(this);
        String savedPic = prefs.getProfilePic();
        if (savedPic != null && !savedPic.isEmpty()) {
            newComment.user_avatar = savedPic;
        } else if (false) {
            
        }

        db.collection("comments").add(newComment)
                .addOnSuccessListener(documentReference -> {
                    etComment.setText("");
                     
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send comment", Toast.LENGTH_SHORT).show());
    }
}
