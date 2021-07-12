package com.example.e440.menu;

public class EvaluationCount {
    String text;
    int count;
    public String getText(){return this.text;}
    public String getCount(){return String.format("%d",this.count);}
    EvaluationCount(String text, int count){
        this.text = text;
        this.count = count;
    }
}
