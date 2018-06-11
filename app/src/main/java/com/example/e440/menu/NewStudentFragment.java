package com.example.e440.menu;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    School[] schools;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.new_student_fragment, container, false);
        networkManager=NetworkManager.getInstance(getContext());
        databaseManager= DatabaseManager.getInstance(getContext());
        Button fetchSchoolsButton=inflatedView.findViewById(R.id.fetchSchoolsButton);
        fetchSchoolsButton.setOnClickListener(this);
        Button fetchStudentsButton=inflatedView.findViewById(R.id.fetchStudentsBySchoolButton);
        fetchStudentsButton.setOnClickListener(this);
        school_names.add(SELECCIONE_STR);
        Spinner schoolSpinner=inflatedView.findViewById(R.id.schoolsSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, school_names);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);

        loadSchools();
        return inflatedView;
    }

    void loadSchools(){
        AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                schools=databaseManager.testDatabase.daoAccess().fetchAllSchools();
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
            School school=new School();
            if(what==INSERT_STUDENTS){
                int school_server_id=(int)objects[1];
                school=databaseManager.testDatabase.daoAccess().fetchSchoolByServerId(school_server_id);
                if(school==null){
                    return null;
                }
            }
            JSONArray jsonArray=(JSONArray) objects[0];
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.optJSONObject(i);
                int server_id=jsonObject.optInt("id");
                String name=jsonObject.optString("name");
                if(what==INSERT_STUDENTS){
                    String rut=jsonObject.optString("rut");
                    String last_name=jsonObject.optString("last_name");
                    Student new_student=new Student(name,last_name,rut,server_id,school.getId());
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
                else {
                    School school_in_db=databaseManager.testDatabase.daoAccess().fetchSchoolByServerId(server_id);
                    School new_school=new School(server_id,name);

                    if (school_in_db!=null){
                        new_school.setId(school_in_db.getId());
                        databaseManager.testDatabase.daoAccess().updateSchool(new_school);
                        updated_objects++;
                    }
                    else{

                        databaseManager.testDatabase.daoAccess().insertSchool(new_school);
                        inserted_objects++;

                    }
                }

            }


            if(getContext()!=null && what==INSERT_SCHOOLS){
                schools=databaseManager.testDatabase.daoAccess().fetchAllSchools();
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

        for (School s:schools){
            school_names.add(s.getName());
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.fetchSchoolsButton){
                networkManager.fetchSchools(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray schools_ja=response.optJSONArray("schools");
                        if(schools_ja!=null){
                            DataInserter dataInserter=new DataInserter(INSERT_SCHOOLS);
                            dataInserter.execute(schools_ja);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int a=1;
                    }
                });
        }

        else if(view.getId()==R.id.fetchStudentsBySchoolButton){

            Spinner schoolspinner=inflatedView.findViewById(R.id.schoolsSpinner);
            String selected=(String)schoolspinner.getSelectedItem();

            for (final School school:schools){

                if(school.getName().equals(selected)){
                    int school_db_id=school.getServer_id();
                    networkManager.fetchStudentsBySchoolId(new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray students_ja=response.optJSONArray("students");
                            int school_server_id=response.optInt("id",-1);

                            if(students_ja!=null && school_server_id!=-1){
                                DataInserter dataInserter=new DataInserter(INSERT_STUDENTS);
                                dataInserter.execute(students_ja,school_server_id);

                            }
                             }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    },school_db_id);
                }
            }

        }
    }

}
