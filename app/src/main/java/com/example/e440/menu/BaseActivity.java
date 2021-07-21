package com.example.e440.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.example.e440.menu.wally_original.WallyOriginalActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by e440 on 29-06-18.
 */

public class BaseActivity extends AppCompatActivity implements MainFragmentListener {
    Long student_server_id;
    String studentFullName;

    @Override
    public void backFromPractice() {

    }


    @Override
    public void backFromTest(JSONObject jo) {

    }

    @Override
    public void goBackFromMainFragment() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras=getIntent().getExtras();
        student_server_id=extras.getLong(Student.EXTRA_STUDENT_SERVER_ID);
        studentFullName=extras.getString(WallyOriginalActivity.EXTRA_STUDENT_NAME);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Está apunto de abortar el test");
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO:manage the abort of a test
            }
        });
        builder.setPositiveButton("Abortar test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                abortTest();
            }
        });
        builder.show();

    }

    void abortTest(){
        Toast.makeText(this,"Test abortado!",Toast.LENGTH_LONG).show();
        this.finish();
    }

    void scheduleResultsSendind(){
        Context context=getApplicationContext();
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        List<JobInfo> pending_jobs=jobScheduler.getAllPendingJobs();
        boolean already_scheduled=false;
        for (int i=0;i<pending_jobs.size();i++){
            JobInfo jobInfo1=pending_jobs.get(i);
            Log.d("id:",Integer.toString(jobInfo1.getId()));
            Log.d("info:",Integer.toString(jobInfo1.getId()));
            if(jobInfo1.getId()==1){
                already_scheduled=true;
                break;

              //  jobScheduler.cancel(i);
            }
        }
        if(!already_scheduled) {
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context, ResultSendJobService.class));
            JobInfo jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).setBackoffCriteria(100000, JobInfo.BACKOFF_POLICY_LINEAR).build();
            jobScheduler.schedule(jobInfo);
        }
    }

    static void forceResultsSendind(Context context){
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        List<JobInfo> pending_jobs=jobScheduler.getAllPendingJobs();
        for (int i=0;i<pending_jobs.size();i++){
            JobInfo jobInfo1=pending_jobs.get(i);
            Log.d("id:",Integer.toString(jobInfo1.getId()));
            Log.d("info:",Integer.toString(jobInfo1.getId()));
            if(jobInfo1.getId()==1){
                jobScheduler.cancel(i);
            }
        }
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context, ResultSendJobService.class));
        JobInfo jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).setBackoffCriteria(100000, JobInfo.BACKOFF_POLICY_LINEAR).build();
        jobScheduler.schedule(jobInfo);

    }
    public void insertRequest(JSONObject payload    , String test_name, int evaluator_server_id){

        final DatabaseManager databaseManager= DatabaseManager.getInstance(getApplicationContext());

        if(student_server_id!=0) {
            try {
                payload.put("student_id", student_server_id);
                payload.put("test_name", test_name);
                payload.put("evaluator_id", evaluator_server_id);
                payload.put("timestamp", new Timestamp(System.currentTimeMillis()));
                ResponseRequest responseRequest = new ResponseRequest(payload.toString(), test_name,false, student_server_id);
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        ResponseRequest r = (ResponseRequest) objects[0];
                        databaseManager.testDatabase.daoAccess().insertResponseRequest(r);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        scheduleResultsSendind();
                    }
                }.execute(responseRequest);

            }
            catch (JSONException e){
                e.printStackTrace();
            }


        }



    }

}
