package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 21-05-18.
 */
@Entity(foreignKeys = @ForeignKey(entity =Csequence.class,
        parentColumns = "id",
        childColumns = "csequence_id",
        onDelete = CASCADE))
public class Csquare {
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

    public int getCsequence_id() {
        return csequence_id;
    }

    public void setCsequence_id(int csequence_id) {
        this.csequence_id = csequence_id;
    }

    public int getSquare() {
        return square;
    }

    public void setSquare(int square) {
        this.square = square;
    }

    @PrimaryKey (autoGenerate = true)
    private int id;
    private int server_id;
    private int index;
    private int csequence_id;
    private int square;

    public Csquare(int server_id, int index, int csequence_id, int square) {
        this.server_id = server_id;
        this.index = index;
        this.csequence_id = csequence_id;
        this.square = square;
    }
}
