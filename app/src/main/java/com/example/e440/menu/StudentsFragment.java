package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by e440 on 23-04-18.
 */

public class StudentsFragment extends Fragment {

    View inflatedView;
   ListView studentsListView;
    DatabaseManager databaseManager;

    OnStudentSelectedListener mCallback;


    public interface OnStudentSelectedListener {
        public void onStudentSelected(int student_id);
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

    RecyclerView students_recycler_view;
    List<Student> studentList=new ArrayList<>();
    StudentsAdapter studentsAdapter;
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

        students_recycler_view=inflatedView.findViewById(R.id.studentsRecycleView);
        students_recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getContext(), students_recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Student student = studentList.get(position);
                Toast.makeText(getContext(), student.getLast_name() + " is selected!", Toast.LENGTH_SHORT).show();
                mCallback.onStudentSelected(student.getServer_id());
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
        StudentsLoader studentsLoader=new StudentsLoader();
        studentsLoader.execute();

        return inflatedView;
    }



    class StudentsLoader extends AsyncTask<Void,Void,Student[]>{

        @Override
        protected Student[] doInBackground(Void... voids) {

            Student[] students=databaseManager.testDatabase.daoAccess().fetchAllStudents();


            for (Student student:students){
                studentList.add(student);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Student[] students) {
       //     studentsListView.requestLayout();
          //  ((BaseAdapter) studentsListView.getAdapter()).notifyDataSetChanged();
            studentsAdapter.notifyDataSetChanged();

        }
    }



}
