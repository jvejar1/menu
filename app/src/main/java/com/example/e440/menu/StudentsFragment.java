package com.example.e440.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e440 on 23-04-18.
 */

public class StudentsFragment extends Fragment {

    View inflatedView;
    DatabaseManager databaseManager;
    OnStudentSelectedListener mCallback;
    public interface OnStudentSelectedListener {
        public void onStudentSelected(Long student_id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnStudentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    TextView students_count_text_view;
    Spinner schools_names_spinner;
    Spinner courseNamesSpinner;
    ArrayList<String> schools_names_list=new ArrayList();
    ArrayList<String> courseNamesList=new ArrayList();

    final static String SCHOOL_SELECTION_TEXT="-- TODOS LOS ALUMNOS --";
    RecyclerView students_recycler_view;
    List<Student> studentList=new ArrayList<>();
    StudentsAdapter studentsAdapter;
    ArrayAdapter<String> schools_names_adapter;
    ArrayAdapter<String> courseNamesAdapter;
    StudentsLoader studentsLoader;
    HashMap<String, List<int[]>> courseLevelAndLetterBySchoolName = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.students_fragment, container, false);
        databaseManager= DatabaseManager.getInstance(getContext());

//        studentsListView=inflatedView.findViewById(R.id.studentsListView);
//
//        studentsListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//                TextView studentIdTextView=view.findViewById(R.id.studentServerIdTextView);
//                int student_server_id=Integer.parseInt(studentIdTextView.getText().toString());
//                mCallback.onStudentSelected(student_server_id);
//            }
//        });
//        Student s1 = new Student("","01 Alumno prueba","0",0,0);
//        studentList.add(s1);
//        ListAdapter customAdapter = new ListAdapter(getContext(), R.layout.students_list_item, studentList);
//        studentsListView.setAdapter(customAdapter);
//        studentsListView.setClickable(true);

        students_count_text_view=inflatedView.findViewById(R.id.studentsCountTextView);


        schools_names_spinner=inflatedView.findViewById(R.id.schoolsNamesSpinner);
        schools_names_adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, schools_names_list);

        schools_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schools_names_spinner.setAdapter(schools_names_adapter);
        schools_names_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String school_name=schools_names_list.get(i);
                studentsLoader=new StudentsLoader();
                if(school_name==SCHOOL_SELECTION_TEXT){
                    //studentsLoader.execute((String)null);

                }else{
                    //studentsLoader.execute(school_name);
                    courseNamesList.clear();
                    courseNamesList = new ArrayList<>();
                    courseNamesAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, courseNamesList);
                    courseNamesSpinner.setAdapter(courseNamesAdapter);
                    courseNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    List<int[]> coursesLevelAndLetter = courseLevelAndLetterBySchoolName.get(school_name);
                    for (int[] courseLevelAndLetter : coursesLevelAndLetter ){
                        String courseLevelStr = Student.course_level_by_number.get(courseLevelAndLetter[0]);
                        char courseLetterStr = Student.course_letter_by_number.get(courseLevelAndLetter[1]);

                        String courseFullName = courseLevelStr+ ' ' + courseLetterStr;
                        courseNamesList.add(courseFullName);

                    }

                    courseNamesAdapter.notifyDataSetChanged();
                    //courseNamesSpinner.setSelection(0, true);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        courseNamesSpinner=inflatedView.findViewById(R.id.coursesSpinner);
        courseNamesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, courseNamesList);

        courseNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //sc.add(SCHOOL_SELECTION_TEXT);
        courseNamesSpinner.setAdapter(courseNamesAdapter);

        courseNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selectedCourseIndex, long l) {

                String schoolName = (String)schools_names_spinner.getSelectedItem();
                int[] courseLevelAndLetter = courseLevelAndLetterBySchoolName.get(schoolName).get(selectedCourseIndex);

                studentsLoader=new StudentsLoader();
                studentsLoader.execute(schoolName, courseLevelAndLetter[0], courseLevelAndLetter[1]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        students_recycler_view=inflatedView.findViewById(R.id.studentsRecycleView);
        students_recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getContext(), students_recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Student student = studentList.get(position);

                Toast.makeText(getContext(), student.getLast_name() + " is selected!", Toast.LENGTH_SHORT).show();
                //mCallback.onStudentSelected(student.getServer_id());

                CharSequence colors[] = new CharSequence[] {"Aces", "Wally", "Cubos de Corsi", "Hearts and Flowers","FonoTest"};
                final Context context = getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Seleccione un test");
                builder.setItems(colors, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        Bundle b=new Bundle();
                        b.putLong(Student.EXTRA_STUDENT_SERVER_ID,student.getServer_id());
                        Intent intent;
                        if (which==0){
                            intent = new Intent(context, AceActivity.class);
                        }else if(which==1){
                            intent = new Intent(context, WallyActivity.class);

                        }else if(which==2){
                            intent = new Intent(context, CorsiActivity.class);
                        }else if(which==3){
                            intent = new Intent(context, HnfActivity.class);
                        }
                        else{
                            intent = new Intent(context, FonoTestActivity.class);
                        }
                        intent.putExtras(b);
                        startActivityForResult(intent,1);
                    }
                });
                builder.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        studentsAdapter=new StudentsAdapter(studentList);
        RecyclerView.LayoutManager mLayoutManager= new LinearLayoutManager(getContext());
        students_recycler_view.setHasFixedSize(true);
        students_recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        students_recycler_view.setLayoutManager(mLayoutManager);
        students_recycler_view.setItemAnimator(new DefaultItemAnimator());
        students_recycler_view.setAdapter(studentsAdapter);

        SchoolsNamesLoader schoolsNamesLoader=new SchoolsNamesLoader();
        schoolsNamesLoader.execute();
        return inflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            int selectedCoursePosition = courseNamesSpinner.getSelectedItemPosition();
            String schoolName = (String)schools_names_spinner.getSelectedItem();
            int[] courseLevelAndLetter = courseLevelAndLetterBySchoolName.get(schoolName).get(selectedCoursePosition);
            studentsLoader=new StudentsLoader();
            studentsLoader.execute(schoolName, courseLevelAndLetter[0], courseLevelAndLetter[1]);

        }
    }

    public void onCourseSelected(AdapterView<?> adapterView, View view, int i, long l){

        return;
    }

    class SchoolsNamesLoader extends  AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {


            List<CourseTuple> courseTuples = databaseManager.testDatabase.daoAccess().getCourseTouples();
            //schools_names_list.add(SCHOOL_SELECTION_TEXT);
            for (CourseTuple courseTuple: courseTuples){
                courseLevelAndLetterBySchoolName.put(courseTuple.schoolName, new ArrayList<int[]>() );
            }

            for (CourseTuple courseTuple: courseTuples){

                if (!schools_names_list.contains(courseTuple.schoolName)){
                    schools_names_list.add(courseTuple.schoolName);
                }
            }
            for (CourseTuple courseTuple: courseTuples){
                courseLevelAndLetterBySchoolName.get(courseTuple.schoolName).add(new int[]{courseTuple.courseLevel,courseTuple.courseLetter});
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            schools_names_adapter.notifyDataSetChanged();

        }
    }
    class StudentsLoader extends AsyncTask<Object,Object,Student[]>{

        @Override
        protected Student[] doInBackground(Object[] objects) {

            String school_name=(String)objects[0];
            int courseLevel = (int)objects[1];
            int courseLetter = (int)objects[2];
            Student[] students;
            if(school_name==null){
                students = databaseManager.testDatabase.daoAccess().fetchAllStudents();

            }else{
                students=databaseManager.testDatabase.daoAccess().fetchStudentsBySchoolAndCourse(school_name, courseLevel, courseLetter);
            }


            studentList.clear();
            Student fake_student=new Student(" ","0 Alumno Prueba","0",(long)0){
                @Override
                String getCourseFullName() {
                    return "";
                }
            };
            studentList.add(fake_student);
            for(Student s :students){


                //getLocalEvaluationsCounts
                int acesCount = databaseManager.testDatabase.daoAccess().getTestEvaluationsCountByStudentServerId(s.getServer_id(), "ace");
                int wallyCount = databaseManager.testDatabase.daoAccess().getTestEvaluationsCountByStudentServerId(s.getServer_id(), "wally");
                int corsiCount = databaseManager.testDatabase.daoAccess().getTestEvaluationsCountByStudentServerId(s.getServer_id(), "corsi");
                int hnfCount = databaseManager.testDatabase.daoAccess().getTestEvaluationsCountByStudentServerId(s.getServer_id(), "hnf");
                int fonotestCount = databaseManager.testDatabase.daoAccess().getTestEvaluationsCountByStudentServerId(s.getServer_id(),"fonotest");


                s.aces_count=acesCount;
                s.wally_count = wallyCount;
                s.corsis_count = corsiCount;
                s.hnf_count = hnfCount;
                s.fonotest_count = fonotestCount;

                studentList.add(s);
            }
            return students;
        }


        @Override
        protected void onPostExecute(Student[] students) {
       //     studentsListView.requestLayout();
          //  ((BaseAdapter) studentsListView.getAdapter()).notifyDataSetChanged();
            studentsAdapter.notifyDataSetChanged();

            students_count_text_view.setText("Mostrando "+studentList.size()+" alumnos");

        }
    }



}
