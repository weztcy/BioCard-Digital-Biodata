package com.example.biocard.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.biocard.R;
import com.example.biocard.data.Biodata;

/** Builds the plain-text biodata and opens the Android share sheet (ACTION_SEND). */
public final class ShareUtils {

    private ShareUtils() {
    }

    public static void shareBiodata(@NonNull Context context, @NonNull Biodata b) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_subject, b.fullName));
        send.putExtra(Intent.EXTRA_TEXT, buildShareText(b));
        context.startActivity(Intent.createChooser(send, context.getString(R.string.share_chooser_title)));
    }

    /** Creates the shareable text. Empty optional fields are omitted. */
    @NonNull
    public static String buildShareText(@NonNull Biodata b) {
        StringBuilder sb = new StringBuilder("BIOCARD\n\n");
        line(sb, "Name", b.fullName);
        line(sb, "Nickname", b.nickname);
        line(sb, "Gender", b.gender);
        line(sb, "Place of Birth", b.birthPlace);
        line(sb, "Date of Birth", DateUtils.toDisplay(b.birthDate));
        line(sb, "Nationality", b.nationality);
        line(sb, "Phone", b.phone);
        line(sb, "Email", b.email);
        line(sb, "Address", join(", ", b.address, b.city, b.postalCode));
        if (b.bloodType != null || b.emergencyContactPhone != null) {
            line(sb, "Blood Type", b.bloodType);
            line(sb, "Medical Notes", b.medicalNotes);
            line(sb, "Emergency Contact", join(" • ", b.emergencyContactName, b.emergencyContactRelation));
            line(sb, "Emergency Phone", b.emergencyContactPhone);
        }
        line(sb, "Education", formatEducation(b));
        line(sb, "Study Period", b.educationPeriod);
        line(sb, "GPA", b.gpa);
        line(sb, "Occupation", join(" • ", b.occupation, b.employmentType));
        line(sb, "Position", b.position);
        line(sb, "Company", b.company);
        line(sb, "Work Period", b.workPeriod);
        line(sb, "Job Description", b.jobDescription);
        line(sb, "Certifications", join(" – ", b.certifications, b.certIssuer));
        line(sb, "Credential URL", b.certUrl);
        line(sb, "Featured Project", b.projectName);
        line(sb, "Project Details", b.projectDescription);
        line(sb, "Project URL", b.projectUrl);
        line(sb, "Skills", b.skills);
        line(sb, "Languages", b.languages);
        line(sb, "Hobbies", b.hobbies);
        line(sb, "Website", b.website);
        line(sb, "Instagram", b.instagram);
        line(sb, "LinkedIn", b.linkedin);
        line(sb, "Facebook", b.facebook);
        line(sb, "About", b.biography);
        sb.append("\nShared via BioCard");
        return sb.toString();
    }

    /** e.g. "Bachelor – Information Technology, Universitas XYZ (2026)". */
    @NonNull
    public static String formatEducation(@NonNull Biodata b) {
        String degree = join(" – ", b.educationLevel, b.major);
        String text = join(", ", degree, b.institution);
        if (b.graduationYear != null) {
            text = text.isEmpty() ? String.valueOf(b.graduationYear) : text + " (" + b.graduationYear + ")";
        }
        return text;
    }

    /** Joins the non-empty parts with the given separator. */
    @NonNull
    public static String join(@NonNull String separator, @Nullable String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.trim().isEmpty()) continue;
            if (sb.length() > 0) sb.append(separator);
            sb.append(part.trim());
        }
        return sb.toString();
    }

    private static void line(StringBuilder sb, String label, @Nullable String value) {
        if (value != null && !value.trim().isEmpty()) {
            sb.append(label).append(": ").append(value.trim()).append('\n');
        }
    }
}

