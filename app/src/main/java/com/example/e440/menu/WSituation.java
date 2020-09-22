package com.example.e440.menu;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by e440 on 07-05-18.
 */
@Entity
public class WSituation {
    static String WREACTION_RESPONSE_EXTRA="wreaction";
    static String WFEELING_RESPONSE_EXTRA="wfeeling";
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    public WSituation(int server_id, String description, byte[] image_bytes) {
        this.server_id = server_id;
        this.description = description;
        this.image_bytes = image_bytes;
    }

    private int server_id;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage_bytes() {
        return image_bytes;
    }

    public void setImage_bytes(byte[] image_bytes) {
        this.image_bytes = image_bytes;
    }

    String description;
    byte[] image_bytes;


}
