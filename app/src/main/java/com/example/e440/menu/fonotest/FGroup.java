package com.example.e440.menu.fonotest;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 30-05-18.
 */
@Entity
public class FGroup {
    public FGroup(String name, String description, boolean example, int index, int server_id) {
        this.name = name;
        this.description = description;
        this.example = example;
        this.index = index;
        this.server_id = server_id;
    }

    @PrimaryKey (autoGenerate =true)
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExample() {
        return example;
    }

    public void setExample(boolean example) {
        this.example = example;
    }

    String name;
    String description;
    boolean example;
    int index;
    int server_id;
}
