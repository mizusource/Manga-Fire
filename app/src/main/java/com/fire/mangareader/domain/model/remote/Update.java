package com.fire.mangareader.domain.model.remote;

public class Update {
    private long version;
    private String updateUrl;
    private String message;
    private String changeLog;
    private Long fileSize;
    private Boolean isForced;

    public Update(long version, String updateUrl, String message, String changeLog, Long fileSize, Boolean isForced) {
        this.version = version;
        this.updateUrl = updateUrl;
        this.message = message;
        this.changeLog = changeLog;
        this.fileSize = fileSize;
        this.isForced = isForced;
    }

    public long getVersion() { return version; }
    public String getUpdateUrl() { return updateUrl; }
    public String getMessage() { return message; }
    public String getChangeLog() { return changeLog; }
    public Long getFileSize() { return fileSize; }
    public Boolean isForced() { return isForced; }
}
