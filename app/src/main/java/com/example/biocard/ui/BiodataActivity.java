package com.example.biocard.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.biocard.R;
import com.example.biocard.data.AppDatabase;
import com.example.biocard.data.Biodata;
import com.example.biocard.databinding.ActivityBiodataBinding;
import com.example.biocard.utils.DateUtils;
import com.example.biocard.utils.ImageUtils;
import com.example.biocard.utils.ValidationUtils;
import com.example.biocard.viewmodel.BiodataViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

/** Create / edit biodata form (PRD §9–§12, §14). */
public class BiodataActivity extends BaseActivity {

    private static final String STATE_PHOTO_URI = "state_photo_uri";
    private static final String STATE_BIRTH_DATE = "state_birth_date";
    private static final String STATE_LOADED = "state_loaded";
    private static final String TAG_DATE_PICKER = "birth_date_picker";

    private ActivityBiodataBinding binding;
    private BiodataViewModel viewModel;

    @Nullable private String photoUri;
    @Nullable private String birthDateIso;
    private boolean loaded;
    private boolean saving;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::onPhotoPicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBiodataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applySystemBarInsets(binding.getRoot());

        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getString(STATE_PHOTO_URI);
            birthDateIso = savedInstanceState.getString(STATE_BIRTH_DATE);
            loaded = savedInstanceState.getBoolean(STATE_LOADED);
        }

        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                save();
                return true;
            }
            return false;
        });

        setupPhoto();
        setupBirthDate();
        setupErrorClearing();
        binding.buttonSave.setOnClickListener(v -> save());

        updatePhotoUi();
        updateBirthDateUi();

        viewModel = new ViewModelProvider(this).get(BiodataViewModel.class);
        viewModel.getBiodata().observe(this, data -> {
            binding.toolbar.setTitle(data == null ? R.string.title_create_biodata : R.string.title_edit_biodata);
            if (!loaded && data != null) {
                populate(data);
            }
            loaded = true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHOTO_URI, photoUri);
        outState.putString(STATE_BIRTH_DATE, birthDateIso);
        outState.putBoolean(STATE_LOADED, loaded);
    }

    // region Photo

    private void setupPhoto() {
        View.OnClickListener choose = v -> pickPhoto.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
        binding.imagePhoto.setOnClickListener(choose);
        binding.buttonChangePhoto.setOnClickListener(choose);
        binding.buttonChoosePhoto.setOnClickListener(choose);
        binding.buttonRemovePhoto.setOnClickListener(v -> {
            photoUri = null;
            updatePhotoUi();
        });
    }

    private void onPhotoPicked(@Nullable Uri uri) {
        if (uri == null) return;
        binding.progressPhoto.setVisibility(View.VISIBLE);
        AppDatabase.IO_EXECUTOR.execute(() -> {
            String imported = null;
            try {
                imported = ImageUtils.importPhoto(getApplicationContext(), uri);
            } catch (Exception ignored) {
                // handled below
            }
            final String result = imported;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                binding.progressPhoto.setVisibility(View.GONE);
                if (result == null) {
                    Toast.makeText(this, R.string.msg_photo_failed, Toast.LENGTH_SHORT).show();
                } else {
                    photoUri = result;
                    updatePhotoUi();
                }
            });
        });
    }

    private void updatePhotoUi() {
        ImageUtils.loadProfilePhoto(binding.imagePhoto, photoUri);
        boolean hasPhoto = photoUri != null;
        binding.buttonRemovePhoto.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
        binding.buttonChoosePhoto.setText(hasPhoto ? R.string.action_change_photo : R.string.action_choose_photo);
    }

    // endregion

    // region Date of birth

    private void setupBirthDate() {
        binding.inputBirthDate.setOnClickListener(v -> showDatePicker());
        binding.layoutBirthDate.setEndIconOnClickListener(v -> {
            birthDateIso = null;
            updateBirthDateUi();
        });
    }

    private void showDatePicker() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_DATE_PICKER) != null) return;
        Long selection = DateUtils.toUtcMillis(birthDateIso);
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.label_birth_date)
                .setSelection(selection != null ? selection : MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.now())
                        .build())
                .build();
        picker.addOnPositiveButtonClickListener(millis -> {
            birthDateIso = DateUtils.fromUtcMillis(millis);
            updateBirthDateUi();
        });
        picker.show(getSupportFragmentManager(), TAG_DATE_PICKER);
    }

    private void updateBirthDateUi() {
        binding.inputBirthDate.setText(DateUtils.toDisplay(birthDateIso));
        binding.layoutBirthDate.setEndIconVisible(birthDateIso != null);
    }

    // endregion

    private void populate(Biodata d) {
        setText(binding.inputFullName, d.fullName);
        setText(binding.inputNickname, d.nickname);
        binding.inputGender.setText(d.gender, false);
        setText(binding.inputBirthPlace, d.birthPlace);
        setText(binding.inputNationality, d.nationality);

        setText(binding.inputPhone, d.phone);
        setText(binding.inputEmail, d.email);
        setText(binding.inputAddress, d.address);
        setText(binding.inputCity, d.city);
        setText(binding.inputPostalCode, d.postalCode);

        // Emergency & Medical (ICE)
        binding.inputBloodType.setText(d.bloodType == null ? "" : d.bloodType, false);
        setText(binding.inputMedicalNotes, d.medicalNotes);
        setText(binding.inputEmergencyName, d.emergencyContactName);
        binding.inputEmergencyRelation.setText(d.emergencyContactRelation == null ? "" : d.emergencyContactRelation, false);
        setText(binding.inputEmergencyPhone, d.emergencyContactPhone);

        binding.inputEducationLevel.setText(d.educationLevel == null ? "" : d.educationLevel, false);
        setText(binding.inputInstitution, d.institution);
        setText(binding.inputMajor, d.major);
        setText(binding.inputEducationPeriod, d.educationPeriod);
        setText(binding.inputGpa, d.gpa);
        setText(binding.inputGraduationYear, d.graduationYear == null ? null : String.valueOf(d.graduationYear));

        setText(binding.inputOccupation, d.occupation);
        setText(binding.inputCompany, d.company);
        setText(binding.inputPosition, d.position);
        binding.inputEmploymentType.setText(d.employmentType == null ? "" : d.employmentType, false);
        setText(binding.inputWorkPeriod, d.workPeriod);
        setText(binding.inputJobDescription, d.jobDescription);
        setText(binding.inputWorkAddress, d.workAddress);

        // Certifications & Licenses
        setText(binding.inputCertifications, d.certifications);
        setText(binding.inputCertIssuer, d.certIssuer);
        setText(binding.inputCertUrl, d.certUrl);

        // Featured Projects & Portfolio
        setText(binding.inputProjectName, d.projectName);
        setText(binding.inputProjectDescription, d.projectDescription);
        setText(binding.inputProjectUrl, d.projectUrl);

        setText(binding.inputWebsite, d.website);
        setText(binding.inputInstagram, d.instagram);
        setText(binding.inputLinkedin, d.linkedin);
        setText(binding.inputFacebook, d.facebook);
        setText(binding.inputSkills, d.skills);
        setText(binding.inputLanguages, d.languages);
        setText(binding.inputHobbies, d.hobbies);
        setText(binding.inputBiography, d.biography);
        setText(binding.inputOtherInfo, d.otherInfo);

        photoUri = d.photoUri;
        birthDateIso = d.birthDate;
        updatePhotoUi();
        updateBirthDateUi();
    }

    // region Validation & save

    private void save() {
        if (saving) return;
        TextInputLayout firstInvalid = validate();
        if (firstInvalid != null) {
            Snackbar.make(binding.getRoot(), R.string.msg_invalid_input, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.buttonSave)
                    .show();
            binding.scrollView.post(() -> {
                binding.scrollView.smoothScrollTo(0, Math.max(0, topWithinScroll(firstInvalid) - 48));
                if (firstInvalid.getEditText() != null && firstInvalid != binding.layoutGender) {
                    firstInvalid.getEditText().requestFocus();
                }
            });
            return;
        }

        Biodata d = new Biodata();
        d.fullName = text(binding.inputFullName);
        d.nickname = optional(binding.inputNickname);
        d.gender = text(binding.inputGender);
        d.birthPlace = optional(binding.inputBirthPlace);
        d.birthDate = birthDateIso;
        d.nationality = optional(binding.inputNationality);

        d.phone = optional(binding.inputPhone);
        d.email = optional(binding.inputEmail);
        d.address = optional(binding.inputAddress);
        d.city = optional(binding.inputCity);
        d.postalCode = optional(binding.inputPostalCode);

        // Emergency & Medical (ICE)
        d.bloodType = optional(binding.inputBloodType);
        d.medicalNotes = optional(binding.inputMedicalNotes);
        d.emergencyContactName = optional(binding.inputEmergencyName);
        d.emergencyContactRelation = optional(binding.inputEmergencyRelation);
        d.emergencyContactPhone = optional(binding.inputEmergencyPhone);

        d.educationLevel = optional(binding.inputEducationLevel);
        d.institution = optional(binding.inputInstitution);
        d.major = optional(binding.inputMajor);
        d.educationPeriod = optional(binding.inputEducationPeriod);
        d.gpa = optional(binding.inputGpa);
        String year = optional(binding.inputGraduationYear);
        d.graduationYear = year == null ? null : Integer.parseInt(year);

        d.occupation = optional(binding.inputOccupation);
        d.company = optional(binding.inputCompany);
        d.position = optional(binding.inputPosition);
        d.employmentType = optional(binding.inputEmploymentType);
        d.workPeriod = optional(binding.inputWorkPeriod);
        d.jobDescription = optional(binding.inputJobDescription);
        d.workAddress = optional(binding.inputWorkAddress);

        // Certifications & Licenses
        d.certifications = optional(binding.inputCertifications);
        d.certIssuer = optional(binding.inputCertIssuer);
        d.certUrl = optional(binding.inputCertUrl);

        // Featured Projects & Portfolio
        d.projectName = optional(binding.inputProjectName);
        d.projectDescription = optional(binding.inputProjectDescription);
        d.projectUrl = optional(binding.inputProjectUrl);

        d.website = optional(binding.inputWebsite);
        d.instagram = optional(binding.inputInstagram);
        d.linkedin = optional(binding.inputLinkedin);
        d.facebook = optional(binding.inputFacebook);
        d.skills = normalizeList(optional(binding.inputSkills));
        d.languages = normalizeList(optional(binding.inputLanguages));
        d.hobbies = optional(binding.inputHobbies);
        d.biography = optional(binding.inputBiography);
        d.otherInfo = optional(binding.inputOtherInfo);
        d.photoUri = photoUri;

        setSaving(true);
        viewModel.save(d, success -> {
            setSaving(false);
            if (success) {
                Toast.makeText(this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, BioCardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else {
                Snackbar.make(binding.getRoot(), R.string.msg_save_failed, Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.buttonSave)
                        .show();
            }
        });
    }

    /** Validates the form, shows inline errors and returns the first invalid field (or null). */
    @Nullable
    private TextInputLayout validate() {
        TextInputLayout first = null;

        if (ValidationUtils.isBlank(text(binding.inputFullName))) {
            binding.layoutFullName.setError(getString(R.string.error_full_name));
            first = binding.layoutFullName;
        }
        if (ValidationUtils.isBlank(text(binding.inputGender))) {
            binding.layoutGender.setError(getString(R.string.error_gender));
            if (first == null) first = binding.layoutGender;
        }
        String phone = optional(binding.inputPhone);
        if (phone != null && !ValidationUtils.isValidPhone(phone)) {
            binding.layoutPhone.setError(getString(R.string.error_phone));
            if (first == null) first = binding.layoutPhone;
        }
        String email = optional(binding.inputEmail);
        if (email != null && !ValidationUtils.isValidEmail(email)) {
            binding.layoutEmail.setError(getString(R.string.error_email));
            if (first == null) first = binding.layoutEmail;
        }
        String emergPhone = optional(binding.inputEmergencyPhone);
        if (emergPhone != null && !ValidationUtils.isValidPhone(emergPhone)) {
            binding.layoutEmergencyPhone.setError(getString(R.string.error_emergency_phone));
            if (first == null) first = binding.layoutEmergencyPhone;
        }
        String year = optional(binding.inputGraduationYear);
        if (year != null && !ValidationUtils.isValidGraduationYear(year)) {
            binding.layoutGraduationYear.setError(getString(R.string.error_graduation_year));
            if (first == null) first = binding.layoutGraduationYear;
        }
        String website = optional(binding.inputWebsite);
        if (website != null && !ValidationUtils.isValidWebsite(website)) {
            binding.layoutWebsite.setError(getString(R.string.error_website));
            if (first == null) first = binding.layoutWebsite;
        }
        String certUrl = optional(binding.inputCertUrl);
        if (certUrl != null && !ValidationUtils.isValidWebsite(certUrl)) {
            binding.layoutCertUrl.setError(getString(R.string.error_cert_url));
            if (first == null) first = binding.layoutCertUrl;
        }
        String projUrl = optional(binding.inputProjectUrl);
        if (projUrl != null && !ValidationUtils.isValidWebsite(projUrl)) {
            binding.layoutProjectUrl.setError(getString(R.string.error_project_url));
            if (first == null) first = binding.layoutProjectUrl;
        }
        return first;
    }

    private void setupErrorClearing() {
        clearErrorOnChange(binding.layoutFullName);
        clearErrorOnChange(binding.layoutGender);
        clearErrorOnChange(binding.layoutPhone);
        clearErrorOnChange(binding.layoutEmail);
        clearErrorOnChange(binding.layoutEmergencyPhone);
        clearErrorOnChange(binding.layoutGraduationYear);
        clearErrorOnChange(binding.layoutWebsite);
        clearErrorOnChange(binding.layoutCertUrl);
        clearErrorOnChange(binding.layoutProjectUrl);
    }

    private static void clearErrorOnChange(TextInputLayout layout) {
        EditText edit = layout.getEditText();
        if (edit == null) return;
        edit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                if (layout.getError() != null) layout.setError(null);
            }
        });
    }

    private void setSaving(boolean value) {
        saving = value;
        binding.buttonSave.setEnabled(!value);
        binding.buttonSave.setText(value ? R.string.msg_saving : R.string.action_save_biodata);
    }

    private int topWithinScroll(View view) {
        int top = 0;
        View v = view;
        while (v != null && v != binding.scrollView) {
            top += v.getTop();
            v = v.getParent() instanceof View ? (View) v.getParent() : null;
        }
        return top;
    }

    // endregion

    // region Helpers

    private static String text(EditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    @Nullable
    private static String optional(EditText edit) {
        String value = text(edit);
        return value.isEmpty() ? null : value;
    }

    private static void setText(EditText edit, @Nullable String value) {
        edit.setText(value == null ? "" : value);
    }

    /** "Java,Android ,  UI/UX" -> "Java, Android, UI/UX". */
    @Nullable
    private static String normalizeList(@Nullable String value) {
        if (value == null) return null;
        StringBuilder sb = new StringBuilder();
        for (String part : value.split(",")) {
            String item = part.trim();
            if (item.isEmpty()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(item);
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    // endregion
}

