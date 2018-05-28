package com.example.e440.menu;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

public class AceActivity extends AppCompatActivity implements InstructionFragment.backFromInstructionListener,MainFragmentListener{


    @Override
    public void backFromTest(Bundle b) {
        finish();
    }

    @Override
    public void backFromPractice() {

    }

    @Override
    public void backFromInstruction() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        AceMainFragment aceFragment= new AceMainFragment();
        fragmentTransaction.replace(R.id.fragment_place,aceFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ace);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        InstructionFragment instructionFragment= new InstructionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text","“Ahora voy a mostrarte algunas imágenes de niños y niñas," +
                "y quiero que tú me digas cómo se siente cada uno. Ella/Él se" +
                "siente ¿feliz, triste, enojada/o, o asustada/o?”");

        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();
    }
}
