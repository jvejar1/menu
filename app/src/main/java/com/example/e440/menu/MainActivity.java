package com.example.e440.menu;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
        CharSequence colors[] = new CharSequence[] {"Aces", "Wally", "Cubos de Corsi", "Hearts and Flowers"};
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
            }
        });
        builder.show();
        return;

    }


    @Override
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






        //getTest("corsi/get_current_data",CORSI);

        requestInfoToServer();

    }



    void launchHnfActivity(){

        Intent intent = new Intent(this, HnfActivity.class);
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
            JSONObject hnf_jo=response.optJSONObject("hnf");
            int hnf_server_id=hnf_jo.optInt("id");
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

            JSONObject corsi_jo=response.optJSONObject("corsi");
            if (corsi_jo==null){
                //handle

            }
            else{
                int corsi_id = corsi_jo.optInt("id");
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
            JSONArray acasesJA = aceJO.optJSONArray("acases");
            for (int i = 0; i < acasesJA.length(); i++) {

                JSONObject acaseJO = acasesJA.optJSONObject(i);
                int acase_server_id = acaseJO.optInt("id");
                int acase_index = acaseJO.optInt("index");
                int picture_id = acaseJO.optInt("picture_id");

                //try to DL image
                byte[] image_bytes=downloadImageAsByteArray(picture_id);

                Acase acase = new Acase(acase_index, acase_server_id, image_bytes);

                databaseManager.testDatabase.daoAccess().insertAcase(acase);
            }


            ///// WALLY


            JSONObject wally_jo = response.optJSONObject("wally");
            int wally_server_id = wally_jo.optInt("id");
            Wally w = new Wally(1, "", wally_server_id);

            databaseManager.testDatabase.daoAccess().insertWally(w);

            JSONArray feelings_ja = wally_jo.optJSONArray("feelings");

            for (int j = 0; j < feelings_ja.length(); j++) {
                JSONObject feeling_jo = feelings_ja.optJSONObject(j);
                int feeling_server_id = feeling_jo.optInt("id");
                int feeling_image_id = feeling_jo.optInt("picture_id");

                int feeling_wfeeling=feeling_jo.optInt("wfeeling");
                byte[] feeling_image_bytes =downloadImageAsByteArray(feeling_image_id);


                WFeeling wf=new WFeeling(feeling_server_id,feeling_wfeeling,feeling_image_bytes);
                databaseManager.testDatabase.daoAccess().insertWFeeling(wf);

            }



            JSONArray wsituations_ja = wally_jo.optJSONArray("wsituations");
            for (int i = 0; i < wsituations_ja.length(); i++) {
                JSONObject wsituation_jo = wsituations_ja.optJSONObject(i);
                int wsituation_server_id = wsituation_jo.optInt("id");
                int wsituation_image_id = wsituation_jo.optInt("picture_id");
                byte[] wsituation_image_bytes = downloadImageAsByteArray(wsituation_image_id);
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
                    byte[] wreaction_image_bytes = downloadImageAsByteArray(wreaction_image_id);
                    WReaction wa = new WReaction(ws_id,wreaction_server_id,wreaction_wreaction,wreaction_description, wreaction_image_bytes);
                    databaseManager.testDatabase.daoAccess().insertWAction(wa);
                }


            }
            return 1;

        }

            byte[] downloadImageAsByteArray ( int picture_id){
                Bitmap bm = null;
                byte[] byteArray;
                try {
                    URL aURL = new URL(NetworkManager.BASE_URL + "/pictures/download/" + picture_id);
                    InputStream inputStream = aURL.openStream();
                    bm = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    bm.recycle();
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


    }
