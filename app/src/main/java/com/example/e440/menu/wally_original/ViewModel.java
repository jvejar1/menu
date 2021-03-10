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
    private List<ItemAnswer> answers = new ArrayList<ItemAnswer>();

    public Evaluation getEvaluation() {
        return evaluation;
    }

    private Evaluation evaluation;

    public void setEvaluation(Evaluation evaluation){
        this.evaluation = evaluation;
        answers = evaluation.itemAnswerList;
        items = evaluation.itemsList;
        //currentItemIndex = answers.size();
    }

    private int currentItemIndex = 0;

    List<WallyOriginalItem> items = new ArrayList<>();


    public void insertAnswer(ItemAnswer answer, int index){

        answer.setItemId(items.get(index).getId());
        answers.set(index, answer);

    }
    public ViewModel(){}

    public WallyOriginalItem GetItem(int index){

        return items.get(index);
    }

    public void IncrementCurrentItemIndex(){
        currentItemIndex++;
    }

     public void DecrementCurrentItemIndex(){

        currentItemIndex--;
    }

    public boolean CurrentItemIsFirstItem(){
        return GetCurrentItem() == items.get(0);
    }

    public boolean CurrentItemIsLastItem(){

        return GetCurrentItem() == items.get(items.size()-1);
    }

    public WallyOriginalItem GetCurrentItem(){
        return items.get(GetCurrentItemIndex());

    }

    public int getItemsCount(){
     return items.size();
    }


    public void LoadItems(){

    }

    public int GetCurrentItemIndex() {
        return currentItemIndex;
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        this.currentItemIndex = currentItemIndex;
    }

    public void SavePersistent(){

        evaluation.itemAnswerList = answers;
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

        try{
            answers.get(index);
        }catch(IndexOutOfBoundsException e){

            ItemAnswer itemAnswer = new ItemAnswer();
            itemAnswer.setItemId(items.get(index).getId());
            answers.add(itemAnswer);
        }

        return answers.get(index);
    }


}
