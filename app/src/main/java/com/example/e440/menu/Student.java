package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 23-04-18.
 */
@Entity
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
    int course_id;

    public int getSchool_id() {
        return course_id;
    }

    public void setSchool_id(int school_id) {
        this.course_id = school_id;
    }

    public Student(String name, String last_name, String rut, int server_id, int course_id) {
        this.name = name;
        this.last_name = last_name;
        this.rut = rut;
        this.server_id = server_id;
        this.course_id = course_id;
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

    public String getFullName(){

        return this.getLast_name()+" "+this.getName();
    }
}
