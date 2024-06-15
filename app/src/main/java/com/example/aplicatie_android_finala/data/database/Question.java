package com.example.aplicatie_android_finala.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Question {
    @PrimaryKey
    public int id;
    public String type;
    public String questionText;
    public String correctAnswer;
    public String otherAnswers;
    public Long testId;

    public Long subcategoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}