package com.fire.mangareader.util;

public enum ExportFormat {
    PDF("مستند PDF", "pdf", "application/pdf"),
    CBZ("أرشيف CBZ (كوميكس)", "cbz", "application/x-cbz");

    private final String displayName;
    private final String extension;
    private final String mimeType;

    ExportFormat(String displayName, String extension, String mimeType) {
        this.displayName = displayName;
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
