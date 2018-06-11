package com.example.e440.menu;

/**
 * Created by e440 on 07-05-18.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;

@Entity
public class Wally {


    final static HashMap<Integer,String> feelings_by_number=new HashMap<Integer, String>() {{
        put(3,"ENOJADO");
        put(4,"SOLO BIEN");
        put(1,"FELIZ");
        put(2,"TRISTE");
    }};
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
