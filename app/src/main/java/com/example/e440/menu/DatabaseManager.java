package com.example.e440.menu;

import android.arch.persistence.room.Room;
import android.content.Context;

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

}
