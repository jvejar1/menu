package com.example.e440.menu;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;

/**
 * Created by e440 on 23-04-18.
 */
@Entity
public class Student {

    public static HashMap<Integer,String> course_level_by_number=new HashMap<Integer, String>(){{
        String[] course_levels={
                "PRE KINDER",
                "KINDER",
                "1° BASICO",
                "2° BASICO",
                "3° BASICO",
                "4° BASICO",
                "5° BASICO",
                "6° BASICO",
                "7° BASICO"};
        for (int i=0;i<course_levels.length;i++){
            put(i+1,course_levels[i]);
        }
    }};
    public static HashMap<Integer,Character> course_letter_by_number=new HashMap<Integer, Character>(){{

        char[] course_letters={
                'A',
                'B',
                'C',
                'D',
                'E',
                'F',
                'G',
                'H',
                'I',
                'J',
                'K',
                'L',
                'M',
                'N',
                'Ñ',
        };
        for(int index=0; index<course_letters.length;index++){
            put(index+1,course_letters[index]);
        }

    }
    };

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
    Long server_id;

    public int getAces_count() {
        return aces_count;
    }

    public void setAces_count(int aces_count) {
        this.aces_count = aces_count;
    }

    public int getCorsis_count() {
        return corsis_count;
    }

    public void setCorsis_count(int corsis_count) {
        this.corsis_count = corsis_count;
    }

    public int getHnf_count() {
        return hnf_count;
    }

    public void setHnf_count(int hnf_count) {
        this.hnf_count = hnf_count;
    }

    public int getFonotest_count() {
        return fonotest_count;
    }

    public void setFonotest_count(int fonotest_count) {
        this.fonotest_count = fonotest_count;
    }

    int aces_count;

    public int getWally_count() {
        return wally_count;
    }

    public void setWally_count(int wally_count) {
        this.wally_count = wally_count;
    }

    int wally_count;
    int corsis_count;
    int hnf_count;
    int fonotest_count;

    int course_level;
    int course_letter;

    public int getCourse_level() {
        return course_level;
    }

    public void setCourse_level(int course_level) {
        this.course_level = course_level;
    }

    public int getCourse_letter() {
        return course_letter;
    }

    public void setCourse_letter(int course_letter) {
        this.course_letter = course_letter;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    String school_name;

    public Student(String name, String last_name, String rut,Long server_id) {
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

    public Long getServer_id() {
        return server_id;
    }

    public void setServer_id(Long db_id) {
        this.server_id = db_id;
    }

    public String getFullName(){
        return this.getLast_name()+" "+this.getName();
    }

    String getCourseFullName(){
        String course_level_str=course_level_by_number.get(this.course_level);
        char course_letter_str=(course_letter_by_number.get(this.course_letter));
        return course_level_str+" "+course_letter_str;
    }
}
