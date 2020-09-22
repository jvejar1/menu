package com.example.e440.menu;


import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


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
            databaseManager.testDatabase= Room.databaseBuilder(context,
                    TestDatabase.class,DATABASE_NAME).addMigrations(ADD_TEST_COUNTS_TO_STUDENTS, ADD_STUDENT_SERVER_ID_TO_EVALUATIONS)
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
