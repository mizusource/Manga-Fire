package com.fire.mangareader.domain.model;

public enum CommentSortOrder {
    NEWEST("الأحدث"),
    OLDEST("الأقدم"),
    TOP_LIKED("أفضل التعليقات");

    private final String title;

    CommentSortOrder(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
