package com.example.e440.menu;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by e440 on 01-05-18.
 */


@Database(entities = {Acase.class,Ace.class,Afeeling.class,Student.class,Wally.class,WSituation.class,
                        WReaction.class,WFeeling.class,Csequence.class,Csquare.class,HnfTest.class,HnfFigure.class}, version = 1, exportSchema = false)
public abstract class TestDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess() ;
}