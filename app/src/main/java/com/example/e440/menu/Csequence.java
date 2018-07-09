package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 12-05-18.
 */
@Entity
public class Csequence {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    @PrimaryKey (autoGenerate = true)
    private int id;
    private int server_id;
    private int index;
    private boolean ordered;

    public boolean isExample() {
        return example;
    }

    public void setExample(boolean example) {
        this.example = example;
    }

    public String getCsequence() {
        return csequence;
    }

    public void setCsequence(String csequence) {
        this.csequence = csequence;
    }

    private boolean example;
    private String csequence;

    public Csequence(int server_id, int index, boolean ordered,boolean example, String csequence) {
        this.server_id = server_id;
        this.index = index;
        this.ordered = ordered;
        this.example=example;
        this.csequence=csequence;
    }
}
