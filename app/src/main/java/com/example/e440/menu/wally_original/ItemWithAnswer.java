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

    public int getChoicesCount(){
        return this.item.getChoices().size();
    }
    public boolean hasChoiceSelected(ItemChoice itemChoice){

        boolean ret = this.itemAnswer.isThisChoiceSelected(itemChoice);
        return ret;
    }

    public ItemChoice getChoice(int choiceIdx){
        return this.item.getChoice(choiceIdx);
    }
    public boolean insertChoice(int choiceIdx){
        ItemChoice choice = this.getChoice(choiceIdx);
        Long choiceTimeStamp = System.currentTimeMillis()/1000;
        int deltaTimeStampSeconds = (int)(choiceTimeStamp - this.getItemAnswer().getDisplayTimeStamp());
        ChoiceSelection choiceSelection = new ChoiceSelection(choice.id,choiceTimeStamp, deltaTimeStampSeconds);
        if (this.getItem().getItemTypeId()==2){
            this.getItemAnswer().getChoiceSelections().clear();
        }
        return this.itemAnswer.add(choiceSelection);
    }

    public boolean getSelectionFlag(int choiceIdx){
        ItemChoice c = this.getItem().getChoice(choiceIdx);
        if (this.getItemAnswer().isThisChoiceSelected(c)){
            return true;
        }

        return false;
    }

    public ItemAnswer getItemAnswer() {
        return this.itemAnswer;
    }

    public WallyOriginalItem getItem(){
        return this.item;
    }



}
