package com.fire.mangareader.domain.usecase.auth;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class UserAuthValidator {

    private static final List<String> BAD_WORDS = Arrays.asList(
            "admin", "root", "fuck", "shit", "bitch", "asshole"
    );

    public static class ValidationResult {
        public boolean isValid;
        public String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
    }

    public static ValidationResult validateSignUp(String name, String email, String password, String confirmPassword) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return new ValidationResult(false, "يرجى ملء جميع الفراغات");
        }

        if (password.length() < 8) {
            return new ValidationResult(false, "يجب أن تكون كلمة المرور مكونة من 8 أحرف على الأقل");
        }
        
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            return new ValidationResult(false, "كلمات المرور غير متطابقة");
        }

        if (name.length() < 5) {
            return new ValidationResult(false, "الحد الأدنى لطول الاسم هو 5 أحرف");
        }

        if (name.length() > 25) {
            return new ValidationResult(false, "الحد الأقصى لطول الاسم هو 25 حرفًا");
        }

        if (containsBadWords(name)) {
            return new ValidationResult(false, "الاسم يحتوي على كلمات غير لائقة.");
        }

        if (!isValidEmail(email)) {
            return new ValidationResult(false, "البريد الإلكتروني المُقدم غير صالح");
        }

        return new ValidationResult(true, null);
    }

    private static boolean containsBadWords(String name) {
        String lowerName = name.toLowerCase();
        for (String word : BAD_WORDS) {
            if (lowerName.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
