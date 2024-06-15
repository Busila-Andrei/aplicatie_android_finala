package com.example.aplicatie_android_finala.adapters;


import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.data.CategoryRepository;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Subcategory;
import com.example.aplicatie_android_finala.utils.SectionItem;

import java.util.ArrayList;
import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {

    private List<SectionItem> sectionItems;
    private SparseArray<RecyclerView.Adapter> subcategoryAdapters = new SparseArray<>();
    private CategoryRepository repository;

    public SectionsAdapter(List<SectionItem> sectionItems, AppDatabase db) {
        this.sectionItems = sectionItems;
        this.repository = new CategoryRepository(db);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SectionItem sectionItem = sectionItems.get(position);
        holder.bind(sectionItem, position);
    }

    @Override
    public int getItemCount() {
        return sectionItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionText;
        RecyclerView subcategoryRecyclerView;
        TextView comingSoonText;

        ViewHolder(View itemView) {
            super(itemView);
            sectionText = itemView.findViewById(R.id.section_text);
            subcategoryRecyclerView = itemView.findViewById(R.id.subcategory_recycler_view);
            comingSoonText = itemView.findViewById(R.id.comingSoonText);
        }

        void bind(SectionItem sectionItem, int position) {
            sectionText.setText(sectionItem.getText());

            repository.getSubcategoriesFromDatabase(sectionItem.getId(), subcategories -> {
                itemView.post(() -> {
                    if (subcategories.isEmpty()) {
                        comingSoonText.setVisibility(View.VISIBLE);
                        subcategoryRecyclerView.setVisibility(View.GONE);
                    } else {
                        comingSoonText.setVisibility(View.GONE);
                        subcategoryRecyclerView.setVisibility(View.VISIBLE);

                        List<String> subcategoryNames = new ArrayList<>();
                        List<Integer> subcategoryIds = new ArrayList<>();
                        for (Subcategory subcategory : subcategories) {
                            subcategoryNames.add(subcategory.name);
                            subcategoryIds.add(subcategory.id);
                        }

                        RecyclerView.Adapter adapter = subcategoryAdapters.get(position);
                        if (adapter == null) {
                            adapter = new ButtonAdapter(itemView.getContext(), subcategoryNames, subcategoryIds);
                            subcategoryAdapters.put(position, adapter);
                        }

                        subcategoryRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                        subcategoryRecyclerView.setAdapter(adapter);
                    }
                });
            });
        }

    }
}
