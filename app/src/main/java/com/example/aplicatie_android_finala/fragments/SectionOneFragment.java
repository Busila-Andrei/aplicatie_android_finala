package com.example.aplicatie_android_finala.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.NewActivity;
import com.example.aplicatie_android_finala.R;

public class SectionOneFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section_one, container, false);

        // Find Button 1 by its ID
        AppCompatButton button1 = view.findViewById(R.id.button1);

        // Set an OnClickListener to open the new Activity
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
