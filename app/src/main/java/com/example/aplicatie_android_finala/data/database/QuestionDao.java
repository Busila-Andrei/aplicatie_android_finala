package com.example.aplicatie_android_finala.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Question... questions);

    @Query("SELECT * FROM questions")
    List<Question> getAll();

    @Query("SELECT * FROM questions WHERE testId = :testId")
    List<Question> getQuestionsByTestId(int testId);

    @Query("SELECT * FROM questions WHERE testId = :testId AND subcategoryId = :subcategoryId")
    List<Question> getQuestionsByTestIdAndSubcategoryId(int testId, int subcategoryId);

    @Query("SELECT COUNT(DISTINCT testId) FROM questions WHERE subcategoryId = :subcategoryId")
    int getQuestionsCountBySubcategoryId(int subcategoryId);
}
