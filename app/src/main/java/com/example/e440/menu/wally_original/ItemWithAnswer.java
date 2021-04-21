package com.example.e440.menu.wally_original;

import java.util.ArrayList;
import java.util.List;

public class ItemWithAnswer {
    WallyOriginalItem item;
    ItemAnswer itemAnswer;
    List<ItemAnswer> answersHistory;
    public ItemWithAnswer( WallyOriginalItem item){
        this.item = item;
        this.itemAnswer = new ItemAnswer();
        this.itemAnswer.itemId = item.getId();

        this.answersHistory = new ArrayList<>();
        answersHistory.add(itemAnswer);
    }

    public ItemAnswer getItemAnswer() {
        return this.itemAnswer;
    }

    public WallyOriginalItem getItem(){
        return this.item;
    }


    public void prepareAnotherAnswer(){
        this.itemAnswer = new ItemAnswer();
        this.itemAnswer.itemId = item.getId();

        answersHistory.add(this.itemAnswer);
    }

    public int totalNTries(){
        return this.answersHistory.size();
    }
}
