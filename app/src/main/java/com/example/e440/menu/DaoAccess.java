package com.example.e440.menu;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;

/**
 * Created by e440 on 09-04-18.
 */

@Dao
public interface DaoAccess {


    //School
    @Query("SELECT * FROM School")
    School[] fetchAllSchools();

    @Query("SELECT * FROM School where server_id=:server_id LIMIT 1")
    School fetchSchoolByServerId(int server_id);

    @Query("DELETE FROM School where server_id=:server_id")
    int deleteSchoolByServerId(int server_id);

    @Query("DELETE FROM Student where school_id=:school_id")
    int deleteSchoolBySchoolId(int school_id);

    @Delete
    int deleteSchool(School school);

    @Update
    int updateSchool(School school);
    @Insert
    void insertSchool(School school);

    @Query("SELECT * FROM ResponseRequest where test_name='fonotest'")
    ResponseRequest[] fetchFonotestResponseRequests();
    @Insert
    void insertResponseRequest(ResponseRequest responseRequest);
    @Delete
    int deleteResponseRequest(ResponseRequest responseRequest);
    @Query("SELECT * FROM ResponseRequest")
    ResponseRequest[] fetchAllResponseRequest();
    @Query("DELETE FROM ResponseRequest where id=:id_to_delete")
    int deleteRequestById(int id_to_delete);
    //Students
    @Insert
    long insertOneStudent(Student student);

    @Update
    int updateStudent(Student student);

    @Query("Delete from Student")
    int deleteAllStudents();

    @Query("SELECT * FROM Student order by last_name ASC")
    Student[] fetchAllStudents();

    @Query("SELECT * FROM Student where server_id=:server_id limit 1")
    Student fetchStudentByServerId(int server_id);

    @Query("DELETE FROM Student where server_id=:server_id")
    int deleteStudentByServerId(int server_id);

    //Fonotest
    @Insert
    long insertFonoTest(FonoTest fonoTest);



    @Insert
    long insertOneItem(Item item);

    @Query("SELECT * FROM FonoTest LIMIT 1")
    FonoTest fetchFonotest();

    @Query("SELECT * FROM ITEM WHERE example=1")
    Item[] fetchStartingPointsItems();



    @Query("SELECT * From Item")
    Item[] fetchAllItems();

    @Query("SELECT * From Item where id=:id")
    Item fetchItemById(int id);


    //HNF


    @Insert
    long insertOneHnfTest(HnfTest hnfTest);
    @Insert
    long insertHnfSet(HnfSet hnfSet);
    @Query("SELECT * FROM HnfSet LIMIT 1")
    HnfSet fetchHnfSet();
    @Insert
    long insertOneHnfFigure(HnfFigure hnfFigure);

    @Query("SELECT * FROM HnfTest where hnf_type=:hnf_type LIMIT 1")
    HnfTest fetchHnfTestByType(int hnf_type);

    @Query("SELECT * FROM HnfFigure where hnftest_id=:hnf_test_id")
    HnfFigure[] fetchAllHnfFiguresByHnfTestId(int hnf_test_id);


    //CORSI


     @Insert
    long insertCorsi(Corsi corsi);

    @Query("Select * from Corsi LIMIT 1")
    Corsi fetchCorsi();


    @Insert
    long insertCSequence(Csequence csequence);

    @Query("SELECT * FROM CSEQUENCE where ordered=1 and example=0")
    Csequence[] fetchOrderedCsequences();

    @Query("SELECT * FROM CSEQUENCE where ordered=0 and example=0")
    Csequence[] fetchReversedCsequences();

    @Query("SELECT * FROM CSEQUENCE where ordered=:ordered_or_not and example=1")
    Csequence[] fetchPracticeCsequences(int ordered_or_not);


    //WALLY
    @Query("SELECT * FROM Wally LIMIT 1")
    Wally fetchFirstWally();

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
