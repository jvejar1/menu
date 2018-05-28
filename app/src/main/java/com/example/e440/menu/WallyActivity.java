package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WallyActivity extends AppCompatActivity implements DisplaySituationFragment.ReturnToWallyTestListener,MainFragmentListener{

    @Override
    public void backFromTest(Bundle b) {
        current_wsituation_id_index++;
        displaySituationDescription();
    }

    @Override
    public void backFromPractice() {

    }

    public void displaySituationDescription() {

        if (wsituation_ids.length==current_wsituation_id_index){

            //TODO: save result and finish
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

    long[] wsituation_ids;
    int current_wsituation_id_index =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wally);
        fragmentManager=getFragmentManager();

        DataLoader dl = new DataLoader();
        dl.execute();
    }

    void startWsituation(){
        displaySituationDescription();

    }


    public class DataLoader extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            wsituation_ids=DatabaseManager.getInstance(getApplicationContext()).testDatabase.daoAccess().fetchWsituationsIds();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startWsituation();
        }
    }
}
