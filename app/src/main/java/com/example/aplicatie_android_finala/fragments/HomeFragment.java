package com.example.aplicatie_android_finala.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: HomeFragment view being created");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textViewHome = view.findViewById(R.id.textViewHome);
        textViewHome.setText(R.string.home);
        return view;
    }
}
