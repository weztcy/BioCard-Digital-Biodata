package com.example.biocard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity holding the user's biodata.
 * <p>
 * The MVP supports a single BioCard, therefore the row always uses {@link #SINGLE_ID}.
 * All fields except {@link #fullName} and {@link #gender} are optional and may be {@code null}.
 */
@Entity(tableName = "biodata")
public class Biodata {

    /** The MVP stores exactly one biodata record. */
    public static final int SINGLE_ID = 1;

    @PrimaryKey
    public int id = SINGLE_ID;

    // Personal information
    @NonNull
    public String fullName = "";
    @Nullable public String nickname;
    @NonNull
    public String gender = "";
    @Nullable public String birthPlace;
    /** ISO-8601 date (yyyy-MM-dd). */
    @Nullable public String birthDate;
    @Nullable public String nationality;

    // Contact information
    @Nullable public String phone;
    @Nullable public String email;
    @Nullable public String address;
    @Nullable public String city;
    @Nullable public String postalCode;

    // Education information
    @Nullable public String educationLevel;
    @Nullable public String institution;
    @Nullable public String major;
    @Nullable public Integer graduationYear;
    @Nullable public String educationPeriod;
    @Nullable public String gpa;

    // Work information
    @Nullable public String occupation;
    @Nullable public String company;
    @Nullable public String position;
    @Nullable public String workAddress;
    @Nullable public String employmentType;
    @Nullable public String workPeriod;
    @Nullable public String jobDescription;

    // Certifications & Licenses
    @Nullable public String certifications;
    @Nullable public String certIssuer;
    @Nullable public String certUrl;

    // Portfolio & Projects
    @Nullable public String projectName;
    @Nullable public String projectDescription;
    @Nullable public String projectUrl;

    // Languages
    @Nullable public String languages;

    // Emergency & Medical Information (ICE)
    @Nullable public String bloodType;
    @Nullable public String medicalNotes;
    @Nullable public String emergencyContactName;
    @Nullable public String emergencyContactRelation;
    @Nullable public String emergencyContactPhone;

    // Additional information
    @Nullable public String website;
    @Nullable public String instagram;
    @Nullable public String linkedin;
    @Nullable public String facebook;
    /** Comma separated list of skills. */
    @Nullable public String skills;
    @Nullable public String hobbies;
    @Nullable public String biography;
    @Nullable public String otherInfo;

    /** URI (file://) of the profile photo stored in the app's private storage. */
    @Nullable public String photoUri;

    /** Last update timestamp in epoch millis. */
    public long updatedAt;
}

