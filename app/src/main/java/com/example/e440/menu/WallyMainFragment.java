package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by e440 on 07-05-18.
 */

public class WallyMainFragment extends Fragment {

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity=(Activity)context;
         try {
            mCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }
    MainFragmentListener mCallback;
    NetworkManager networkManager;
    HashMap<Integer, Integer> result = new HashMap<>();
    int nextSituationIndex= 0;

    View inflatedView;
    DatabaseManager databaseManager;
    int[] int_arr=new int[]{0,1,2,3};
    WReaction[] wreactions;
    long wsituation_id;
    boolean first_start=true;
    ImageView[] imageViews;
    WFeeling[] wFeelings;
    int innerQuestionPointer=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.wally_fragment, container, false);
        databaseManager= DatabaseManager.getInstance(getContext());

        ImageView imageView1=inflatedView.findViewById(R.id.wallyImage1);
        ImageView imageView2=inflatedView.findViewById(R.id.wallyImage2);
        ImageView imageView3=inflatedView.findViewById(R.id.wallyImage3);
        ImageView imageView4=inflatedView.findViewById(R.id.wallyImage4);
        imageViews=new ImageView[]{imageView1,imageView2,imageView3,imageView4};


        Bundle args= getArguments();
        wsituation_id=args.getLong("wsituation_id");

        for (ImageView i:imageViews
             ) {

            i.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayNextQuestion();
                }
            });
        }
        if(first_start==true){
            LoadWallyData lwd=new LoadWallyData();
            lwd.execute();}
        else{
            displayNextQuestion();
        }
        return inflatedView;
    }



    void displayFeelingQuestion(){

        ((TextView)inflatedView.findViewById(R.id.wallyTopTextView)).setText("Cuando eso te pasa, ¿Cómo te sientes?");

        //TODO: Shuffle
        for (int  i: int_arr
             ) {

            imageViews[i].setImageBitmap(Utilities.convertBytesArrayToBitmap(wFeelings[i].getImage_bytes()));
            imageViews[i].setId(wFeelings[i].getWfeeling());
        }

         innerQuestionPointer+=1;
    }

    void displayActionQuestion(){
        Collections.shuffle(Arrays.asList(int_arr));
        ((TextView)inflatedView.findViewById(R.id.wallyTopTextView)).setText("Cuando eso te pasa, ¿Qué haces tu?");

        for (int i:int_arr
             ) {

            WReaction wac=wreactions[i];
            imageViews[i].setImageBitmap(Utilities.convertBytesArrayToBitmap(wac.getImage_bytes()));

        }

        innerQuestionPointer++;
        nextSituationIndex+=1;

    }

    void displayNextQuestion(){

        if (innerQuestionPointer==0) {
            displayFeelingQuestion();
        }
        else if (innerQuestionPointer==1) {
            displayActionQuestion();
        }
        else{
            //TODO: back with result

            mCallback.backFromTest(null);
        }
    }




    private class LoadWallyData extends LoadData {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog

        }

        @Override
        protected ArrayList<Object[]> doInBackground(Void... voids) {
            wreactions =databaseManager.testDatabase.daoAccess().fetchWActionsByWSituationId(wsituation_id);
            wFeelings=databaseManager.testDatabase.daoAccess().fetchAllWFeelings();
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<Object[]> result) {
            first_start=false;
           displayNextQuestion();
        }
    }


}