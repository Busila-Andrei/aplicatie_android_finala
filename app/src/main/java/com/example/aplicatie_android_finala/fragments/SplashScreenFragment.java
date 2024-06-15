package com.example.aplicatie_android_finala.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.config.ApiService;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.dto.TokenRequest;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {
    private boolean tokenIsValid;

    public interface OnSplashScreenListener {
        void onSplashScreenComplete(boolean isSuccess);
    }

    private static final String TAG = "SplashScreenFragment";
    private OnSplashScreenListener listener;
    private TextView statusMessage;
    private ProgressBar spinner;
    private final Handler handler = new Handler();
    private int retryCount = 0;
    private final int MAX_RETRY = 3;
    private ApiService apiService;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSplashScreenListener) {
            listener = (OnSplashScreenListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnSplashScreenListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        statusMessage = view.findViewById(R.id.textViewMessage);
        spinner = view.findViewById(R.id.spinner);
        apiService = RetrofitClient.getApiService();
        performChecksSequentially();
        return view;
    }

    private void performChecksSequentially() {
        checkNetworkConnection();
    }

    private void checkNetworkConnection() {
        statusMessage.setText(R.string.checking_internet_connection);
        handler.postDelayed(() -> {
            if (isAdded()) {
                ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    retryCount = 0;
                    checkServerConnection();
                } else {
                    statusMessage.setText(R.string.no_internet_connection);
                    if (retryCount < MAX_RETRY) {
                        retryChecks();
                    } else {
                        statusMessage.setText(R.string.failed_multiple_attempts);
                        spinner.setVisibility(View.GONE);
                    }
                }
            }
        }, 1000);
    }

    private void checkServerConnection() {
        statusMessage.setText(R.string.checking_server_connection);
        handler.postDelayed(() -> {
            if (isAdded()) {
                apiService.checkServerConnection().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            retryCount = 0;
                            checkToken();
                        } else {
                            statusMessage.setText(R.string.server_connection_failed);
                            if (retryCount < MAX_RETRY) {
                                retryChecks();
                            } else {
                                statusMessage.setText(R.string.failed_multiple_attempts);
                                spinner.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                        Log.e(TAG, "Server connection failed: " + t.getMessage());
                        statusMessage.setText(R.string.server_connection_failed);
                        if (retryCount < MAX_RETRY) {
                            retryChecks();
                        } else {
                            statusMessage.setText(R.string.failed_multiple_attempts);
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }, 1000);
    }

    private void checkToken() {
        statusMessage.setText(R.string.checking_token);
        handler.postDelayed(() -> {
            if (isAdded()) {
                String token = extractTokenFromCookies();
                if (token != null) {
                    verifyToken(token);
                } else {
                    tokenIsValid = false;
                    listener.onSplashScreenComplete(tokenIsValid);
                }
            }
        }, 1000);
    }

    private String extractTokenFromCookies() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return preferences.getString("jwt_token", null);
    }

    private void verifyToken(String token) {
        TokenRequest tokenRequest = new TokenRequest(token);
        apiService.verifyToken(tokenRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    tokenIsValid = true;
                } else {
                    Log.e(TAG, "Failed to verify token: " + response.code());
                    tokenIsValid = false;
                }
                listener.onSplashScreenComplete(tokenIsValid);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, "Error during token verification: " + t.getMessage());
                tokenIsValid = false;
                listener.onSplashScreenComplete(tokenIsValid);
            }
        });
    }

    private void retryChecks() {
        retryCount++;
        handler.postDelayed(this::performChecksSequentially, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
