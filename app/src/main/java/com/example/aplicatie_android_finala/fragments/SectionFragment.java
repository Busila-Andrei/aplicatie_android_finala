package com.example.aplicatie_android_finala.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;

public class SectionFragment extends Fragment {

    private static final String ARG_SECTION_TITLE = "section_title";
    private static final String ARG_SECTION_CONTENT = "section_content";

    public static SectionFragment newInstance(String title, String content) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, title);
        args.putString(ARG_SECTION_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_SECTION_TITLE);
            String content = getArguments().getString(ARG_SECTION_CONTENT);

            TextView titleTextView = view.findViewById(R.id.sectionTitle);
            TextView contentTextView = view.findViewById(R.id.sectionContent);

            titleTextView.setText(title);
            contentTextView.setText(content);
        }

        return view;
    }
}
