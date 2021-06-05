package com.example.e440.menu.wally_original;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Evaluation implements Serializable {

    public Evaluation(ItemsBank instrument, long studentId, long userId){

        this.currentItemIndex = WallyOriginalActivity.SpecialItemIndex.ASSENT_ITEM;
        if (!instrument.items.isEmpty()){
            Integer itemTypeId = instrument.items.get(0).getItemTypeId();
            if (itemTypeId!=null && itemTypeId>=100){
                this.currentItemIndex=0;
            }
        }

        this.studentId=studentId;
        this.userId=userId;
        this.finished = false;
        this.timestamp =new Timestamp(System.currentTimeMillis());
        this.instrumentId = instrument.id;
        this.itemWithAnswers = new ArrayList<>();
        for (int i=0; i<instrument.items.size(); i++){

            WallyOriginalItem item = instrument.items.get(i);
            this.itemWithAnswers.add(new ItemWithAnswer(item));
        }

    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    long userId;

    public Integer getCurrentItemIndex() {
        return currentItemIndex;
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        this.currentItemIndex = currentItemIndex;
    }

    public int currentItemIndex;

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

    public void setAsFinished() {
        this.finished = true;
        this.itemAnswerList = new ArrayList<>();
        for (ItemWithAnswer itemWithAnswer:this.itemWithAnswers
             ) {
            ItemAnswer itemAnswer = itemWithAnswer.getItemAnswer();
            this.itemAnswerList.add(itemAnswer);
        }
        this.itemWithAnswers =null;
        this.itemsList = null;

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

    List<WallyOriginalItem> itemsList;
    List<ItemAnswer> itemAnswerList;
    
    List<ItemWithAnswer> itemWithAnswers;
}
