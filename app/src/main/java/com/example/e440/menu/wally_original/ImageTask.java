package com.example.e440.menu.wally_original;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ImageTask {
    int id;
    ImageView localView;
    Bitmap connectedBitmap;
    Bitmap disconnectedBitmap;
    Bitmap messageWrittenBitmap;
    Bitmap messageNotWrittenBitmap;
    String messageToSend;


    public ImageTask(String msgToSend, ImageView localView){
        this.messageToSend = msgToSend;
        this.localView = localView;


    }
    String getMessageToSend(){

        return this.messageToSend;

    }


    void setMessageToSend(String msg){

        this.messageToSend = msg;
    }

}
