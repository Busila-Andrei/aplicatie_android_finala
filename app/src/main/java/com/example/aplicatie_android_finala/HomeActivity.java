package com.example.aplicatie_android_finala;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.aplicatie_android_finala.config.ApiRepository;
import com.example.aplicatie_android_finala.config.ApiResponse;
import com.example.aplicatie_android_finala.config.ApiService;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Category;
import com.example.aplicatie_android_finala.data.database.Progress;
import com.example.aplicatie_android_finala.data.database.Question;
import com.example.aplicatie_android_finala.data.database.Subcategory;
import com.example.aplicatie_android_finala.data.database.Word;
import com.example.aplicatie_android_finala.data.database.WordDao;
import com.example.aplicatie_android_finala.data.dto.TokenRequest;
import com.example.aplicatie_android_finala.fragments.QuestionsFragment;
import com.example.aplicatie_android_finala.fragments.SectionsFragment;
import com.example.aplicatie_android_finala.fragments.WordsFragment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private AppDatabase db;
    private WordDao wordDao;
    private ApiRepository apiRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "word-database").build();
        wordDao = db.wordDao();
        apiRepository = new ApiRepository();
        fetchAndStoreCategoriesAndSubcategories(() -> {
            onCategoriesAndSubcategoriesFetched();
            addProgressForAllSubcategories();
        });
        fetchQuestionsFromApi();

        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);
        Button btn5 = findViewById(R.id.button5);
        TextView tvLogout = findViewById(R.id.tv_logout);

        // Listeneri pentru butoane
        btn1.setOnClickListener(v -> loadFragment(new SectionsFragment()));
        btn3.setOnClickListener(v -> loadFragment(new QuestionsFragment()));
        btn4.setOnClickListener(v -> loadFragment(new WordsFragment()));
        btn5.setOnClickListener(v -> fetchWordsFromApi());

        // Listener pentru TextView Log Out
        tvLogout.setOnClickListener(v -> logout());

        // Încarcă implicit primul fragment
        if (savedInstanceState == null) {
            loadFragment(new SectionsFragment());
        }

        Log.d(TAG, "HomeActivity created");
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
    }

    private void fetchWordsFromApi() {
        Log.d(TAG, "Fetching words from API...");
        apiRepository.getWords(new ApiRepository.ApiResponseCallback<List<Word>>() {
            @Override
            public void onSuccess(List<Word> words) {
                Log.d(TAG, "Words parsed from API response, inserting into database...");
                executorService.execute(() -> {
                    wordDao.insertAll(words.toArray(new Word[0]));
                    Log.d(TAG, "Words inserted into database.");
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to fetch words from API: " + errorMessage);
            }
        });
    }

    private void fetchAndStoreCategoriesAndSubcategories(Runnable onComplete) {
        Log.d(TAG, "Fetching categories and subcategories from API...");
        apiRepository.getCategories(new ApiRepository.ApiResponseCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                executorService.execute(() -> {
                    try {
                        db.categoryDao().insertAll(categories);
                        Log.d(TAG, "Categories inserted into database: " + categories.size());
                        for (Category category : categories) {
                            fetchAndStoreSubcategories(category.id, onComplete);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error inserting categories: " + e.getMessage());
                        onComplete.run();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "API error: " + errorMessage);
                onComplete.run();
            }
        });
    }

    private void fetchAndStoreSubcategories(int categoryId, Runnable onComplete) {
        Log.d(TAG, "Fetching subcategories for category ID: " + categoryId);
        apiRepository.getSubcategories(categoryId, new ApiRepository.ApiResponseCallback<List<Subcategory>>() {
            @Override
            public void onSuccess(List<Subcategory> subcategories) {
                executorService.execute(() -> {
                    try {
                        for (Subcategory subcategory : subcategories) {
                            subcategory.categoryId = categoryId;
                        }
                        db.subcategoryDao().insertAll(subcategories);
                        Log.d(TAG, "Subcategories inserted into database: " + subcategories.size());
                        onComplete.run();
                    } catch (Exception e) {
                        Log.e(TAG, "Error inserting subcategories: " + e.getMessage());
                        onComplete.run();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "API error: " + errorMessage);
                onComplete.run();
            }
        });
    }

    private void addProgressForSubcategory(int subcategoryId, String subcategoryName, int totalTests) {
        Log.d(TAG, "Adding to Progress: Subcategory ID: " + subcategoryId + ", Total Tests: " + totalTests);

        Progress progress = new Progress();
        progress.subcategoryId = subcategoryId;
        progress.subcategoryName = subcategoryName;
        progress.completed = 0;
        progress.total = totalTests;

        executorService.execute(() -> {
            db.progressDao().insertAll(progress);
            Log.d(TAG, "Progress inserted into database: " + progress);
        });
    }

    private void addProgressForAllSubcategories() {
        executorService.execute(() -> {
            List<Subcategory> subcategories = db.subcategoryDao().getAllSubcategories();
            Log.d(TAG, "Fetched subcategories from database: " + subcategories.size());
            for (Subcategory subcategory : subcategories) {
                int totalTests = db.questionDao().getQuestionsCountBySubcategoryId(subcategory.id);
                Log.d(TAG, "Subcategory ID: " + subcategory.id + ", Name: " + subcategory.name + ", Total Tests: " + totalTests);
                addProgressForSubcategory(subcategory.id, subcategory.name, totalTests);
            }
        });
    }

    private void fetchQuestionsFromApi() {
        Log.d(TAG, "Fetching questions from API...");
        apiRepository.getQuestions(new ApiRepository.ApiResponseCallback<List<Question>>() {
            @Override
            public void onSuccess(List<Question> questions) {
                Log.d(TAG, "Questions parsed from API response, inserting into database...");
                executorService.execute(() -> {
                    db.questionDao().insertAll(questions.toArray(new Question[0]));
                    Log.d(TAG, "Questions inserted into database.");
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to fetch questions from API: " + errorMessage);
            }
        });
    }

    private void onCategoriesAndSubcategoriesFetched() {
        Log.d(TAG, "Categories and subcategories fetched.");
        // Notifică fragmentul că datele sunt gata
        SectionsFragment sectionsFragment = (SectionsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (sectionsFragment != null) {
            sectionsFragment.displayCategories();
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);

        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences.");
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        TokenRequest tokenRequest = new TokenRequest(token);
        Call<ApiResponse> call = apiService.logoutAccount(tokenRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Logout successful.");
                    // Clear the token from SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("jwt_token");
                    editor.apply();
                    // Redirect to MainActivity
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Logout failed with status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to execute logout request", t);
            }
        });
    }
}
