package com.example.e440.menu.fonotest;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.example.e440.menu.WSituation;

import java.sql.Blob;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 30-05-18.
 */
@Entity(foreignKeys = @ForeignKey(entity =FGroup.class,
        parentColumns = "id",
        childColumns = "fgroup_id",
        onDelete = CASCADE))
public class Item {
    @PrimaryKey (autoGenerate = true)
    int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFgroup_id() {
        return fgroup_id;
    }

    public void setFgroup_id(int fgroup_id) {
        this.fgroup_id = fgroup_id;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSever_id() {
        return sever_id;
    }

    public void setSever_id(int sever_id) {
        this.sever_id = sever_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public Item(String correct_sequence, int fgroup_id, byte[] audio, String description, int sever_id, int index) {
        this.correct_sequence = correct_sequence;
        this.fgroup_id = fgroup_id;
        this.audio = audio;
        this.description = description;
        this.sever_id = sever_id;
        this.index = index;
    }

    public String getCorrect_sequence() {
        return correct_sequence;
    }

    public void setCorrect_sequence(String correct_sequence) {
        this.correct_sequence = correct_sequence;
    }

    String correct_sequence;
    int fgroup_id;
    byte[] audio;
    String description;
    int sever_id;
    int index;
}
