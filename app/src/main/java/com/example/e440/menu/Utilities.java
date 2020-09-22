package com.example.e440.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


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

    final static String WALLY_NAME="wally";
    final static String ACE_NAME="ace";
    final static String CORSI_NAME="corsi";
    final static String FONOTEST_NAME="fonotest";
    final static String HNF_NAME="hnf";
    final static String[] TEST_NAMES={WALLY_NAME,ACE_NAME,CORSI_NAME,FONOTEST_NAME,HNF_NAME};

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
