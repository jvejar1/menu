package com.example.e440.menu;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;
import com.example.e440.menu.wally_original.InstrumentsManager;
import com.example.e440.menu.wally_original.ItemsBank;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,StudentsFragment.OnStudentSelectedListener {



    AlertDialog needsGetTestInfoAlertDialog;
    CredentialsManager credentialsManager;
    int LOGIN_REQUEST = 1;
    String STUDENTS_FILENAME = "students.json";
    final int ACES =0;
    final int WALLY=1;
    final int CORSI=2;
    final int HNF=1;
    private HashMap<Integer,Boolean> available_tests;
    NetworkManager networkManager;
    String ACES_PACKAGE_NAME = "com.example.e440.aces";
    ImageView imgview;
    FragmentManager fragmentManager;
    ProgressDialog mProgressDialog;

    DatabaseManager databaseManager;

    public void onStudentSelected(final Long studentId) {
        CharSequence colors[] = new CharSequence[] {"Aces", "Wally", "Cubos de Corsi", "Hearts and Flowers","Fonológico"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un test");
        builder.setItems(colors, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                Bundle b=new Bundle();
                b.putLong(Student.EXTRA_STUDENT_SERVER_ID,studentId);
                Intent intent;
                if (which==0){
                    intent = new Intent(getApplicationContext(), AceActivity.class);
                }else if(which==1){
                    intent = new Intent(getApplicationContext(), WallyActivity.class);

                }else if(which==2){
                    intent = new Intent(getApplicationContext(), CorsiActivity.class);
                }else if(which==3){
                    intent = new Intent(getApplicationContext(), HnfActivity.class);
                }
                else{
                    intent = new Intent(getApplicationContext(), FonoTestActivity.class);
                }
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        builder.show();
        return;

    }

    private FusedLocationProviderClient mFusedLocationClient;

    protected void onCreate(Bundle savedInstanceState) {

        Configuration configuration = getResources().getConfiguration();
        int smallestWidthDp  = configuration.smallestScreenWidthDp;
        Log.d("SELB smallest width", Integer.toString(smallestWidthDp));

        int screenWidthDp = configuration.screenWidthDp;
        Log.d("SELB screen width", Integer.toString(screenWidthDp));

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        Log.d("SELB display metrics h ", Integer.toString(dm.heightPixels));

        Log.d("SELB display metrics w", Integer.toString(dm.widthPixels));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        networkManager = NetworkManager.getInstance(this);
        databaseManager = DatabaseManager.getInstance(this);


        imgview = findViewById(R.id.imageView);

        fragmentManager = getFragmentManager();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        credentialsManager = CredentialsManager.getInstance(this);



        //databaseManager.sendAllResults(networkManager);


        checkAllTestAreReady();

        if (credentialsManager.getToken() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            //  intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==LOGIN_REQUEST){

            if(resultCode== Activity.RESULT_OK){
                //checkAllTestAreReady();
            }
            else{
                finish();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        final Handler handler = new Handler(this.getMainLooper()){

            };

       /* if(credentialsManager.isFirstRun()){

            Thread db_thread = new Thread(){
                @Override
                public void run() {
                    databaseManager.testDatabase.daoAccess().deleteAllStudents();
                    databaseManager.cleanAce();
                    databaseManager.cleanCorsi();
                    databaseManager.cleanWally();
                    databaseManager.cleanHnf();
                    databaseManager.cleanFonotest();
                    credentialsManager.setAllTestUnready();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            checkAllTestAreReady();
                        }
                    });
                }
            };
            db_thread.start();

        }*/



    }


    void checkAllTestAreReady() {
        if (!credentialsManager.AllTestAreReady()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage("Se necesita descargar información para realizar los tests, asegúrese de tener una buena conexión a intenet y presione OK para continuar");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestInfoToServer();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }


    void requestInfoToServer() {

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setCancelable(false);
        // Set progressdialog title
        mProgressDialog.setTitle("Solicitando datos al servidor");
        // Set progressdialog message
        mProgressDialog.setMessage("Por favor espere...");
        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.show();

        networkManager.getAll(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mProgressDialog.dismiss();

                InsertAll ia = new InsertAll();
                ia.execute(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                checkAllTestAreReady();
            }
        });
    }


    private class InsertAll extends AsyncTask<JSONObject, Object, Object> {

        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            mProgressDialog.setMessage(progress[0]+" % completado");
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            // Set progressdialog title
            mProgressDialog.setTitle("Guardando Datos");
            // Set progressdialog message
            mProgressDialog.setMessage("Por favor espere...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }


        @Override
        protected Object doInBackground(JSONObject... jsonObjects) {

            int current_progress=0;
            publishProgress(current_progress);

            JSONObject response = jsonObjects[0];

            JSONObject fonotest_jo=response.optJSONObject("fonotest");

            if (fonotest_jo!=null){
               // databaseManager.testDatabase.daoAccess().cleanFonotest();
                databaseManager.cleanFonotest();
                int fonotest_server_id=fonotest_jo.optInt("id");
                float fonotest_version=(float)fonotest_jo.optDouble("version");
                FonoTest fonoTest=new FonoTest(fonotest_server_id,false);
                databaseManager.testDatabase.daoAccess().insertFonoTest(fonoTest);
                JSONArray items =fonotest_jo.optJSONArray("items");
                float fonotest_percent_count=0;
                float fonotest_unitary_percent=(float)20/items.length();
                int errors=0;
                for (int j=0;j<items.length();j++){
                    JSONObject item_jo=items.optJSONObject(j);
                    String item_description=item_jo.optString("description");
                    int item_index=item_jo.optInt("index",0);
                    int item_server_id=item_jo.optInt("id");
                    int audio_id=item_jo.optInt("audio_id",-1);
                    String item_instruction=item_jo.optString("instruction");
                    boolean example=item_jo.optBoolean("example");
                    String audio_path=null;
                    if(audio_id!=-1){
                        audio_path=downloadAudio(audio_id,NetworkManager.BASE_URL);
                    }
                    String correct_sequence=item_jo.optString("correct_sequence");

                    String name=item_jo.optString("name");

                    if(audio_path==null && audio_id!=-1){
                        errors++;
                    }
                    Item item=new Item(correct_sequence,item_description,item_server_id,item_index,item_instruction,example,name);
                    item.setAudio_path(audio_path);
                    databaseManager.testDatabase.daoAccess().insertOneItem(item);
                    fonotest_percent_count+=fonotest_unitary_percent;
                    publishProgress(current_progress+(int)fonotest_percent_count);
                }
                if(errors==0){
                    
                    credentialsManager.setTestAvailability(Utilities.FONOTEST_NAME,true);
                }






            }
            current_progress=20;
            publishProgress(current_progress);

            JSONObject hnf_jo=response.optJSONObject("hnf");

            if(hnf_jo!=null){
           //     databaseManager.testDatabase.daoAccess().cleanHnf();
                databaseManager.cleanHnf();
                int hnf_server_id=hnf_jo.optInt("id");
                float hnf_version=(float)hnf_jo.optDouble("version");
                HnfSet hnfSet=new HnfSet(hnf_server_id,hnf_version);
                databaseManager.testDatabase.daoAccess().insertHnfSet(hnfSet);
                JSONArray test_ja=hnf_jo.optJSONArray("set");
                for (int i=0;i<test_ja.length();i++){

                    JSONObject test_jo=test_ja.optJSONObject(i);
                    int test_server_id=test_jo.optInt("id");
                    int test_type =test_jo.optInt("hnf_type");
                    JSONArray figures_ja=test_jo.optJSONArray("sequence");
                    HnfTest hnfTest=new HnfTest(test_server_id,test_type);
                    long hnf_test_id=databaseManager.testDatabase.daoAccess().insertOneHnfTest(hnfTest);
                    for(int j=0;j<figures_ja.length();j++){

                        JSONObject figure_jo=figures_ja.optJSONObject(j);
                        int figure_server_id=figure_jo.optInt("id");
                        int figure_number=figure_jo.optInt("figure");
                        int figure_position=figure_jo.optInt("position");
                        int figure_index=figure_jo.optInt("index");
                        HnfFigure hnfFigure=new HnfFigure(figure_number,(int)hnf_test_id,figure_server_id,figure_position,figure_index);
                        long hnf_figure_id = databaseManager.testDatabase.daoAccess().insertOneHnfFigure(hnfFigure);
                        int a =1;
                    }

                }
                credentialsManager.setTestAvailability(Utilities.HNF_NAME,true);

            }
            current_progress=40;
            publishProgress(current_progress);
            JSONObject corsi_jo=response.optJSONObject("corsi");
            if (corsi_jo==null){
                //handle

            }
            else{
          //      databaseManager.testDatabase.daoAccess().cleanCorsi();
                databaseManager.cleanCorsi();
                int corsi_id = corsi_jo.optInt("id");
                float version=(float)corsi_jo.optDouble("version");
                Corsi corsi=new Corsi(corsi_id,version);
                databaseManager.testDatabase.daoAccess().insertCorsi(corsi);
                JSONArray set=corsi_jo.optJSONArray("set");
                for (int i=0;i<set.length();i++){
                    JSONObject sequence_jo = set.optJSONObject(i);
                    int sequence_server_id = sequence_jo.optInt("id");
                    int sequence_index= sequence_jo.optInt("index");
                    boolean sequence_ordered=sequence_jo.optBoolean("ordered");
                    String csequence_str=sequence_jo.optString("csequence");
                    boolean example=sequence_jo.optBoolean("example");
                    Csequence csequence = new Csequence(sequence_server_id,sequence_index,sequence_ordered,example,csequence_str);
                    long csequence_id = databaseManager.testDatabase.daoAccess().insertCSequence(csequence);
                }
                credentialsManager.setTestAvailability(Utilities.CORSI_NAME,true);


            }

            current_progress=60;

            publishProgress(current_progress);
            JSONObject aceJO = response.optJSONObject("ace");
            if(aceJO!=null){
         //     databaseManager.testDatabase.daoAccess().cleanAce();
                databaseManager.cleanAce();
                int ace_id=aceJO.optInt("id");
                float ace_version=(float)aceJO.optDouble("version");
                Ace ace=new Ace(ace_version,ace_id);
                databaseManager.testDatabase.daoAccess().insertAce(ace);
                JSONArray acasesJA = aceJO.optJSONArray("acases");
                int errors=0;
                for (int i = 0; i < acasesJA.length(); i++) {

                    JSONObject acaseJO = acasesJA.optJSONObject(i);
                    int acase_server_id = acaseJO.optInt("id");
                    int acase_index = acaseJO.optInt("index");
                    int picture_id = acaseJO.optInt("picture_id");
                    //try to DL image
                    String description=acaseJO.optString("description");
                    char sex=acaseJO.optString("sex").charAt(0);
                    byte[] image_bytes= downloadAsByteArray(picture_id,NetworkManager.BASE_URL+"/pictures","vertical");
                    Acase acase = new Acase(acase_index, acase_server_id, image_bytes,description,sex);

                    if(image_bytes==null){
                        errors++;
                    }
                    databaseManager.testDatabase.daoAccess().insertAcase(acase);
                }
                if(errors==0){
                    credentialsManager.setTestAvailability(Utilities.ACE_NAME,true);
                }


            }

            ///// WALLY
            current_progress=80;
            publishProgress(current_progress);


            JSONObject wally_jo = response.optJSONObject("wally");


            if(wally_jo!=null){
                databaseManager.cleanWally();
          //      databaseManager.testDatabase.daoAccess().cleanWally();
                int wally_server_id = wally_jo.optInt("id");
                Wally w = new Wally(1, "", wally_server_id);
                databaseManager.testDatabase.daoAccess().insertWally(w);

                JSONArray feelings_ja = wally_jo.optJSONArray("feelings");
                int errors=0;
                for (int j = 0; j < feelings_ja.length(); j++) {
                    JSONObject feeling_jo = feelings_ja.optJSONObject(j);
                    int feeling_server_id = feeling_jo.optInt("id");
                    int feeling_image_id = feeling_jo.optInt("picture_id");

                    int feeling_wfeeling=feeling_jo.optInt("wfeeling");
                    byte[] feeling_image_bytes = downloadAsByteArray(feeling_image_id,NetworkManager.BASE_URL+"/pictures","squared");

                    if(feeling_image_bytes==null){
                        errors++;
                    }

                    WFeeling wf=new WFeeling(feeling_server_id,feeling_wfeeling,feeling_image_bytes);
                    databaseManager.testDatabase.daoAccess().insertWFeeling(wf);

                }

                JSONArray wsituations_ja = wally_jo.optJSONArray("wsituations");

                float wally_unitary_percent=(float)20/wsituations_ja.length();

                float completed_percent=0;
                for (int i = 0; i < wsituations_ja.length(); i++) {
                    JSONObject wsituation_jo = wsituations_ja.optJSONObject(i);
                    int wsituation_server_id = wsituation_jo.optInt("id");
                    int wsituation_image_id = wsituation_jo.optInt("picture_id");
                    byte[] wsituation_image_bytes = downloadAsByteArray(wsituation_image_id,NetworkManager.BASE_URL+"/pictures","horizontal");

                    if(wsituation_image_bytes==null){

                        errors++;
                    }
                    String wsituation_description = wsituation_jo.optString("description");
                    WSituation ws = new WSituation(wsituation_server_id,wsituation_description,wsituation_image_bytes);
                    long ws_id=databaseManager.testDatabase.daoAccess().insertWSituation(ws);
                    completed_percent+=wally_unitary_percent;

                    publishProgress(current_progress+(int)completed_percent);

                    //parse Wreactions

                    JSONArray wreactions_ja = wsituation_jo.optJSONArray("wreactions");

                    for (int j = 0; j < wreactions_ja.length(); j++) {

                        JSONObject wreaction_jo = wreactions_ja.optJSONObject(j);
                        int wreaction_server_id = wreaction_jo.optInt("wreaction_id");
                        int wreaction_image_id = wreaction_jo.optInt("picture_id");
                        int wreaction_wreaction=wreaction_jo.optInt("wreaction");
                        String wreaction_description =wreaction_jo.optString("description");
                        byte[] wreaction_image_bytes = downloadAsByteArray(wreaction_image_id,NetworkManager.BASE_URL+"/pictures","horizontal");
                        if(wreaction_image_bytes==null){

                            errors++;
                        }
                        WReaction wa = new WReaction(ws_id,wreaction_server_id,wreaction_wreaction,wreaction_description, wreaction_image_bytes);
                        databaseManager.testDatabase.daoAccess().insertWAction(wa);
                    }


                }

                if(errors==0){

                    credentialsManager.setTestAvailability(Utilities.WALLY_NAME,true);
                }



            }


            ItemsBank[] instruments = null;
            Gson gson = new Gson();
            try{

                JSONArray instrumentsJson = response.getJSONArray("instruments");
                instruments = gson.fromJson(instrumentsJson.toString(), ItemsBank[].class);
                for(int i=0; i<instruments.length; i++ ){

                    for (int j=0; j<instruments[i].items.size(); j++){

                        //download the photo

                        byte[] imgBytes = downloadAsByteArray(instruments[i].items.get(j).pictureId,NetworkManager.BASE_URL+"/pictures", "original" );
                        Context context = MainActivity.this;

                        File file = new File(context.getFilesDir(), "pictures_"+Integer.toString(instruments[i].items.get(j).pictureId)+".jpg");

                        try {
                            file.createNewFile();
                            FileOutputStream fOut2 = new FileOutputStream(file);
                            fOut2.write(imgBytes);
                            fOut2.close();
                            instruments[i].items.get(j).setImagePath(file.getPath());

                            //ObjectOutputStream out = new ObjectOutputStream(fOut2);
                            //out.writeObject(imgBytes);
                            //out.close();
                            Log.d("SAVING IMG", "Serialized data to "+ file.getPath());
                            ;}catch (IOException exc){
                            exc.printStackTrace();
                        }
                    }
                        //String encoded = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                        //instruments[i].items.get(j).setEncoded_image(encoded);

                        //if (j==4){break;}

                    }



        }
            catch (JSONException exc){


        }

            try{
                School[] schools = gson.fromJson(response.getJSONArray("schools").toString(), School[].class);
                Log.println(Log.DEBUG,"downloadedSchools", schools.length + "");
            }catch (JsonIOException | JSONException jsonIOException){

                ;
            }



/*
                try {
                    File file = new File(MainActivity.this.getFilesDir(), "employee.txt");

                    FileOutputStream fOut = openFileOutput("employee.txt",
                            MODE_PRIVATE);
                    FileOutputStream fOut2 = new FileOutputStream(file);


                    ObjectOutputStream out = new ObjectOutputStream(fOut2);
                    out.writeObject(instruments[1]);
                    out.close();
                    System.out.printf("Serialized data is saved in /tmp/employee.ser");
                } catch (IOException i) {
                    i.printStackTrace();
                }


                ItemsBank e = null;
                try {

                    FileInputStream fileIn = new FileInputStream(MainActivity.this.getFilesDir() + "/employee.txt");
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    e = (ItemsBank) in.readObject();
                    in.close();
                    fileIn.close();

                } catch (IOException i) {
                    i.printStackTrace();

                } catch (ClassNotFoundException c) {
                    System.out.println("Employee class not found");
                    c.printStackTrace();
                }
*/






            publishProgress(100);
            return instruments;

        }


            String downloadAudio(int audio_server_id,String base_url){
                File f;
                try{

                    File cacheDir=new File(getApplicationContext().getFilesDir(),"fonotes_audios");
                    if(!cacheDir.exists())
                        cacheDir.mkdirs();

                    f=new File(cacheDir,audio_server_id+".mp3");
                    URL url = new URL(base_url+"/audios/download/"+Integer.toString(audio_server_id));

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(f);

                    byte data[] = new byte[1024];
                    long total = 0;
                    int count=0;
                    while ((count = input.read(data)) != -1) {
                        total++;
                        Log.e("while","A"+total);

                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                    return null;
                }

                return f.getPath();


            }
            byte[] downloadAsByteArray(int resource_id,String base_url,String style){
                Bitmap bm = null;
                byte[] byteArray;
                try {
                    URL aURL = new URL(base_url + "/download/" + resource_id+"/"+style);
                    InputStream inputStream = aURL.openStream();
                    byteArray= IOUtils.toByteArray(inputStream);
                  } catch (IOException e) {
                    Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
                    byteArray = null;
                }

                return byteArray;

            }


            @Override
            protected void onPostExecute (Object result){
                // Set the bitmap into ImageView
                mProgressDialog.dismiss();

                InstrumentsManager instrumentsManager = InstrumentsManager.getInstance(MainActivity.this);
                instrumentsManager.setInstruments(Arrays.asList( ( (ItemsBank[]) result)));

                checkAllTestAreReady();
            }
        }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        else if (id== R.id.action_update){

            requestInfoToServer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(id==R.id.nav_home){
            HomeFragment homeFragment=new HomeFragment();
            fragmentTransaction.replace(R.id.fragment_place,homeFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_my_students) {


            StudentsFragment resultFragment = new StudentsFragment();
            Bundle bundle = new Bundle();

            resultFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.fragment_place, resultFragment);
            fragmentTransaction.commit();}
            // Handle the camera action
      else if (id == R.id.nav_add_student) {
            NewStudentFragment nsf = new NewStudentFragment();
            fragmentTransaction.replace(R.id.fragment_place, nsf);
            fragmentTransaction.commit();

        }
       else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
