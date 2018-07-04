package com.example.e440.menu;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HnfActivity extends BaseActivity implements MainFragmentListener,InstructionFragment.backFromInstructionListener {

    @Override
    public void backFromInstruction() {
        startMainFragment();
        return;
    }
    @Override
    public void backFromPractice() {
        current_mode+=1;
        startInstruction();
    }
    @Override
    public void backFromTest(JSONObject jo) {
        //TODO:PROCESS JSON_OBJECT
        int score=jo.optInt("score");
        if (current_mode==HnfTest.HEARTS_AND_FLOWERS_TEST_MODE){

            try{
                result.put("hnf_score",score);
                result.put("test_id",hnfSet.getServer_id());
            }

            catch (JSONException e){
                e.printStackTrace();
            }

            DatabaseManager.getInstance(getApplicationContext()).insertRequest(result,student_server_id,"hnf",0);
            finish();

        }

        else {

            try{
                if(current_mode==HnfTest.HEARTS_TEST_MODE){

                    result.put("hearts_score",score);

                }
                else{

                    result.put("flowers_score",score);
                }
            }

            catch (JSONException e){
                e.printStackTrace();
            }
            current_mode += 1;
            startInstruction();
        }
    }
    JSONObject result;
    private int current_mode;
    private HnfSet hnfSet;
    int student_server_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hnf);

        Bundle extras= getIntent().getExtras();
        student_server_id=extras.getInt(Student.EXTRA_STUDENT_SERVER_ID);
        current_mode=0;
        result=new JSONObject();
        AsyncTask asyncTask =new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                hnfSet= DatabaseManager.getInstance(getApplicationContext()).testDatabase.daoAccess().fetchHnfSet();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                startInstruction();
                super.onPostExecute(o);
            }
        }.execute();





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
        int drawable1=-1;
        int drawable2=-1;
        if(current_mode==HnfTest.HEARTS_PRACTICE_MODE){
            text="Bienvenido a la prueba de CORAZONES, es bastante simple. Cada vez que aparece un corazón, debes presionar el botón que está en el mismo lado que el corazón. (Entonces si aparece acá, presionas este. Y si aparece acá, presionas este botón) El que viene a continuación es un ensayo. ¿Lo has entendido?... (espere la respuesta del niño) ....Comencemos...";
            drawable1=R.drawable.heart_example;
            drawable2=R.drawable.heart_example1;
        }

        else if (current_mode==HnfTest.FLOWERS_PRACTICE_MODE){
            text="Ahora viene la prueba de FLORES. Cada vez que aparece una flor, debes presionar el boton que está en el LADO CONTRARIO a la flor. (Entonces si aparece acá, presionas este. Y si aparece acá, presionas este botón). ¿Lo has entendido?... (espere la respuesta del niño) ....Comencemos...";
            drawable1=R.drawable.flower_example;
            drawable2=R.drawable.flower_example1;
        }
        else if(current_mode==HnfTest.HEARTS_TEST_MODE ){
            text="Ahora iniciarás la prueba de CORAZONES pero contra el tiempo. Recuerda que debes presionar el boton que está en el MISMO LADO. ¡Mucha suerte!";
        }
        else if(current_mode==HnfTest.FLOWERS_TEST_MODE){

            text="Ahora iniciarás la prueba de FLORES pero contra el tiempo. Recuerda, cada vez que aparece una flor debes presionar el botón que está en el LADO CONTRARIO. ¡Mucha suerte! .... Comencemos ...";

        }

        else{

            text="En esta prueba aparecerán CORAZONES y FLORES. Recuerda," +
                    "CORAZONES al MISMO LADO," +
                    "FLORES al LADO CONTRARIO. Tendrás que hacerlo contra el tiempo. ¡Mucha suerte!...Comencemos...";

        }
        bundle.putString("text",text);
        bundle.putInt("drawable1",drawable1);

        bundle.putInt("drawable2",drawable2);
        instructionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_place,instructionFragment);
        fragmentTransaction.commit();


    }




}
