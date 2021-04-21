package com.example.e440.menu;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by e440 on 17-05-18.
 */

public class HnfMainFragment extends Fragment implements View.OnClickListener{
    //handlers what


    MainFragmentListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    private final static int DISPLAY_FIGURE=0;
    private final static int REMOVE_FIGURE=1;
    private final static int TIME_EXCEDEED=2;

    private int mode;
    private final static long FIGURE_TIME=1500;
    private final static int BLANK_TIME=700;

    private long start_time;
    private ReentrantLock reentrantLock;
    private HashMap<Integer,Long> answer_time_by_index;
    private HashMap<Integer,Boolean> answered_on_time_by_index;
    private HashMap<Integer,Boolean> correct_by_index=new HashMap<>();
    private DatabaseManager databaseManager;
    private int total_time;
    private int omitted=0;
    private int errors=0;
    private int corrects=0;
    long initial_time;
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if (what==DISPLAY_FIGURE){
                Bundle bundle=msg.getData();
                int position=bundle.getInt("position");
                int figure=bundle.getInt("figure");
                int bitmap_resource_id=(position==HnfFigure.LEFT && figure==HnfFigure.HEART)?R.drawable.hnf_hl
                        :(position==HnfFigure.RIGHT && figure==HnfFigure.HEART)?R.drawable.hnf_hr
                        :(position==HnfFigure.LEFT && figure==HnfFigure.FLOWER)?R.drawable.hnf_fl
                        :R.drawable.hnf_fr;
                ImageView hnf_image_view=inflatedView.findViewById(R.id.hnfImageView);
                hnf_image_view.setImageResource(bitmap_resource_id);
                start_time=System.currentTimeMillis();
            }else if (what==REMOVE_FIGURE){
                ImageView hnf_image_view=inflatedView.findViewById(R.id.hnfImageView);
                hnf_image_view.setImageResource(R.drawable.hnf_fix);
                //TODO: display next figure

            }

            else if(what==TIME_EXCEDEED){

                reentrantLock.lock();
                try{
                    if(!correct_by_index.containsKey(current_hf_index)){


                        correct_by_index.put(current_hf_index,false);
                        omitted++;
                        answered_on_time_by_index.put(current_hf_index,false);
                        answer_time_by_index.put(current_hf_index,FIGURE_TIME);
                        current_hf_index++;
                        display_hnfFigure();


                    }
                }
                finally {
                    reentrantLock.unlock();
                }
            }

        }
    };
    View inflatedView;
    HnfFigure[] hnfFigure_array;
    HnfTest hnfTest;
    int current_hf_index;
    Button left_button;
    Button right_button;
    int score=0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.hnf_main_fragment, container, false);
        mode=getArguments().getInt("mode",-1);
        current_hf_index=0;
        left_button=inflatedView.findViewById(R.id.hnfLeftButton);
        right_button=inflatedView.findViewById(R.id.hnfRightButton);
        left_button.setOnClickListener(this);
        right_button.setOnClickListener(this);


        DataLoader dataLoader=new DataLoader();
        dataLoader.execute();

        return inflatedView;
    }



    class DataLoader extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(isInTestMode()){
                answer_time_by_index = new HashMap<>();
                reentrantLock=new ReentrantLock();
                answered_on_time_by_index=new HashMap<>();
                databaseManager= DatabaseManager.getInstance(getContext());
            }
            if(mode==HnfTest.HEARTS_PRACTICE_MODE){
                hnfFigure_array=new HnfFigure[6];
                hnfFigure_array[0]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.RIGHT,0);
                hnfFigure_array[1]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.LEFT,1);
                hnfFigure_array[2]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.RIGHT,2);
                hnfFigure_array[3]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.RIGHT,3);
                hnfFigure_array[4]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.LEFT,4);
                hnfFigure_array[5]=new HnfFigure(HnfFigure.HEART,0,0,HnfFigure.LEFT,5);
            }

            else if(mode==HnfTest.FLOWERS_PRACTICE_MODE){
                hnfFigure_array=new HnfFigure[6];
                hnfFigure_array[0]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.RIGHT,0);
                hnfFigure_array[1]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.LEFT,1);
                hnfFigure_array[2]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.RIGHT,2);
                hnfFigure_array[3]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.LEFT,3);
                hnfFigure_array[4]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.RIGHT,4);
                hnfFigure_array[5]=new HnfFigure(HnfFigure.FLOWER,0,0,HnfFigure.RIGHT,5);
            }


            else if (mode==HnfTest.HEARTS_TEST_MODE || mode==HnfTest.FLOWERS_TEST_MODE ||mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){
                int test_type=(mode==HnfTest.HEARTS_TEST_MODE )?HnfTest.HEARTS_TEST_TYPE:(mode==HnfTest.FLOWERS_TEST_MODE)?HnfTest.FLOWERS_TEST_TYPE:
                        HnfTest.HEARTS_AND_FLOWERS_TEST_TYPE;
                hnfTest=databaseManager.testDatabase.daoAccess().fetchHnfTestByType(test_type);
                hnfFigure_array=databaseManager.testDatabase.daoAccess().fetchAllHnfFiguresByHnfTestId(hnfTest.getId());

            }
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initial_time=System.nanoTime();
            display_hnfFigure();

        }
    }

    public void onClick(View v){ processButtonClick(v.getId());}

    void processButtonClick(int button_id){
        if(current_hf_index>=hnfFigure_array.length){
            return;
        }
        int button_position=(button_id==R.id.hnfLeftButton)?HnfFigure.LEFT:HnfFigure.RIGHT;
        HnfFigure current_hf_figure=hnfFigure_array[current_hf_index];
        boolean correct=(current_hf_figure.getFigure()==HnfFigure.HEART &&
                button_position==current_hf_figure.getPosition()) ||
                (current_hf_figure.getFigure()==HnfFigure.FLOWER && button_position!=current_hf_figure.getPosition());

        if(isInTestMode()){


            reentrantLock.lock();
            try{
                if(!correct_by_index.containsKey(current_hf_index)){
                    uiHandler.removeMessages(TIME_EXCEDEED);
                    correct_by_index.put(current_hf_index,correct);
                    answered_on_time_by_index.put(current_hf_index,true);
                                        answer_time_by_index.put(current_hf_index, (System.currentTimeMillis() - start_time));
                    current_hf_index++;
                


                    if(correct) {
                        score++;

                    }else {
                      errors++;
                    }
                    display_hnfFigure();
                }
            }
            finally {
                reentrantLock.unlock();
            }


        }

        else if(!isInTestMode() && correct){

            current_hf_index++;
            display_hnfFigure();
        }

    }



    void display_hnfFigure(){

        uiHandler.sendEmptyMessage(REMOVE_FIGURE);
        if(current_hf_index==hnfFigure_array.length){


            //TODO: Finish fragment

            if(isInTestMode()){
                long current_time=System.nanoTime();
                float delta_time=(((current_time-initial_time)/(float)1000000)/1000);

                JSONArray results=new JSONArray();
                int index=0;
                /*for (HnfFigure figure:hnfFigure_array
                     ) {

                    JsonObject result=new JsonObject();
                    result.addProperty("id",figure.getServer_id());
                    result.addProperty("correct",correct_by_index.get(index));
                    result.addProperty("completion_time", answer_time_by_index.get(index));
                    result.addProperty("answered_on_time",answered_on_time_by_index.get(index));
                    results.put(result);
                    index++;
                }*/
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("score", score);
                    jsonObject.put("omitted",omitted);
                    jsonObject.put("errors",errors);
                    jsonObject.put("delta_time",delta_time);
                    jsonObject.put("test_id",hnfTest.getServer_id());
                }

                catch (JSONException e ){
                    e.printStackTrace();
                }

                mCallback.backFromTest(jsonObject);
            }

            else{
                mCallback.backFromPractice();
            }



            return;
        }

        if(isInTestMode()){

            answer_time_by_index.put(current_hf_index,null);

        }
        HnfFigure current_hf=hnfFigure_array[current_hf_index];
        Message msg=new Message();
        Bundle bundle=new Bundle();
        bundle.putInt("position",current_hf.getPosition());
        bundle.putInt("figure",current_hf.getFigure());
        msg.what=DISPLAY_FIGURE;
        msg.setData(bundle);
        uiHandler.sendMessageDelayed(msg,BLANK_TIME);
        left_button.setEnabled(false);
        right_button.setEnabled(false);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                left_button.setEnabled(true);
                right_button.setEnabled(true);
            }
        },BLANK_TIME);
        if(isInTestMode()){
            uiHandler.sendEmptyMessageDelayed(TIME_EXCEDEED,FIGURE_TIME+BLANK_TIME);

        }


    }

    boolean isInTestMode(){
        return (mode==HnfTest.HEARTS_TEST_MODE || mode==HnfTest.FLOWERS_TEST_MODE || mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE);
    }





}
