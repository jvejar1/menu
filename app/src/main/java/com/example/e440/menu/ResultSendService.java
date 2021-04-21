package com.example.e440.menu;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Created by e440 on 26-06-18.
 */

public class ResultSendService extends IntentService {


    public ResultSendService(){
        super("");

    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this,"SERVICIO INICIADO",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"SERVICIO DESTRUYENDOSE",Toast.LENGTH_LONG).show();


    }
}
