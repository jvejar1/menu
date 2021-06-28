package com.example.e440.menu.wally_original;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import com.example.e440.menu.NetworkManager;
import com.example.e440.menu.R;
import com.example.e440.menu.ResponseRequest;
import com.example.e440.menu.ResultSendJobService;
import com.example.e440.menu.ResultsSenderListener;
import com.example.e440.menu.Student;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

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

            InstrumentsManager instrumentsManager= InstrumentsManager.getInstance(this);
            model.configure(instrumentIndex,studentId,userId, instrumentsManager);

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

        final DatabaseManager databaseManager = DatabaseManager.getInstance(getApplicationContext());
        final NetworkManager networkManager = NetworkManager.getInstance(getApplicationContext());
        Gson gson = new Gson();
        JsonElement jsonElement = (gson.toJsonTree(evaluation,Evaluation.class)).getAsJsonObject();
        final JSONObject payload = new JSONObject();
        try{
            String evaluationStr = gson.toJson(jsonElement);
            JSONObject evalJSONObject = new JSONObject(evaluationStr);
            payload.put("evaluation", evalJSONObject);
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
        }
        ResponseRequest responseRequest = new ResponseRequest(payload.toString(), null, false, studentServerId, true, evaluation.getInstrumentId());
        final Handler handler = new Handler(getMainLooper());
        databaseManager.insertResponseRequestAsync(responseRequest, new DatabaseManager.QueryResultListener() {
            @Override
            public void onResult(final ResponseRequest responseRequest) {
                networkManager.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (networkManager.responseCodeIsOKorCreated(response)){
                            Log.d("Wally original activity", String.format("Success network response when sending evaluation"));
                            responseRequest.setSaved(true);
                            databaseManager.updateResponseRequestAsync(responseRequest, handler, new DatabaseManager.QueryResultListener() {
                                @Override
                                public void onResult(ResponseRequest responseRequest) {
                                    progressDialog.dismiss();
                                    WallyOriginalActivity.this.finish();
                                }
                            });
                        }else{
                            progressDialog.dismiss();
                            WallyOriginalActivity.this.finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d("Wally original activity", String.format("Volley error: %s", error.getMessage()));
                        progressDialog.dismiss();
                        WallyOriginalActivity.this.finish();
                    }
                });
            }
        }, handler);
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
