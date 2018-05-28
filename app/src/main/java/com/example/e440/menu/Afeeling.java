package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 08-04-18.
 */
@Entity
public class Afeeling {
    @PrimaryKey(autoGenerate = true)
    private int id;
    String text;
    int index;
    int server_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Afeeling(String text, int index, int server_id) {
        this.text = text;
        this.index = index;
        this.server_id=server_id;

    }
}
