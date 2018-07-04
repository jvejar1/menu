package com.example.e440.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by e440 on 29-06-18.
 */

public class BaseActivity extends AppCompatActivity{
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Está apunto de abortar el test");
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO:manage the abort of a test
            }
        });
        builder.setPositiveButton("Abortar test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                abortTest();
            }
        });
        builder.show();

    }


    void abortTest(){
        Toast.makeText(this,"Test abortado!",Toast.LENGTH_LONG).show();
        this.finish();
    }
}
