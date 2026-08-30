package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.CommentAdapter;
import com.fire.mangareader.model.Comment;
import com.fire.mangareader.network.SupabaseManager;
import com.fire.mangareader.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CommentsBottomSheetDialog extends BottomSheetDialogFragment {
    private String mangaUrl;
    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> commentsList;
    private EditText etCommentInput;
    private android.widget.ImageView btnSendComment;
    private ProgressBar progressBar;
    private TextView tvEmptyComments;
    private SupabaseManager supabaseManager;
    private PreferenceManager prefManager;
    
    public CommentsBottomSheetDialog() {}
    
    public static CommentsBottomSheetDialog newInstance(String mangaUrl) {
        CommentsBottomSheetDialog fragment = new CommentsBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("mangaUrl", mangaUrl);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mangaUrl = getArguments().getString("mangaUrl");
        }
        supabaseManager = SupabaseManager.getInstance(getContext());
        prefManager = new PreferenceManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvComments = view.findViewById(R.id.rvComments);
        etCommentInput = view.findViewById(R.id.etCommentInput);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyComments = view.findViewById(R.id.tvEmptyComments);
        
        commentsList = new ArrayList<>();
        adapter = new CommentAdapter(getContext(), commentsList);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(adapter);
        
        btnSendComment.setOnClickListener(v -> sendComment());
        
        loadComments();
    }
    
    private void loadComments() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyComments.setVisibility(View.GONE);
        supabaseManager.getComments(mangaUrl, new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                progressBar.setVisibility(View.GONE);
                commentsList.clear();
                if (data == null || data.length() == 0) {
                    tvEmptyComments.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyComments.setVisibility(View.GONE);
                    try {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Comment comment = new Comment();
                            comment.id = obj.optString("id");
                            comment.id = obj.optString("id");
                            comment.mangaUrl = obj.optString("manga_url");
                            comment.username = obj.optString("username");
                            comment.user_id = obj.optString("user_id");
                            comment.user_id = obj.optString("user_id");
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
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;
        
        btnSendComment.setEnabled(false);
        String userName = prefManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "مستخدم";
        }
        
        supabaseManager.addComment(mangaUrl, text, false, userName, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnSendComment.setEnabled(true);
                etCommentInput.setText("");
                loadComments();
            }
            @Override
            public void onError(String error) {
                btnSendComment.setEnabled(true);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
