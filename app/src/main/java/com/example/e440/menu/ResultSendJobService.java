package com.example.e440.menu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        mContext=getApplicationContext();
        this.jobParameters=jobParameters;
        ResultsSender rs=new ResultsSender(this,this);
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

    static class ResultsSender extends AsyncTask<Object,Integer,Object> {
        DatabaseManager databaseManager;
        NetworkManager networkManager;
        int sended_requests;
        int error_resquests;
        int total_requests;
        Context mCtx;
        ResultsSenderListener mCallback;
        ResultsSender(Context context,ResultsSenderListener listener){
            mCtx=context.getApplicationContext();
            this.databaseManager=DatabaseManager.getInstance(mCtx);
            this.networkManager=NetworkManager.getInstance(mCtx);
            this.sended_requests=0;
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

            ResponseRequest[] responseRequests=this.databaseManager.testDatabase.daoAccess().fetchNotSavedResponseRequest();
            return responseRequests;

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
            ResponseRequest[] responseRequests=(ResponseRequest[])o;
            total_requests =responseRequests.length;
            int index=0;
            for (ResponseRequest responseRequest:responseRequests){

                try {
                    JSONObject payload = new JSONObject(responseRequest.getPayload());
                    payload.put("request_id_to_delete",responseRequest.getId());
                    this.networkManager.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject headers= response.optJSONObject("headers");
                            int status=headers.optInt("status");
                            boolean request_was_saved=status== HttpURLConnection.HTTP_OK || status==HttpURLConnection.HTTP_CREATED;
                            if (request_was_saved){
                                int id_to_delete=response.optInt("request_id_to_delete");
                                AsyncTask delete_response_request=new AsyncTask() {
                                    @Override
                                    protected Object doInBackground(Object[] objects) {
                                        int id_to_delete=(int)objects[0];
                                        databaseManager.testDatabase.daoAccess().setEvaluationSavedToTrue(id_to_delete);
                                        int a =1;
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);


                                    }
                                }.execute(id_to_delete);
                                sended_requests++;

                            }else{

                                sended_requests++;
                                //TODO: stuffs
                            }
                            check_if_last_request();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("volley error"+error.getMessage());
                            error_resquests++;
                            sended_requests++;
                            check_if_last_request();
                        }
                    });
                }

                catch(JSONException e ){
                    e.printStackTrace();

                }

                index++;

            }
            if(total_requests==0){
                mCallback.OnSendingFinish(sended_requests,error_resquests);
            }



        }
    }
}
