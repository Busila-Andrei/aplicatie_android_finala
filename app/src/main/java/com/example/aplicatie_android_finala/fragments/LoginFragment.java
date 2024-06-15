package com.example.aplicatie_android_finala.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.aplicatie_android_finala.*;
import com.example.aplicatie_android_finala.config.ApiResponse;
import com.example.aplicatie_android_finala.config.ApiService;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.dto.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    public interface OnLoginListener {
        void onLoginComplete();
        void onRegisterSelected(); // Adăugat pentru a gestiona navigarea
    }

    private OnLoginListener listener;
    private static final String TAG = "LoginFragment";
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewError;
    private ApiService apiService;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener) {
            listener = (OnLoginListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnLoginListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        textViewError = view.findViewById(R.id.textViewError);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        TextView textViewRegisterPrompt = view.findViewById(R.id.textViewRegisterPrompt);

        apiService = RetrofitClient.getApiService();

        buttonLogin.setOnClickListener(v -> authenticateUser());
        textViewRegisterPrompt.setOnClickListener(v -> listener.onRegisterSelected());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void authenticateUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            textViewError.setText("Fields cannot be empty");
            textViewError.setVisibility(View.VISIBLE);
            Log.e(TAG, "authenticateUser: Email or password fields are empty.");
            return;
        }

        Log.d(TAG, "authenticateUser: Preparing authentication request for email: " + email);

        LoginRequest authRequest = new LoginRequest(email, password);
        Call<ApiResponse> call = apiService.loginAccount(authRequest);

        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Authentication successful.");

                    // Save the JWT to SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    editor.putString("jwt_token", String.valueOf(apiResponse.getData()));
                    editor.apply();
                    listener.onLoginComplete();
                } else {
                    Log.e(TAG, "Authentication failed with status code: " + response.code());
                    textViewError.setText("Authentication failed. Status code: " + response.code());
                    textViewError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Failed to execute login request", t);
                textViewError.setText("Login Failed: " + t.getMessage());
                textViewError.setVisibility(View.VISIBLE);
            }
        });
    }
}
