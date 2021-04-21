package com.example.e440.menu;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by e440 on 20-05-18.
 */
@Entity
public class HnfSet {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public HnfSet(int server_id, float version) {
        this.server_id = server_id;
        this.version = version;
    }

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

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    private int server_id;
    private float version;

}
