package com.fire.mangareader.activity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etComment;
    private CheckBox cbSpoiler;
    private ImageButton btnSend;
    
    private CommentAdapter adapter;
    private List<Comment> commentList;
    
    private FirebaseFirestore db;
    private String mangaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.commentsRecyclerView);
        etComment = findViewById(R.id.etComment);
        cbSpoiler = findViewById(R.id.cbSpoiler);
        btnSend = findViewById(R.id.btnSendComment);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentList);
        
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
        String text = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        // التحقق من أن المستخدم مسجل دخول (لنجلب اسمه)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String username = (currentUser != null && currentUser.getDisplayName() != null) 
                ? currentUser.getDisplayName() : "مستخدم مجهول";

        boolean isSpoiler = cbSpoiler.isChecked();
        long timestamp = System.currentTimeMillis();

        Comment newComment = new Comment(mangaUrl, username, text, timestamp, isSpoiler);

        // رفع التعليق لقاعدة البيانات السحابية
        db.collection("comments").add(newComment)
                .addOnSuccessListener(documentReference -> {
                    etComment.setText(""); // تفريغ حقل النص
                    cbSpoiler.setChecked(false); // إزالة التحديد عن الحرق
                })
                .addOnFailureListener(e -> Toast.makeText(this, "فشل إرسال التعليق", Toast.LENGTH_SHORT).show());
    }
}
