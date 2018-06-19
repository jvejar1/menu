package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WallyActivity extends AppCompatActivity implements InstructionFragment.backFromInstructionListener,DisplaySituationFragment.ReturnToWallyTestListener,MainFragmentListener{

    @Override
    public void backFromInstruction() {
        startWsituation();
    }

    @Override
    public void backFromTest(final JSONObject jsonObject) {
        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                WSituation wSituation=databaseManager.testDatabase.daoAccess().fetchWSituationById(wsituation_ids[current_wsituation_id_index]);
                try {
                    jsonObject.putOpt("wsituation_id", wSituation.getServer_id());
                    responses.put(jsonObject);

                }
                catch (JSONException e){

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                current_wsituation_id_index++;

                displaySituationDescription();

            }
        }.execute();


    }

    @Override
    public void backFromPractice() {

    }

    public void displaySituationDescription() {

        if (wsituation_ids.length==current_wsituation_id_index){


            JSONObject payload=new JSONObject();
            try {
                payload.putOpt("test_id", wally.getServer_id());
                payload.putOpt("responses",responses);
                databaseManager.insertRequest(payload,student_server_id,"wally",0);
            }

            catch (JSONException e){

                e.printStackTrace();
            }

            finish();

            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DisplaySituationFragment dsf =new DisplaySituationFragment();
        Bundle args = new Bundle();

        args.putLong("wsituation_id",wsituation_ids[current_wsituation_id_index]);
        dsf.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_place,dsf);
        fragmentTransaction.commit();

    }

    @Override
    public void returnToWally() {
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        WallyMainFragment wallyMainFragment = new WallyMainFragment();
        Bundle b=new Bundle();
        b.putLong("wsituation_id",wsituation_ids[current_wsituation_id_index]);
        wallyMainFragment.setArguments(b);
        fragmentTransaction.replace(R.id.fragment_place,wallyMainFragment);
        fragmentTransaction.commit();
    }
    FragmentManager fragmentManager;

    Wally wally;
    long[] wsituation_ids;
    int current_wsituation_id_index =0;
    JSONArray responses;
    DatabaseManager databaseManager;
    int student_server_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager= DatabaseManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_wally);
        Bundle extras=getIntent().getExtras();
        student_server_id=extras.getInt(Student.EXTRA_STUDENT_SERVER_ID);
        fragmentManager=getFragmentManager();
        responses=new JSONArray();
        DataLoader dl = new DataLoader();
        dl.execute();
    }

    void startWsituation(){
        displaySituationDescription();

    }


    public class DataLoader extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            wsituation_ids=databaseManager.testDatabase.daoAccess().fetchWsituationsIds();
            wally=databaseManager.testDatabase.daoAccess().fetchFirstWally();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(wally==null || wsituation_ids.length==0){

                Toast.makeText(getApplicationContext(),"No hay datos, intente actualizar",Toast.LENGTH_SHORT).show();
                finish();
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            InstructionFragment corsiInstructionFragment= new InstructionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("text","“Ahora vamos a hacer un juego donde yo te cuento algunas historias sobre unos niños de tu edad. Voy a utilizar unas tarjetas con imágenes para ayudar a contarte mi historia. Puedes usar las tarjetas para decirme lo que piensas de la historia”. ¿Lo has entendido?... (espere la respuesta del niño) Comencemos..");

            corsiInstructionFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
            fragmentTransaction.commit();
        }
    }
}
