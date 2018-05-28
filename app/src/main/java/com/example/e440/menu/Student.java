package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 23-04-18.
 */
@Entity
public class Student {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    String name;
    String last_name;
    String rut;
    int server_id;

    public Student() {
    }

    public Student(String name, String last_name, String rut, int server_id) {
        this.name = name;
        this.last_name = last_name;
        this.rut = rut;

        this.server_id = server_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int db_id) {
        this.server_id = db_id;
    }
}
