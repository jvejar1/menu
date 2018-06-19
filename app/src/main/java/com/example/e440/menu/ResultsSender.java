package com.example.e440.menu;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by e440 on 11-06-18.
 */

class ResultsSender extends AsyncTask {
    DatabaseManager databaseManager;
    NetworkManager networkManager;

    ResultsSender(Context context){

        this.databaseManager=DatabaseManager.getInstance(context);
        this.networkManager=NetworkManager.getInstance(context);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ResponseRequest[] responseRequests=this.databaseManager.testDatabase.daoAccess().fetchAllResponseRequest();
        for (final ResponseRequest responseRequest:responseRequests){
            try {
                JSONObject payload = new JSONObject(responseRequest.getPayload());
                this.networkManager.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject headers= response.optJSONObject("headers");
                        int status=headers.optInt("id_to_erase");
                        if (status== HttpURLConnection.HTTP_ACCEPTED || status==HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                            databaseManager.testDatabase.daoAccess().deleteResponseRequest(responseRequest);
                            int a =1;
                        }else{
                            //TODO: stuffs
                            int a =1;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int a =1;
                    }
                });
            }

            catch(JSONException e ){
                e.printStackTrace();

            }


        }
        return null;

    }
}