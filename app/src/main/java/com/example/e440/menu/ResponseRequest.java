package com.example.e440.menu;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    public ResponseRequest(String payload, String test_name, boolean saved, Long student_server_id) {
        this.payload = payload;
        this.test_name = test_name;
        this.saved =saved;
        this.student_server_id = student_server_id;
    }

    @Ignore
    public ResponseRequest(String payload, String test_name, boolean saved, Long student_server_id, boolean finished, long instrumentId) {
        this.payload = payload;
        this.test_name = test_name;
        this.saved =saved;
        this.student_server_id = student_server_id;
        this.finished = finished;
        this.instrumentId = instrumentId;
    }



    String payload;
    String test_name;

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    boolean finished;
    long instrumentId;


    public Long getStudent_server_id() {
        return student_server_id;
    }

    public void setStudent_server_id(Long student_server_id) {
        this.student_server_id = student_server_id;
    }

    Long student_server_id;
    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    public boolean getSaved(){
        return this.saved;
    }

    boolean saved;

}
