package com.example.e440.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by e440 on 01-05-18.
 */

class Utilities {

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
