package com.example.biocard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.example.biocard.R;
import com.example.biocard.data.Biodata;
import com.example.biocard.databinding.ActivityBiocardBinding;
import com.example.biocard.databinding.ItemCardSectionBinding;
import com.example.biocard.databinding.ItemInfoRowBinding;
import com.example.biocard.utils.DateUtils;
import com.example.biocard.utils.ImageUtils;
import com.example.biocard.utils.ShareUtils;
import com.example.biocard.viewmodel.BiodataViewModel;
import com.google.android.material.chip.Chip;

/** Digital BioCard (PRD §13). Updates automatically whenever the biodata changes. */
public class BioCardActivity extends BaseActivity {

    private ActivityBiocardBinding binding;
    private BiodataViewModel viewModel;
    @Nullable
    private Biodata current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBiocardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applySystemBarInsets(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                openForm();
                return true;
            } else if (id == R.id.action_delete) {
                confirmDelete(viewModel, this::finish);
                return true;
            }
            return false;
        });

        binding.buttonShare.setOnClickListener(v -> {
            if (current != null) ShareUtils.shareBiodata(this, current);
        });
        binding.buttonEdit.setOnClickListener(v -> openForm());
        binding.buttonCreate.setOnClickListener(v -> openForm());

        viewModel = new ViewModelProvider(this).get(BiodataViewModel.class);
        viewModel.getBiodata().observe(this, this::render);
    }

    private void openForm() {
        startActivity(new Intent(this, BiodataActivity.class));
    }

    private void render(@Nullable Biodata d) {
        current = d;
        boolean hasData = d != null;
        binding.groupEmpty.setVisibility(hasData ? View.GONE : View.VISIBLE);
        binding.scrollCard.setVisibility(hasData ? View.VISIBLE : View.GONE);
        binding.toolbar.getMenu().setGroupVisible(R.id.group_card_actions, hasData);
        if (!hasData) return;

        ImageUtils.loadProfilePhoto(binding.imagePhoto, d.photoUri);
        binding.textName.setText(d.fullName.toUpperCase(java.util.Locale.getDefault()));
        MainActivity.setOptionalText(binding.textNickname,
                d.nickname == null ? null : getString(R.string.nickname_format, d.nickname));
        MainActivity.setOptionalText(binding.textHeadline, ShareUtils.join(" • ",
                MainActivity.firstNonBlank(d.position, d.occupation), d.company));
        MainActivity.setOptionalText(binding.textBiography, d.biography);

        LinearLayout container = binding.sectionContainer;
        container.removeAllViews();

        LinearLayout contact = addSection(container, R.string.section_contact);
        addRow(contact, R.drawable.ic_phone, R.string.label_phone, d.phone, Linkify.PHONE_NUMBERS);
        addRow(contact, R.drawable.ic_email, R.string.label_email, d.email, Linkify.EMAIL_ADDRESSES);
        addRow(contact, R.drawable.ic_location, R.string.label_address,
                ShareUtils.join(", ", d.address, d.city, d.postalCode), 0);
        finishSection(container, contact);

        LinearLayout emergency = addSection(container, R.string.section_emergency);
        addRow(emergency, R.drawable.ic_medical, R.string.label_blood_type, d.bloodType, 0);
        addRow(emergency, R.drawable.ic_notes, R.string.label_medical_notes, d.medicalNotes, 0);
        addRow(emergency, R.drawable.ic_person, R.string.label_emergency_name,
                ShareUtils.join(" • ", d.emergencyContactName, d.emergencyContactRelation), 0);
        addRow(emergency, R.drawable.ic_phone, R.string.label_emergency_phone, d.emergencyContactPhone, Linkify.PHONE_NUMBERS);
        finishSection(container, emergency);

        LinearLayout personal = addSection(container, R.string.section_personal);
        addRow(personal, R.drawable.ic_person, R.string.label_gender, d.gender, 0);
        addRow(personal, R.drawable.ic_event, R.string.label_birth,
                ShareUtils.join(", ", d.birthPlace, DateUtils.toDisplay(d.birthDate)), 0);
        addRow(personal, R.drawable.ic_flag, R.string.label_nationality, d.nationality, 0);
        finishSection(container, personal);

        LinearLayout education = addSection(container, R.string.section_education);
        addRow(education, R.drawable.ic_school, R.string.label_institution, d.institution, 0);
        addRow(education, R.drawable.ic_badge, R.string.label_education_level,
                ShareUtils.join(" – ", d.educationLevel, d.major), 0);
        addRow(education, R.drawable.ic_event, R.string.label_education_period, d.educationPeriod, 0);
        addRow(education, R.drawable.ic_notes, R.string.label_gpa, d.gpa, 0);
        addRow(education, R.drawable.ic_event, R.string.label_graduation_year,
                d.graduationYear == null ? null : String.valueOf(d.graduationYear), 0);
        finishSection(container, education);

        LinearLayout work = addSection(container, R.string.section_work);
        addRow(work, R.drawable.ic_work, R.string.label_occupation,
                ShareUtils.join(" • ", d.occupation, d.employmentType), 0);
        addRow(work, R.drawable.ic_badge, R.string.label_position, d.position, 0);
        addRow(work, R.drawable.ic_business, R.string.label_company, d.company, 0);
        addRow(work, R.drawable.ic_event, R.string.label_work_period, d.workPeriod, 0);
        addRow(work, R.drawable.ic_notes, R.string.label_job_description, d.jobDescription, 0);
        addRow(work, R.drawable.ic_location, R.string.label_work_address, d.workAddress, 0);
        finishSection(container, work);

        LinearLayout certs = addSection(container, R.string.section_certifications);
        addRow(certs, R.drawable.ic_certificate, R.string.label_certifications, d.certifications, 0);
        addRow(certs, R.drawable.ic_business, R.string.label_cert_issuer, d.certIssuer, 0);
        addRow(certs, R.drawable.ic_link, R.string.label_cert_url, d.certUrl, Linkify.WEB_URLS);
        finishSection(container, certs);

        LinearLayout projects = addSection(container, R.string.section_projects);
        addRow(projects, R.drawable.ic_project, R.string.label_project_name, d.projectName, 0);
        addRow(projects, R.drawable.ic_notes, R.string.label_project_description, d.projectDescription, 0);
        addRow(projects, R.drawable.ic_link, R.string.label_project_url, d.projectUrl, Linkify.WEB_URLS);
        finishSection(container, projects);

        addChipsSection(container, R.string.section_skills, d.skills);
        addChipsSection(container, R.string.section_languages, d.languages);

        LinearLayout extra = addSection(container, R.string.section_additional);
        addRow(extra, R.drawable.ic_favorite, R.string.label_hobbies, d.hobbies, 0);
        addRow(extra, R.drawable.ic_web, R.string.label_website, d.website, Linkify.WEB_URLS);
        addRow(extra, R.drawable.ic_link, R.string.label_instagram, d.instagram, Linkify.WEB_URLS);
        addRow(extra, R.drawable.ic_link, R.string.label_linkedin, d.linkedin, Linkify.WEB_URLS);
        addRow(extra, R.drawable.ic_link, R.string.label_facebook, d.facebook, Linkify.WEB_URLS);
        addRow(extra, R.drawable.ic_notes, R.string.label_other_info, d.otherInfo, 0);
        finishSection(container, extra);
    }

    private LinearLayout addSection(LinearLayout container, @StringRes int title) {
        ItemCardSectionBinding section = ItemCardSectionBinding.inflate(LayoutInflater.from(this), container, false);
        section.textSectionTitle.setText(title);
        container.addView(section.getRoot());
        return section.rows;
    }

    /** Hides sections that ended up without any filled-in rows. */
    private void finishSection(LinearLayout container, LinearLayout rows) {
        if (rows.getChildCount() == 0) {
            container.removeView((View) rows.getParent());
        }
    }

    private void addRow(LinearLayout rows, @DrawableRes int icon, @StringRes int label,
                        @Nullable String value, int linkMask) {
        if (value == null || value.trim().isEmpty()) return;
        ItemInfoRowBinding row = ItemInfoRowBinding.inflate(LayoutInflater.from(this), rows, false);
        row.imageIcon.setImageResource(icon);
        row.textLabel.setText(label);
        row.textValue.setText(value.trim());
        if (linkMask != 0) Linkify.addLinks(row.textValue, linkMask);
        rows.addView(row.getRoot());
    }

    private void addChipsSection(LinearLayout container, @StringRes int title, @Nullable String items) {
        if (items == null || items.trim().isEmpty()) return;
        LinearLayout rows = addSection(container, title);
        com.google.android.material.chip.ChipGroup group =
                new com.google.android.material.chip.ChipGroup(this);
        for (String item : items.split(",")) {
            String s = item.trim();
            if (s.isEmpty()) continue;
            Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.item_skill_chip, group, false);
            chip.setText(s);
            group.addView(chip);
        }
        rows.addView(group);
        finishSection(container, rows);
    }
}

