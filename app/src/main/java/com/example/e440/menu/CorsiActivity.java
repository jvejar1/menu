package com.example.e440.menu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CorsiActivity extends AppCompatActivity implements InstructionFragment.backFromInstructionListener, CorsiMainFragment.CorsiMainFragmentListener{

    final static int ORDERED_PRACTICE=0;
    final static int ORDERED_TEST=1;
    final static int REVERSED_PRACTICE=2;
    final static int REVERSED_TEST=3;
    int next_instruction=ORDERED_PRACTICE;
    int db_request_id=0;
    @Override
    public void backFromPracticeListener() {
        next_instruction++;
        startInstructions(next_instruction);
    }

    @Override
    public void backFromOrderedTestListener(int id_db_request) {
        next_instruction++;
        startInstructions(next_instruction);

    }

    @Override
    public void finalBack() {

        finish();
    }



    @Override
    public void backFromInstruction () {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CorsiMainFragment corsiMainFragment= new CorsiMainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode",next_instruction);
        corsiMainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,corsiMainFragment);
        fragmentTransaction.commit();

    }

    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corsi);
        fragmentManager = getFragmentManager();

        startInstructions(next_instruction);

    }

    void startInstructions(int mode){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InstructionFragment corsiInstructionFragment= new InstructionFragment();
        Bundle bundle = new Bundle();
        if (next_instruction==ORDERED_PRACTICE){

            bundle.putString("text","A continuación van aparecer un conjunto de cuadrados en al pantalla. De todos ellos, algunos se van a encender en un determinado orden. Tu tarea consiste en recordar el orden en que se activaron. \n\n\tLos dos primeros son de práctica. Presiona Comenzar si lo has entendido.");
        }
        else if (next_instruction==ORDERED_TEST || next_instruction==REVERSED_TEST){
            bundle.putString("text","Si lo has entendido, presiona Comenzar y comenzamos...");

        }

        else{

            bundle.putString("text","A continuación van aparecer un conjunto de cuadrados en al pantalla. De todos ellos, algunos se van a encender en un determinado orden. Tu tarea consiste en señalar el ORDEN INVERSO en que se activaron. \n\n\tLos dos primeros son de práctica. Presiona enter si lo has entendido.");


        }


        corsiInstructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,corsiInstructionFragment);
        fragmentTransaction.commit();

    }



}
