package com.example.e440.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by e440 on 01-05-18.
 */

class Utilities {
    static boolean isParseableToInt(String s){
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }

    }

    static Bitmap convertBytesArrayToBitmap(byte[] ba){
        Bitmap bm = BitmapFactory.decodeByteArray(ba, 0, ba.length);
        return bm;
    }
    static byte[] convertBitmapToBytesArray(Bitmap bm){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }





}
