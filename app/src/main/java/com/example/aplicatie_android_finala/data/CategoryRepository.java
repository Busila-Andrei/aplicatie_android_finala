package com.example.aplicatie_android_finala.data;

import android.util.Log;

import com.example.aplicatie_android_finala.config.ApiRepository;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Category;
import com.example.aplicatie_android_finala.data.database.Subcategory;

import java.util.List;

public class CategoryRepository {

    private static final String TAG = "CategoryRepository";
    private AppDatabase db;
    private ApiRepository apiRepository;

    public CategoryRepository(AppDatabase db) {
        this.db = db;
        this.apiRepository = new ApiRepository();
    }

    public void getCategoriesFromDatabase(DatabaseCallback callback) {
        new Thread(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            Log.d(TAG, "Read categories from database: " + categories);
            callback.onResult(categories);
        }).start();
    }

    public void getSubcategoriesFromDatabase(int categoryId, SubcategoryDatabaseCallback callback) {
        new Thread(() -> {
            List<Subcategory> subcategories = db.subcategoryDao().getSubcategoriesByCategoryId(categoryId);
            Log.d(TAG, "Read subcategories from database: " + subcategories);
            callback.onResult(subcategories);
        }).start();
    }

    public interface DatabaseCallback {
        void onResult(List<Category> categories);
    }

    public interface SubcategoryDatabaseCallback {
        void onResult(List<Subcategory> subcategories);
    }
}
