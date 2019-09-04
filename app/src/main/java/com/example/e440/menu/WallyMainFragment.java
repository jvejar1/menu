package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

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
    Handler ui_handler=new Handler();
    MainFragmentListener mCallback;
    View inflatedView;
    DatabaseManager databaseManager;
    ArrayList<Integer> int_arr=new ArrayList<Integer>(Arrays.asList(0,1,2,3));
    WReaction[] wreactions;
    long wsituation_id;
    boolean first_start=true;
    ImageView[] imageViews;
    WFeeling[] wFeelings;
    int wfeeling_response;
    int wreaction_response;
    static int RESPONSE_DEFAULT_VALUE=-1;
    Button back_button;
    HashMap<String,Integer> wreaction_number_by_text;
    HashMap <Integer,Integer> wfeeling_number_by_img_id;
    HashMap<Integer,Integer> text_view_id_by_img_id;
    int innerQuestionPointer=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.wally_fragment, container, false);
        databaseManager= DatabaseManager.getInstance(getContext());
        back_button=inflatedView.findViewById(R.id.backButton);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                innerQuestionPointer-=2;
                if(innerQuestionPointer==-1){
                    //back to activity
                    mCallback.goBackFromMainFragment();
                }
                else{
                    ui_handler.removeCallbacksAndMessages(null);
                    displayNextQuestion();
                }
            }
        });
        ImageView imageView1=inflatedView.findViewById(R.id.wallyImage1);
        ImageView imageView2=inflatedView.findViewById(R.id.wallyImage2);
        ImageView imageView3=inflatedView.findViewById(R.id.wallyImage3);
        ImageView imageView4=inflatedView.findViewById(R.id.wallyImage4);
        imageViews=new ImageView[]{imageView1,imageView2,imageView3,imageView4};
        text_view_id_by_img_id=new HashMap<>();
        text_view_id_by_img_id.put(imageView1.getId(),R.id.textViewWallyImage1);
        text_view_id_by_img_id.put(imageView2.getId(),R.id.textViewWallyImage2);
        text_view_id_by_img_id.put(imageView3.getId(),R.id.textViewWallyImage3);
        text_view_id_by_img_id.put(imageView4.getId(),R.id.textViewWallyImage4);

        Bundle args= getArguments();
        wsituation_id=args.getLong("wsituation_id");
        wfeeling_response=args.getInt(WSituation.WFEELING_RESPONSE_EXTRA,RESPONSE_DEFAULT_VALUE);
        wreaction_response=args.getInt(WSituation.WREACTION_RESPONSE_EXTRA,RESPONSE_DEFAULT_VALUE);
        for (ImageView i:imageViews
             ) {

            i.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(innerQuestionPointer==1){
                        wfeeling_response=wfeeling_number_by_img_id.get(view.getId());

                    }
                    else{
                        TextView img_text_view=inflatedView.findViewById(text_view_id_by_img_id.get(view.getId()));
                        wreaction_response=wreaction_number_by_text.get(img_text_view.getText());
                    }
                    ui_handler.removeCallbacksAndMessages(null);
                    displayNextQuestion();
                }
            });
        }
        LoadWallyData lwd=new LoadWallyData();
        lwd.execute();

        return inflatedView;
    }


    void displayFeelingQuestion(){

        ((TextView)inflatedView.findViewById(R.id.wallyTopTextView)).setText("Cuando eso te pasa, ¿Cómo te sientes?");


        wfeeling_number_by_img_id=new HashMap<>();
        //TODO: Shuffle
        for (int  i: int_arr
             ) {

            int i_index=int_arr.indexOf(i);
            final ImageView image_view=imageViews[i_index];
            image_view.setImageBitmap(Utilities.convertBytesArrayToBitmap(wFeelings[i].getImage_bytes()));
            wfeeling_number_by_img_id.put(image_view.getId(),wFeelings[i].getWfeeling());
            int img_id=image_view.getId();
            final TextView img_text_view=inflatedView.findViewById(text_view_id_by_img_id.get(img_id));
            img_text_view.setText(Wally.feelings_by_number.get(wFeelings[i].getWfeeling()));

        }

         innerQuestionPointer+=1;
    }

    void displayActionQuestion(){


        ((TextView)inflatedView.findViewById(R.id.wallyTopTextView)).setText("Cuando eso te pasa, ¿Qué haces tu?");

        wreaction_number_by_text=new HashMap<>();
        for (int i:int_arr
             ) {

            WReaction wac=wreactions[i];
            int i_index=int_arr.indexOf(i);
            final ImageView image_view=imageViews[i_index];
            image_view.setImageBitmap(Utilities.convertBytesArrayToBitmap(wac.getImage_bytes()));
            wreaction_number_by_text.put(wac.getDescription(),wac.getWreaction());
            int img_id=image_view.getId();
            final TextView img_text_view=inflatedView.findViewById(text_view_id_by_img_id.get(img_id));
            img_text_view.setText(wac.getDescription());

        }

        innerQuestionPointer++;

    }

    void displayNextQuestion(){
        Collections.shuffle(int_arr,new Random(System.currentTimeMillis()));
        int i=1;
        for (final View img_view:imageViews){
            img_view.setVisibility(View.INVISIBLE);
            final TextView img_text_view=inflatedView.findViewById(text_view_id_by_img_id.get(img_view.getId()));
            img_text_view.setVisibility(View.INVISIBLE);

            i++;
        }
        if (innerQuestionPointer==0) {
            displayFeelingQuestion();
        }
        else if (innerQuestionPointer==1) {
            displayActionQuestion();
        }
        else{
            //TODO: back with result


            JSONObject jsonObject=new JSONObject();
            try{
                jsonObject.putOpt("wfeeling",wfeeling_response);
                jsonObject.putOpt("wreaction",wreaction_response);
                mCallback.backFromTest(jsonObject);
                return;
            }
            catch(JSONException e){


                e.printStackTrace();
                return;
            }
        }

        int delay=2800;
        int counter=0;
        int delay_from_last_lenght=0;

        for (final ImageView imageView:imageViews){
            int img_text_view_id=text_view_id_by_img_id.get(imageView.getId());
            final TextView img_text_view=inflatedView.findViewById(img_text_view_id);
            String img_text=img_text_view.getText().toString();
            int text_lenght=img_text.length();
            delay=delay+delay_from_last_lenght;

            if(text_lenght<10){

                delay_from_last_lenght=1900;
            }
            else if(text_lenght>=10 && text_lenght<20){
                delay_from_last_lenght=2350;

            }
            else if(text_lenght>=20 && text_lenght<30){
                delay_from_last_lenght=3250;

            }
            else{
                delay_from_last_lenght=4000;

            }

            ui_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_text_view.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
            },delay);
            counter++;
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

            //JUMP TO THE END IF ALREADY EXIST RESPONSES
            if(wfeeling_response!=RESPONSE_DEFAULT_VALUE && wreaction_response!=RESPONSE_DEFAULT_VALUE){
                innerQuestionPointer++;
            }
           displayNextQuestion();
        }
    }

}