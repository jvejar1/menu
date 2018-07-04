package com.example.e440.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class AceActivity extends BaseActivity implements InstructionFragment.backFromInstructionListener,MainFragmentListener{


    @Override
    public void backFromTest(JSONObject payload) {
        databaseManager=DatabaseManager.getInstance(getApplicationContext());
        ResponseRequest responseRequest=new ResponseRequest(payload.toString(),"ace");
        databaseManager.insertRequest(payload,student_server_id,"ace",0);
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

    DatabaseManager databaseManager;
    int student_server_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ace);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        InstructionFragment instructionFragment= new InstructionFragment();
        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        student_server_id=b.getInt("student_server_id");
        Bundle bundle = new Bundle();
        bundle.putString("text","“Ahora voy a mostrarte algunas imágenes de niños y niñas," +
                "y quiero que tú me digas cómo se siente cada uno. Ella/Él se " +
                "siente ¿feliz, triste, enojada/o, o asustada/o?. ¿Lo has entendido?... (espere la respuesta del niño) Comencemos.”");

        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();
    }
}
