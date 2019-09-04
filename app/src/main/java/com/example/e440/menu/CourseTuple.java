package com.example.e440.menu;

import android.arch.persistence.room.ColumnInfo;

public class CourseTuple {
    @ColumnInfo(name = "school_name")
    public String schoolName;
    @ColumnInfo(name = "course_level")
    public int courseLevel;
    @ColumnInfo(name = "course_letter")
    public int courseLetter;
}
