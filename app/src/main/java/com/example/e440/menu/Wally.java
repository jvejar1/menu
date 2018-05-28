package com.example.e440.menu;

/**
 * Created by e440 on 07-05-18.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Wally {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;
    float version;
    String description;
    int server_id;

  
    public Wally(float version, String description, int server_id) {
        this.version = version;
        this.description = description;
        this.server_id = server_id;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }
}
