package com.example.aplicatie_android_finala.config;

import com.example.aplicatie_android_finala.data.dto.EmailRequest;
import com.example.aplicatie_android_finala.data.dto.LoginRequest;
import com.example.aplicatie_android_finala.data.dto.RegisterRequest;
import com.example.aplicatie_android_finala.data.dto.TokenRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/api/v1/auth/check-server-connection")
    Call<ApiResponse> checkServerConnection();

    @POST("api/v1/auth/create-account")
    Call<ApiResponse> createAccount(@Body RegisterRequest registerRequest);

    @POST("/api/v1/auth/check-enabled-account")
    Call<ApiResponse> checkEnableAccount(@Body EmailRequest emailRequest);

    @POST("api/v1/auth/resend-confirmation-email")
    Call<ApiResponse> resendConfirmationEmail(@Body EmailRequest emailRequest);

    @POST("api/v1/auth/login-account")
    Call<ApiResponse> loginAccount(@Body LoginRequest loginRequest);

    @POST("api/v1/auth/verify-token")
    Call<ApiResponse> verifyToken(@Body TokenRequest tokenRequest);

    @POST("api/v1/auth/logout-account")
    Call<ApiResponse> logoutAccount(@Body TokenRequest tokenJson);

    @GET("/api/v1/auth/words")
    Call<ApiResponse> getWords();

    @GET("/api/v1/auth/questions")
    Call<ApiResponse> getQuestions();

    @GET("/api/v1/auth/categories")
    Call<ApiResponse> getCategories();

    @GET("/api/v1/auth/subcategory/{categoryId}")
    Call<ApiResponse> getSubcategories(@Path("categoryId") int categoryId);
}
