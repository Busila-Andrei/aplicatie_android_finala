package com.example.aplicatie_android_finala.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.data.database.Question;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {
    private final List<Question> questions;

    public QuestionsAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.type.setText(question.type);
        holder.questionText.setText(question.questionText);
        holder.correctAnswer.setText(question.correctAnswer);
        holder.otherAnswers.setText(question.otherAnswers);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView type, questionText, correctAnswer, otherAnswers;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            questionText = itemView.findViewById(R.id.questionText);
            correctAnswer = itemView.findViewById(R.id.correctAnswer);
            otherAnswers = itemView.findViewById(R.id.otherAnswers);
        }
    }
}
