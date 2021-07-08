package com.example.e440.menu.wally_original;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemAnswer implements Serializable {

    int itemId;
    String answer;
    int latencySeconds;
    int evaluationId;
    public List<Integer> answersChoices;
    public List<ChoiceSelection> choiceSelections;

    public Long getDisplayTimeStamp() {
        return displayTimeStamp;
    }

    public void setDisplayTimeStamp(Long displayTimeStamp) {
        this.displayTimeStamp = displayTimeStamp;
    }

    public Long displayTimeStamp;

    public ItemAnswer(WallyOriginalItem item){
        this.itemId = item.getId();
        this.answer = "";
        this.latencySeconds = 0;
        answersChoices = new ArrayList<>();
        choiceSelections=new ArrayList<>();
    }


    public boolean add(ChoiceSelection choiceSelection){
        return this.choiceSelections.add(choiceSelection);
    }
    public List<ChoiceSelection> getChoiceSelections(){
        if (this.choiceSelections==null){
            this.choiceSelections = new ArrayList<>();
        }
        return this.choiceSelections;
    }


    public boolean isThisChoiceSelected(ItemChoice choice){
        for (ChoiceSelection choiceSelection: choiceSelections){
            if (choiceSelection.choiceId.equals(choice)){
                return true;
            }
        }
        return false;
    }
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getLatencySeconds() {
        return latencySeconds;
    }

    public void setLatencySeconds(int latencySeconds) {
        this.latencySeconds = latencySeconds;
    }

}

class ChoiceSelection implements Serializable{

    @SerializedName("choiceId")
    Integer choiceId;
    @SerializedName("latencySeconds")
    Integer latencySeconds;
    @SerializedName("evaluationId")
    Integer evaluationId;
    @SerializedName("timeStamp")
    Long timeStamp;
    public ChoiceSelection(int choiceId, Long timeStamp, Integer latencySeconds){
        this.choiceId = choiceId;
        this.timeStamp = timeStamp;
        this.latencySeconds = latencySeconds;
    }
}
