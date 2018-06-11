package com.example.e440.menu;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HnfActivity extends AppCompatActivity implements MainFragmentListener,InstructionFragment.backFromInstructionListener {

    @Override
    public void backFromInstruction() {
        startMainFragment();
        return;
    }
    @Override
    public void backFromPractice() {
        current_mode+=1;
        startInstruction();
    }
    @Override
    public void backFromTest(JSONObject jo) {
        //TODO:PROCESS JSON_OBJECT
        int score=jo.optInt("score");
        if (current_mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){

            try{
                result.put("hnf_score",score);
                result.put("test_name","hnf");
                result.put("test_id",hnfSet.getServer_id());
            }

            catch (JSONException e){
                e.printStackTrace();
            }



            AsyncTask asyncTask=new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    ResponseRequest responseRequest=new ResponseRequest(result.toString(),"hnf");
                    DatabaseManager.getInstance(getApplicationContext()).testDatabase.daoAccess().insertResponseRequest(responseRequest);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    finish();
                }
            }.execute();

        }

        else {

            try{
                if(current_mode==HnfTest.HEARTS_TEST_MODE){

                    result.put("hearts_score",score);

                }
                else{

                    result.put("flowers_score",score);
                }
            }

            catch (JSONException e){
                e.printStackTrace();
            }
            current_mode += 1;
            startInstruction();
        }
    }
    JSONObject result;
    private int current_mode;
    private HnfSet hnfSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hnf);
        current_mode=0;

        result=new JSONObject();
        AsyncTask asyncTask =new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                hnfSet= DatabaseManager.getInstance(getApplicationContext()).testDatabase.daoAccess().fetchHnfSet();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                startInstruction();
                super.onPostExecute(o);
            }
        }.execute();





        }
    void startMainFragment(){
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        HnfMainFragment hnfMainFragment =new HnfMainFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("mode",current_mode);
        hnfMainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,hnfMainFragment);
        fragmentTransaction.commit();


    }

    void startInstruction(){
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        InstructionFragment instructionFragment=new InstructionFragment();
        Bundle bundle=new Bundle();
        String text="";
        if(current_mode==HnfTest.HEARTS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de HEARTS";
        }

        else if (current_mode==HnfTest.FLOWERS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de FLOWERS";
        }
        else if(current_mode==HnfTest.HEARTS_AND_FLOWERS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de HEARTS and FLOWERS";
        }
        else if(current_mode==HnfTest.HEARTS_TEST_MODE || current_mode==HnfTest.FLOWERS_TEST_MODE || current_mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){
            text="Ahora iniciaras la prueba real pero con tiempo, presiona comenzar para continuar";
        }
        bundle.putString("text",text);
        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();


    }




}
