package com.example.e440.menu.fonotest;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by e440 on 29-05-18.
 */
@Entity
public class FonoTest {
    @PrimaryKey(autoGenerate = true)
    int id;

    public FonoTest(int server_id, boolean correct) {
        this.server_id = server_id;
        this.correct = correct;
    }

    int server_id;
    boolean correct;

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
