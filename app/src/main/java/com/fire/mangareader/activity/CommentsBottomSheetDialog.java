package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class CommentsBottomSheetDialog extends BottomSheetDialogFragment {
    private String mangaUrl;
    private String docId;
    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> commentsList;
    private EditText etCommentInput;
    private ImageButton btnSendComment;
    private ProgressBar progressBar;
    private TextView tvEmptyComments;
    
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
            docId = mangaUrl.replaceAll("[^a-zA-Z0-9]", "_");
        }
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
    }

    private void sendComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;
        etCommentInput.setText("");
    }
}
