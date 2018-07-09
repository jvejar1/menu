package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by e440 on 12-05-18.
 */

public class CorsiMainFragment extends Fragment{
    private CorsiMainFragmentListener mCallback;
    public interface CorsiMainFragmentListener{

        void backFromPracticeListener(JSONArray results);
        void backFromTestListener(JSONArray results);
        void prepareInTheMiddleFinalization();
        void backToRepeatPractice(JSONArray results);
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

    MediaPlayer mediaPlayer;
    Csequence[] csequences;
    HashMap<Integer,Integer> corrects_by_index=new HashMap<>();
    HashMap<Integer,Integer> incorrects_by_index=new HashMap<>();
    ButtonClickProcessor buttonClickProcessor;
    int current_sequence_index=0;
    int current_index_in_sequence=0;
    View inflatedView;

    private final static int PAINT_BUTTON = 0;
    private final static int REMOVE_BUTTON_PAINT = 1;
    private final static int DISABLE_BUTTON=2;
    private final static int ENABLE_BUTTON=3;
    private HashMap<Integer,Integer> ButtonIdByIndex=new HashMap<>();
    private HashMap<Integer,Long> timeCompletionByIndex=new HashMap<>();
    private HashMap<Integer,Integer> button_index_by_button_id=new HashMap<>();
    private HashMap<Integer,String> response_string_by_cequence_server_id=new HashMap<>(); //{1:'1-2-4-2'}
    private ArrayList<Integer> csquares=new ArrayList<>();
    int mode;
    long last_time;
    DatabaseManager databaseManager;
    private NextSequenceDisplayer dns;
    int score;
    int total_incorrects=0;
    JSONArray results;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        databaseManager=DatabaseManager.getInstance(getContext());
        mode = getArguments().getInt("mode");

        //Initialize buttons Hash, should be done in background

        results=new JSONArray();
        inflatedView = inflater.inflate(R.layout.corsi_main_fragment, container, false);
        dns=new NextSequenceDisplayer();
        new DataLoader().execute();
        mediaPlayer=MediaPlayer.create(getContext(),R.raw.go);
        mediaPlayer=MediaPlayer.create(getContext(),R.raw.go);

        return inflatedView;
    }

    public class DataLoader extends AsyncTask<Integer,Void,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {

            if (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE){

                int ordered_int=(mode==CorsiActivity.ORDERED_PRACTICE)?1:0;
                boolean ordered_bool=(mode==CorsiActivity.ORDERED_PRACTICE)?true:false;
                csequences=databaseManager.testDatabase.daoAccess().fetchPracticeCsequences(ordered_int);
            }

            else if(mode==CorsiActivity.ORDERED_TEST){


                csequences=databaseManager.testDatabase.daoAccess().fetchOrderedCsequences();

            }

            else if(mode==CorsiActivity.REVERSED_TEST){



                csequences=databaseManager.testDatabase.daoAccess().fetchReversedCsequences();


            }
            ButtonIdByIndex.put(1,R.id.corsiButton1);
            button_index_by_button_id.put(R.id.corsiButton1,1);
            ButtonIdByIndex.put(2,R.id.corsiButton2);
            button_index_by_button_id.put(R.id.corsiButton2,2);

            ButtonIdByIndex.put(3,R.id.corsiButton3);
            button_index_by_button_id.put(R.id.corsiButton3,3);

            ButtonIdByIndex.put(4,R.id.corsiButton4);
            button_index_by_button_id.put(R.id.corsiButton4,4);

            ButtonIdByIndex.put(5,R.id.corsiButton5);
            button_index_by_button_id.put(R.id.corsiButton5,5);

            ButtonIdByIndex.put(6,R.id.corsiButton6);
            button_index_by_button_id.put(R.id.corsiButton6,6);

            ButtonIdByIndex.put(7,R.id.corsiButton7);
            button_index_by_button_id.put(R.id.corsiButton7,7);

            ButtonIdByIndex.put(8,R.id.corsiButton8);
            button_index_by_button_id.put(R.id.corsiButton8,8);

            ButtonIdByIndex.put(9,R.id.corsiButton9);
            button_index_by_button_id.put(R.id.corsiButton9,9);

            ButtonIdByIndex.put(10,R.id.corsiButton10);
            button_index_by_button_id.put(R.id.corsiButton10,10);

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


            for (int id:button_index_by_button_id.keySet()
                    ) {

                Button b=inflatedView.findViewById(id);
                b.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             int pressed_button_index=button_index_by_button_id.get(view.getId());
                                             int current_csequence_server_id=csequences[current_sequence_index].getServer_id();

                                             if (response_string_by_cequence_server_id.containsKey(current_csequence_server_id)){

                                                response_string_by_cequence_server_id.put(current_csequence_server_id,response_string_by_cequence_server_id.get(current_csequence_server_id)+"-"+pressed_button_index);
                                             }
                                             else{
                                                 String response_string=""+pressed_button_index;
                                                 response_string_by_cequence_server_id.put(current_csequence_server_id,response_string);
                                             }
                                             buttonClickProcessor=new ButtonClickProcessor();
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
            int button_index=button_index_by_button_id.get(button_id);
            boolean correct=false;
            int csquares_lenght=csquares.size();
            if(mode==CorsiActivity.ORDERED_TEST || mode==CorsiActivity.ORDERED_PRACTICE) {
                if (button_index == getNumberSquareAtPosition(current_index_in_sequence)) {
                    corrects_by_index.put(current_sequence_index, corrects_by_index.get(current_sequence_index) + 1);
                    correct=true;

                    //int pot=csquares_lenght-current_index_in_sequence-1;
          //        score+=Math.pow(2,pot);
                    Log.d("myTag", "CORRECT");
                } else {

                    incorrects_by_index.put(current_sequence_index, incorrects_by_index.get(current_sequence_index) + 1);
                    score=0;
                }
            }

            else if(mode==CorsiActivity.REVERSED_TEST || mode==CorsiActivity.REVERSED_PRACTICE){

                if (button_index == getNumberAtPositionInStrRevSeq(current_index_in_sequence)) {
                    corrects_by_index.put(current_sequence_index, corrects_by_index.get(current_sequence_index) + 1);
                    correct=true;
                    //int pot=csquares_lenght-current_index_in_sequence-1;
                    //score+=Math.pow(2,pot);
                    Log.d("myTag", "CORRECT");
                }
                 else {
                    incorrects_by_index.put(current_sequence_index, incorrects_by_index.get(current_sequence_index) + 1);
                    score=0;
                }


            }


            sendMessageToHandler(button_id,0);
            current_index_in_sequence++;


            if (current_index_in_sequence==csquares.size()){


                //get the delta time of the sequence
                long current_time=System.nanoTime();
                float delta_time=(((current_time-last_time)/(float)1000000)/1000);
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("time",delta_time);
                    jsonObject.put("csequence_id", current_csequence.getServer_id());
                    jsonObject.put("score", score);
                    jsonObject.put("response_string",response_string_by_cequence_server_id.get(current_csequence.getServer_id()));
                    results.put(jsonObject);
                }

                catch (JSONException e){
                    e.printStackTrace();
                }

                //check if the test must be terminated caused two wrong sequences
                if(score==0){
                    total_incorrects++;


                }

                if(total_incorrects==2 && !isInPracticeMode()){

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Test finalizado debido a 2 errores",Toast.LENGTH_LONG).show();

                        }
                    });

                    finish();
                }
                else {
                    current_sequence_index++;
                    dns = new NextSequenceDisplayer();
                    dns.execute();
                }
            }


            return null;
        }
    }

    Integer getNumberSquareAtPosition(int position){
        if(position>= csquares.size()){
            return null;
        }
        int csquare=csquares.get(position);


        return csquare;

    }

    Integer getNumberAtPositionInStrRevSeq(int position){
        int csquare=csquares.get(csquares.size()-1-position);
        return csquare;

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


    void finish(){
        if (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE){
            if(total_incorrects>0){
                mCallback.backToRepeatPractice(results);

            }
            else{

                mCallback.backFromPracticeListener(results);
            }

        }

        else if (mode==CorsiActivity.ORDERED_TEST || mode==CorsiActivity.REVERSED_TEST){
            //TODO: save result and send integer to activity
            mCallback.backFromTestListener(results);
        }
    }
    public class NextSequenceDisplayer extends AsyncTask<Integer,Void,Integer>{



        @Override
        protected Integer doInBackground(Integer... integers) {

            //disable buttons

            for (int id:button_index_by_button_id.keySet()
                 ) {

                button_enable_attr(DISABLE_BUTTON,id,0);
            }

            try{Thread.sleep(1250);}
            catch (InterruptedException e){
                e.printStackTrace();
            }


            //
            if(current_sequence_index==csequences.length){
                //save and finnish

                finish();

                return null;

            }


            current_index_in_sequence=0;

            score=1;
            Csequence current_csequence=csequences[current_sequence_index];

            String[] csequence_str_arr=current_csequence.getCsequence().split("-");
            csquares.clear();
            for (String str_square:csequence_str_arr){
                csquares.add(Integer.parseInt(str_square));
            }

            Bundle b;
            int counter=1;
            for (Integer csquare:csquares
                 ) {

                int sequence_instruction=csquare;
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



            for (int id:button_index_by_button_id.keySet()
                    ) {

                button_enable_attr(ENABLE_BUTTON,id,700*counter);
            }


            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.start();
                    last_time=System.nanoTime();
                }
            },700*counter);



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

    private boolean isInPracticeMode(){
     return (mode==CorsiActivity.ORDERED_PRACTICE || mode==CorsiActivity.REVERSED_PRACTICE);
    }

    @Override
    public void onDestroy() {

        if(dns!=null && dns.getStatus()!=AsyncTask.Status.FINISHED){
            dns.cancel(true);
        }
        if(buttonClickProcessor!=null && buttonClickProcessor.getStatus()!=AsyncTask.Status.FINISHED){
            buttonClickProcessor.cancel(true);
        }
        super.onDestroy();
    }
}
