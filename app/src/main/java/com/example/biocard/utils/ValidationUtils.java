package com.example.biocard.utils;

import androidx.annotation.Nullable;

import java.time.Year;
import java.util.regex.Pattern;

/** Input validation rules defined in PRD §12. */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern WEB_URL_PATTERN = Pattern.compile(
            "^(https?://)?[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}(:[0-9]{1,5})?(/.*)?$",
            Pattern.CASE_INSENSITIVE);

    private static final String PHONE_PATTERN = "^\\+?[0-9 ()\\-]+$";
    private static final int MIN_PHONE_DIGITS = 6;
    private static final int MAX_PHONE_DIGITS = 15; // E.164 maximum
    private static final int MIN_YEAR = 1900;
    private static final int MAX_FUTURE_YEARS = 10;

    private ValidationUtils() {
    }

    public static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(@Nullable String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /** Accepts digits, spaces, dashes, parentheses and an optional leading '+', 6–15 digits. */
    public static boolean isValidPhone(@Nullable String phone) {
        if (phone == null) return false;
        String value = phone.trim();
        if (!value.matches(PHONE_PATTERN)) return false;
        int digits = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) digits++;
        }
        return digits >= MIN_PHONE_DIGITS && digits <= MAX_PHONE_DIGITS;
    }

    public static boolean isValidGraduationYear(@Nullable String year) {
        if (year == null) return false;
        try {
            int value = Integer.parseInt(year.trim());
            return value >= MIN_YEAR && value <= Year.now().getValue() + MAX_FUTURE_YEARS;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidWebsite(@Nullable String url) {
        return url != null && WEB_URL_PATTERN.matcher(url.trim()).matches();
    }
}

