package com.example.aplicatie_android_finala.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecondFragment extends Fragment {

    private TextView questionText;
    private RadioGroup optionsGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        questionText = view.findViewById(R.id.question_text);
        optionsGroup = view.findViewById(R.id.options_group);

        // Set question text and options if provided
        if (getArguments() != null) {
            String question = getArguments().getString("question");
            String correctAnswer = getArguments().getString("correctAnswer");
            String otherAnswers = getArguments().getString("otherAnswers");
            questionText.setText(question);

            if (otherAnswers != null) {
                List<String> options = new ArrayList<>();
                Collections.addAll(options, otherAnswers.split(","));
                options.add(correctAnswer); // Add the correct answer to the options
                Collections.shuffle(options); // Shuffle the options to randomize the order

                for (String option : options) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(option);
                    optionsGroup.addView(radioButton);
                }
            }
        }

        return view;
    }

    public String getSelectedOption() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = optionsGroup.findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return null;
    }
}
