package com.example.aplicatie_android_finala.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.config.ApiResponse;
import com.example.aplicatie_android_finala.config.ApiService;
import com.example.aplicatie_android_finala.config.RetrofitClient;
import com.example.aplicatie_android_finala.data.dto.EmailRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmationFragment extends Fragment {

    public interface OnConfirmationListener {
        void onConfirmationComplete();
        void onBackToRegister(); // Adăugat pentru a gestiona navigarea înapoi la register
    }

    private OnConfirmationListener listener;
    private Button buttonResendEmail;
    private TextView textViewTimer;
    private boolean timerRunning = false;
    private final Handler timerHandler = new Handler();
    private int remainingTime = 60;
    private ApiService apiService;

    private final String email;

    public ConfirmationFragment(String email) {
        this.email = email;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnConfirmationListener) {
            listener = (OnConfirmationListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnConfirmationListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_email, container, false);
        buttonResendEmail = view.findViewById(R.id.buttonResendEmail);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        ImageButton buttonClose = view.findViewById(R.id.buttonClose);

        apiService = RetrofitClient.getApiService();

        buttonResendEmail.setOnClickListener(v -> {
            if (!timerRunning) {
                resendConfirmationEmail();
            }
        });

        buttonClose.setOnClickListener(v -> listener.onBackToRegister());

        startTimerService();
        startAccountCheckService();
        return view;
    }

    private void checkEnableAccount() {
        EmailRequest request = new EmailRequest(email);
        Call<ApiResponse> call = apiService.checkEnableAccount(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        listener.onConfirmationComplete();
                    } else {
                        // Reprogramarea verificării
                        scheduleNextCheck();
                    }
                } else {
                    // Gestionarea răspunsurilor care nu sunt succes sau nu conțin corp
                    scheduleNextCheck();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), String.format(getString(R.string.account_check_failed), t.getMessage()), Toast.LENGTH_LONG).show();
                    scheduleNextCheck();
                }
            }
        });
    }

    private void scheduleNextCheck() {
        if (isAdded()) {
            timerHandler.postDelayed(this::checkEnableAccount, 5000);
        }
    }

    private void startAccountCheckService() {
        final Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    checkEnableAccount();
                    timerHandler.postDelayed(this, 5000); // Repetă la fiecare 5 secunde
                }
            }
        };
        timerHandler.postDelayed(checkRunnable, 5000); // Start după 5 secunde
    }

    private void resendConfirmationEmail() {
        EmailRequest request = new EmailRequest(email);
        Call<ApiResponse> call = apiService.resendConfirmationEmail(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    Toast.makeText(getActivity(), String.format(getString(R.string.resend_successful), response.body().getMessage()), Toast.LENGTH_LONG).show();
                    startTimerService(); // Restart the timer after a successful resend
                } else if (isAdded()) {
                    Toast.makeText(getActivity(), String.format(getString(R.string.resend_failed_code), response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), String.format(getString(R.string.resend_failed_message), t.getMessage()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startTimerService() {
        if (!timerRunning) {
            timerRunning = true;
            buttonResendEmail.setEnabled(false);
            buttonResendEmail.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_color_disabled));
            textViewTimer.setVisibility(View.VISIBLE);
            remainingTime = 60;
            updateTimer();
        }
    }

    private void updateTimer() {
        Runnable timerRunnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (remainingTime > 0 && isAdded()) {
                    textViewTimer.setText(remainingTime + " s");
                    remainingTime--;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    endTimer();
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void endTimer() {
        if (isAdded()) {
            buttonResendEmail.setEnabled(true);
            buttonResendEmail.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_color_default));
            textViewTimer.setVisibility(View.GONE);
            timerRunning = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacksAndMessages(null); // Anulează toate callbacks și mesajele

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null); // Curăță handler-ul la distrugerea fragmentului
    }
}
