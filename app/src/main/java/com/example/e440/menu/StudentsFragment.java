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
import android.widget.ListView;
import android.widget.Toast;
import java.util.Arrays;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.students_fragment, container, false);
        studentsListView=inflatedView.findViewById(R.id.studentsListView);
        databaseManager= DatabaseManager.getInstance(getContext());

        studentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = adapterView.getItemAtPosition(i);
                Student std = (Student) o; //As you are using Default String Adapter
                Toast.makeText(getContext(),std.getName(),Toast.LENGTH_SHORT).show();
            }
        });

        studentsListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Student item = (Student)studentsListView.getItemAtPosition(position);
                Toast.makeText(getContext(),"You selected : " + item.toString(),Toast.LENGTH_SHORT).show();
                mCallback.onStudentSelected(position);
            }
        });
        StudentsLoader studentsLoader=new StudentsLoader();
        studentsLoader.execute();
        return inflatedView;
    }


    class StudentsLoader extends AsyncTask<Void,Void,Student[]>{

        @Override
        protected Student[] doInBackground(Void... voids) {

            Student s1 = new Student("juan", "vejar", "1", 1);
            Student s2 = new Student("javier", "osores", "2", 2);
            Student s3 = new Student("camilo", "henriquez", "2", 3);

            databaseManager.testDatabase.daoAccess().insertOneStudent(s1);
            databaseManager.testDatabase.daoAccess().insertOneStudent(s2);
            databaseManager.testDatabase.daoAccess().insertOneStudent(s3);
            Student[] students=databaseManager.testDatabase.daoAccess().fetchAllStudents();
            return students;

        }

        @Override
        protected void onPostExecute(Student[] students) {

            ListAdapter customAdapter = new ListAdapter(getContext(), R.layout.students_list_item, Arrays.asList(students));
            studentsListView.setAdapter(customAdapter);
            studentsListView.setClickable(true);


        }
    }



}
