package com.example.e440.menu.wally_original;

import java.io.Serializable;
import java.util.List;

public class ItemAnswer implements Serializable {

    int itemId;
    String answer;
    int latencySeconds;
    int evaluationId;
    public List<Integer> answersChoices;
    public ItemAnswer(){
        this.answer = "";
        this.latencySeconds = 0;
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
