package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 20-05-18.
 */
@Entity
public class HnfSet {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private int server_id;
    private float version;

}
