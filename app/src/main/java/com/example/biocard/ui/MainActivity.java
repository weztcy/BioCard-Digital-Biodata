package com.example.biocard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.biocard.R;
import com.example.biocard.data.Biodata;
import com.example.biocard.databinding.ActivityMainBinding;
import com.example.biocard.utils.ImageUtils;
import com.example.biocard.utils.ShareUtils;
import com.example.biocard.viewmodel.BiodataViewModel;

/** Home / Dashboard (PRD §8). */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private BiodataViewModel viewModel;
    @Nullable
    private Biodata current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applySystemBarInsets(binding.getRoot());

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_about) {
                openAbout();
                return true;
            }
            return false;
        });

        binding.buttonCreate.setOnClickListener(v -> openForm());
        binding.buttonAboutEmpty.setOnClickListener(v -> openAbout());
        binding.cardPreview.setOnClickListener(v -> openBioCard());
        binding.buttonView.setOnClickListener(v -> openBioCard());
        binding.buttonEdit.setOnClickListener(v -> openForm());
        binding.buttonShare.setOnClickListener(v -> share());
        binding.buttonDelete.setOnClickListener(v -> confirmDelete(viewModel, null));
        binding.buttonAbout.setOnClickListener(v -> openAbout());

        viewModel = new ViewModelProvider(this).get(BiodataViewModel.class);
        viewModel.getBiodata().observe(this, this::render);

        if (getIntent() != null && getIntent().getBooleanExtra("seed_sample", false)) {
            seedSampleData();
        }
    }

    private void seedSampleData() {
        Biodata sample = new Biodata();
        sample.fullName = "Yohanes Dimas Pratama";
        sample.nickname = "Dimas";
        sample.gender = "Male";
        sample.birthPlace = "Jakarta";
        sample.birthDate = "1998-05-14";
        sample.nationality = "Indonesian";

        sample.phone = "+62 812 3456 7890";
        sample.email = "yohanes.dimas@example.com";
        sample.address = "Jl. Jenderal Sudirman Kav. 52-53";
        sample.city = "Jakarta Selatan";
        sample.postalCode = "12190";

        sample.bloodType = "O+";
        sample.medicalNotes = "Tidak memiliki riwayat alergi obat berat";
        sample.emergencyContactName = "Siti Rahmawati";
        sample.emergencyContactRelation = "Spouse / Partner";
        sample.emergencyContactPhone = "+62 811 9876 5432";

        sample.educationLevel = "Bachelor";
        sample.institution = "Institut Teknologi Bandung";
        sample.major = "Teknik Informatika";
        sample.educationPeriod = "2016 – 2020";
        sample.gpa = "3.88 / 4.00";
        sample.graduationYear = 2020;

        sample.occupation = "Senior Mobile Engineer";
        sample.company = "TechNova Digital Solutions";
        sample.position = "Lead Android Architect";
        sample.employmentType = "Full-time";
        sample.workPeriod = "2020 – Sekarang";
        sample.jobDescription = "Memimpin arsitektur mobile apps Android berbasis MVVM, Clean Architecture, Material 3, dan Room Database.";
        sample.workAddress = "Pacific Century Place Lt. 21, SCBD, Jakarta";

        sample.certifications = "Google Associate Android Developer & AWS Certified Solutions Architect";
        sample.certIssuer = "Google & Amazon Web Services";
        sample.certUrl = "https://developers.google.com";

        sample.projectName = "BioCard — Digital Biodata & Business Card";
        sample.projectDescription = "Aplikasi mobile modern untuk membuat, mengelola, dan membagikan biodata digital interaktif berbasis offline-first.";
        sample.projectUrl = "https://github.com/example/biocard";

        sample.website = "https://yohanesdimas.dev";
        sample.linkedin = "https://linkedin.com/in/yohanesdimas";
        sample.instagram = "https://instagram.com/yohanes.dimas";
        sample.facebook = "https://facebook.com/yohanesdimas";
        sample.skills = "Java, Android SDK, MVVM, Room SQLite, Material 3, Git, REST API";
        sample.languages = "Bahasa Indonesia (Native), English (Professional), Japanese (Basic)";
        sample.hobbies = "Mobile App Development, Photography, Cycling";
        sample.biography = "Senior Android Engineer dengan pengalaman lebih dari 5 tahun dalam merancang dan mengembangkan aplikasi mobile berkualitas tinggi dengan fokus pada user experience intuitif dan performa optimal.";
        sample.otherInfo = "Tersedia untuk pembicara tech talk, workshop mobile development, dan konsultasi arsitektur Android.";

        viewModel.save(sample, null);
    }

    private void render(@Nullable Biodata data) {
        current = data;
        boolean hasData = data != null;
        binding.groupEmpty.setVisibility(hasData ? View.GONE : View.VISIBLE);
        binding.groupContent.setVisibility(hasData ? View.VISIBLE : View.GONE);
        if (!hasData) return;

        ImageUtils.loadProfilePhoto(binding.imagePhoto, data.photoUri);
        binding.textName.setText(data.fullName);

        String headline = ShareUtils.join(" • ",
                firstNonBlank(data.position, data.occupation), data.company);
        setOptionalText(binding.textHeadline, headline);
        setOptionalText(binding.textContact, ShareUtils.join("  ·  ", data.city, data.email, data.phone));
    }

    private void share() {
        if (current == null) {
            Toast.makeText(this, R.string.msg_empty_biodata, Toast.LENGTH_SHORT).show();
            return;
        }
        ShareUtils.shareBiodata(this, current);
    }

    private void openForm() {
        startActivity(new Intent(this, BiodataActivity.class));
    }

    private void openBioCard() {
        startActivity(new Intent(this, BioCardActivity.class));
    }

    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    static void setOptionalText(android.widget.TextView view, @Nullable String text) {
        boolean empty = text == null || text.trim().isEmpty();
        view.setVisibility(empty ? View.GONE : View.VISIBLE);
        view.setText(empty ? "" : text);
    }

    @Nullable
    static String firstNonBlank(@Nullable String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }
}

