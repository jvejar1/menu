package com.example.e440.menu;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by e440 on 07-05-18.
 */
@Entity
public class WFeeling {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;
    int server_id;

    public WFeeling(int server_id, int wfeeling, byte[] image_bytes) {
        this.server_id = server_id;
        this.wfeeling = wfeeling;
        this.image_bytes = image_bytes;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getWfeeling() {
        return wfeeling;
    }

    public void setWfeeling(int wfeeling) {
        this.wfeeling = wfeeling;
    }

    public byte[] getImage_bytes() {
        return image_bytes;
    }

    public void setImage_bytes(byte[] image_bytes) {
        this.image_bytes = image_bytes;
    }

    int wfeeling;
    byte[] image_bytes;


}
