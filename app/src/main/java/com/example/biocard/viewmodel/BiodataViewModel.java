package com.example.biocard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.biocard.data.Biodata;
import com.example.biocard.data.BiodataRepository;

public class BiodataViewModel extends AndroidViewModel {

    private final BiodataRepository repository;

    public BiodataViewModel(@NonNull Application application) {
        super(application);
        repository = new BiodataRepository(application);
    }

    /** Emits the stored biodata, or {@code null} when none has been created yet. */
    public LiveData<Biodata> getBiodata() {
        return repository.getBiodata();
    }

    public void save(@NonNull Biodata biodata, @Nullable BiodataRepository.Callback callback) {
        repository.save(biodata, callback);
    }

    public void delete(@Nullable BiodataRepository.Callback callback) {
        repository.delete(callback);
    }
}

