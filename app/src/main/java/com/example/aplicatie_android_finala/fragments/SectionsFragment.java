package com.example.aplicatie_android_finala.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.adapters.SectionsAdapter;
import com.example.aplicatie_android_finala.data.CategoryRepository;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Category;
import com.example.aplicatie_android_finala.utils.SectionItem;

import java.util.ArrayList;
import java.util.List;

public class SectionsFragment extends Fragment {

    private static final String TAG = "SectionsFragment";
    private AppDatabase db;
    private CategoryRepository repository;
    private RecyclerView sectionsRecyclerView;
    private BroadcastReceiver refreshReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sections, container, false);
        sectionsRecyclerView = view.findViewById(R.id.sectionsRecyclerView);
        sectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = Room.databaseBuilder(requireActivity().getApplicationContext(), AppDatabase.class, "word-database").build();
        repository = new CategoryRepository(db);

        // Afișează categoriile
        displayCategories();

        // Configurăm BroadcastReceiver pentru reîmprospătarea datelor
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received: Refreshing data");
                displayCategories();
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver, new IntentFilter("REFRESH_DATA"));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (refreshReceiver != null) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(refreshReceiver);
        }
    }

    public void displayCategories() {
        repository.getCategoriesFromDatabase(categories -> {
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    List<SectionItem> items = new ArrayList<>();
                    for (Category category : categories) {
                        items.add(new SectionItem(category.name, category.id));
                        Log.d(TAG, "Added SectionItem: " + category.name + " with ID: " + category.id);
                    }
                    sectionsRecyclerView.setAdapter(new SectionsAdapter(items, db));
                    Log.d(TAG, "Set adapter with items: " + items);
                });
            }
        });
    }
}
