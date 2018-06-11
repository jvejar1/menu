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

public class WallyActivity extends AppCompatActivity implements DisplaySituationFragment.ReturnToWallyTestListener,MainFragmentListener{

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

            //TODO: save result and finish
                AsyncTask asyncTask=new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        JSONObject payload=new JSONObject();
                        try {
                        payload.putOpt("test_name", "wally");
                        payload.putOpt("test_id", wally.getServer_id());
                        payload.putOpt("response",responses);
                        ResponseRequest responseRequest=new ResponseRequest(payload.toString(),"wally");
                        databaseManager.testDatabase.daoAccess().insertResponseRequest(responseRequest);
                        ResponseRequest[] responseRequests=databaseManager.testDatabase.daoAccess().fetchAllResponseRequest();
                        }

                        catch (JSONException e){

                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {

                        super.onPostExecute(o);

                        finish();
                    }
                }.execute();


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager= DatabaseManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_wally);
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
            startWsituation();
        }
    }
}
