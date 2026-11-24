package com.example.biocard.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biocard.databinding.ActivitySplashBinding;

/** FR-01: shows logo, app name and tagline, then opens Home. */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1200;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openHome = () -> {
        Intent intent = new Intent(this, MainActivity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.splashContent.setAlpha(0f);
        binding.splashContent.setTranslationY(24f);
        binding.splashContent.animate().alpha(1f).translationY(0f).setDuration(450).start();

        // Ignore back presses while the splash is visible.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(openHome, SPLASH_DURATION_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(openHome);
    }
}

