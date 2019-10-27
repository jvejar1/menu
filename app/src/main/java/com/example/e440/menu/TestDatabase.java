package com.example.e440.menu;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;

/**
 * Created by e440 on 01-05-18.
 */


@Database(entities = {Acase.class,Ace.class,Afeeling.class,Student.class,Wally.class,WSituation.class,
                        WReaction.class,WFeeling.class,Csequence.class,HnfTest.class,HnfFigure.class,
        FonoTest.class,Item.class,Corsi.class,ResponseRequest.class,HnfSet.class,Course.class}, version = 3, exportSchema = false)
public abstract class TestDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess() ;
}