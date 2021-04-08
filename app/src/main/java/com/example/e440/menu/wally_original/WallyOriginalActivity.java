package com.example.e440.menu.wally_original;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.CredentialsManager;
import com.example.e440.menu.DatabaseManager;
import com.example.e440.menu.EXTRA;
import com.example.e440.menu.NetworkManager;
import com.example.e440.menu.R;
import com.example.e440.menu.ResponseRequest;
import com.example.e440.menu.Student;
import com.example.e440.menu.fonotest.Item;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

public class WallyOriginalActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener{

    static String ASSENT_TEXT = "Hola, mi nombre es _____________ (ayudante de investigación), vendremos varias veces " +
            "a tu escuela y queremos que participes en un juego que está en una Tablet y que respondas. " +
            "Si no quieres seguir, me dices y paramos. No hay ningún problema, nadie se enojará " +
            "contigo. ¿Quieres participar?";
    static String INSTRUCTION_TEXT = "Aquí hay una prueba de detectives para ver qué tan bueno eres como detective " +
            "para resolver problemas. Aquí tienes tu sombrero de detective, " +
            "si quieres usarlo (usted deberá hacer la acción de entregarle un sombrero imaginario). " +
            "Te mostraré algunas imágenes de escenas donde aparecen problemas. " +
            "Ve si puedes resolverlos por tu cuenta. ¡Buena suerte!";
    static String FINISHING_TEXT = "MUCHAS GRACIAS";
    static String STATUS_STACK_ARG = "status_stack_arg";
    private ArrayList<String> stateStack;
    ViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wally_original);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        model = new ViewModelProvider(this).get(ViewModel.class);

        if( savedInstanceState != null){

        }else{

            stateStack = new ArrayList<>();
            int instrumentIndex = getIntent().getExtras().getInt(EXTRA.EXTRA_INSTRUMENT_INDEX);
            final ItemsBank instrument = InstrumentsManager.getInstance(this).getInstruments().get(instrumentIndex);

            final Long studentId = getIntent().getExtras().getLong(Student.EXTRA_STUDENT_SERVER_ID);


            instrument.items.add(0,new WallyOriginalItem(ASSENT_TEXT, null, 0, 1));
            instrument.items.add(1,new WallyOriginalItem(INSTRUCTION_TEXT, null, 0, 1));
            model.items = instrument.items;

            INSTRUCTION_TEXT = instrument.instruction;

            Evaluation evaluation = new Evaluation();
            evaluation.itemAnswerList = new ArrayList<>();
            evaluation.instrumentId = instrument.id;
            evaluation.timestamp = new Timestamp(System.currentTimeMillis());
            evaluation.studentId =  studentId;
            evaluation.itemsList = instrument.items;
            evaluation.itemWithAnswers = new ArrayList<>();
            for (int i=1; i<instrument.items.size(); i++){

                WallyOriginalItem item = instrument.items.get(i);
                if(i != 0 || i != 1 ){
                    item.itemTypeId = 3;
                }
                evaluation.itemWithAnswers.add(new ItemWithAnswer(item));
            }

            evaluation.setFinished(false);
            long userId = CredentialsManager.getInstance(this).getUserId();
            evaluation.setUserId(userId);

            model.setEvaluation(evaluation);

            ItemFragment itemFragment = ItemFragment.newInstance(0,"wadawdawddwa","",false);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, itemFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void OnAllItemsFinished() {

        Evaluation evaluation = model.getEvaluation();
        evaluation.setFinished(true);
        DatabaseManager.getInstance(this).insertResponseRequestAsync(evaluation);

        this.finish();
    }


    @Override
    public void onItemAnswered(int itemId, String answer, int answeredItemIndex) {

        model.IncrementCurrentItemIndex();
        ItemFragment itemFragment = ItemFragment.newInstance(0,"", "", false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, itemFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
/*
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice("90:2B:D2:4A:86:FE");
        ConnectThread connectThread = new ConnectThread(bluetoothDevice);
        connectThread.run();*/
    }

    @Override
    public void OnItemBack() {
        //use the backstack transaction
        getSupportFragmentManager().popBackStack();
        model.DecrementCurrentItemIndex();

    }

    @Override
    public void OnFinishRequest() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AssentFragment assentFragment = new AssentFragment(FINISHING_TEXT, "VOLVER", "FIN");
        fragmentTransaction.replace(R.id.frameLayout, assentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        MyBluetoothService.GetInstance(this).finish();
        super.onDestroy();

    }
}
