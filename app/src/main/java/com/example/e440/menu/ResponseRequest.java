package com.example.e440.menu;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.AsyncTask;

import java.sql.Timestamp;

/**
 * Created by e440 on 03-06-18.
 */
@Entity
public class ResponseRequest {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    int id;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public ResponseRequest(String payload, String test_name) {
        this.payload = payload;
        this.test_name = test_name;
    }


    String payload;
    String test_name;

}
