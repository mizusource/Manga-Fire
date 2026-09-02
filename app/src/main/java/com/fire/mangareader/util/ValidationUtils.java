package com.fire.mangareader.util;

import java.util.Arrays;
import java.util.List;

public class ValidationUtils {
    
    /**
     * التحقق من صحة الإيميل (Email Validation) مثلما تفعل تطبيقات المانجا الاحترافية
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.length() > 100) {
            return false;
        }
        
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email.matches(regex)) {
            List<String> validDomains = Arrays.asList("gmail.com", "yahoo.com", "outlook.com", "hotmail.com");
            String[] parts = email.split("@");
            if (parts.length == 2) {
                return validDomains.contains(parts[1].toLowerCase());
            }
        }
        return false;
    }
    
    /**
     * قص النصوص الطويلة وإضافة ثلاث نقاط في النهاية ...
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() > maxLength) {
            return text.substring(0, Math.max(0, maxLength - 3)) + "...";
        }
        return text;
    }
}
