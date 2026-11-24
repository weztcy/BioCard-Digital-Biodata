package com.example.biocard.utils;

import com.example.biocard.data.Biodata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShareUtilsTest {

    @Test
    public void testJoin() {
        assertEquals("", ShareUtils.join(", ", null, "", "   "));
        assertEquals("Jakarta, Indonesia", ShareUtils.join(", ", "Jakarta", null, "Indonesia"));
        assertEquals("A – B", ShareUtils.join(" – ", "A", "B"));
    }

    @Test
    public void testFormatEducation() {
        Biodata b = new Biodata();
        b.educationLevel = "Bachelor";
        b.major = "Information Technology";
        b.institution = "Universitas XYZ";
        b.graduationYear = 2026;

        String formatted = ShareUtils.formatEducation(b);
        assertEquals("Bachelor – Information Technology, Universitas XYZ (2026)", formatted);
    }

    @Test
    public void testBuildShareTextMinimal() {
        Biodata b = new Biodata();
        b.fullName = "John Doe";
        b.gender = "Male";

        String text = ShareUtils.buildShareText(b);
        assertTrue(text.contains("Name: John Doe"));
        assertTrue(text.contains("Gender: Male"));
        assertTrue(text.contains("Shared via BioCard"));
    }

    @Test
    public void testBuildShareTextFull() {
        Biodata b = new Biodata();
        b.fullName = "Dimas Pratama";
        b.nickname = "Dimas";
        b.gender = "Male";
        b.birthPlace = "Jakarta";
        b.birthDate = "2000-01-01";
        b.phone = "+628123456789";
        b.email = "dimas@example.com";
        b.city = "Jakarta";
        b.occupation = "Software Engineer";
        b.company = "Tech Corp";
        b.skills = "Java, Android, Room";

        String text = ShareUtils.buildShareText(b);
        assertTrue(text.contains("Name: Dimas Pratama"));
        assertTrue(text.contains("Nickname: Dimas"));
        assertTrue(text.contains("Date of Birth: 01 January 2000"));
        assertTrue(text.contains("Phone: +628123456789"));
        assertTrue(text.contains("Email: dimas@example.com"));
        assertTrue(text.contains("Occupation: Software Engineer"));
        assertTrue(text.contains("Company: Tech Corp"));
        assertTrue(text.contains("Skills: Java, Android, Room"));
    }

    @Test
    public void testBuildShareTextEnhancedFields() {
        Biodata b = new Biodata();
        b.fullName = "Dr. Jane Smith";
        b.gender = "Female";
        b.bloodType = "O+";
        b.emergencyContactName = "John Smith";
        b.emergencyContactRelation = "Spouse";
        b.emergencyContactPhone = "+62811223344";
        b.educationPeriod = "2018 – 2022";
        b.gpa = "3.92 / 4.00";
        b.employmentType = "Full-time";
        b.occupation = "Lead AI Architect";
        b.certifications = "Google Cloud Professional";
        b.certIssuer = "Google";
        b.projectName = "BioCard Android App";
        b.languages = "Indonesian, English, German";

        String text = ShareUtils.buildShareText(b);
        assertTrue(text.contains("Blood Type: O+"));
        assertTrue(text.contains("Emergency Contact: John Smith • Spouse"));
        assertTrue(text.contains("Emergency Phone: +62811223344"));
        assertTrue(text.contains("Study Period: 2018 – 2022"));
        assertTrue(text.contains("GPA: 3.92 / 4.00"));
        assertTrue(text.contains("Occupation: Lead AI Architect • Full-time"));
        assertTrue(text.contains("Certifications: Google Cloud Professional – Google"));
        assertTrue(text.contains("Featured Project: BioCard Android App"));
        assertTrue(text.contains("Languages: Indonesian, English, German"));
    }
}

