package com.example.aplicatie_android_finala.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.data.database.Word;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordViewHolder> {
    private List<Word> words;

    public WordsAdapter(List<Word> words) {
        this.words = words;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = words.get(position);
        holder.englishWord.setText(word.englishWord);
        holder.romanianWord.setText(word.romanianWord);
    }

    @Override
    public int getItemCount() {
        return words != null ? words.size() : 0;
    }

    public void updateWords(List<Word> newWords) {
        this.words = newWords;
        notifyDataSetChanged();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        public TextView englishWord;
        public TextView romanianWord;

        public WordViewHolder(View itemView) {
            super(itemView);
            englishWord = itemView.findViewById(R.id.english_word);
            romanianWord = itemView.findViewById(R.id.romanian_word);
        }
    }
}
