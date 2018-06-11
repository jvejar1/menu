package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

    List<Student> studentList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.students_fragment, container, false);
        studentsListView=inflatedView.findViewById(R.id.studentsListView);
        databaseManager= DatabaseManager.getInstance(getContext());


        studentsListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                mCallback.onStudentSelected(position);
            }
        });
        Student s1 = new Student("01 Alumno prueba","01 Alumno prueba","0",0);
        studentList.add(s1);
        ListAdapter customAdapter = new ListAdapter(getContext(), R.layout.students_list_item, studentList);
        studentsListView.setAdapter(customAdapter);
        studentsListView.setClickable(true);
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
            ((BaseAdapter) studentsListView.getAdapter()).notifyDataSetChanged();

        }
    }



}
