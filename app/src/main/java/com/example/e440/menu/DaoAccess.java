package com.example.e440.menu;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;

import java.util.List;

/**
 * Created by e440 on 09-04-18.
 */

@Dao
public interface DaoAccess {


    //School
    @Query("SELECT * FROM Course")
    Course[] fetchAllCourses();

    @Query("SELECT * FROM Course where server_id=:server_id LIMIT 1")
    Course fetchSchoolByServerId(int server_id);

    @Query("DELETE FROM Course where server_id=:server_id")
    int deleteSchoolByServerId(int server_id);

    @Delete
    int deleteSchool(Course school);

    @Update
    int updateSchool(Course school);
    @Insert
    void insertSchool(Course school);

    @Insert
    void insertResponseRequest(ResponseRequest responseRequest);
    @Delete
    int deleteResponseRequest(ResponseRequest responseRequest);
    @Query("SELECT * FROM ResponseRequest")
    ResponseRequest[] fetchAllResponseRequest();

    @Query("SELECT * FROM ResponseRequest where saved=0")
    ResponseRequest[] fetchNotSavedResponseRequest();


    @Query("SELECT count() FROM ResponseRequest where test_name=:testName")
    int fetchTestEvaluationsCount(String testName);


    @Query("SELECT count(*) FROM ResponseRequest")
    int fetchEvaluationsCount();

    @Query("UPDATE ResponseRequest SET saved=1 where id=:eval_id")
    int setEvaluationSavedToTrue(int eval_id);


    @Query("SELECT count(*) FROM ResponseRequest where saved=1")
    int fetchReceivedEvaluationsCount();


    @Query("Select school_name, course_level, course_letter from Student GROUP by school_name, course_level, course_letter ")
    List<CourseTuple> getCourseTouples();

    @Query("DELETE FROM ResponseRequest where id=:id_to_delete")
    int deleteRequestById(int id_to_delete);
    //Students

    @Query("Select count() from ResponseRequest where student_server_id=:studentServerId and test_name=:testName")
    int getTestEvaluationsCountByStudentServerId(Long studentServerId, String testName);

    @Insert
    long insertOneStudent(Student student);

    @Update
    int updateStudent(Student student);

    @Query("Delete from Student")
    int deleteAllStudents();

    @Query("SELECT * FROM Student order by last_name ASC")
    Student[] fetchAllStudents();


    @Query("SELECT school_name FROM Student group by school_name")
    String[] fetchSchoolsNames();

    @Query("SELECT * FROM Student where server_id=:server_id limit 1")
    Student fetchStudentByServerId(long server_id);

    @Query("SELECT * FROM Student where school_name=:school_name order by course_level asc,course_letter asc, last_name asc")
    Student[] fetchStudentsBySchoolName(String school_name);

    @Query("SELECT * FROM Student where school_name=:school_name and course_level=:courseLevel and course_letter=:courseLetter order by last_name asc")
    Student[] fetchStudentsBySchoolAndCourse(String school_name, int courseLevel, int courseLetter);

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

    @Query("Delete From HnfTest")
    int deleteAllHnfTests();

    @Query("Delete From HnfFigure")
    int deleteAllHnfFigure();

    @Query("Delete From HnfSet")
    int deleteAllHnfSet();




    @Query("Delete From FonoTest")
    int deleteAllFonotest();
    @Query("Delete From Item")
    int deleteAllItems();


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

    @Query("Delete from Corsi")
    int deleteAllCorsis();

    @Query("Delete from Csequence")
    int deleteAllCsequences();


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

    @Query("SELECT server_id FROM WSituation where id=:wsituation_id")
    int fetchWsituationServerIdByWsituationId(long wsituation_id);



    @Query("SELECT * FROM WFeeling")
    WFeeling[] fetchAllWFeelings();

    @Query("SELECT * FROM WReaction where wsituation_id=:id")
    WReaction[] fetchWActionsByWSituationId(long id);


    @Query("DELETE FROM WReaction")
    int deleteAllWreactions();

    @Query("DELETE FROM WFeeling")
    int deleteAllWFeelings();

    @Query("DELETE FROM WSituation")
    int deleteAllWSituations();

    @Query("DELETE FROM Wally")
    int deleteAllWally();


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

    @Query("Delete FROM Acase")
    int deleteAllAcases();

    @Query("Delete FROM Ace")
    int deleteAllAces();

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
