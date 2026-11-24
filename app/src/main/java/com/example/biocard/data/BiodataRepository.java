package com.example.biocard.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.biocard.utils.ImageUtils;

import java.util.concurrent.ExecutorService;

/** Mediates between the ViewModel and the Room DAO. All writes run off the main thread. */
public class BiodataRepository {

    private static final String TAG = "BiodataRepository";

    /** Result callback, always invoked on the main thread. */
    public interface Callback {
        void onComplete(boolean success);
    }

    private final Context appContext;
    private final BiodataDao dao;
    private final LiveData<Biodata> biodata;
    private final ExecutorService executor = AppDatabase.IO_EXECUTOR;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public BiodataRepository(@NonNull Context context) {
        appContext = context.getApplicationContext();
        dao = AppDatabase.getInstance(appContext).biodataDao();
        biodata = dao.observeBiodata();
    }

    public LiveData<Biodata> getBiodata() {
        return biodata;
    }

    public void save(@NonNull Biodata data, @Nullable Callback callback) {
        executor.execute(() -> {
            boolean success;
            try {
                data.id = Biodata.SINGLE_ID;
                data.updatedAt = System.currentTimeMillis();
                dao.upsert(data);
                // Remove photos that are no longer referenced (replaced or removed).
                ImageUtils.deleteUnusedPhotos(appContext, data.photoUri);
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to save biodata", e);
                success = false;
            }
            deliver(callback, success);
        });
    }

    public void delete(@Nullable Callback callback) {
        executor.execute(() -> {
            boolean success;
            try {
                dao.deleteAll();
                ImageUtils.deleteUnusedPhotos(appContext, null);
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete biodata", e);
                success = false;
            }
            deliver(callback, success);
        });
    }

    private void deliver(@Nullable Callback callback, boolean success) {
        if (callback != null) {
            mainHandler.post(() -> callback.onComplete(success));
        }
    }
}

