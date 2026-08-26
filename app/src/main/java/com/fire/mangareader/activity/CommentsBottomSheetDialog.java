package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.adapter.CommentAdapter;
import com.fire.mangareader.model.Comment;
import com.fire.mangareader.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CommentsBottomSheetDialog extends BottomSheetDialogFragment {

    private String mangaUrl;
    private RecyclerView rvComments;
    private TextView tvEmptyComments, tvCommentsCount;
    private EditText etCommentInput;
    private ImageView btnSendComment;
    private View layoutInput, layoutLoginPrompt, btnLoginPrompt;
    
    private CommentAdapter adapter;
    private List<Comment> commentList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public CommentsBottomSheetDialog(String mangaUrl) {
        this.mangaUrl = mangaUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comments, container, false);
        
        rvComments = view.findViewById(R.id.rvComments);
        tvEmptyComments = view.findViewById(R.id.tvEmptyComments);
        tvCommentsCount = view.findViewById(R.id.tvCommentsCount);
        etCommentInput = view.findViewById(R.id.etCommentInput);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        layoutInput = view.findViewById(R.id.layoutInput);
        layoutLoginPrompt = view.findViewById(R.id.layoutLoginPrompt);
        btnLoginPrompt = view.findViewById(R.id.btnLoginPrompt);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(getContext(), commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(adapter);

        checkAuthStatus();
        loadComments();

        btnSendComment.setOnClickListener(v -> postComment());
        btnLoginPrompt.setOnClickListener(v -> {
            dismiss();
            // Start LoginActivity if needed, or redirect
        });

        return view;
    }

    private void checkAuthStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        PreferenceManager prefs = new PreferenceManager(requireContext());
        if (user == null || prefs.isGuest()) {
            layoutInput.setVisibility(View.GONE);
            layoutLoginPrompt.setVisibility(View.VISIBLE);
        } else {
            layoutInput.setVisibility(View.VISIBLE);
            layoutLoginPrompt.setVisibility(View.GONE);
            etCommentInput.setHint("اكتب تعليقك بصفتك " + prefs.getUserName() + "...");
        }
    }

    private void loadComments() {
        String docId = mangaUrl.replaceAll("[^a-zA-Z0-9]", "_");
        db.collection("mangas").document(docId).collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    commentList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                        Comment comment = doc.toObject(Comment.class);
                        if (comment != null) {
                            comment.id = doc.getId();
                            commentList.add(comment);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvCommentsCount.setText("التعليقات (" + commentList.size() + ")");
                    
                    if (commentList.isEmpty()) {
                        tvEmptyComments.setVisibility(View.VISIBLE);
                        rvComments.setVisibility(View.GONE);
                    } else {
                        tvEmptyComments.setVisibility(View.GONE);
                        rvComments.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void postComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;

        PreferenceManager prefs = new PreferenceManager(requireContext());
        String userName = prefs.getUserName();
        String docId = mangaUrl.replaceAll("[^a-zA-Z0-9]", "_");

        Comment newComment = new Comment();
        newComment.user_name = userName;
        newComment.username = userName;
        newComment.text = text;
        newComment.timestamp = System.currentTimeMillis();

        db.collection("mangas").document(docId).collection("comments")
                .add(newComment)
                .addOnSuccessListener(documentReference -> {
                    etCommentInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "فشل إضافة التعليق", Toast.LENGTH_SHORT).show());
    }
}
