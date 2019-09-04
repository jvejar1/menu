package com.example.e440.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

public class FonoTestActivity extends BaseActivity{
    @Override
    public void backFromTest(JSONObject jo) {
        getSupportFragmentManager().popBackStack();
        this.insertRequest(jo,"fonotest",0);
        finish();
    }

    @Override
    public void goBackFromMainFragment() {

    }

    @Override
    public void backFromPractice() {
    }

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



}
