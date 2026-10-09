package com.petconnect.util;

import java.util.regex.Pattern;

/**
 * Server-side input validation, used by RegisterServlet, LoginServlet,
 * and the Phase 5 shelter pet servlets.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    private ValidationUtil() {
    }

    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNonEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return isNonEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || phone.trim().isEmpty() || PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidProfileName(String name) {
        if (!isNonEmpty(name)) return false;
        String trimmed = name.trim();
        return trimmed.length() <= 100 && trimmed.chars().noneMatch(Character::isISOControl);
    }

    public static boolean isValidRole(String role) {
        return "SHELTER".equals(role) || "ADOPTER".equals(role);
    }

    // ---- Phase 5: Pet validation ----

    public static boolean isValidGender(String gender) {
        return "MALE".equals(gender) || "FEMALE".equals(gender);
    }

    public static boolean isValidAge(String age) {
        if (!isNonEmpty(age)) return false;
        try {
            int value = Integer.parseInt(age.trim());
            return value >= 0 && value <= 40;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
