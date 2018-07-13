package com.example.e440.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e440 on 30-05-18.
 */

public class FonoTestMainFragment extends Fragment implements View.OnClickListener{

    //TODO: the mobile device should trust in the server correct sequence, for performance purposes

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

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.evaluateWithZeroButton){
            evaluateWithZero();

        }
    }

    void evaluateWithZero(){
        Button zero_button=inflatedView.findViewById(R.id.evaluateWithZeroButton);
        if(evaluated_with_zero) {
            zero_button.setText("Evaluar con 0");
            evaluated_with_zero=false;
        }
        else{
            zero_button.setText("Revertir");
            evaluated_with_zero=true;
            Toast.makeText(getContext(),"Item evaluado con 0",Toast.LENGTH_SHORT).show();
        }

    }

    View inflatedView;
    MediaPlayer mediaPlayer;
    Item[] items;
    int errors=0;
    LinearLayout AnswersVerticalLinearLayout;
    int current_item_index=0;
    List<String> response_list=new ArrayList<>();
    DatabaseManager databaseManager= DatabaseManager.getInstance(getContext());
    Button playButton;
    String correct_seq_str;
    List<String> correct_words=new ArrayList<>();
    HashMap<Integer,String> response_by_item_server_id=new HashMap<>();
    HashMap<Integer,Boolean> success_buttons_hash=new HashMap<>();
    HashMap<Button,Integer> index_elem_by_button =new HashMap<>();
    HashMap<Button,Boolean> pressed_by_button=new HashMap<>();

    static int SEQ_ITEM_TEXT_VIEW_WIDTH_IN_DP;

    static int ANSWER_HORIZONTAL_LL_HEIGHT_IN_DP;

    int extra_elements_count=0;

    int extra_words_count=0;
    int extra_numbers_count=0;
    List<int[]> pressed_boolean_touples= new ArrayList<>();
    List<String> original_sequence_list=new ArrayList<>();
    List<String> correct_sequence_list=new ArrayList<>();

    FonoTest fonoTest;
    boolean evaluated_with_zero;
    List<String> correct_numbers =new ArrayList<>();
    HashMap<Integer,Integer> scores_by_item_server_id=new HashMap<>(); //(server_item_id,score);
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fonotest_main_fragment, container, false);
        mediaPlayer=new MediaPlayer();
        AnswersVerticalLinearLayout =inflatedView.findViewById(R.id.optionsLinearLayout);

        SEQ_ITEM_TEXT_VIEW_WIDTH_IN_DP=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
        ANSWER_HORIZONTAL_LL_HEIGHT_IN_DP=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

        playButton=inflatedView.findViewById(R.id.playAudioButton);
        playButton.setEnabled(false);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });

        TextView remindTextView=inflatedView.findViewById(R.id.remindTextView);
        final String text_to_remind="Si responde con el dígito primero califique el item de 0 y diga <b>'recuerda que debes decirme primero la palabra, luego el número'</b>";
        remindTextView.setText(Html.fromHtml(text_to_remind));

        Button nextButton=inflatedView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:SAVE RESULT



                if(current_item_index>=items.length){

                    return;
                }
                AnswersVerticalLinearLayout.removeAllViews();
                inflatedView.setVisibility(View.VISIBLE);


                int score=0;

                if(!evaluated_with_zero){
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

                }

                Item current_item=items[current_item_index];
                response_by_item_server_id.put(current_item.getSever_id(),TextUtils.join("..",response_list));
                scores_by_item_server_id.put(current_item.getSever_id(),score);
                if(score==0 && !current_item.isExample()){
                      errors++;
                }
                if(score>0){
                    errors=0;
                }
                if(errors==3){

                    finish();
                    //TODO: remove the above //, only the //
                    return;
                }
                current_item_index++;
                resetVariablesForItem();
                displayNextItem();
                }
        });

        //Button that evaluates with zero
        Button zero_button=inflatedView.findViewById(R.id.evaluateWithZeroButton);
        zero_button.setOnClickListener(this);


        AsyncTask loader=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                items=databaseManager.testDatabase.daoAccess().fetchAllItems();
                fonoTest=databaseManager.testDatabase.daoAccess().fetchFonotest();
                Item[] starting_points_items=databaseManager.testDatabase.daoAccess().fetchStartingPointsItems();
                return starting_points_items;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(items.length==0 || fonoTest==null){
                    finish();
                }

                //display the options to starting points
                final Item[] starting_points_items=(Item[])o;
                final String[] examples_titles=new String[starting_points_items.length];
                int item_index=0;
                for (int i=0;i<starting_points_items.length;i++){
                    examples_titles[item_index]=starting_points_items[i].getName();
                    item_index++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Seleccione un punto de partida");
                builder.setCancelable(false);

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     //TODO:manage the abort of a test
                        BaseActivity b=(BaseActivity)mCallback;
                        b.abortTest();
                    }
                });
                builder.setItems(examples_titles, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // check the index of the selected example
                        int index_item_to_start=0;
                        for (int i=0;i<items.length;i++ ){

                            if(items[i].getId()==starting_points_items[which].getId()){
                                index_item_to_start=i;


                            }
                        }


                        current_item_index=index_item_to_start;
                        displayNextItem();

                    }
                });
                builder.show();


            }
        }.execute();
        return inflatedView;
    }
    void finish(){
        //TODO:finish

        JSONObject payload=new JSONObject();
        try{

            Gson gson =new Gson();
            payload.put("responses",new JSONObject(gson.toJson(response_by_item_server_id)));
            payload.put("scores",new JSONObject(gson.toJson(scores_by_item_server_id)));
            payload.put("test_id",fonoTest.getServer_id());

            mCallback.backFromTest(payload);
        }
        catch(JSONException e){
            e.printStackTrace();
        }


    }

    void resetVariablesForItem(){
        //reset zero button
        Button zero_button=inflatedView.findViewById(R.id.evaluateWithZeroButton);
        zero_button.setText("Evaluar con 0");
        evaluated_with_zero=false;


        correct_words.clear();
        correct_numbers.clear();
        pressed_boolean_touples.clear();
        correct_sequence_list.clear();
        original_sequence_list.clear();
        index_elem_by_button.clear();
        extra_elements_count=0;
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

            LinearLayout.LayoutParams seq_element_layout_params= new LinearLayout.LayoutParams(SEQ_ITEM_TEXT_VIEW_WIDTH_IN_DP,LinearLayout.LayoutParams.MATCH_PARENT);
            seq_element_layout_params.setMargins(0,0,0,0);

            String[] audio_desc=item.getDescription().split("\\.\\.");
            LinearLayout.LayoutParams responseButtonLayoutParams=new LinearLayout.LayoutParams(AnswersVerticalLinearLayout.getLayoutParams().width/3, LinearLayout.LayoutParams.MATCH_PARENT);

            LinearLayout.LayoutParams answers_ll_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ANSWER_HORIZONTAL_LL_HEIGHT_IN_DP);

            for (int i=0;i<audio_desc.length;i++){
                String audio_desc_item=audio_desc[i];
                Button b=new Button(getContext());
                original_sequence_list.add(audio_desc_item);
                if(Utilities.isParseableToInt(audio_desc_item)){
                    correct_numbers.add(audio_desc_item);
                }else{
                    correct_words.add(audio_desc_item);
                }

                //create the textview to display the word or number

                TextView seq_elem_text_view=new TextView(getContext());
                seq_elem_text_view.setTextColor(getResources().getColor(R.color.defaultBlack));
                seq_elem_text_view.setTextSize(18);
                seq_elem_text_view.setGravity(Gravity.CENTER);
                seq_elem_text_view.setText(audio_desc_item);
                seq_elem_text_view.setLayoutParams(seq_element_layout_params);

                LinearLayout horizontal_linear_layout=new LinearLayout(getContext());
                horizontal_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
                answers_ll_params.setMargins(0,10,0,0);
                horizontal_linear_layout.setLayoutParams(answers_ll_params);

                horizontal_linear_layout.addView(seq_elem_text_view);

                responseButtonLayoutParams.setMargins(0,0,0,0);
                b.setLayoutParams(responseButtonLayoutParams);
                evaluated_with_zero=false;
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button clicked_button=(Button)view;
                        if (pressed_by_button.containsKey(clicked_button)){
                            //do stuff
                            pressed_by_button.remove(clicked_button);
                            response_list.remove(original_sequence_list.get(index_elem_by_button.get(clicked_button)));
                            clicked_button.setBackgroundResource(android.R.drawable.btn_default);

                        }
                        else{

                            pressed_by_button.put(clicked_button,true);
                            response_list.add(original_sequence_list.get(index_elem_by_button.get(clicked_button)));
                            clicked_button.setBackgroundResource(R.drawable.success_button_bg);
                        }

                    }
                });
                index_elem_by_button.put(b,i);
                success_buttons_hash.put(b.getId(),true);
                b.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_ticket,0,0);
                horizontal_linear_layout.addView(b);



                //create the error button
                Button error_button=new Button(getContext());

                responseButtonLayoutParams.setMargins(20,0,0,0);
                error_button.setLayoutParams(responseButtonLayoutParams);
                error_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button pressed_button=(Button)view;
                        if (pressed_by_button.containsKey(pressed_button)){
                            pressed_by_button.remove(pressed_button);
                            extra_elements_count--;
                            pressed_button.setBackgroundResource(android.R.drawable.btn_default);

                        }
                        else{
                            pressed_by_button.put(pressed_button,true);
                            extra_elements_count++;
                            pressed_button.setBackgroundResource(R.drawable.error_button_bg);
                        }
                    }
                });
                error_button.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_close,0,0);
               // horizontal_linear_layout.addView(error_button);

                AnswersVerticalLinearLayout.addView(horizontal_linear_layout);
            }


            TextView itemNameTextView=inflatedView.findViewById(R.id.itemNameTextView);
            if(item.isExample()){
                itemNameTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWarning));
                itemNameTextView.setText(item.getName());
            }
            else{
                itemNameTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.defaultBlack));
                itemNameTextView.setText(item.getName());
            }
            TextView instructionTextView=inflatedView.findViewById(R.id.fonotestInstructionTextView);
            instructionTextView.setText(item.getInstruction());

            correct_seq_str=TextUtils.join("..",correct_numbers)+".."+TextUtils.join("..",correct_words);

            for (String number:correct_numbers){

                correct_sequence_list.add(number);

            }

            for (String word:correct_words){

                correct_sequence_list.add(word);

            }

        }
        return;

    }



}
