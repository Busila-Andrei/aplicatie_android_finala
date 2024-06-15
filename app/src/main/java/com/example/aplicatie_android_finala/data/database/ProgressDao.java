package com.example.aplicatie_android_finala.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProgressDao {

    @Insert
    void insertAll(Progress... progresses);

    @Query("SELECT * FROM progress WHERE subcategoryId = :subcategoryId")
    List<Progress> getProgressBySubcategoryId(int subcategoryId);

    @Update
    void updateProgress(Progress progress);
}
