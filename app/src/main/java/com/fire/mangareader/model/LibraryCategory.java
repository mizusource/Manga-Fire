package com.fire.mangareader.model;

public enum LibraryCategory {
    ALL("الكل"),
    DOWNLOADED("المحملة (بدون نت)"),
    FAVORITE("المفضلة"),
    READING("أقرأها حالياً"),
    PLAN_TO_READ("أرغب بقراءتها"),
    COMPLETED("تمت قراءتها"),
    HISTORY("سجل القراءة");

    private final String displayName;

    LibraryCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
