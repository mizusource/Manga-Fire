package com.fire.mangareader.network;
import com.fire.mangareader.model.Comment;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    public interface CommentsCallback {
        void onCommentsLoaded(List<Comment> comments);
        void onError(String error);
    }
    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    public CommentRepository() {}

    public static String sanitizeKey(String rawKey) {
        if (rawKey == null) return "general";
        return rawKey.replaceAll("[.#$\\[\\]/]", "_");
    }

    public void getComments(String targetId, CommentsCallback callback) {
        if (callback != null) callback.onCommentsLoaded(new ArrayList<>());
    }

    public void addComment(String targetId, String text, String userName, String userAvatar, String userId, boolean isSpoiler, ActionCallback callback) {
        if (callback != null) callback.onSuccess();
    }

    public void toggleLike(String targetId, String commentId, String userId, ActionCallback callback) {
        if (callback != null) callback.onSuccess();
    }

    public void deleteComment(String targetId, String commentId, ActionCallback callback) {
        if (callback != null) callback.onSuccess();
    }
}
