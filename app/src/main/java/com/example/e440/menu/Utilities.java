package com.example.e440.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
