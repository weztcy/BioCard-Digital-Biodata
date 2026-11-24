package com.example.biocard.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {

    @Test
    public void testIsBlank() {
        assertTrue(ValidationUtils.isBlank(null));
        assertTrue(ValidationUtils.isBlank(""));
        assertTrue(ValidationUtils.isBlank("   "));
        assertFalse(ValidationUtils.isBlank("John"));
        assertFalse(ValidationUtils.isBlank("  John  "));
    }

    @Test
    public void testIsValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("john.doe@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user+tag@sub.domain.co.id"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtils.isValidEmail("username@.com"));
    }

    @Test
    public void testIsValidPhone() {
        assertTrue(ValidationUtils.isValidPhone("+62 812 3456 7890"));
        assertTrue(ValidationUtils.isValidPhone("08123456789"));
        assertTrue(ValidationUtils.isValidPhone("(021) 555-1234"));
        assertFalse(ValidationUtils.isValidPhone(null));
        assertFalse(ValidationUtils.isValidPhone(""));
        assertFalse(ValidationUtils.isValidPhone("123")); // too short (< 6 digits)
        assertFalse(ValidationUtils.isValidPhone("123456789012345678")); // too long (> 15 digits)
        assertFalse(ValidationUtils.isValidPhone("abc-def-ghij")); // contains letters
    }

    @Test
    public void testIsValidGraduationYear() {
        assertTrue(ValidationUtils.isValidGraduationYear("2020"));
        assertTrue(ValidationUtils.isValidGraduationYear("2026"));
        assertFalse(ValidationUtils.isValidGraduationYear(null));
        assertFalse(ValidationUtils.isValidGraduationYear(""));
        assertFalse(ValidationUtils.isValidGraduationYear("1899")); // < 1900
        assertFalse(ValidationUtils.isValidGraduationYear("2100")); // too far in future
        assertFalse(ValidationUtils.isValidGraduationYear("year"));
    }

    @Test
    public void testIsValidWebsite() {
        assertTrue(ValidationUtils.isValidWebsite("https://biocard.app"));
        assertTrue(ValidationUtils.isValidWebsite("http://example.com/user"));
        assertTrue(ValidationUtils.isValidWebsite("my-portfolio.dev"));
        assertFalse(ValidationUtils.isValidWebsite(null));
        assertFalse(ValidationUtils.isValidWebsite(""));
        assertFalse(ValidationUtils.isValidWebsite("not a url"));
    }
}

