package com.example.aplicatie_android_finala.config;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aplicatie_android_finala.config.ApiResponse;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.database.Category;
import com.example.aplicatie_android_finala.data.database.Subcategory;
import com.example.aplicatie_android_finala.data.database.Word;
import com.example.aplicatie_android_finala.data.database.Question;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {

    private static final String TAG = "ApiRepository";

    public interface ApiResponseCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }

    public void getCategories(ApiResponseCallback<List<Category>> callback) {
        Log.d(TAG, "getCategories: Fetching categories from API.");
        Call<ApiResponse> call = RetrofitClient.getApiService().getCategories();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    Gson gson = new Gson();
                    Type categoryListType = new TypeToken<ArrayList<Category>>() {}.getType();
                    List<Category> categories = gson.fromJson(gson.toJson(data), categoryListType);
                    Log.d(TAG, "getCategories: Success - " + categories.size() + " categories fetched.");
                    callback.onSuccess(categories);
                } else {
                    String errorMessage = "Failed to load categories";
                    Log.e(TAG, "getCategories: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "getCategories: Error - " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getSubcategories(int categoryId, ApiResponseCallback<List<Subcategory>> callback) {
        Log.d(TAG, "getSubcategories: Fetching subcategories for category ID: " + categoryId);
        Call<ApiResponse> call = RetrofitClient.getApiService().getSubcategories(categoryId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    Gson gson = new Gson();
                    Type subcategoryListType = new TypeToken<ArrayList<Subcategory>>() {}.getType();
                    List<Subcategory> subcategories = gson.fromJson(gson.toJson(data), subcategoryListType);
                    Log.d(TAG, "getSubcategories: Success - " + subcategories.size() + " subcategories fetched.");
                    callback.onSuccess(subcategories);
                } else {
                    String errorMessage = "Failed to load subcategories";
                    Log.e(TAG, "getSubcategories: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "getSubcategories: Error - " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getWords(ApiResponseCallback<List<Word>> callback) {
        Log.d(TAG, "getWords: Fetching words from API.");
        Call<ApiResponse> call = RetrofitClient.getApiService().getWords();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LinkedTreeMap> data = (List<LinkedTreeMap>) response.body().getData();
                    List<Word> words = new ArrayList<>();
                    for (LinkedTreeMap map : data) {
                        int id = ((Double) map.get("id")).intValue();
                        String englishWord = (String) map.get("englishWord");
                        String romanianWord = (String) map.get("romanianWord");
                        Word word = new Word();
                        word.id = id;
                        word.englishWord = englishWord;
                        word.romanianWord = romanianWord;
                        words.add(word);
                    }
                    Log.d(TAG, "getWords: Success - " + words.size() + " words fetched.");
                    callback.onSuccess(words);
                } else {
                    String errorMessage = "Failed to fetch words from API";
                    Log.e(TAG, "getWords: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "getWords: Error - " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getQuestions(ApiResponseCallback<List<Question>> callback) {
        Log.d(TAG, "getQuestions: Fetching questions from API.");
        Call<ApiResponse> call = RetrofitClient.getApiService().getQuestions();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LinkedTreeMap> data = (List<LinkedTreeMap>) response.body().getData();
                    List<Question> questions = new ArrayList<>();
                    for (LinkedTreeMap map : data) {
                        int id = ((Double) map.get("id")).intValue();
                        String type = (String) map.get("type");
                        String questionText = (String) map.get("questionText");
                        String correctAnswer = (String) map.get("correctAnswer");
                        String otherAnswers = (String) map.get("otherAnswers");
                        Long testId = ((Double) map.get("testId")).longValue();
                        Long subcategoryId = ((Double) map.get("subcategoryId")).longValue();

                        Question question = new Question();
                        question.id = id;
                        question.type = type;
                        question.questionText = questionText;
                        question.correctAnswer = correctAnswer;
                        question.otherAnswers = otherAnswers;
                        question.testId = testId;
                        question.subcategoryId = subcategoryId;

                        questions.add(question);
                    }
                    Log.d(TAG, "getQuestions: Success - " + questions.size() + " questions fetched.");
                    callback.onSuccess(questions);
                } else {
                    String errorMessage = "Failed to fetch questions from API";
                    Log.e(TAG, "getQuestions: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "getQuestions: Error - " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

}
