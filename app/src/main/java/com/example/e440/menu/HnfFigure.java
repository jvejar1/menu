package com.example.e440.menu;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by e440 on 17-05-18.
 */
@Entity(foreignKeys = @ForeignKey(entity =HnfTest.class,
        parentColumns = "id",
        childColumns = "hnftest_id",
        onDelete = CASCADE))
public class HnfFigure {

    static int LEFT=0;
    static int RIGHT=1;
    static int HEART=0;
    static int FLOWER=1;
    @PrimaryKey(autoGenerate = true)
    int id;
    int figure;

    public HnfFigure(int figure, int hnftest_id, int server_id, int position, int index) {
        this.figure = figure;
        this.hnftest_id = hnftest_id;
        this.server_id = server_id;
        this.position = position;
        this.index = index;
    }

    public int getHnftest_id() {
        return hnftest_id;
    }

    public void setHnftest_id(int hnftest_id) {
        this.hnftest_id = hnftest_id;
    }

    int hnftest_id;
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

    int server_id;

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    int position;
    int index;


}
