package com.example.e440.menu;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

/**
 * Created by e440 on 09-04-18.
 */

@Dao
public interface DaoAccess {


    //Students
    @Insert
    long insertOneStudent(Student student);

    @Query("SELECT * FROM Student")
    Student[] fetchAllStudents();


    //HNF
    @Insert
    long insertOneHnfTest(HnfTest hnfTest);
    @Insert
    long insertOneHnfFigure(HnfFigure hnfFigure);

    @Query("SELECT * FROM HnfTest where hnf_type=:hnf_type LIMIT 1")
    HnfTest fetchHnfTestByType(int hnf_type);

    @Query("SELECT * FROM HnfFigure where hnftest_id=:hnf_test_id")
    HnfFigure[] fetchAllHnfFiguresByHnfTestId(int hnf_test_id);





    //CORSI
    @Insert
    long insertCSequence(Csequence csequence);
    @Insert
    long insertCSquare(Csquare csquare);

    @Query("SELECT * FROM CSEQUENCE where ordered=1")
    Csequence[] fetchOrderedCsequences();

    @Query("SELECT * FROM CSEQUENCE where ordered=0")
    Csequence[] fetchReversedCsequences();

    @Query("SELECT * FROM CSEQUENCE where ordered=:ordered_or_not and server_id=0")
    Csequence[] fetchPracticeCsequences(int ordered_or_not);

    @Query("SELECT * FROM csquare where csequence_id=:csequence_id")
    Csquare[] fetchCsquareByCsequenceId(int csequence_id);


    //WALLY
    @Insert
    void insertWally(Wally wally);

    @Insert
    long insertWSituation(WSituation wSituation);

    @Insert
    long insertWAction(WReaction wAction);
    @Insert
    void insertWFeeling(WFeeling wFeeling);

    @Query("SELECT * FROM WSituation")
    WSituation[] fetchAllWSituations();

    @Query("SELECT * FROM WSituation where id=:id")
    WSituation fetchWSituationById(long id);

    @Query("SELECT id FROM WSituation")
    long[] fetchWsituationsIds();

    @Query("SELECT * FROM WFeeling")
    WFeeling[] fetchAllWFeelings();


    @Query("SELECT * FROM WReaction where wsituation_id=:id")
    WReaction[] fetchWActionsByWSituationId(long id);



    //ACE
    @Insert
    void insertAce(Ace ace);

    @Query("SELECT * FROM Ace LIMIT 1")
    Ace fetchFirstAce();

    @Query("SELECT * FROM Acase")
    Acase[] fetchAllAcases();

    @Query("SELECT * FROM Acase where id=:id")
    Acase fetchAcaseById(int id);

    @Query("SELECT * FROM Acase LIMIT 1 OFFSET :offset")
    Acase fetchOneAcaceWithOffset(int offset);

    @Query("SELECT id FROM Acase")
    int[] fetchAcacesIds();

    @Insert
    void insertAcase(Acase acase);

   /* @Query("SELECT * FROM Form")
    Form[] fetchAllForms();

    @Query("DELETE from Form")
    void deleteAllForms();



    @Update
    void updateForm(Form form);
    @Delete
    void deleteForm(Form form);


*/

}
