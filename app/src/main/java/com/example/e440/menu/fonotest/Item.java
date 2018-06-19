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
@Entity
public class Item {
    @PrimaryKey (autoGenerate = true)
    int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public Item(String correct_sequence, byte[] audio, String description, int sever_id, int index, String instruction,boolean example,String name) {
        this.correct_sequence = correct_sequence;
        this.audio = audio;
        this.instruction=instruction;
        this.description = description;
        this.sever_id = sever_id;
        this.index = index;
        this.example=example;
        this.name=name;
    }

    public String getCorrect_sequence() {
        return correct_sequence;
    }

    public void setCorrect_sequence(String correct_sequence) {
        this.correct_sequence = correct_sequence;
    }

    String correct_sequence;
    byte[] audio;
    String description;
    int sever_id;
    String instruction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isExample() {
        return example;
    }

    public void setExample(boolean example) {
        this.example = example;
    }

    boolean example;
    int index;
}
