package com.example.aplicatie_android_finala.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Word... words);

    @Query("SELECT * FROM words")
    List<Word> getAllWords();
}
