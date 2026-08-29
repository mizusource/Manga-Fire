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
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etComment;
    
    private ImageView btnSend;
    
    private CommentAdapter adapter;
    private List<Comment> commentList;
    
    private Object db;
    private String mangaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        db = null;

        recyclerView = findViewById(R.id.commentsRecyclerView);
        etComment = findViewById(R.id.etComment);
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

        
                     
                
                
    }
}
