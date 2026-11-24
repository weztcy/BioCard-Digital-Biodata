package com.example.biocard.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DateUtilsTest {

    @Test
    public void testParse() {
        assertNull(DateUtils.parse(null));
        assertNull(DateUtils.parse(""));
        assertNull(DateUtils.parse("invalid-date"));

        LocalDate date = DateUtils.parse("2000-01-15");
        assertNotNull(date);
        assertEquals(2000, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());
    }

    @Test
    public void testToDisplay() {
        assertEquals("", DateUtils.toDisplay(null));
        assertEquals("", DateUtils.toDisplay(""));
        assertEquals("01 January 2000", DateUtils.toDisplay("2000-01-01"));
        assertEquals("25 December 1995", DateUtils.toDisplay("1995-12-25"));
    }

    @Test
    public void testUtcMillisConversion() {
        Long millis = DateUtils.toUtcMillis("2026-05-10");
        assertNotNull(millis);
        String backToIso = DateUtils.fromUtcMillis(millis);
        assertEquals("2026-05-10", backToIso);
    }
}

