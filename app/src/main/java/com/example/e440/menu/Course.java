package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by e440 on 09-06-18.
 */
@Entity
public class Course {
    @PrimaryKey(autoGenerate = true)
    int id;
    int server_id;
    String school_name;

    public int getSchool_id() {
        return school_id;
    }

    public Course(int server_id, String school_name, int school_id, String level, String letter) {
        this.server_id = server_id;
        this.school_name = school_name;
        this.school_id = school_id;
        this.level = level;
        this.letter = letter;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    int school_id;

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    String level;
    String letter;



    public Course() {
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

    public String getName() {
        return school_name+" - " + level+" "+letter;
    }

    public void setName(String name) {

    }


}
