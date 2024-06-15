package com.example.aplicatie_android_finala.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progress")
public class Progress {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int subcategoryId;
    public String subcategoryName;
    public int completed;
    public int total;
}