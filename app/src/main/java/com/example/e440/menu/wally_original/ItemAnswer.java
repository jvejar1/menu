package com.example.e440.menu.wally_original;

public class ItemAnswer {

    int itemId;
    String answer;
    int latencySeconds;
    int evaluationId;

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
