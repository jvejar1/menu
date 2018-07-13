package com.example.e440.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

public class FonoTestActivity extends BaseActivity implements MainFragmentListener{
    @Override
    public void backFromTest(JSONObject jo) {
        getSupportFragmentManager().popBackStack();
        DatabaseManager.getInstance(getApplicationContext()).insertRequest(jo,student_id,"fonotest",0);
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
        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        student_id=b.getInt(Student.EXTRA_STUDENT_SERVER_ID);
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
