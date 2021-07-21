package com.example.e440.menu.wally_original;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.CredentialsManager;
import com.example.e440.menu.DatabaseManager;
import com.example.e440.menu.EXTRA;
import com.example.e440.menu.MainActivity;
import com.example.e440.menu.NetworkManager;
import com.example.e440.menu.R;
import com.example.e440.menu.ResponseRequest;
import com.example.e440.menu.ResultSendJobService;
import com.example.e440.menu.Student;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WallyOriginalActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener{

    public static String EXTRA_STUDENT_NAME="student_full_name";
    ViewModel model;
    DatabaseManager databaseManager;
    String studentFullName;
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
        databaseManager = DatabaseManager.getInstance(this);
        if( savedInstanceState != null){

        }else {
            Bundle extras = getIntent().getExtras();
            studentFullName= extras.getString(EXTRA_STUDENT_NAME, "");
            int instrumentIndex = getIntent().getExtras().getInt(EXTRA.EXTRA_INSTRUMENT_INDEX);
            final ItemsBank instrument = InstrumentsManager.getInstance(this).getInstruments().get(instrumentIndex);
            final Long studentId = getIntent().getExtras().getLong(Student.EXTRA_STUDENT_SERVER_ID);
            long userId = CredentialsManager.getInstance(this).getUserId();

            ArrayList<ItemWithAnswer> itemWithAnswers = new ArrayList<>();
            ArrayList<ItemAnswer> answers = new ArrayList<>();
            for (int i=0; i<instrument.items.size(); i++){
                WallyOriginalItem item = instrument.items.get(i);
                ItemAnswer itemAnswer = new ItemAnswer(item);
                answers.add(itemAnswer);
                itemWithAnswers.add(new ItemWithAnswer(item, itemAnswer));
            }
            model.setItemWithAnwers(itemWithAnswers);
            InstrumentsManager instrumentsManager= InstrumentsManager.getInstance(this);
            Evaluation evaluation = new Evaluation(instrument,studentId,userId,answers);
            model.configure(instrumentIndex,studentId,userId, instrumentsManager, evaluation);

            int currentItemIndex = WallyOriginalActivity.SpecialItemIndex.ASSENT_ITEM;
            if (!instrument.items.isEmpty()){
                Integer itemTypeId = instrument.items.get(0).getItemTypeId();
                if (itemTypeId!=null && itemTypeId>=100){
                    currentItemIndex=0;
                }
            }
            model.setCurrentItemIndex(currentItemIndex);

            ResponseRequest responseRequest = new ResponseRequest("",null,false,studentId,false, instrument.id);
            model.responseRequest = responseRequest;
            model.getMustBeginWithTheItems().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean){
                        ItemFragment itemFragment = ItemFragment.newInstance(0,"wadawdawddwa","",false, false);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, itemFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });

            TextView studentFullNameView = findViewById(R.id.studentFullName);
            studentFullNameView.setText(studentFullName);
            //Intent intent = new Intent( WallyOriginalActivity.this, ResultSendJobService.class);
            //startService(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void OnRequestSaveSendAndClose(Evaluation evaluation, Long studentServerId) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Guardando Datos");
        progressDialog.setMessage("Por favor espere...");
        progressDialog.show();

        ResponseRequest responseRequest = model.responseRequest;
        responseRequest.setFinished(true);
        String payload = getJsonPayload(model.getEvaluation());
        responseRequest.setPayload(payload);
        databaseManager.insertOrUpdateResponseRequestAsync(responseRequest, new DatabaseManager.QueryResultListener() {
            @Override
            public void onResult(ResponseRequest responseRequest) {
                final Handler handler = new Handler(getMainLooper());
                databaseManager.insertOrUpdateResponseRequestAsync(responseRequest, new DatabaseManager.QueryResultListener() {
                    @Override
                    public void onResult(final ResponseRequest responseRequest) {
                        Context context=getApplicationContext();
                        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
                        List<JobInfo> pending_jobs=jobScheduler.getAllPendingJobs();
                        boolean already_scheduled=false;
                        for (int i=0;i<pending_jobs.size();i++){
                            JobInfo jobInfo1=pending_jobs.get(i);
                            if(jobInfo1.getId()==1){
                                already_scheduled=true;
                                break;
                            }
                        }
                        if(!already_scheduled) {
                            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context, ResultSendJobService.class));
                            JobInfo jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).setBackoffCriteria(100000, JobInfo.BACKOFF_POLICY_LINEAR).build();
                            jobScheduler.schedule(jobInfo);
                        }
                        progressDialog.dismiss();
                        WallyOriginalActivity.this.finish();
                    }
                }, handler);
            }
        }
        , new Handler(getMainLooper()));
    }

    String getJsonPayload(Evaluation evaluation){
        EvaluationWrapper evaluationWrapper = new EvaluationWrapper(evaluation);
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(evaluationWrapper,evaluationWrapper.getClass());
        return jsonPayload;
    }

    @Override
    public void onItemAnswered(int itemId, String answer, int answeredItemIndex) {

        ResponseRequest responseRequest = model.responseRequest;
        String payload = getJsonPayload(model.getEvaluation());
        responseRequest.setPayload(payload);
        databaseManager.insertOrUpdateResponseRequestAsync(responseRequest, new DatabaseManager.QueryResultListener() {
            @Override
            public void onResult(ResponseRequest responseRequest) {
                ;
            }
        }, new Handler(getMainLooper()));

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
        ResponseRequest responseRequest = model.responseRequest;
        if (!responseRequest.isFinished()){
            responseRequest.setFinished(true);
            String payload = getJsonPayload(model.getEvaluation());
            responseRequest.setPayload(payload);
            Handler handler = new Handler(getMainLooper());
            databaseManager.insertOrUpdateResponseRequestAsync(responseRequest, new DatabaseManager.QueryResultListener() {
                @Override
                public void onResult(ResponseRequest responseRequest) {
                }
            },handler);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
