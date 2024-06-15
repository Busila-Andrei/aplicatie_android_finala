package com.example.aplicatie_android_finala.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.config.ApiResponse;
import com.example.aplicatie_android_finala.config.ApiService;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    public interface OnRegisterListener {
        void onRegisterComplete(String req);
        void onLoginSelected(); // Adăugat pentru a gestiona navigarea
    }

    private OnRegisterListener listener;

    private ApiService apiService;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewMessage;
    private TextView passwordTooltip;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterListener) {
            listener = (OnRegisterListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnRegisterListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        apiService = RetrofitClient.getApiService();
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        textViewMessage = view.findViewById(R.id.textViewMessage);
        passwordTooltip = view.findViewById(R.id.passwordTooltip);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);
        TextView textViewLoginPrompt = view.findViewById(R.id.textViewLoginPrompt);

        editTextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passwordTooltip.setVisibility(View.VISIBLE);
            } else {
                passwordTooltip.setVisibility(View.GONE);
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordTooltip(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        buttonRegister.setOnClickListener(v -> registerUser());
        textViewLoginPrompt.setOnClickListener(v -> listener.onLoginSelected());

        return view;
    }

    private void updatePasswordTooltip(String password) {
        SpannableString tooltipText = new SpannableString(
                getString(R.string.password_tooltip));

        int minLengthStart = 0;
        int minLengthEnd = "Minimum 8 characters in length".length();
        int upperStart = minLengthEnd + 1;
        int upperEnd = upperStart + "- Uppercase Letters".length();
        int lowerStart = upperEnd + 1;
        int lowerEnd = lowerStart + "- Lowercase Letters".length();
        int digitStart = lowerEnd + 1;
        int digitEnd = digitStart + "- Numbers".length();
        int symbolStart = digitEnd + 1;
        int symbolEnd = symbolStart + "- Symbols".length();

        if (password.length() >= 8) {
            tooltipText.setSpan(new ForegroundColorSpan(Color.GREEN), minLengthStart, minLengthEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            tooltipText.setSpan(new ForegroundColorSpan(Color.RED), minLengthStart, minLengthEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (containsUppercase(password)) {
            tooltipText.setSpan(new ForegroundColorSpan(Color.GREEN), upperStart, upperEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            tooltipText.setSpan(new ForegroundColorSpan(Color.RED), upperStart, upperEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (containsLowercase(password)) {
            tooltipText.setSpan(new ForegroundColorSpan(Color.GREEN), lowerStart, lowerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            tooltipText.setSpan(new ForegroundColorSpan(Color.RED), lowerStart, lowerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (containsDigit(password)) {
            tooltipText.setSpan(new ForegroundColorSpan(Color.GREEN), digitStart, digitEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            tooltipText.setSpan(new ForegroundColorSpan(Color.RED), digitStart, digitEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (containsSpecialCharacter(password)) {
            tooltipText.setSpan(new ForegroundColorSpan(Color.GREEN), symbolStart, symbolEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            tooltipText.setSpan(new ForegroundColorSpan(Color.RED), symbolStart, symbolEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        passwordTooltip.setText(tooltipText);
    }

    private boolean containsUppercase(String s) { return !s.equals(s.toLowerCase()); }
    private boolean containsLowercase(String s) { return !s.equals(s.toUpperCase()); }
    private boolean containsDigit(String s) { return s.matches(".*\\d.*"); }
    private boolean containsSpecialCharacter(String s) { return !s.matches("[A-Za-z0-9 ]*"); }

    @SuppressLint("SetTextI18n")
    private void registerUser() {
        String firstname = editTextFirstName.getText().toString();
        String lastname = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!isValidEmail(email)) {
            textViewMessage.setText("Invalid Email");
            textViewMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (password.length() < 8) {
            textViewMessage.setText("Password must be at least 8 characters long");
            textViewMessage.setVisibility(View.VISIBLE);
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(firstname, lastname, email, password);
        Call<ApiResponse> call = apiService.createAccount(registerRequest);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRegisterComplete(editTextEmail.getText().toString());
                } else {
                    textViewMessage.setText(String.format(getString(R.string.registration_failed_code), response.code()));
                    textViewMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                textViewMessage.setText(String.format(getString(R.string.registration_failed_message), t.getMessage()));
                textViewMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
