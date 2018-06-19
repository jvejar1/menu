package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e440 on 30-05-18.
 */

public class FonoTestMainFragment extends Fragment{


    MainFragmentListener mCallback;
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

    View inflatedView;
    int[] fgroup_ids;
    MediaPlayer mediaPlayer;
    Item[] items;
    LinearLayout optionsLinearLayout;
    LinearLayout responsesLinearLayout;
    int current_item_index=0;
    int current_fgroup_index=0;
    List<String> response_list=new ArrayList<>();
    DatabaseManager databaseManager= DatabaseManager.getInstance(getContext());
    Button playButton;
    String correct_seq_str;
    List<String> correct_words=new ArrayList<>();
    HashMap<Integer,String> response_by_item_server_id=new HashMap<>();

    FonoTest fonoTest;

    List<String> correct_numbers =new ArrayList<>();
    ArrayList<Integer[]> scores_douples=new ArrayList<>(); //(server_item_id,score);
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fonotest_main_fragment, container, false);
        mediaPlayer=new MediaPlayer();
        optionsLinearLayout=inflatedView.findViewById(R.id.optionsLinearLayout);


        responsesLinearLayout=inflatedView.findViewById(R.id.responsesLinearLayout);
        playButton=inflatedView.findViewById(R.id.playAudioButton);
        playButton.setEnabled(false);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });

        TextView remindTextView=inflatedView.findViewById(R.id.remindTextView);
        final String text_to_remind="Si responde con el dígito primero califique el item de 0 y diga 'recuerda que debes decirme primer la palabra,luego el número'";
        remindTextView.setText(text_to_remind);

        Button nextButton=inflatedView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:SAVE RESULT

                inflatedView.setVisibility(View.INVISIBLE);
                optionsLinearLayout.removeAllViews();
                responsesLinearLayout.removeAllViews();
                inflatedView.setVisibility(View.VISIBLE);


                int score=0;
                List<String> part_to_analize;
                if(response_list.size()>=correct_words.size()){
                    part_to_analize=response_list.subList(0,correct_words.size());
                    if(part_to_analize.equals(correct_words)){

                        score+=1;
                    }
                }
                if(response_list.size()>=correct_numbers.size()){
                    part_to_analize=response_list.subList(response_list.size()-correct_numbers.size(),response_list.size());
                    if(part_to_analize.equals(correct_numbers)){
                        score+=1;
                    }
                }
                Item current_item=items[current_item_index];
                Integer[] score_douple={current_item.getSever_id(),score};
                response_by_item_server_id.put(current_item.getSever_id(),TextUtils.join("..",response_list));
                scores_douples.add(score_douple);
                current_item_index++;
                resetVariablesForItem();
                displayNextItem();
                }
        });

        AsyncTask loader=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                items=databaseManager.testDatabase.daoAccess().fetchAllItems();
                fonoTest=databaseManager.testDatabase.daoAccess().fetchFonotest();
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(items.length==0 || fonoTest==null){
                    finish();
                }
                displayNextItem();
            }
        }.execute();
        return inflatedView;
    }
    void finish(){
        //TODO:finish

        JSONObject payload=new JSONObject();
        try{payload.put("responses",response_by_item_server_id);
            payload.put("scores",scores_douples);
            payload.put("test_id",fonoTest.getServer_id());
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        mCallback.backFromTest(payload);

    }

    void resetVariablesForItem(){
        correct_words.clear();
        correct_numbers.clear();

    }

    void playAudio(){
        Button b= inflatedView.findViewById(R.id.nextButton);
        b.setEnabled(false);
        try {
            Item item=items[current_item_index];
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3");
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(item.getAudio());
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            playButton.setEnabled(false);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();

                }

            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playButton.setEnabled(true);
                    Button nextButton=inflatedView.findViewById(R.id.nextButton);
                    nextButton.setEnabled(true);
                }
            });
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    void moveButton(Button view){
        LinearLayout parent=(LinearLayout) view.getParent();
        if(parent.getId()==responsesLinearLayout.getId()){
            int button_index = parent.indexOfChild(view);
            response_list.remove(button_index);
            responsesLinearLayout.removeView(view);
            optionsLinearLayout.addView(view);
        }

        else{
            response_list.add(view.getText().toString());
            optionsLinearLayout.removeView(view);
            responsesLinearLayout.addView(view);
        }
    }

    void displayNextItem(){

        if(current_item_index==items.length){
            finish();

        }

        else{
            response_list.clear();
            Item item=items[current_item_index];
            if(item.getAudio()!=null){
                playButton.setEnabled(true);
            }
            TextView audioDescription=inflatedView.findViewById(R.id.audioDescriptionTextView);
            audioDescription.setText(item.getDescription());



            String[] audio_desc=item.getDescription().split("\\.\\.");
            for (int i=0;i<audio_desc.length;i++){
                String audio_desc_item=audio_desc[i];
                Button b=new Button(getContext());
                b.setText(audio_desc_item);
                if(Utilities.isParseableToInt(audio_desc_item)){
                    correct_numbers.add(audio_desc_item);
                }else{
                    correct_words.add(audio_desc_item);
                }

                b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveButton((Button)view);
                    }
                });


                optionsLinearLayout.addView(b);
            }


            TextView itemNameTextView=inflatedView.findViewById(R.id.itemNameTextView);
            if(item.isExample()){
                itemNameTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWarning));
                itemNameTextView.setText(item.getName());

            }
            else{

                itemNameTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.defaultBlack));
                itemNameTextView.setText("Item "+item.getName());

            }
            TextView instructionTextView=inflatedView.findViewById(R.id.fonotestInstructionTextView);
            instructionTextView.setText(item.getInstruction());

            correct_seq_str=TextUtils.join("..",correct_numbers)+".."+TextUtils.join("..",correct_words);
        }
        return;

    }



}
