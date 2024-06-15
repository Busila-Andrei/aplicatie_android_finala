package com.example.aplicatie_android_finala.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubcategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Subcategory> subcategories);

    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId")
    List<Subcategory> getSubcategoriesByCategoryId(int categoryId);

    @Query("SELECT * FROM questions WHERE testId = :testId AND subcategoryId = :subcategoryId")
    List<Question> getQuestionsByTestIdAndSubcategoryId(int testId, int subcategoryId);


    @Query("SELECT * FROM subcategories")
    List<Subcategory> getAllSubcategories();


}
