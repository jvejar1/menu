package com.example.e440.menu.wally_original;

import java.io.Serializable;

public class ItemChoice implements Serializable {
    int id;

    public String getText() {
        return choiceText;
    }

    public void setText(String text) {
        this.choiceText = text;
    }

    String choiceText;
    int value;
    int order;
    Picture picture;
    String getPicturePath(){
        if (picture==null){
            return "";
        }
        return picture.getFilePath();
    }
}
