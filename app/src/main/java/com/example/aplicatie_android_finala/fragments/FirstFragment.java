package com.example.aplicatie_android_finala.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.R;

public class FirstFragment extends Fragment {

    private TextView questionText;
    private EditText answerEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        questionText = view.findViewById(R.id.question_text);
        answerEditText = view.findViewById(R.id.answer_edit_text);

        // Set question text if provided
        if (getArguments() != null) {
            String question = getArguments().getString("question");
            questionText.setText(question);
        }

        // Set up touch listener to close keyboard when tapping outside of EditText
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View focusedView = getActivity().getCurrentFocus();
                    if (focusedView != null && focusedView instanceof EditText) {
                        Rect outRect = new Rect();
                        focusedView.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            focusedView.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                            }
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    public String getAnswer() {
        return answerEditText.getText().toString();
    }
}
