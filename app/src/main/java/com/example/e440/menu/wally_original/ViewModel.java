package com.example.e440.menu.wally_original;

import android.app.Application;
import android.database.Observable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e440.menu.CredentialsManager;
import com.example.e440.menu.DatabaseManager;
import com.example.e440.menu.Student;
import com.example.e440.menu.fonotest.Item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {

    File filesDir;
    InstrumentsManager instrumentsManager;
    public ViewModel() {
    }
    
    public Evaluation getEvaluation() {
        return evaluation;
    }
    private Evaluation evaluation;
    public String instructionText;
    public void setEvaluation(Evaluation evaluation, String instructionText){
        this.evaluation = evaluation;
        this.instructionText = instructionText;
    }

    public void configure(int instrumentIdx, Long studentId, Long userId, InstrumentsManager instrumentsManager){
        ItemsBank instrument = instrumentsManager.getInstruments().get(instrumentIdx);
        Evaluation evaluation = new Evaluation(instrument, studentId,userId);
        this.evaluation = evaluation;
        this.instructionText = instrument.getInstruction();
        this.mustBeginWithTheItems.postValue(true);
    }


    private MutableLiveData<Boolean> mustBeginWithTheItems= new MutableLiveData<>(false);

    public LiveData<Boolean> getMustBeginWithTheItems(){
        return this.mustBeginWithTheItems;
    }

    public boolean setEvaluationFinished(){
        this.evaluation.setAsFinished();
        boolean status= this.evaluation.finished;
        return status;
    }

    private List<MutableLiveData<Boolean>[]> choiceSelectedValues;
    public LiveData<Boolean> getChoiceSelectedFlag(int itemIndex, int choiceIndex){
        if (choiceSelectedValues == null){
            choiceSelectedValues = new ArrayList<>(getItemsCount());
            for(int i = 0; i< getItemsCount(); i++){
                choiceSelectedValues.add(null);
            }
        }
        if (choiceSelectedValues.get(itemIndex) == null){
            int choicesCount = this.evaluation.itemWithAnswers.get(itemIndex).getChoicesCount();
            MutableLiveData<Boolean>[] choicesSelected = new MutableLiveData[choicesCount];
            choiceSelectedValues.set(itemIndex, choicesSelected);
        }
        if (choiceSelectedValues.get(itemIndex)[choiceIndex] == null){
            choiceSelectedValues.get(itemIndex)[choiceIndex] = new MutableLiveData<>(false);
        }
        ItemAnswer answer = this.evaluation.itemWithAnswers.get(itemIndex).getItemAnswer();
        ItemChoice itemChoice = this.evaluation.itemWithAnswers.get(itemIndex).getChoice(choiceIndex);
        if (answer.isThisChoiceSelected(itemChoice)){
            choiceSelectedValues.get(itemIndex)[choiceIndex].postValue(true);
        }

        return this.choiceSelectedValues.get(itemIndex)[choiceIndex];
    }

    public Long[] getShowHideProgram(int itemIdx, int choiceIdx){
        WallyOriginalItem item = this.evaluation.itemWithAnswers.get(itemIdx).getItem();
        Long[] timeProgram = new Long[2];
        timeProgram[0] = 0L;
        timeProgram[1]=null;
        if (item.getItemTypeId() == 2){
            timeProgram[0] = 000L*(choiceIdx+1);
        }
        return timeProgram;
    }


    public boolean userTouchesChoice(int itemIndex,int whichChoiceInsert){
        ItemWithAnswer itemWithAnswer = this.evaluation.itemWithAnswers.get(itemIndex);
        boolean inserted = itemWithAnswer.insertChoice(whichChoiceInsert);
        WallyOriginalItem item = this.evaluation.itemWithAnswers.get(itemIndex).getItem();

        for (ItemChoice itemChoice: item.getChoices()){
            boolean selected = false;
            for(ChoiceSelection choiceSelection: getAnswer(itemIndex).getChoiceSelections()){
                if (choiceSelection.choiceId.equals(itemChoice.id)){
                    selected = true;
                }
            }
            int choiceIdx = item.getChoices().indexOf(itemChoice);
            choiceSelectedValues.get(itemIndex)[choiceIdx].postValue(selected);

        }
        return inserted;
    }

    public String getInstructionText(){
        return instructionText;
    }

    public void IncrementCurrentItemIndex(){
        this.evaluation.currentItemIndex++;
    }

     public void DecrementCurrentItemIndex(){
        this.evaluation.currentItemIndex--;
    }

    public Integer getCurrentItemIndex(){
        return this.evaluation.getCurrentItemIndex();
    }

    public boolean CurrentItemIsThanksItem(){
        return getCurrentItemIndex() >= evaluation.itemWithAnswers.size();
    }

    public boolean currentItemIsTheLast(){
        return getCurrentItemIndex() == evaluation.itemWithAnswers.size() -1 ;
    }

    public WallyOriginalItem GetCurrentItem(){
        return evaluation.itemWithAnswers.get(getCurrentItemIndex()).getItem();
    }

    public int getItemsCount(){
        return evaluation.itemWithAnswers.size();
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
    public ItemAnswer generateAnswerForView(int index){
        ItemAnswer itemAnswer= this.evaluation.itemWithAnswers.get(index).getItemAnswer();
        Long timestamp = System.currentTimeMillis()/1000;
        itemAnswer.setDisplayTimeStamp(timestamp);
        return itemAnswer;
    }


}
