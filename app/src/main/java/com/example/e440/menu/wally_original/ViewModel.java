package com.example.e440.menu.wally_original;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e440.menu.DatabaseManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    public Evaluation getEvaluation() {
        return evaluation;
    }
    private Evaluation evaluation;
    public void setEvaluation(Evaluation evaluation){
        this.evaluation = evaluation;
        items = evaluation.itemsList;
        //currentItemIndex = answers.size();
    }

    private int currentItemIndex = 0;

    List<WallyOriginalItem> items;

    public ViewModel(){}


    public void IncrementCurrentItemIndex(){
        currentItemIndex++;
    }

     public void DecrementCurrentItemIndex(){
        currentItemIndex--;
    }

    public boolean CurrentItemIsThanksItem(){
        return currentItemIndex >= evaluation.itemWithAnswers.size();
    }

    public WallyOriginalItem GetCurrentItem(){
        return evaluation.itemWithAnswers.get(GetCurrentItemIndex()).getItem();
    }

    public int getItemsCount(){
        return evaluation.itemWithAnswers.size();
    }

    public int GetCurrentItemIndex() {
        return currentItemIndex;
    }

    public void SavePersistent(){

        File folder = new File(  "", "evaluations/");
        folder.mkdirs();
        String folderPath = folder.getPath();
        File file = new File(folderPath, "eval.json");
        try {

            file.createNewFile();
            FileOutputStream fOut2 = new FileOutputStream(file);


            ObjectOutputStream out = new ObjectOutputStream(fOut2);
            out.writeObject(evaluation);
            out.close();
            System.out.printf("Serialized data to "+ folderPath);

            ;}catch (IOException exc){
            Log.d("Perro", "gato");
        Log.d("sapo", exc.getMessage());}


        //access the persistent layer and save the actual data and next submit it to server
    }

    public ItemAnswer getAnswer(int index){

        return this.evaluation.itemWithAnswers.get(index).getItemAnswer();

    }


}
