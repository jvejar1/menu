package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 23-04-18.
 */
@Entity(foreignKeys = @ForeignKey(entity =School.class,
        parentColumns = "id",
        childColumns = "school_id",
        onDelete = CASCADE))
public class Student {

    public static final String EXTRA_STUDENT_SERVER_ID="student_server_id";

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
    int school_id;

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public Student(String name, String last_name, String rut, int server_id, int school_id) {
        this.name = name;
        this.last_name = last_name;
        this.rut = rut;
        this.server_id = server_id;
        this.school_id = school_id;
    }

    @Ignore
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
