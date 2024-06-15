package com.example.aplicatie_android_finala.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subcategories")
public class Subcategory {
    @PrimaryKey
    public int id;
    public int categoryId;
    public String name;

    // Getters and Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
