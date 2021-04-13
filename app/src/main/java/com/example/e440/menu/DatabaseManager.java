package com.example.e440.menu;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.e440.menu.wally_original.Evaluation;
import com.example.e440.menu.wally_original.ItemsBank;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by e440 on 09-04-18.
 */

public class DatabaseManager {
    static final String DATABASE_NAME = "TestDatabase";
    static DatabaseManager databaseManager;
    TestDatabase testDatabase;
    public static synchronized DatabaseManager getInstance(Context context){
        if (databaseManager==null){

            databaseManager =new DatabaseManager();
            databaseManager.testDatabase= Room.databaseBuilder(context,
                    TestDatabase.class,DATABASE_NAME).addMigrations(ADD_TEST_COUNTS_TO_STUDENTS, ADD_STUDENT_SERVER_ID_TO_EVALUATIONS)
                    .fallbackToDestructiveMigration().build();
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

    public ResponseRequest insertResponseRequestAsync(final Evaluation eval){
        Log.d("DATABASE MANAGER", "Insert eval");
        Gson gson = new Gson();
        String evalJson = gson.toJson(eval);
        final ResponseRequest responseRequest = new ResponseRequest(evalJson, null, false, eval.getStudentId());
        responseRequest.finished = eval.isFinished();
        responseRequest.instrumentId = eval.getInstrumentId();

        if (eval.isFinished()){

            Log.d("DATABASE MANAGER", "Eval finished");
            try{
                evalJson = gson.toJson(eval);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("evaluation", evalJson);
                responseRequest.setPayload(jsonObject.toString());

            }catch( JSONException e){
                Log.d("DATABASE MANAGER", "Faled to create the HTTP json payload");
                e.printStackTrace();
            }
        }

        if (eval.getId() != null){
            testDatabase.daoAccess().updateResponseReQuest(responseRequest);
        }

        Long theReturnedId = testDatabase.daoAccess().insertResponseRequest(responseRequest);
        eval.setId(theReturnedId);

        String evalJsonStr = gson.toJson(eval);
        responseRequest.setPayload(evalJsonStr);
        testDatabase.daoAccess().updateResponseReQuest(responseRequest);

        return responseRequest;
    }



    public Evaluation checkIfCanContinueAnEvaluation(long studentId, ItemsBank instrument){

        ResponseRequest[] result = this.testDatabase.daoAccess().loadContinuableEvaluationInAJsonString(studentId, instrument.id);
        if (result.length == 0){

            return null;
        }
        ResponseRequest theFirst = result[0];

        long id =theFirst.getId();
        String evalAsStr = theFirst.payload;

        Gson gson = new Gson();
        gson.toJson(evalAsStr);
        Evaluation evaluation  = gson.fromJson(evalAsStr, Evaluation.class);
        evaluation.setId( id);

        return evaluation;

    }

    public void Update(final Evaluation e){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                int updated = testDatabase.daoAccess().updateResponseRequest(e.getId(), true);

                if (updated == 0){

                    insertResponseRequestAsync(e);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();

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
