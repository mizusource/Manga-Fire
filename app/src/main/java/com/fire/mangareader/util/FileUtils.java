package com.fire.mangareader.util;

public class FileUtils {
    
    /**
     * ينظف اسم الملف من الرموز التي يمنع نظام الأندرويد حفظها كملفات
     * لتفادي انهيار التطبيق (Crash) عند التحميل.
     */
    public static String sanitizeFileName(String origName) {
        if (origName == null || origName.trim().isEmpty()) {
            return "unknown_file";
        }
        
        String trimmed = origName.trim();
        StringBuilder sb = new StringBuilder(trimmed.length());
        
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            // الرموز الممنوعة في نظام الملفات: " * / : < > ? \ | 
            if (c == '"' || c == '*' || c == '/' || c == ':' || 
                c == '<' || c == '>' || c == '?' || c == '\\' || 
                c == '|' || c < 32 || c == 127) {
                sb.append('_');
            } else {
                sb.append(c);
            }
        }
        
        String result = sb.toString();
        // ضمان ألا يتجاوز طول الاسم الحد الأقصى لنظام الملفات
        if (result.length() > 200) {
            result = result.substring(0, 200);
        }
        
        return result;
    }
}
