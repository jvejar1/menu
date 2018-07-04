package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CorsiActivity extends BaseActivity implements InstructionFragment.backFromInstructionListener, CorsiMainFragment.CorsiMainFragmentListener{

    final static int ORDERED_PRACTICE=0;
    final static int ORDERED_TEST=1;
    final static int REVERSED_PRACTICE=2;
    final static int REVERSED_TEST=3;

    public int getNext_instruction() {
        return next_instruction;
    }

    public void setNext_instruction(int next_instruction) {
        this.next_instruction = next_instruction;
    }

    int next_instruction=ORDERED_PRACTICE;

    @Override
    public void backFromPracticeListener() {
        next_instruction++;
        startInstructions(next_instruction);
    }

    @Override
    public void backFromTestListener(JSONArray jsonArray) {

        for(int i=0;i<jsonArray.length();i++){
            JSONObject response=jsonArray.optJSONObject(i);
            int score=response.optInt("score");
            if(next_instruction==REVERSED_TEST){

                reversed_score+=score;
            }
            else{

                ordered_score+=score;
            }

            results.put(jsonArray.opt(i));
        }

        if(next_instruction==REVERSED_TEST){

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
                    payload.put("ordered_score", ordered_score);
                    payload.put("reversed_score", reversed_score);
                    payload.put("responses", results);
                    payload.put("test_id",corsi.getServer_id());}
                    catch(JSONException e ){
                        e.printStackTrace();
                    }
                    DatabaseManager.getInstance(getApplicationContext()).insertRequest(payload,student_server_id,"corsi",0);
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
    int student_server_id;
    Corsi corsi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corsi);
        Bundle b=getIntent().getExtras();
        student_server_id=b.getInt(Student.EXTRA_STUDENT_SERVER_ID);
        fragmentManager = getFragmentManager();
        results=new JSONArray();
        startInstructions(next_instruction);

    }

    void startInstructions(int mode){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InstructionFragment corsiInstructionFragment= new InstructionFragment();
        Bundle bundle = new Bundle();
        if (next_instruction==ORDERED_PRACTICE){

            bundle.putString("text","A continuación van a aparecer un conjunto de cuadrados en la pantalla. De todos ellos, algunos se van a encender en un determinado orden. Tu tarea consiste en recordar el orden en que se activaron. \n\n\tLos dos primeros son de práctica. ¿Lo has entendido?... (espere la respuesta del niño).... Comencemos.");
        }
        else if (next_instruction==ORDERED_TEST || next_instruction==REVERSED_TEST){
            bundle.putString("text","Recuerda, ese fue el ensayo, ¿Lo has entendido?... (espere la respuesta del niño) ... Comencemos.");

        }

        else{

            bundle.putString("text","A continuación van a aparecer un conjunto de cuadrados en la pantalla. De todos ellos, algunos se van a encender en un determinado orden. Tu tarea consiste en señalar el ORDEN CONTRARIO al que se activaron. \n\n\tLos dos primeros son de práctica. ¿Lo has entendido?... (espere la respuesta del niño) ... Comencemos.");

        }


        corsiInstructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
        fragmentTransaction.commit();

    }



}
