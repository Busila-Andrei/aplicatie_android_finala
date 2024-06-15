package com.example.aplicatie_android_finala.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Word.class, Question.class, Category.class, Subcategory.class, Progress.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract WordDao wordDao();
    public abstract QuestionDao questionDao();
    public abstract CategoryDao categoryDao();
    public abstract SubcategoryDao subcategoryDao();
    public abstract ProgressDao progressDao();

    // Definirea migrației de la versiunea 1 la versiunea 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Crearea tabelului Progress
            database.execSQL("CREATE TABLE IF NOT EXISTS `progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subcategoryId` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `total` INTEGER NOT NULL)");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "word-database")
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
