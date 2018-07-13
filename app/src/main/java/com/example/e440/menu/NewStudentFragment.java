package com.example.e440.menu;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e440 on 24-04-18.
 */

public class NewStudentFragment extends Fragment implements View.OnClickListener{
    View inflatedView;
    DatabaseManager databaseManager;
    List<String> school_names=new ArrayList<>();
    final int INSERT_SCHOOLS=0;
    final int INSERT_STUDENTS=1;
    final String SELECCIONE_STR="SELECCIONE";
    NetworkManager networkManager;
    Course[] schools;
    HashMap<String,Long> course_id_by_full_name=new HashMap<>();
    HashMap<String,Long> school_id_by_school_name=new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.new_student_fragment, container, false);
        networkManager=NetworkManager.getInstance(getContext());
        databaseManager= DatabaseManager.getInstance(getContext());
        Button fetchStudentsButton=inflatedView.findViewById(R.id.fetchStudentsBySchoolButton);
        fetchStudentsButton.setOnClickListener(this);
        school_names.add(SELECCIONE_STR);
        Spinner schoolSpinner=inflatedView.findViewById(R.id.schoolsSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, school_names);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);

        networkManager.fetchCourses(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray courses_ja=response.optJSONArray("courses");
                if(courses_ja!=null){

                    DataInserter dataInserter=new DataInserter(INSERT_SCHOOLS);
                    for(int i=0;i<courses_ja.length();i++){
                        JSONObject course_jo=courses_ja.optJSONObject(i);
                        String course_full_name=course_jo.optString("school_name")+" - "+ course_jo.optString("level")+" "+course_jo.optString("letter");
        //                school_names.add(course_full_name);

                        long course_id=course_jo.optLong("id");
                        course_id_by_full_name.put(course_full_name,course_id);

                    }

                }
                JSONArray schools_ja=response.optJSONArray("schools");
                if(schools_ja!=null){
                    for(int i=0; i<schools_ja.length();i++){
                        JSONObject school_json_object=schools_ja.optJSONObject(i);
                        String school_name=school_json_object.optString("name");
                        long school_server_id=school_json_object.optLong("id");
                        school_id_by_school_name.put(school_name,school_server_id);
                        school_names.add(school_name);

                    }
                    int a =1;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int a=1;
            }
        });
        return inflatedView;
    }

    void loadSchools(){
        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                schools=databaseManager.testDatabase.daoAccess().fetchAllCourses();
                generate_schools_names();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

            }
        }.execute();
    }


    class DataInserter extends AsyncTask{ //first parameter will be 0,1 (insert schools,insert students), second parameter is json array containing the data
        int what;
        int inserted_objects;
        int updated_objects;
        DataInserter(int what){
            this.what=what;
            this.inserted_objects=0;
            this.updated_objects=0;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            databaseManager= DatabaseManager.getInstance(getContext());

            JSONArray jsonArray=(JSONArray) objects[0];
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.optJSONObject(i);
                int server_id=jsonObject.optInt("id");
                if(what==INSERT_STUDENTS){
                    String name=jsonObject.optString("name");

                    String rut=jsonObject.optString("rut");
                    String last_name=jsonObject.optString("last_name");
                    Student new_student=new Student(name,last_name,rut,server_id,1);
                    Student old_student=databaseManager.testDatabase.daoAccess().fetchStudentByServerId(server_id);
                    if(old_student!=null){
                        new_student.setId(old_student.getId());
                        databaseManager.testDatabase.daoAccess().updateStudent(new_student);
                        updated_objects++;
                    }
                    else{

                        databaseManager.testDatabase.daoAccess().insertOneStudent(new_student);
                        inserted_objects++;
                    }
                }

            }


            if(getContext()!=null && what==INSERT_SCHOOLS){
                schools=databaseManager.testDatabase.daoAccess().fetchAllCourses();
                generate_schools_names();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String object=(what==INSERT_SCHOOLS)?"escuela(s)":(what==INSERT_STUDENTS)?"estudiante(s)":"";
            if(getContext()!=null){
                Toast.makeText(getContext(),"Se han insertado "+inserted_objects+" y actualizado "+updated_objects+" "+object+" exitosamente...",Toast.LENGTH_LONG).show();
            }
        }
    }

    void generate_schools_names(){
        school_names.subList(1,school_names.size()).clear();

        for (Course s:schools){
            school_names.add(s.getName());
        }
    }
    @Override
    public void onClick(View view) {


        if(view.getId()==R.id.fetchStudentsBySchoolButton){

            Spinner schoolspinner=inflatedView.findViewById(R.id.schoolsSpinner);
            String selected=(String)schoolspinner.getSelectedItem();



            if(selected!=SELECCIONE_STR) {
                long school_server_id=school_id_by_school_name.get(selected);

                networkManager.fetchStudentsBySchoolId(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray students_ja = response.optJSONArray("students");
                        int school_server_id = response.optInt("id", -1);

                        if (students_ja != null && school_server_id != -1) {
                            DataInserter dataInserter = new DataInserter(INSERT_STUDENTS);
                            dataInserter.execute(students_ja);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }, school_server_id);
            }



        }
    }

}
