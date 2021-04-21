package com.example.e440.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AceActivity extends BaseActivity implements InstructionFragment.backFromInstructionListener{


    @Override
    public void goBackFromMainFragment() {

    }

    @Override
    public void backFromTest(JSONObject payload) {
        databaseManager=DatabaseManager.getInstance(getApplicationContext());
        this.insertRequest(payload,"ace",0);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ace);

        TextView studentInfo = findViewById(R.id.studentInfoTextView);
        studentInfo.setText(studentFullName);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        InstructionFragment instructionFragment= new InstructionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text","“Ahora voy a mostrarte algunas imágenes de niños y niñas " +
                "y quiero que tú me digas cómo se siente cada uno. Ella/Él se " +
                "siente ¿feliz, triste, enojada/o, o asustada/o?. ¿Lo has entendido?... (espere la respuesta del niño) Comencemos.”");

        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();
    }

}
