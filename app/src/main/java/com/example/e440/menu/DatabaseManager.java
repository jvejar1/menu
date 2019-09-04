package com.example.e440.menu;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.sql.Timestamp;

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
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Csequence "
                    + " ADD COLUMN csequence_string STRING default ''");
        }
    };

    public void insertRequest(JSONObject payload,int student_server_id,String test_name,int evaluator_server_id){

        if(student_server_id!=0) {
            try {
                payload.put("student_id", student_server_id);
                payload.put("test_name", test_name);
                payload.put("evaluator_id", evaluator_server_id);
                payload.put("timestamp", new Timestamp(System.currentTimeMillis()));
                ResponseRequest responseRequest = new ResponseRequest(payload.toString(), test_name,false);
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


                    }
                }.execute(responseRequest);

            }
            catch (JSONException e){
                e.printStackTrace();
            }


        }


    }



    void cleanAce(){

        this.testDatabase.daoAccess().deleteAllAcases();
        this.testDatabase.daoAccess().deleteAllAces();
    }

    void cleanWally(){
        this.testDatabase.daoAccess().deleteAllWFeelings();
        this.testDatabase.daoAccess().deleteAllWreactions();
        this.testDatabase.daoAccess().deleteAllWSituations();
        this.testDatabase.daoAccess().deleteAllWally();

    }

    void cleanCorsi(){

        this.testDatabase.daoAccess().deleteAllCsequences();
        this.testDatabase.daoAccess().deleteAllCorsis();
    }

    void cleanFonotest(){

        this.testDatabase.daoAccess().deleteAllItems();
        this.testDatabase.daoAccess().deleteAllFonotest();
    }

    void cleanHnf(){

        this.testDatabase.daoAccess().deleteAllHnfFigure();
        this.testDatabase.daoAccess().deleteAllHnfTests();
        this.testDatabase.daoAccess().deleteAllHnfSet();
    }

    void sendAllResults(final NetworkManager networkManagerInstance){

        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ResponseRequest[] responseRequests=DatabaseManager.this.testDatabase.daoAccess().fetchAllResponseRequest();
                for (final ResponseRequest responseRequest:responseRequests){
                    try {
                        JSONObject payload = new JSONObject(responseRequest.getPayload());
                        networkManagerInstance.sendEvaluation(payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject headers= response.optJSONObject("headers");
                                int status=headers.optInt("status");
                                int id_to_erase=response.optInt("id_to_erase");
                                if (status==HttpURLConnection.HTTP_OK){

                                    DatabaseManager.this.testDatabase.daoAccess().setEvaluationSavedToTrue(id_to_erase);

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
