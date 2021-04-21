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
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

public class WallyOriginalActivity extends AppCompatActivity implements AssentFragment.OnFragmentInteractionListener, ItemFragment.OnFragmentInteractionListener{


    static int CONTIBUABLE_EVALUATION_ENCOUNTERED = 0;

    static int NOTHING_CONTINUABLE_EVALUATION_ENCOUNTERED = 1;

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

            stateStack = new ArrayList<>();
            stateStack.add(ActivityStatus.ASSENT);

            //final ItemsBank instrument = (ItemsBank) getIntent().getExtras().getSerializable("bank");
            int instrumentIndex = getIntent().getExtras().getInt(EXTRA.EXTRA_INSTRUMENT_INDEX);
            final ItemsBank instrument = InstrumentsManager.getInstance(this).getInstruments().get(instrumentIndex);

            final Long studentId = getIntent().getExtras().getLong(Student.EXTRA_STUDENT_SERVER_ID);

            model.items = instrument.items;

            INSTRUCTION_TEXT = instrument.instruction;

            Evaluation evaluation = new Evaluation();
            evaluation.itemAnswerList = new ArrayList<>();
            evaluation.instrumentId = instrument.id;
            evaluation.timestamp = new Timestamp(System.currentTimeMillis());
            evaluation.studentId =  studentId;
            evaluation.itemsList = instrument.items;
            evaluation.setFinished(false);

            long userId = CredentialsManager.getInstance(this).getUserId();
            evaluation.setUserId(userId);

            model.setEvaluation(evaluation);


            /*
            NetworkManager networkManager = NetworkManager.getInstance(this);

            Gson gson = new Gson();
            String jsonObject = gson.toJson(evaluation);
            JsonElement a = gson.toJsonTree(evaluation);

            try{

                JSONObject j = new JSONObject(jsonObject);


               /* networkManager.sendEvaluation(j, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("res",response.toString());
                        ;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("res", error.toString());
                        ;
                    }
                });
                Log.d("Pato",j.toString());

                Log.d("Patoa",jsonObject);

                Log.d("Patob",a.toString());

            }catch(JSONException e){
                Log.d("Pato","malo");
            }

            ResponseRequest responseRequest = new ResponseRequest(jsonObject, null, false, evaluation.studentId);
            responseRequest.setFinished(true);



            databaseManager.insertResponseRequestAsync(evaluation);
            final DatabaseManager databaseManager = DatabaseManager.getInstance(this);
            final Context context = this;
            Looper looper = getMainLooper();

            final Handler handler1 = new Handler(looper){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);


                    Log.d("Handling msg", msg.toString());
                    if (msg.what == CONTIBUABLE_EVALUATION_ENCOUNTERED){
                        final Evaluation evaluation = (Evaluation) msg.obj;
                        post(new Runnable() {
                            @Override
                            public void run() {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                //Yes button clicked

                                                model = new ViewModelProvider((WallyOriginalActivity)context).get(ViewModel.class);
                                                model.setEvaluation(evaluation);


                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                evaluation.setFinished(true);
                                                databaseManager.insertResponseRequestAsync(evaluation);
                                                break;
                                        }
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("¿Continuar con la última evaluación?").setPositiveButton("Si", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            }
                        });

                    }
                    else if(msg.what == NOTHING_CONTINUABLE_EVALUATION_ENCOUNTERED){


                    }
                }
            };


            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Evaluation e = databaseManager.checkIfCanContinueAnEvaluation(studentId, instrument);

                    if (e!=null){
                        Message messsage = new Message();
                        messsage.what = CONTIBUABLE_EVALUATION_ENCOUNTERED;

                        messsage.obj = e;
                        handler1.sendMessage(messsage);
                    }
                    else{

                        Message messsage = new Message();
                        messsage.what = NOTHING_CONTINUABLE_EVALUATION_ENCOUNTERED;
                        messsage.obj = e;
                        handler1.sendMessage(messsage);
                    }
                }

            };

            //Thread t = new Thread(r);
            //t.start();

            File folder = new File(  this.getFilesDir(), "evaluations/");
            folder.mkdirs();
            String folderPath = folder.getPath();
            File file = new File(folderPath, "eval.json");
            try {

                file.createNewFile();
                FileOutputStream fOut2 = new FileOutputStream(file);


                ObjectOutputStream out = new ObjectOutputStream(fOut2);
                out.writeObject(evaluation);
                out.close();
                Log.d("aa", "Serialized data to "+ file.getPath());

                ;}catch (IOException exc){
                Log.d("Perro", "gato");
                Log.d("sapo", exc.getMessage());}

*/

           AssentFragment assentFragment = new AssentFragment(ASSENT_TEXT, "NO","SI");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, assentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }

    interface TaskCallback{
        void onComplete(Evaluation e);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(STATUS_STACK_ARG, stateStack);
    }

    @Override
    public void onContinue() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        Log.d("Stack: ", stateStack.toString());
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
                Evaluation evaluation = model.getEvaluation();

                evaluation.setFinished(true);
                DatabaseManager.getInstance(this).insertResponseRequestAsync(evaluation);

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
