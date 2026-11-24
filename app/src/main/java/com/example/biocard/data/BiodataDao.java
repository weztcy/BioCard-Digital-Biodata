package com.example.biocard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BiodataDao {

    @Query("SELECT * FROM biodata WHERE id = " + Biodata.SINGLE_ID + " LIMIT 1")
    LiveData<Biodata> observeBiodata();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Biodata biodata);

    @Query("DELETE FROM biodata")
    void deleteAll();
}

