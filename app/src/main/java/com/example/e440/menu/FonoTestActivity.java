package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class FonoTestActivity extends AppCompatActivity implements MainFragmentListener{
    @Override
    public void backFromTest(JSONObject jo) {
        getSupportFragmentManager().popBackStack();
        try{
            jo.put("student_id",student_id);
            jo.put("evaluator_id",null);
            jo.put("test_name","fonotest");
        }
        catch (JSONException e ){
            e.printStackTrace();
        }
        ResponseRequest responseRequest=new ResponseRequest(jo.toString(),"fonotest");
        DatabaseManager.getInstance(getApplicationContext()).insertRequest(responseRequest);
        finish();
    }
    @Override
    public void backFromPractice() {

    }

    int student_id;
    int[] fgroup_ids;
    int item_id_index;
    int[] item_ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fono_test);
        start_main_fragment();
    }
    void start_main_fragment(){
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        FonoTestMainFragment fonoTestMainFragment=new FonoTestMainFragment();
        fragmentTransaction.replace(R.id.fragment_place,fonoTestMainFragment);
        fragmentTransaction.commit();
    }

    void intelligence(int group_id_index,int item_id_index){
        if(group_id_index==fgroup_ids.length) {
            //TODO:FIISH
            return;
        }

    }


}
