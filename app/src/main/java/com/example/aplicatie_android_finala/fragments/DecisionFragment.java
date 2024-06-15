package com.example.aplicatie_android_finala.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;

import org.jetbrains.annotations.NotNull;

public class DecisionFragment extends Fragment {
    public interface OnLoginSelectedListener {
        void onLoginSelected();
    }

    public interface OnRegisterSelectedListener {
        void onRegisterSelected();
    }
    private OnLoginSelectedListener loginListener;
    private OnRegisterSelectedListener registerListener;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            loginListener = (OnLoginSelectedListener) context;
            registerListener = (OnRegisterSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + " must implement OnLoginSelectedListener and OnRegisterSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decision, container, false);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(v -> loginListener.onLoginSelected());
        buttonRegister.setOnClickListener(v -> registerListener.onRegisterSelected());

        return view;
    }
}
