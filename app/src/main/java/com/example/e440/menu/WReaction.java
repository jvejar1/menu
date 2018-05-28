package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 07-05-18.
 */
@Entity(foreignKeys = @ForeignKey(entity =WSituation.class,
        parentColumns = "id",
        childColumns = "wsituation_id",
        onDelete = CASCADE) )
public class WReaction {
    public long getWsituation_id() {
        return wsituation_id;
    }

    public void setWsituation_id(long wsituation_id) {
        this.wsituation_id = wsituation_id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getWreaction() {
        return wreaction;
    }

    public void setWreaction(int wreaction) {
        this.wreaction = wreaction;
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

    public WReaction(long wsituation_id, int server_id, int wreaction, String description, byte[] image_bytes) {
        this.wsituation_id = wsituation_id;
        this.server_id = server_id;
        this.wreaction = wreaction;
        this.description = description;
        this.image_bytes = image_bytes;
    }

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;
    long wsituation_id;
    int server_id;
    int wreaction;
    String description;
    byte[] image_bytes;


}
