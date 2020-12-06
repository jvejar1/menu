package com.example.e440.menu.wally_original;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.e440.menu.R;
import com.example.e440.menu.fonotest.Item;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.datatype.Duration;

public class WallyOriginalActivity extends AppCompatActivity implements AssentFragment.OnFragmentInteractionListener, ItemFragment.OnFragmentInteractionListener{


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
    public interface ActivityStatus {
        String ASSENT = "ASSENT";
        String INSTRUCTION = "INSTRUCTION";
        String ANSWERING_ITEMS ="ITEMS";
        String FINISHING = "FINISHING";}

    static String STATUS_STACK_ARG = "status_stack_arg";
    private ArrayList<String> stateStack;
    ViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wally_original);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        model = new ViewModelProvider(this).get(ViewModel.class);
        //first, display assent
        if( savedInstanceState != null){
            //restore data
            stateStack = savedInstanceState.getStringArrayList(STATUS_STACK_ARG);

            //draw all states;
        }else{

            //Initialize data
            model.LoadItems();
            stateStack = new ArrayList<>();
            stateStack.add(ActivityStatus.ASSENT);

            AssentFragment assentFragment = new AssentFragment(ASSENT_TEXT, "NO","SI");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, assentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(STATUS_STACK_ARG, stateStack);
    }

    @Override
    public void onContinue() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        switch (stateStack.get(stateStack.size()-1)){
            case ActivityStatus.ASSENT:
                //
                AssentFragment assentFragment = new AssentFragment(INSTRUCTION_TEXT, "CANCELAR", "CONTINUAR");

                fragmentTransaction.replace(R.id.frameLayout, assentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                stateStack.add(ActivityStatus.INSTRUCTION);
                break;
            case ActivityStatus.INSTRUCTION:

                WallyOriginalItem item = model.GetItem(0);
                ItemFragment itemFragment = ItemFragment.newInstance(item.getServer_id(), item.getText(), item.getEncoded_image());
                fragmentTransaction.replace(R.id.frameLayout, itemFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                stateStack.add(ActivityStatus.ANSWERING_ITEMS);

                break;
            case ActivityStatus.FINISHING:

                this.finish();
                //finish the evaluation of the instrument and save data

        }

    }

    @Override
    public void onCancel() {

        switch (stateStack.get(stateStack.size()-1)){
            case ActivityStatus.ASSENT:
                case ActivityStatus.INSTRUCTION:

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    AssentFragment assentFragment = new AssentFragment(FINISHING_TEXT, "VOLVER", "FIN");
                    fragmentTransaction.replace(R.id.frameLayout, assentFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    stateStack.add(ActivityStatus.FINISHING);
                //display the finish fragment

                break;
            case ActivityStatus.FINISHING:
                stateStack.remove(stateStack.size()-1);
                getSupportFragmentManager().popBackStack();
                if (stateStack.get(stateStack.size()-1).equals(ActivityStatus.ANSWERING_ITEMS)){

                    // ?

                }

        }    }

    @Override
    public void onItemAnswered(int itemId, String answer, int answeredItemIndex) {
        ///save the result
        //display next item

        if (model.CurrentItemIsLastItem()){

            stateStack.add(ActivityStatus.FINISHING);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            AssentFragment assentFragment = new AssentFragment(FINISHING_TEXT, "VOLVER", "FIN");
            fragmentTransaction.replace(R.id.frameLayout, assentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //display finish
            return;
        }

        model.IncrementCurrentItemIndex();

        WallyOriginalItem item = model.GetCurrentItem();
        ItemFragment itemFragment = ItemFragment.newInstance(item.getServer_id(), item.getText(), item.getEncoded_image());
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

        if (model.CurrentItemIsFirstItem()){
            stateStack.remove(stateStack.size()-1);
            return;
        }
        model.DecrementCurrentItemIndex();

    }

    @Override
    public void OnFinishRequest() {
        stateStack.add(ActivityStatus.FINISHING);
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
