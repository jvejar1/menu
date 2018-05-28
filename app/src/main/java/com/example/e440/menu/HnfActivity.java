package com.example.e440.menu;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;

public class HnfActivity extends AppCompatActivity implements MainFragmentListener,InstructionFragment.backFromInstructionListener {

    @Override
    public void backFromInstruction() {
        startMainFragment();
        int a=1;
        return;
    }
    @Override
    public void backFromPractice() {
        current_mode+=1;
        startInstruction();
    }
    @Override
    public void backFromTest(Bundle b) {
        //TODO:PROCESS JSON_OBJECT

        if (current_mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){

            finish();
        }

        else {
            current_mode += 1;
            startInstruction();
        }
    }
    private int current_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hnf);

        current_mode=0;

        startInstruction();





        }
    void startMainFragment(){
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        HnfMainFragment hnfMainFragment =new HnfMainFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("mode",current_mode);
        hnfMainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,hnfMainFragment);
        fragmentTransaction.commit();


    }

    void startInstruction(){
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        InstructionFragment instructionFragment=new InstructionFragment();
        Bundle bundle=new Bundle();
        String text="";
        if(current_mode==HnfTest.HEARTS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de HEARTS";
        }

        else if (current_mode==HnfTest.FLOWERS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de FLOWERS";
        }
        else if(current_mode==HnfTest.HEARTS_AND_FLOWERS_PRACTICE_MODE){
            text="Ahora iniciaras la practica de HEARTS and FLOWERS";
        }
        else if(current_mode==HnfTest.HEARTS_TEST_MODE || current_mode==HnfTest.FLOWERS_TEST_MODE || current_mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){
            text="Ahora iniciaras la prueba real pero con tiempo, presiona comenzar para continuar";
        }
        bundle.putString("text",text);
        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();


    }




}
