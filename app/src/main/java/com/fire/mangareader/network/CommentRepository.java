package com.fire.mangareader.network;

import android.util.Log;
import com.fire.mangareader.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRepository {
    private static final String TAG = "CommentRepository";
    private static final String DB_URL = "https://speed-manga-default-rtdb.europe-west1.firebasedatabase.app";
    private final FirebaseDatabase database;
    private final DatabaseReference commentsRef;

    public interface CommentsCallback {
        void onCommentsLoaded(List<Comment> comments);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    public CommentRepository() {
        FirebaseDatabase fb;
        try {
            fb = FirebaseDatabase.getInstance(DB_URL);
        } catch (Exception e) {
            fb = FirebaseDatabase.getInstance();
        }
        this.database = fb;
        this.commentsRef = database.getReference("comments");
    }

    public static String sanitizeKey(String rawKey) {
        if (rawKey == null) return "general";
        return rawKey.replaceAll("[.#$\\[\\]/]", "_");
    }

    public void getComments(String targetId, CommentsCallback callback) {
        String safeKey = sanitizeKey(targetId);
        DatabaseReference targetRef = commentsRef.child(safeKey);

        targetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Comment> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Comment c = new Comment();
                        c.id = child.getKey();
                        c.comment_id = child.child("comment_id").getValue(String.class);
                        if (c.comment_id == null) c.comment_id = c.id;

                        c.user_id = child.child("user_id").getValue(String.class);
                        c.user_name = child.child("user_name").getValue(String.class);
                        if (c.user_name == null) c.user_name = "مستخدم";
                        c.username = c.user_name;

                        c.user_avatar = child.child("user_avatar").getValue(String.class);
                        if (c.user_avatar == null) c.user_avatar = "";

                        c.text = child.child("text").getValue(String.class);
                        if (c.text == null) c.text = "";

                        Long ts = child.child("timestamp").getValue(Long.class);
                        c.timestamp = ts != null ? ts : 0L;

                        Boolean spoiler = child.child("is_spoiler").getValue(Boolean.class);
                        c.is_spoiler = spoiler != null ? spoiler : false;
                        c.isSpoiler = c.is_spoiler;

                        Integer lk = child.child("likes").getValue(Integer.class);
                        c.likes = lk != null ? lk : 0;

                        Integer dlk = child.child("dislikes").getValue(Integer.class);
                        c.dislikes = dlk != null ? dlk : 0;

                        for (DataSnapshot likeChild : child.child("liked_by").getChildren()) {
                            c.liked_by.put(likeChild.getKey(), true);
                        }

                        for (DataSnapshot dislikeChild : child.child("disliked_by").getChildren()) {
                            c.disliked_by.put(dislikeChild.getKey(), true);
                        }

                        for (DataSnapshot replyChild : child.child("replies").getChildren()) {
                            Comment rep = new Comment();
                            rep.id = replyChild.getKey();
                            rep.comment_id = rep.id;
                            rep.user_id = replyChild.child("user_id").getValue(String.class);
                            rep.user_name = replyChild.child("user_name").getValue(String.class);
                            if (rep.user_name == null) rep.user_name = "مستخدم";
                            rep.username = rep.user_name;
                            rep.user_avatar = replyChild.child("user_avatar").getValue(String.class);
                            rep.text = replyChild.child("text").getValue(String.class);
                            Long repTs = replyChild.child("timestamp").getValue(Long.class);
                            rep.timestamp = repTs != null ? repTs : 0L;
                            rep.parent_id = c.id;
                            c.replies.put(rep.id, rep);
                        }

                        c.replies_count = c.replies.size();
                        list.add(c);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing comment", e);
                    }
                }
                callback.onCommentsLoaded(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void addComment(String targetId, String text, String userName, String userAvatar, String userId, boolean isSpoiler, ActionCallback callback) {
        String safeKey = sanitizeKey(targetId);
        DatabaseReference newCommentRef = commentsRef.child(safeKey).push();
        String commentId = newCommentRef.getKey();

        Map<String, Object> map = new HashMap<>();
        map.put("comment_id", commentId);
        map.put("user_id", userId != null ? userId : "");
        map.put("user_name", userName != null ? userName : "مستخدم");
        map.put("user_avatar", userAvatar != null ? userAvatar : "");
        map.put("text", text);
        map.put("timestamp", System.currentTimeMillis());
        map.put("is_spoiler", isSpoiler);
        map.put("likes", 0);
        map.put("dislikes", 0);
        map.put("replies_count", 0);

        newCommentRef.setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onSuccess();
            } else {
                if (callback != null) callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public void toggleLike(String targetId, String commentId, String userId, ActionCallback callback) {
        String safeKey = sanitizeKey(targetId);
        String safeUserId = sanitizeKey(userId);
        DatabaseReference cRef = commentsRef.child(safeKey).child(commentId);

        cRef.child("liked_by").child(safeUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isLiked = task.getResult().exists();
                if (isLiked) {
                    cRef.child("liked_by").child(safeUserId).removeValue();
                    cRef.child("likes").get().addOnCompleteListener(lt -> {
                        int likes = lt.isSuccessful() && lt.getResult().getValue(Integer.class) != null ? lt.getResult().getValue(Integer.class) : 1;
                        cRef.child("likes").setValue(Math.max(0, likes - 1));
                    });
                } else {
                    cRef.child("liked_by").child(safeUserId).setValue(true);
                    cRef.child("disliked_by").child(safeUserId).removeValue();
                    cRef.child("likes").get().addOnCompleteListener(lt -> {
                        int likes = lt.isSuccessful() && lt.getResult().getValue(Integer.class) != null ? lt.getResult().getValue(Integer.class) : 0;
                        cRef.child("likes").setValue(likes + 1);
                    });
                }
                if (callback != null) callback.onSuccess();
            } else {
                if (callback != null) callback.onError("Failed to like");
            }
        });
    }

    public void deleteComment(String targetId, String commentId, ActionCallback callback) {
        String safeKey = sanitizeKey(targetId);
        commentsRef.child(safeKey).child(commentId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onSuccess();
            } else {
                if (callback != null) callback.onError(task.getException() != null ? task.getException().getMessage() : "Failed to delete");
            }
        });
    }
}
