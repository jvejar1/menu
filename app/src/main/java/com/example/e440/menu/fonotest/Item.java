package com.example.e440.menu.fonotest;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by e440 on 30-05-18.
 */
@Entity
public class Item implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id;
    public String name;
    public boolean isForPractice;
    public int order;
    public String audio_path;

    public String description;
    public int sever_id;
    public String instruction;
    public String correct_sequence;
    public byte[] audio;

    public String picturePath;
    public String pictureUrl;
    public String encodedPicture;
    public int instrumentId;

    public int itemTypeId;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    public Item(String correct_sequence, String description, int sever_id, int order, String instruction, boolean example, String name) {
        this.correct_sequence = correct_sequence;
        this.instruction=instruction;
        this.description = description;
        this.sever_id = sever_id;
        this.order = order;
        this.isForPractice = example;
        this.name=name;
    }
    public Item(){}

    public String getCorrect_sequence() {
        return correct_sequence;
    }

    public void setCorrect_sequence(String correct_sequence) {
        this.correct_sequence = correct_sequence;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isForPractice() {
        return isForPractice;
    }

    public void setForPractice(boolean forPractice) {
        this.isForPractice = forPractice;
    }

}
