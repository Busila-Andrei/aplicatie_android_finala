package com.example.aplicatie_android_finala.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.adapters.WordsAdapter;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Word;
import com.example.aplicatie_android_finala.data.database.WordDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordsFragment extends Fragment {
    private static final String TAG = "WordsFragment";
    private WordDao wordDao;
    private WordsAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "word-database").build();
        wordDao = db.wordDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        TextView emptyMessage = view.findViewById(R.id.emptyMessageWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WordsAdapter(null);
        recyclerView.setAdapter(adapter);

        loadWords(recyclerView, emptyMessage);

        return view;
    }

    private void loadWords(RecyclerView recyclerView, TextView emptyMessage) {
        Log.d(TAG, "loadWords called");
        executorService.execute(() -> {
            try {
                List<Word> words = wordDao.getAllWords();
                Log.d(TAG, "Words fetched from database: " + words.size());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (words.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyMessage.setVisibility(View.GONE);
                            adapter.updateWords(words);
                            Log.d(TAG, "Words loaded into adapter");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading words", e);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        executorService.shutdown();
    }
}

