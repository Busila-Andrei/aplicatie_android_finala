package com.example.aplicatie_android_finala.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.aplicatie_android_finala.NewActivity;
import com.example.aplicatie_android_finala.R;
import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Progress;
import com.example.aplicatie_android_finala.data.database.ProgressDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private static final String TAG = "ButtonAdapter";
    private List<String> subcategories;
    private List<Integer> subcategoryIds;
    private Context context;
    private AppDatabase db;
    private ExecutorService executorService;
    private Handler mainHandler;

    public ButtonAdapter(Context context, List<String> subcategories, List<Integer> subcategoryIds) {
        this.context = context;
        this.subcategories = subcategories;
        this.subcategoryIds = subcategoryIds;
        this.db = Room.databaseBuilder(context, AppDatabase.class, "word-database").build();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String subcategory = subcategories.get(position);
        int subcategoryId = subcategoryIds.get(position);
        holder.button.setText(subcategory);

        Log.d(TAG, "Binding view for subcategory: " + subcategory + " (ID: " + subcategoryId + ")");

        executorService.execute(() -> {
            ProgressDao progressDao = db.progressDao();
            List<Progress> progresses = progressDao.getProgressBySubcategoryId(subcategoryId);
            if (!progresses.isEmpty()) {
                Progress progress = progresses.get(0);
                int completed = progress.completed;
                int total = progress.total;

                Log.d(TAG, "onBindViewHolder: Before setting progress - Completed: " + completed + ", Total: " + total);

                mainHandler.post(() -> {
                    holder.progressBar.setMax(total);
                    holder.progressBar.setProgress(completed);
                    holder.progressBar.setIndeterminate(false);
                    if (completed > 0) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    } else {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    Log.d(TAG, "onBindViewHolder: After setting progress - Completed: " + completed + ", Total: " + total);
                });
            } else {
                mainHandler.post(() -> {
                    holder.progressBar.setMax(1);
                    holder.progressBar.setProgress(0);
                    holder.progressBar.setIndeterminate(false);
                    holder.progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "No progress found for subcategory ID: " + subcategoryId);
                });
            }
        });

        holder.button.setOnClickListener(v -> {
            Log.d(TAG, "Button clicked for subcategory: " + subcategory + " (ID: " + subcategoryId + ")");
            Intent intent = new Intent(context, NewActivity.class);
            intent.putExtra("SUBCATEGORY_NAME", subcategory);
            intent.putExtra("SUBCATEGORY_ID", subcategoryId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton button;
        ProgressBar progressBar;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
