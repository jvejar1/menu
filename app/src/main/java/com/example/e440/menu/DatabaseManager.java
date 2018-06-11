package com.example.e440.menu;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by e440 on 09-04-18.
 */

public class DatabaseManager {
    static final String DATABASE_NAME = "TestDatabase";
    static DatabaseManager databaseManager;
    TestDatabase testDatabase;
    static synchronized DatabaseManager getInstance(Context context){


        if (databaseManager==null){

            databaseManager =new DatabaseManager();
            databaseManager.testDatabase=Room.databaseBuilder(context,
                    TestDatabase.class,DATABASE_NAME).fallbackToDestructiveMigration()
                    .build();
        }
        return databaseManager;

    }

    public void insertRequest(ResponseRequest responseRequest){

        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ResponseRequest r=(ResponseRequest) objects[0];
                databaseManager.testDatabase.daoAccess().insertResponseRequest(r);
                return null;
            }
        }.execute(responseRequest);

    }
    void sendAllResults(final NetworkManager networkManagerInstance){

        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                final ResponseRequest[] responseRequests=DatabaseManager.this.testDatabase.daoAccess().fetchAllResponseRequest();
                for (final ResponseRequest responseRequest:responseRequests){
                    try {
                        JSONObject payload = new JSONObject(responseRequest.getPayload());
                        networkManagerInstance.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject headers= response.optJSONObject("headers");
                                int status=headers.optInt("id_to_erase");
                                if (status==HttpURLConnection.HTTP_ACCEPTED){
                                    //TODO:erase from db
                                    DatabaseManager.this.testDatabase.daoAccess().fetchAllResponseRequest();
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
        }.execute();
    }

}
