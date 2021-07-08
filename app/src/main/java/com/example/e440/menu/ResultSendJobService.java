package com.example.e440.menu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Half;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by e440 on 26-06-18.
 */

public class ResultSendJobService extends JobService implements ResultsSenderListener{
    JobParameters jobParameters;
    Context mContext;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(this, "JOB STARTINTG", Toast.LENGTH_SHORT).show();
        mContext=this;
        this.jobParameters=jobParameters;
        Handler handler = new Handler(getMainLooper());
        ResultsSender rs=new ResultsSender(this,this, handler);
        rs.execute();
        return true;
    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void OnSendingFinish(int total_sended_count, int errors_count) {

        if (errors_count>0){
            jobFinished(jobParameters,true);
        }else{
            jobFinished(jobParameters,false);
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public static class ResultsSender extends AsyncTask<Object,Integer,Object> {
        DatabaseManager databaseManager;
        NetworkManager networkManager;
        Handler handler;
        int sended_requests;
        int error_resquests;
        int total_requests;
        Context mCtx;
        ResultsSenderListener mCallback;
        public ResultsSender(Context context,ResultsSenderListener listener, Handler handler){
            this.handler = handler;
            mCtx=context.getApplicationContext();
            this.databaseManager=DatabaseManager.getInstance(mCtx);
            this.networkManager=NetworkManager.getInstance(mCtx);
            this.sended_requests=0;
            this.error_resquests=0;
            this.mCallback=listener;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            int percent=progress[0];
            mCallback.onProgressUpdate(percent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            databaseManager.fetchRRFinishedAndNotSavedasync(handler, new DatabaseManager.GenericListener<ResponseRequest[]>() {
                @Override
                public void onResult(ResponseRequest[] responseRequests) {
                    total_requests =responseRequests.length;
                    if (responseRequests.length==0){
                        mCallback.OnSendingFinish(sended_requests,error_resquests);
                        return;
                    }
                    for (ResponseRequest responseRequest:responseRequests){
                        JSONObject payload = networkManager.createJSONObject(responseRequest.getPayload());
                        networkManager.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                sended_requests++;
                                if (networkManager.responseCodeIsOKorCreated(response)){
                                    responseRequest.setSaved(true);
                                    databaseManager.updateResponseRequestAsync(responseRequest, handler, responseRequest1 -> {
                                        check_if_last_request();
                                    });
                                }else{
                                    check_if_last_request();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("volley error"+error.getMessage());
                                sended_requests++;
                                error_resquests++;
                                check_if_last_request();
                            }
                        });
                    }
                }
            });
            return null;
        }

        void check_if_last_request(){
            if(sended_requests== total_requests){
                int success_requests=sended_requests-error_resquests;
                NotificationManager nm = (NotificationManager)mCtx.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(mCtx);
                Intent notificationIntent = new Intent(mCtx, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(mCtx,0,notificationIntent,0);

                //set
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(R.drawable.app_logo);
                if(success_requests==0){
                    builder.setContentText("Se produjo un error al enviar las evaluaciones, se intentará luego nuevamente");
                }
                else {
                    builder.setContentText("Se han enviado " + success_requests + " de " + total_requests + " evaluaciones exitosamente.");
                }

                builder.setContentTitle( mCtx.getApplicationInfo().loadLabel(mCtx.getPackageManager()).toString());
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);

                Notification notification = builder.build();
                nm.notify((int)System.currentTimeMillis(),notification);


                //needs to call onfinish listener
                //TODO: show notification here and callback
                mCallback.OnSendingFinish(sended_requests,error_resquests);

            }

        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }
    }
}
