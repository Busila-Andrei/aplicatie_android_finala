package com.example.aplicatie_android_finala.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey
    public int id;
    public String englishWord;
    public String romanianWord;
}
