package com.example.e440.menu;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.wally_original.Evaluation;
import com.example.e440.menu.wally_original.ItemsBank;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.concurrent.Executor;


/**
 * Created by e440 on 09-04-18.
 */

public class DatabaseManager {
    static String DEBUG_TAG="Database Manager";
    static final String DATABASE_NAME = "TestDatabase";
    static DatabaseManager databaseManager;
    TestDatabase testDatabase;

    public DaoAccess getDao(){
        return this.testDatabase.daoAccess();
    }
    public static synchronized DatabaseManager getInstance(Context context){
        if (databaseManager==null){

            databaseManager =new DatabaseManager();
            databaseManager.testDatabase= Room.databaseBuilder(context,
                    TestDatabase.class,DATABASE_NAME).addMigrations(ADD_TEST_COUNTS_TO_STUDENTS, ADD_STUDENT_SERVER_ID_TO_EVALUATIONS).fallbackToDestructiveMigration()
                    .build();
        }
        return databaseManager;

    }
    static final Migration ADD_TEST_COUNTS_TO_STUDENTS = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Student ADD COLUMN aces_count integer default 0 NOT NULL");

            database.execSQL("ALTER TABLE Student ADD COLUMN wally_count integer default 0 NOT NULL");

            database.execSQL("ALTER TABLE Student ADD COLUMN corsis_count integer default 0 NOT NULL");

            database.execSQL("ALTER TABLE Student ADD COLUMN hnf_count integer default 0 NOT NULL");

            database.execSQL("ALTER TABLE Student ADD COLUMN fonotest_count integer default 0 NOT NULL");        }
    };

    static final Migration ADD_STUDENT_SERVER_ID_TO_EVALUATIONS = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE ResponseRequest ADD COLUMN student_server_id integer default 0 ");

            }
    };


    public void insertResponseRequest(ResponseRequest responseRequest){

        this.testDatabase.daoAccess().insertResponseRequest(responseRequest);
    }

    public void updateResponseRequestAsync(final ResponseRequest responseRequest, final Handler handler, final QueryResultListener queryResultListener){
        QueryExecutor queryExecutor = new QueryExecutor();
        queryExecutor.execute(new Runnable() {
            @Override
            public void run() {
                testDatabase.daoAccess().updateResponseReQuest(responseRequest);
                Log.d(DEBUG_TAG, String.format("Updated ResponseRequest with id(%d) set saved to: %b", responseRequest.getId(),responseRequest.getSaved()));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        queryResultListener.onResult(responseRequest);
                    }
                });
            }
        });
    }


    public void fetchRRFinishedAndNotSavedasync(Handler handler, GenericListener<ResponseRequest[]> listener){
        Executor executor = new QueryExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ResponseRequest[] responseRequests = testDatabase.daoAccess().fetchResponseRequestsNotSavedInServerAndFinished();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(responseRequests);
                    }
                });
            }
        });
    }

    public interface QueryResultListener{
        void onResult(ResponseRequest responseRequest);
    }

    public interface GenericListener<T>{
        void onResult(T obj);
    }

    public class QueryExecutor implements Executor {
        @Override
        public void execute(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    public void insertOrUpdateResponseRequestAsync(final ResponseRequest responseRequest, final QueryResultListener listener, final Handler handler){
        QueryExecutor queryExecutor = new QueryExecutor();
        queryExecutor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        Long returnedId = testDatabase.daoAccess().insertResponseRequest(responseRequest);
                        if (returnedId == -1){
                            int nUpdated = testDatabase.daoAccess().updateResponseReQuest(responseRequest);
                            Log.d(DEBUG_TAG, String.format("ResponseRequest nUpdated: %d, id: %d", nUpdated, responseRequest.getId()));
                        }else{
                            Log.d(DEBUG_TAG, String.format("ResponseRequest async inserted in DB returning id %d",returnedId));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (returnedId!=-1){
                                    responseRequest.setId(returnedId);
                                }
                                listener.onResult(responseRequest);
                            }
                        });

                    }
                }
        );
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


}
