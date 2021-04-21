package com.example.e440.menu;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 *  Created by e440 on 08-04-18.
 */
@Entity
public class Acase {
    static char MALE_CHAR='M';
    static char FEMALE_CHAR='F';
    @NonNull
    public int getIndex() {
        return index;
    }

    public void setIndex(@NonNull int index) {
        this.index = index;
    }

    @NonNull
    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(@NonNull int server_id) {
        this.server_id = server_id;
    }

    @NonNull
    public byte[] getImage_bytes() {
        return image_bytes;
    }

    public void setImage_bytes(@NonNull byte[] image_bytes) {
        this.image_bytes = image_bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    int index;
    @NonNull
    int server_id;

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    char sex;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;
    byte[] image_bytes;
    @PrimaryKey(autoGenerate = true)
    private int id;
    public Acase(@NonNull int index, @NonNull int server_id, byte[] image_bytes,String description,char sex) {
        this.index = index;
        this.server_id = server_id;
        this.image_bytes = image_bytes;
        this.description=description;
        this.sex=sex;
    }
}
