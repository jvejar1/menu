package com.example.e440.menu.wally_original;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Evaluation implements Serializable {

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrimaryKey
    Long id;
    @ColumnInfo(name="pass_assent")
    boolean passAssent;

    @ColumnInfo(name="instruction_result")
    boolean passInstruction;

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @ColumnInfo(name="finished")
    public boolean finished = false;

    public int getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }

    @ColumnInfo(name="instrument_id")
    int instrumentId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @ColumnInfo(name="student_id")
    Long studentId;

    @ColumnInfo(name="server_id")
    int serverId;

    Timestamp timestamp;

    public List<WallyOriginalItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<WallyOriginalItem> itemsList) {
        this.itemsList = itemsList;
    }

    List<WallyOriginalItem> itemsList;
    List<ItemAnswer> itemAnswerList;

}
