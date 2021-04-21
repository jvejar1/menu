package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WallyActivity extends BaseActivity implements InstructionFragment.backFromInstructionListener,DisplaySituationFragment.ReturnToWallyTestListener{

    @Override
    public void backFromInstruction() {
        current_wsituation_id_index++;
        going_forward=true;
        startWsituation();
    }

    @Override
    public void goBackFromSituationDisplaying() {
        current_wsituation_id_index--;
        going_forward=false;
        if(current_wsituation_id_index<0){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            InstructionFragment corsiInstructionFragment= new InstructionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("text","“Ahora vamos a hacer un juego donde yo te cuento algunas historias sobre unos niños de tu edad. Voy a utilizar unas imágenes para ayudar a contarte mi historia. Luego aparecerán otras imágenes para que me digas lo que piensas de la historias. ¿Lo has entendido?... (espere la respuesta del niño) Comencemos..");

            corsiInstructionFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
            fragmentTransaction.commit();
            return;
            //

        }
        else {
            goToWally();
        }
    }

    @Override
    public void goBackFromMainFragment() {
        displaySituationDescription();
    }

    @Override
    public void backFromTest(final JSONObject jsonObject) {

        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                WSituation wSituation=databaseManager.testDatabase.daoAccess().fetchWSituationById(wsituation_ids[current_wsituation_id_index]);
                try {
                    responses.put(Integer.toString(wSituation.getServer_id()),jsonObject);

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


        if(current_wsituation_id_index==-1){

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            InstructionFragment corsiInstructionFragment= new InstructionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("text","“Ahora vamos a hacer un juego donde yo te cuento algunas historias sobre unos niños de tu edad. Voy a utilizar unas imágenes para ayudar a contarte mi historia. Luego aparecerán otras imágenes para que me digas lo que piensas de la historias. ¿Lo has entendido?... (espere la respuesta del niño) Comencemos..");

            corsiInstructionFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
            fragmentTransaction.commit();
            return;
            //
        }
        else if (wsituation_ids.length==current_wsituation_id_index){
            JSONObject payload=new JSONObject();
            try {
                payload.putOpt("test_id", wally.getServer_id());
                payload.putOpt("responses",responses);
                this.insertRequest(payload,"wally",0);
            }

            catch (JSONException e){

                e.printStackTrace();
            }

            finish();

            return;
        }
        else{


            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DisplaySituationFragment dsf =new DisplaySituationFragment();
            Bundle args = new Bundle();
            args.putBoolean("going_forward",going_forward);
            args.putLong("wsituation_id",wsituation_ids[current_wsituation_id_index]);
            dsf.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_place,dsf);
            fragmentTransaction.commit();
        }


    }

    @Override
    public void goToWally() {
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        WallyMainFragment wallyMainFragment = new WallyMainFragment();
        Bundle bundle=new Bundle();
        long wsituation_id=wsituation_ids[current_wsituation_id_index];
        bundle.putLong("wsituation_id",wsituation_id);
        //check if already has been answered
        JSONObject wsituation_responses=responses.optJSONObject(Integer.toString(wsituation_server_id_by_wsitation_id.get(wsituation_id)));
        if(wsituation_responses!=null){
            int wfeeling_response=wsituation_responses.optInt(WSituation.WFEELING_RESPONSE_EXTRA);
            bundle.putInt(WSituation.WFEELING_RESPONSE_EXTRA,wfeeling_response);
            int wreaction_response=wsituation_responses.optInt(WSituation.WREACTION_RESPONSE_EXTRA);
            bundle.putInt(WSituation.WREACTION_RESPONSE_EXTRA,wreaction_response);
        }
        wallyMainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,wallyMainFragment);
        fragmentTransaction.commit();
    }
    FragmentManager fragmentManager;

    HashMap<Long,Integer> wsituation_server_id_by_wsitation_id=new HashMap<>();
    Wally wally;
    long[] wsituation_ids;
    int current_wsituation_id_index =-1;
    JSONObject responses;
    DatabaseManager databaseManager;
    boolean going_forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager= DatabaseManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_wally);


        TextView studentInfo = findViewById(R.id.studentInfoTextView);
        studentInfo.setText(studentFullName);

        fragmentManager=getFragmentManager();
        responses=new JSONObject();
        DataLoader dl = new DataLoader();
        dl.execute();
        DisplayMetrics dm = this.getApplicationContext()
                .getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels / dm.xdpi;
        float screenHeight = dm.heightPixels / dm.ydpi;
        Configuration config = getResources().getConfiguration();
        int sw = config.smallestScreenWidthDp;
        ;
    }


    void startWsituation(){
        displaySituationDescription();
    }


    public class DataLoader extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            wsituation_ids=databaseManager.testDatabase.daoAccess().fetchWsituationsIds();
            for(Long wsituation_id:wsituation_ids){
                int wsituation_server_id=databaseManager.testDatabase.daoAccess().fetchWsituationServerIdByWsituationId(wsituation_id);
                wsituation_server_id_by_wsitation_id.put(wsituation_id,wsituation_server_id);
            }


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
            displaySituationDescription();
        }
    }
}
