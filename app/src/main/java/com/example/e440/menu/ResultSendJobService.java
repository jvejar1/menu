package com.example.e440.menu;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by e440 on 26-06-18.
 */

public class ResultSendJobService extends JobService {
    JobParameters jobParameters;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(this, "JOB STARTINTG", Toast.LENGTH_SHORT).show();
        this.jobParameters=jobParameters;
        ResultsSender rs=new ResultsSender(this);
        rs.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }


    class ResultsSender extends AsyncTask {
        DatabaseManager databaseManager;
        NetworkManager networkManager;
        int sended_requests;

        ResultsSender(Context context){
            this.databaseManager=DatabaseManager.getInstance(context);
            this.networkManager=NetworkManager.getInstance(context);
            this.sended_requests=0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            ResponseRequest[] responseRequests=this.databaseManager.testDatabase.daoAccess().fetchAllResponseRequest();
            return responseRequests;

        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            final ResponseRequest[] responseRequests=(ResponseRequest[])o;
            for (ResponseRequest responseRequest:responseRequests){

                try {
                    JSONObject payload = new JSONObject(responseRequest.getPayload());
                    payload.put("request_id_to_delete",responseRequest.getId());
                    this.networkManager.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject headers= response.optJSONObject("headers");
                            int status=headers.optInt("status");
                            if (status== HttpURLConnection.HTTP_ACCEPTED || status==HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                                int id_to_delete=response.optInt("request_id_to_delete");
                                AsyncTask delete_response_request=new AsyncTask() {
                                    @Override
                                    protected Object doInBackground(Object[] objects) {
                                        int id_to_delete=(int)objects[0];
                                        databaseManager.testDatabase.daoAccess().deleteRequestById(id_to_delete);
                                        int a =1;
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);
                                        sended_requests++;
                                    }
                                }.execute(id_to_delete);

                            }else{

                                //TODO: stuffs
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sended_requests++;
                            System.out.println("volley error"+error.getMessage());

                        }
                    });
                }

                catch(JSONException e ){
                    e.printStackTrace();

                }


            }



        }
    }
}
