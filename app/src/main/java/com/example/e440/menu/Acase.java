package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 *  Created by e440 on 08-04-18.
 */
@Entity
public class Acase {
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
    public Acase(@NonNull int index, @NonNull int server_id, byte[] image_bytes,String description) {
        this.index = index;
        this.server_id = server_id;
        this.image_bytes = image_bytes;
        this.description=description;
    }
}
