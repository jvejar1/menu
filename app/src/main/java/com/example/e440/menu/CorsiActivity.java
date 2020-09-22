package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CorsiActivity extends BaseActivity implements InstructionFragment.backFromInstructionListener, CorsiMainFragment.CorsiMainFragmentListener {

    final static int ORDERED_PRACTICE = 0;
    final static int ORDERED_TEST = 1;
    final static int REVERSED_PRACTICE = 2;
    final static int REVERSED_TEST = 3;

    public int getNext_instruction() {
        return next_instruction;
    }

    public void setNext_instruction(int next_instruction) {
        this.next_instruction = next_instruction;
    }

    @Override
    public void backToRepeatPractice(JSONArray results) {
        if(next_instruction==ORDERED_PRACTICE){
            if(ordered_practice_tries==3){
                this.backFromPracticeListener(results);
            }

        }
        else{

            if(reversed_practice_tries==3){
                this.backFromPracticeListener(results);
            }
        }
        startInstructions(next_instruction);
    }

    @Override
    public void prepareInTheMiddleFinalization() {
        finished_in_the_middle=true;
    }

    int next_instruction=ORDERED_PRACTICE;

    @Override
    public void backFromPracticeListener(JSONArray results) {
        backFromTestListener(results);
    }


    @Override
    public void backFromTestListener(JSONArray jsonArray) {
        for(int i=0;i<jsonArray.length();i++){
            JSONObject response=jsonArray.optJSONObject(i);
            int score=response.optInt("score");
            if(next_instruction==REVERSED_TEST){
                reversed_score+=score;
            }
            else if(next_instruction==ORDERED_TEST){
                ordered_score+=score;
            }

            results.put(jsonArray.opt(i));
        }

        if(next_instruction==REVERSED_TEST || finished_in_the_middle){

            AsyncTask asyncTask =new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    DatabaseManager databaseManager= DatabaseManager.getInstance(getApplicationContext());
                    corsi= databaseManager.testDatabase.daoAccess().fetchCorsi();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    JSONObject payload=new JSONObject();
                    try{
                        payload.put("ordered_practice_tries",ordered_practice_tries);
                        payload.put("reversed_practice_tries",reversed_practice_tries);
                        payload.put("ordered_score", ordered_score);
                    payload.put("reversed_score", reversed_score);
                    payload.put("responses", results);
                    payload.put("test_id",corsi.getServer_id());}
                    catch(JSONException e ){
                        e.printStackTrace();
                    }
                    insertRequest(payload,"corsi",0);
                    super.onPostExecute(o);
                    finish();
                }
            }.execute();


        }
        else {
            next_instruction++;
            startInstructions(next_instruction);
        }
    }





    @Override
    public void backFromInstruction () {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CorsiMainFragment corsiMainFragment= new CorsiMainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode",next_instruction);
        corsiMainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,corsiMainFragment);
        fragmentTransaction.commit();

    }

    FragmentManager fragmentManager;
    JSONArray results;
    int ordered_score=0;
    int reversed_score=0;
    int ordered_practice_tries=0;
    int reversed_practice_tries=0;
    boolean finished_in_the_middle=false;
    Corsi corsi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corsi);


        TextView studentInfo = findViewById(R.id.studentInfoTextView);
        studentInfo.setText(studentFullName);

        fragmentManager = getFragmentManager();
        results=new JSONArray();
        startInstructions(next_instruction);

    }

    void startInstructions(int mode){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InstructionFragment corsiInstructionFragment= new InstructionFragment();
        Bundle bundle = new Bundle();
        if (next_instruction==ORDERED_PRACTICE){
            if(ordered_practice_tries==0){
                bundle.putString("text","“A continuación van a aparecer 10 cuadrados en la pantalla. De todos ellos, algunos se van a encender en un determinado orden. Una vez que escuches la palabra “Ahora”, tú tendrás que tocar los cuadrados en el mismo orden en que se encendieron. Los dos primeros son de práctica y a medida que avance el juego se encenderán más cuadrados. ¿Lo has entendido? (espere la respuesta del niño). Comencemos”");

            }
            else{
                bundle.putString("text","Vamos a intentarlo una vez más.... Comencemos.");

            }
            ordered_practice_tries++;
        }
        else if (next_instruction==ORDERED_TEST || next_instruction==REVERSED_TEST){
            bundle.putString("text","Recuerda, ese fue el ensayo, ¿Lo has entendido?... (espere la respuesta del niño) ... Comencemos.");

        }

        else{

            if(reversed_practice_tries==0) {
                bundle.putString("text", "“A continuación van a aparecer 10 cuadrados en la pantalla. De todos ellos, algunos se van a encender en un determinado orden. Una vez que escuches la palabra “Ahora”, tú tendrás que tocar los cuadrados en el orden contrario en que se encendieron.¿Lo has entendido? (espere la respuesta del niño). Comencemos”");
            }

            else{
                bundle.putString("text","Vamos a intentarlo una vez más.... Comencemos.");


            }
            reversed_practice_tries++;
        }


        corsiInstructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
        fragmentTransaction.commit();

    }



}
