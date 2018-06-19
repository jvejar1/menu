package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by e440 on 12-05-18.
 */

public class CorsiMainFragment extends Fragment{
    private CorsiMainFragmentListener mCallback;
    public interface CorsiMainFragmentListener{

        void backFromPracticeListener();
        void backFromTestListener(JSONArray jsonArray);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (CorsiMainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            Bundle b=msg.getData();
            int button_id=b.getInt("button_id");
            if (what==PAINT_BUTTON) {
                paintButton(button_id);
            }
            else if (what==REMOVE_BUTTON_PAINT){
                removeButtonPaint(button_id);

            }
            else if (what==DISABLE_BUTTON){

                inflatedView.findViewById(button_id).setEnabled(false);
            }
            else if (what==ENABLE_BUTTON){

                inflatedView.findViewById(button_id).setEnabled(true);

            }
        }
    };

    void button_enable_attr(int enable_disable,int button_id,int delay){
        Bundle b;
        b=new Bundle();
        b.putInt("button_id",button_id);
        Message msg=new Message();
        msg.setData(b);
        msg.what=enable_disable;
        myHandler.sendMessageDelayed(msg,delay);

    }


    String[] sequenceArray;
    ArrayList<int[]> sequencesList=new ArrayList<>();
    Csequence[] csequences;
    HashMap<Integer,Integer> corrects_by_index=new HashMap<>();
    HashMap<Integer,Integer> incorrects_by_index=new HashMap<>();

    long limit=2000;

    long sequence_start_time;
    int current_sequence_index=0;
    int current_index_in_sequence=0;
    View inflatedView;
    String str_secuence;
    JSONObject result=new JSONObject();
    int PRACTICE;
    private final static int PAINT_BUTTON = 0;
    private final static int REMOVE_BUTTON_PAINT = 1;
    private final static int DISABLE_BUTTON=2;
    private final static int ENABLE_BUTTON=3;
    private HashMap<Integer,Integer> ButtonIdByIndex=new HashMap<>();
    private HashMap<Integer,Long> timeCompletionByIndex=new HashMap<>();
    private HashMap<Integer,Integer> ButtonIndexById=new HashMap<>();
    private Csquare[] csquares;
    int mode;
    Timer t=new Timer();
    TimerTask timerTask;
    DataLoader dataLoader;
    DatabaseManager databaseManager;
    ReentrantLock lock = new ReentrantLock();
    private NextSequenceDisplayer dns;
    int score;
    JSONArray results;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        databaseManager=DatabaseManager.getInstance(getContext());
        mode = getArguments().getInt("mode");

        //Initialize buttons Hash, should be done in background

        try{result.putOpt("responses",new JSONArray());}

        catch (JSONException e ){
            //


        }

        results=new JSONArray();
        inflatedView = inflater.inflate(R.layout.corsi_main_fragment, container, false);
        dns=new NextSequenceDisplayer();
        new DataLoader().execute();
        return inflatedView;
    }

    public class DataLoader extends AsyncTask<Integer,Void,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {

            if (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE){

                int ordered_int=(mode==CorsiActivity.ORDERED_PRACTICE)?1:0;

                boolean ordered_bool=(mode==CorsiActivity.ORDERED_PRACTICE)?true:false;
                csequences=databaseManager.testDatabase.daoAccess().fetchPracticeCsequences(ordered_int);
                if (csequences.length==0){

                    for (int i=1;i<3;i++){

                        Csequence c=new Csequence(0,i,ordered_bool);
                        long c_id=databaseManager.testDatabase.daoAccess().insertCSequence(c);
                        Csquare cs1=new Csquare(0,1,(int)c_id,1+i);
                        databaseManager.testDatabase.daoAccess().insertCSquare(cs1);
                        Csquare cs2=new Csquare(0,1,(int)c_id,4+i);
                        databaseManager.testDatabase.daoAccess().insertCSquare(cs2);


                    }

                }

                csequences=databaseManager.testDatabase.daoAccess().fetchPracticeCsequences(ordered_int);


            }

            else if(mode==CorsiActivity.ORDERED_TEST){

                databaseManager.testDatabase.daoAccess().deleteFakeSquares();
                databaseManager.testDatabase.daoAccess().deleteFakeCsequences();

                csequences=databaseManager.testDatabase.daoAccess().fetchOrderedCsequences();

            }

            else if(mode==CorsiActivity.REVERSED_TEST){


                databaseManager.testDatabase.daoAccess().deleteFakeSquares();
                databaseManager.testDatabase.daoAccess().deleteFakeCsequences();

                csequences=databaseManager.testDatabase.daoAccess().fetchReversedCsequences();


            }
            ButtonIdByIndex.put(1,R.id.corsiButton1);
            ButtonIndexById.put(R.id.corsiButton1,1);
            ButtonIdByIndex.put(2,R.id.corsiButton2);
            ButtonIndexById.put(R.id.corsiButton2,2);

            ButtonIdByIndex.put(3,R.id.corsiButton3);
            ButtonIndexById.put(R.id.corsiButton3,3);

            ButtonIdByIndex.put(4,R.id.corsiButton4);
            ButtonIndexById.put(R.id.corsiButton4,4);

            ButtonIdByIndex.put(5,R.id.corsiButton5);
            ButtonIndexById.put(R.id.corsiButton5,5);

            ButtonIdByIndex.put(6,R.id.corsiButton6);
            ButtonIndexById.put(R.id.corsiButton6,6);

            ButtonIdByIndex.put(7,R.id.corsiButton7);
            ButtonIndexById.put(R.id.corsiButton7,7);

            ButtonIdByIndex.put(8,R.id.corsiButton8);
            ButtonIndexById.put(R.id.corsiButton8,8);

            ButtonIdByIndex.put(9,R.id.corsiButton9);
            ButtonIndexById.put(R.id.corsiButton9,9);

            ButtonIdByIndex.put(10,R.id.corsiButton10);
            ButtonIndexById.put(R.id.corsiButton10,10);

            for (int i=0;i<csequences.length;i++){
                corrects_by_index.put(i,0);
                incorrects_by_index.put(i,0);
                timeCompletionByIndex.put(i,(long)0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);


            for (int id:ButtonIndexById.keySet()
                    ) {

                Button b=inflatedView.findViewById(id);
                b.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             ButtonClickProcessor buttonClickProcessor=new ButtonClickProcessor();
                                             buttonClickProcessor.execute(view.getId());
                                         }
                                     }
                );

            }

            dns.execute();
        }
    }


    public class ButtonClickProcessor extends AsyncTask<Integer,Void,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            int button_id=integers[0];

            //save the response...


            Csequence current_csequence=csequences[current_sequence_index];
            int button_index=ButtonIndexById.get(button_id);
            boolean correct=false;
            int csquares_lenght=csquares.length;
            if(mode==CorsiActivity.ORDERED_TEST || mode==CorsiActivity.ORDERED_PRACTICE) {
                if (button_index == getNumberSquareAtPosition(current_index_in_sequence)) {
                    corrects_by_index.put(current_sequence_index, corrects_by_index.get(current_sequence_index) + 1);
                    correct=true;

                    int pot=csquares_lenght-current_index_in_sequence-1;
                    score+=Math.pow(2,pot);
                    Log.d("myTag", "CORRECT");
                } else {

                    incorrects_by_index.put(current_sequence_index, incorrects_by_index.get(current_sequence_index) + 1);

                }
            }

            else if(mode==CorsiActivity.REVERSED_TEST || mode==CorsiActivity.REVERSED_PRACTICE){

                if (button_index == getNumberAtPositionInStrRevSeq(current_index_in_sequence)) {
                    corrects_by_index.put(current_sequence_index, corrects_by_index.get(current_sequence_index) + 1);
                    correct=true;
                    int pot=csquares_lenght-current_index_in_sequence-1;
                    score+=Math.pow(2,pot);
                    Log.d("myTag", "CORRECT");
                }
                 else {
                    incorrects_by_index.put(current_sequence_index, incorrects_by_index.get(current_sequence_index) + 1);
                }


            }

            if(correct==true && (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE )){

                sendMessageToHandler(button_id,0);
                current_index_in_sequence++;

            }

            else if(mode==CorsiActivity.REVERSED_TEST || mode==CorsiActivity.ORDERED_TEST){


                sendMessageToHandler(button_id,0);
                current_index_in_sequence++;

            }

            if (current_index_in_sequence==csquares.length){




                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("csequence_id", current_csequence.getServer_id());

                    jsonObject.put("score", score);
                    results.put(jsonObject);
                }

                catch (JSONException e){
                    e.printStackTrace();
                }
                current_sequence_index++;
                dns = new NextSequenceDisplayer();
                dns.execute();

            }


            return null;
        }
    }

    Integer getNumberSquareAtPosition(int position){
        Csquare csquare=csquares[position];
        if(position>= csquares.length){

            return null;
        }

        return (csquare.getSquare());

    }

    Integer getNumberAtPositionInStrRevSeq(int position){
        Csquare csquare=csquares[csquares.length-1-position];
        return (csquare.getSquare());

    }

    void sendMessageToHandler(int button_id,int delay){

        Bundle b;
        b=new Bundle();
        b.putInt("button_id",button_id);
        Message msg=new Message();
        msg.setData(b);
        msg.what=PAINT_BUTTON;
        myHandler.sendMessageDelayed(msg,delay);

        msg=new Message();
        msg.setData(b);
        msg.what=REMOVE_BUTTON_PAINT;
        myHandler.sendMessageDelayed(msg,delay+700);


    }


    public class NextSequenceDisplayer extends AsyncTask<Integer,Void,Integer>{



        @Override
        protected Integer doInBackground(Integer... integers) {

            //disable buttons

            for (int id:ButtonIndexById.keySet()
                 ) {

                button_enable_attr(DISABLE_BUTTON,id,0);
            }


            //
            if(current_sequence_index==csequences.length){
                //save and finnish
                if (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE){

                    mCallback.backFromPracticeListener();

                }

                else if (mode==CorsiActivity.ORDERED_TEST || mode==CorsiActivity.REVERSED_TEST){

                    //TODO: save result and send integer to activity
                    mCallback.backFromTestListener(results);
                }



                return null;

            }

            current_index_in_sequence=0;

            try{Thread.sleep(1000);}
            catch (InterruptedException e){
                e.printStackTrace();
            }
            score=0;
            csquares=databaseManager.testDatabase.daoAccess().fetchCsquareByCsequenceId(csequences[current_sequence_index].getId());
            Bundle b;
            int counter=1;
            for (Csquare csquare:csquares
                 ) {

                int sequence_instruction=csquare.getSquare();
                int button_id=ButtonIdByIndex.get(sequence_instruction);
                b=new Bundle();
                b.putInt("button_id",button_id);
                Message msg=new Message();
                msg.setData(b);
                msg.what=PAINT_BUTTON;
                myHandler.sendMessageDelayed(msg,700*counter);
                counter+=1;
                msg=new Message();
                msg.setData(b);
                msg.what=REMOVE_BUTTON_PAINT;
                myHandler.sendMessageDelayed(msg,700*counter);
                counter+=1;
            }



            for (int id:ButtonIndexById.keySet()
                    ) {

                button_enable_attr(ENABLE_BUTTON,id,700*counter);
            }


            final int time_limit=10000;





            return null;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);


        }
    }



    private void paintButton(int button_id) {
        inflatedView.findViewById(button_id).setBackgroundResource(R.drawable.corsi_highlight_button_bg);


    }

    private void removeButtonPaint(int button_id){

        inflatedView.findViewById(button_id).setBackgroundResource(R.drawable.corsi_default_button_bg);


    }
}
