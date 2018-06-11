package com.example.e440.menu;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.e440.menu.fonotest.FGroup;
import com.example.e440.menu.fonotest.FonoTest;
import com.example.e440.menu.fonotest.Item;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,StudentsFragment.OnStudentSelectedListener {


    CredentialsManager credentialsManager;
    String EXTRA_STUDENT = "student_id";
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


    public void onStudentSelected(int studentId) {
        CharSequence colors[] = new CharSequence[] {"Aces", "Wally", "Cubos de Corsi", "Hearts and Flowers","FonoTest"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un test");
        builder.setItems(colors, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if (which==0){

                    launchAcesActivity();
                }else if(which==1){

                    launchWallyActivity();
                }else if(which==2){

                    launchCorsiActivity();
                }else if(which==3){

                    launchHnfActivity();
                }
                else{

                    launchFonoTestActivity();
                }
            }
        });
        builder.show();
        return;

    }

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;
    protected void onCreate(Bundle savedInstanceState) {

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


        credentialsManager = CredentialsManager.getInstance(this);
   /*     if (credentialsManager.getUserName() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            //  intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, LOGIN_REQUEST);
        }*/
        //databaseManager.sendAllResults(networkManager);

    }

    @Override
    protected void onResume() {
        if (credentialsManager.isFirstRun()){
            requestInfoToServer();
        }
        super.onResume();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    protected void createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                0);
                        PendingIntent a=resolvable.getResolution();
                        int b=1;
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }



    void launchHnfActivity(){

        Intent intent = new Intent(this, HnfActivity.class);
        startActivity(intent);

    }
    void launchFonoTestActivity(){

        Intent intent = new Intent(this,FonoTestActivity.class);
        startActivity(intent);

    }

    void launchCorsiActivity(){
        Intent intent = new Intent(this, CorsiActivity.class);
        startActivity(intent);

    }

    void launchWallyActivity() {

        Intent intent = new Intent(this, WallyActivity.class);
        startActivity(intent);

    }

    void launchAcesActivity(){

        Intent intent = new Intent(this, AceActivity.class);
        startActivity(intent);

    }


    void getTest(String test_link, final int test_number){

        networkManager.getTest(test_link, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{response.putOpt("test_number",test_number);}

                catch (JSONException e){

                    //
                }


                JsonInserter jsonInserter=new JsonInserter();
                jsonInserter.execute(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int a =4;
            }
        });

    }

    public class JsonInserterParams{

    }

    public class JsonInserter extends AsyncTask<JSONObject,Void,String>{

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {

            String return_str="";

            JSONObject response=jsonObjects[0];
            int test_number=response.optInt("test_number");

            if (test_number==CORSI){

                JSONObject corsi_jo=response.optJSONObject("corsi");
                if (corsi_jo==null){

                    //handle

                }
                else{
                    int corsi_id = corsi_jo.optInt("id");
                    JSONArray set=corsi_jo.optJSONArray("set");
                    for (int i=0;i<set.length();i++){
                        JSONObject sequence_jo = set.optJSONObject(i);


                        int sequence_id = sequence_jo.optInt("id");
                        int sequence_index= sequence_jo.optInt("index");
                        boolean sequence_ordered=sequence_jo.optBoolean("ordered");
                        String sequence_str=sequence_jo.optString("sequence");
                        Csequence csequence =new Csequence(sequence_id,sequence_index,sequence_ordered);
                        long resultant_id = databaseManager.testDatabase.daoAccess().insertCSequence(csequence);
                        ;


                    }

                }

            }

            return return_str;
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
            }
        });
    }


    private class InsertAll extends AsyncTask<JSONObject, Void, Integer> {

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
        protected Integer doInBackground(JSONObject... jsonObjects) {


            JSONObject response = jsonObjects[0];

            JSONObject fonotest_jo=response.optJSONObject("fonotest");

            if (fonotest_jo!=null){
               // databaseManager.testDatabase.daoAccess().cleanFonotest();
                int fonotest_server_id=fonotest_jo.optInt("id");
                float fonotest_version=(float)fonotest_jo.optDouble("version");
                FonoTest fonoTest=new FonoTest(fonotest_server_id,false);
                databaseManager.testDatabase.daoAccess().insertFonoTest(fonoTest);
                JSONArray fgroups=fonotest_jo.optJSONArray("groups");
                for (int i=0;i<fgroups.length();i++){
                    JSONObject fgroup_jo=fgroups.optJSONObject(i);
                    int fgroup_server_id=fgroup_jo.optInt("id");
                    int fgroup_index=fgroup_jo.optInt("index");
                    String fgroup_name=fgroup_jo.optString("name");

                    String fgroup_description=fgroup_jo.optString("description");
                    boolean fgroup_is_example=fgroup_jo.optBoolean("example");
                    JSONArray items =fgroup_jo.optJSONArray("items");
                    FGroup fGroup=new FGroup(fgroup_name,fgroup_description,fgroup_is_example,fgroup_index,fgroup_server_id);
                    long fgroup_id=databaseManager.testDatabase.daoAccess().insertOneFGroup(fGroup);
                    for (int j=0;j<items.length();j++){
                        JSONObject item_jo=items.optJSONObject(j);
                        String item_description=item_jo.optString("description");
                        int item_index=item_jo.optInt("index",0);
                        int item_server_id=item_jo.optInt("id");
                        int audio_id=item_jo.optInt("audio_id",-1);
                        byte[] audio_bytes=null;
                        if(audio_id!=-1){
                            audio_bytes=downloadAsByteArray(audio_id,NetworkManager.BASE_URL+"/audios");
                        }
                        String correct_sequence=item_jo.optString("correct_sequence");

                        Item item=new Item(correct_sequence,(int)fgroup_id,audio_bytes,item_description,item_server_id,item_index);
                        databaseManager.testDatabase.daoAccess().insertOneItem(item);
                    }
                }





            }

            JSONObject hnf_jo=response.optJSONObject("hnf");

            if(hnf_jo!=null){
           //     databaseManager.testDatabase.daoAccess().cleanHnf();
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

            }

            JSONObject corsi_jo=response.optJSONObject("corsi");
            if (corsi_jo==null){
                //handle

            }
            else{

          //      databaseManager.testDatabase.daoAccess().cleanCorsi();
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
                    Csequence csequence = new Csequence(sequence_server_id,sequence_index,sequence_ordered);
                    long csequence_id = databaseManager.testDatabase.daoAccess().insertCSequence(csequence);
                    JSONArray squares=sequence_jo.optJSONArray("squares");
                    for(int j=0;j<squares.length();j++){
                        JSONObject square_jo = squares.optJSONObject(j);
                        int square_id=square_jo.optInt("id");
                        int square_number=square_jo.optInt("square");
                        int square_index= square_jo.optInt("index");
                        Csquare csquare=new Csquare(square_id,square_index,(int)csequence_id,square_number);
                        databaseManager.testDatabase.daoAccess().insertCSquare(csquare);
                    }
                }


            }


            JSONObject aceJO = response.optJSONObject("ace");
            if(aceJO!=null){
         //       databaseManager.testDatabase.daoAccess().cleanAce();
                int ace_id=aceJO.optInt("id");
                float ace_version=(float)aceJO.optDouble("version");
                Ace ace=new Ace(ace_version,ace_id);
                databaseManager.testDatabase.daoAccess().insertAce(ace);
                JSONArray acasesJA = aceJO.optJSONArray("acases");
                for (int i = 0; i < acasesJA.length(); i++) {

                    JSONObject acaseJO = acasesJA.optJSONObject(i);
                    int acase_server_id = acaseJO.optInt("id");
                    int acase_index = acaseJO.optInt("index");
                    int picture_id = acaseJO.optInt("picture_id");
                    //try to DL image
                    byte[] image_bytes= downloadAsByteArray(picture_id,NetworkManager.BASE_URL+"/pictures");
                    Acase acase = new Acase(acase_index, acase_server_id, image_bytes);

                    databaseManager.testDatabase.daoAccess().insertAcase(acase);
                }


            }

            ///// WALLY


            JSONObject wally_jo = response.optJSONObject("wally");
            if(wally_jo!=null){

          //      databaseManager.testDatabase.daoAccess().cleanWally();
                int wally_server_id = wally_jo.optInt("id");
                Wally w = new Wally(1, "", wally_server_id);

                databaseManager.testDatabase.daoAccess().insertWally(w);

                JSONArray feelings_ja = wally_jo.optJSONArray("feelings");

                for (int j = 0; j < feelings_ja.length(); j++) {
                    JSONObject feeling_jo = feelings_ja.optJSONObject(j);
                    int feeling_server_id = feeling_jo.optInt("id");
                    int feeling_image_id = feeling_jo.optInt("picture_id");

                    int feeling_wfeeling=feeling_jo.optInt("wfeeling");
                    byte[] feeling_image_bytes = downloadAsByteArray(feeling_image_id,NetworkManager.BASE_URL+"/pictures");


                    WFeeling wf=new WFeeling(feeling_server_id,feeling_wfeeling,feeling_image_bytes);
                    databaseManager.testDatabase.daoAccess().insertWFeeling(wf);

                }
                JSONArray wsituations_ja = wally_jo.optJSONArray("wsituations");
                for (int i = 0; i < wsituations_ja.length(); i++) {
                    JSONObject wsituation_jo = wsituations_ja.optJSONObject(i);
                    int wsituation_server_id = wsituation_jo.optInt("id");
                    int wsituation_image_id = wsituation_jo.optInt("picture_id");
                    byte[] wsituation_image_bytes = downloadAsByteArray(wsituation_image_id,NetworkManager.BASE_URL+"/pictures");
                    String wsituation_description = wsituation_jo.optString("description");
                    WSituation ws = new WSituation(wsituation_server_id,wsituation_description,wsituation_image_bytes);
                    long ws_id=databaseManager.testDatabase.daoAccess().insertWSituation(ws);


                    //parse Wreactions

                    JSONArray wreactions_ja = wsituation_jo.optJSONArray("wreactions");

                    for (int j = 0; j < wreactions_ja.length(); j++) {

                        JSONObject wreaction_jo = wreactions_ja.optJSONObject(j);
                        int wreaction_server_id = wreaction_jo.optInt("wreaction_id");
                        int wreaction_image_id = wreaction_jo.optInt("picture_id");
                        int wreaction_wreaction=wreaction_jo.optInt("wreaction");
                        String wreaction_description =wreaction_jo.optString("description");
                        byte[] wreaction_image_bytes = downloadAsByteArray(wreaction_image_id,NetworkManager.BASE_URL+"/pictures");
                        WReaction wa = new WReaction(ws_id,wreaction_server_id,wreaction_wreaction,wreaction_description, wreaction_image_bytes);
                        databaseManager.testDatabase.daoAccess().insertWAction(wa);
                    }


                }



            }




            return 1;

        }

            byte[] downloadAsByteArray(int resource_id,String base_url){
                Bitmap bm = null;
                byte[] byteArray;
                try {
                    URL aURL = new URL(base_url + "/download/" + resource_id);
                    InputStream inputStream = aURL.openStream();
                    byteArray= IOUtils.toByteArray(inputStream);
                  } catch (IOException e) {
                    Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
                    byteArray = null;
                }


                return byteArray;

            }


            @Override
            protected void onPostExecute (Integer result){
                // Set the bitmap into ImageView

                mProgressDialog.dismiss();

            }
        }


        public void startTest(int id_student) {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(ACES_PACKAGE_NAME);
            launchIntent.putExtra("student_id", 1);


            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
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

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (id == R.id.nav_my_students) {


                StudentsFragment resultFragment = new StudentsFragment();
                fragmentTransaction.replace(R.id.fragment_place, resultFragment);
                fragmentTransaction.commit();
                // Handle the camera action
            } else if (id == R.id.nav_tests) {

            } else if (id == R.id.nav_add_student) {
                NewStudentFragment nsf = new NewStudentFragment();
                fragmentTransaction.replace(R.id.fragment_place, nsf);
                fragmentTransaction.commit();


            } else if (id == R.id.nav_saved) {

            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }



        public void sendResults(){

            int a =1;
            ResponseRequest[] responseRequests=databaseManager.testDatabase.daoAccess().fetchAllResponseRequest();


       }
    }
