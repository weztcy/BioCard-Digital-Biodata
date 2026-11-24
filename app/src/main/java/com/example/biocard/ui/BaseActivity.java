package com.example.biocard.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biocard.R;
import com.example.biocard.viewmodel.BiodataViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Common behaviour: edge-to-edge window insets and the shared delete confirmation dialog. */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
    }

    /** Pads the root view so content is never drawn behind system bars, cutouts or the keyboard. */
    protected void applySystemBarInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** PRD §15: confirmation dialog before deleting all biodata. */
    protected void confirmDelete(BiodataViewModel viewModel, @Nullable Runnable onDeleted) {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_delete)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        viewModel.delete(success -> {
                            Toast.makeText(this, success ? R.string.msg_delete_success
                                    : R.string.msg_delete_failed, Toast.LENGTH_SHORT).show();
                            if (success && onDeleted != null) onDeleted.run();
                        }))
                .show();
    }
}

