package com.example.e440.menu;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by e440 on 01-05-18.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progressdialog
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        String baseUrl=NetworkManager.BASE_URL;
        Bitmap bm = null;
        try {
            URL aURL = new URL(baseUrl+url[0]);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        // Set the bitmap into ImageView

        int a =5;


    }
}