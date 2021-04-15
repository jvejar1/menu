package com.example.e440.menu.wally_original;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Database;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.CredentialsManager;
import com.example.e440.menu.DatabaseManager;
import com.example.e440.menu.EXTRA;
import com.example.e440.menu.NetworkManager;
import com.example.e440.menu.R;
import com.example.e440.menu.ResponseRequest;
import com.example.e440.menu.ResultSendJobService;
import com.example.e440.menu.ResultSendService;
import com.example.e440.menu.ResultsSenderListener;
import com.example.e440.menu.Student;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class WallyOriginalActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener{

    ViewModel model;

    interface SpecialItemIndex {
        int INSTRUCTION_ITEM = -1;
        int ASSENT_ITEM = -2;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wally_original);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        model = new ViewModelProvider(this).get(ViewModel.class);

        if( savedInstanceState != null){

        }else {
            int instrumentIndex = getIntent().getExtras().getInt(EXTRA.EXTRA_INSTRUMENT_INDEX);
            final ItemsBank instrument = InstrumentsManager.getInstance(this).getInstruments().get(instrumentIndex);
            final Long studentId = getIntent().getExtras().getLong(Student.EXTRA_STUDENT_SERVER_ID);
            long userId = CredentialsManager.getInstance(this).getUserId();

            Evaluation evaluation = new Evaluation(instrument);
            evaluation.studentId =  studentId;
            evaluation.setUserId(userId);

            model.setEvaluation(evaluation, instrument.instruction);

            ItemFragment itemFragment = ItemFragment.newInstance(0,"wadawdawddwa","",false, false);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, itemFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    class SaveAndSender extends AsyncTask{

        DatabaseManager databaseManager;
        Context context;
        ResultSendJobService.ResultsSender resultSender;

        public SaveAndSender(DatabaseManager databaseManager, ResultSendJobService.ResultsSender resultSender, Context context){
            this.context = context;
            this.databaseManager = databaseManager;
            this.resultSender = resultSender;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            databaseManager.insertResponseRequestAsync(model.getEvaluation());
            resultSender.execute();
            return null;
        }

    }

    @Override
    public void OnFinishActivityRequest() {

        model.setEvaluationFinished();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Guardando Datos");
        progressDialog.setMessage("Por favor espere...");
        progressDialog.show();

        final DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        ResultSendJobService.ResultsSender resultsSender=new ResultSendJobService.ResultsSender(this, new ResultsSenderListener() {
            @Override
            public void OnSendingFinish(int total_sended_count, int errors_count) {
                WallyOriginalActivity.this.finish();
                progressDialog.dismiss();
            }

            @Override
            public void onProgressUpdate(int progress) {

            }
        });

        SaveAndSender saveAndSender = new SaveAndSender(databaseManager, resultsSender , this);
        saveAndSender.execute();
    }


    @Override
    public void onItemAnswered(int itemId, String answer, int answeredItemIndex) {

        if(model.currentItemIsTheLast()){
            OnNavigateToThanksRequest();
            return;
        }

        model.IncrementCurrentItemIndex();
        ItemFragment itemFragment = ItemFragment.newInstance(0,"", "", false, false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, itemFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void OnItemBack() {
        getSupportFragmentManager().popBackStack();
        model.DecrementCurrentItemIndex();
    }

    @Override
    public void OnNavigateToThanksRequest() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ItemFragment itemFragment = ItemFragment.newInstance(0,"", "", true, false);
        fragmentTransaction.replace(R.id.frameLayout, itemFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void OnRequestPopFragment() {

        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        MyBluetoothService.GetInstance(this).finish();
        super.onDestroy();
    }
}
