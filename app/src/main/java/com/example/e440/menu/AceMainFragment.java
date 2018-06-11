package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by e440 on 22-04-18.
 */

public class AceMainFragment extends Fragment{

    MainFragmentListener mCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    NetworkManager networkManager;
    HashMap<String,Integer> result = new HashMap<>();

    int nextAcaseIndex= 0;
    ImageView caseImageView;
    ArrayList<Button> feelingsButtons = new ArrayList<>();
    LinearLayout feelingsButtonsLinearLayout;
    View inflatedView;
    DatabaseManager databaseManager;

    int[] acases_ids;
    Acase current_acase;
    Acase[] acases;

    Ace ace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.test_fragment, container, false);
        databaseManager = DatabaseManager.getInstance(getContext());
        caseImageView = inflatedView.findViewById(R.id.caseImageView);
        feelingsButtonsLinearLayout=inflatedView.findViewById(R.id.feelingsButtonsLinearLayout);

        Button backButton = inflatedView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        Button nextButton = inflatedView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.containsKey(Integer.toString(current_acase.getServer_id()))) {
                    LoadAceData lad=new LoadAceData();
                    lad.execute();

                } else {

                    //restrict

                }
            }
        });


        LoadAceData lad=new LoadAceData();
        lad.execute();
        return inflatedView;
    }



    public void reorderFeelings(){
        Set<Integer> a=Ace.feelings_by_number.keySet();
        Integer[] v=new Integer[a.size()];
        a.toArray(v);
        Collections.shuffle(Arrays.asList(v));


        for (int index=0;index<feelingsButtonsLinearLayout.getChildCount();index++
                )

        {
                Button b = (Button) feelingsButtonsLinearLayout.getChildAt(index);

                int new_id=v[index];
                String new_text=Ace.feelings_by_number.get(v[index]);
                b.setId(new_id);
                b.setText(new_text);

                if(nextAcaseIndex==0){
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button button=(Button) view;
                            int feeling_number=Ace.feelings_by_name.get(button.getText());

                            result.put(Integer.toString(current_acase.getServer_id()),feeling_number);
                            //change color
                        }
                    });

                }

            }

    }

    void displayNextCase(){

        if(nextAcaseIndex==acases_ids.length){
            JSONObject payload=new JSONObject();
            try{
                payload.putOpt("test_name","ace");
                payload.putOpt("test_id",ace.getServer_id());
                payload.putOpt("response",new JSONObject(result));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            ResponseRequest responseRequest=new ResponseRequest(payload.toString(),"ace");
            databaseManager.insertRequest(responseRequest);
            mCallback.backFromTest(null);
            return;


        }

        else {

            inflatedView.setVisibility(View.GONE);
            byte[] byteArray=current_acase.getImage_bytes();
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
            Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);

            caseImageView.setImageBitmap(bitmap);

            reorderFeelings();


            nextAcaseIndex += 1;
            inflatedView.setVisibility(View.VISIBLE);
        }
    }

    private class LoadAceData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog

        }

        @Override
        protected Void doInBackground(Void... voids) {



            if(acases_ids==null){
                ace=databaseManager.testDatabase.daoAccess().fetchFirstAce();
                acases_ids=databaseManager.testDatabase.daoAccess().fetchAcacesIds();
            }

            if(nextAcaseIndex==acases_ids.length){
                return null;
            }
            current_acase = databaseManager.testDatabase.daoAccess().fetchAcaseById(acases_ids[nextAcaseIndex]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayNextCase();

        }

    }

    void get_data(){


        networkManager.getAcesInfo(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject ace_jo=response.optJSONObject("aces");
                    float ace_version=(float)ace_jo.optDouble("version");
                    int ace_server_id=ace_jo.optInt("id");
                    final Ace ace =new Ace(1,1);
                    ace.setServer_id(ace_server_id);
                    ace.setVersion(ace_version);

                    final ArrayList<Acase> acaseArrayList=new ArrayList<>();
                    JSONArray acases_ja = response.getJSONArray("acases");
                    for (int p=0;p<acases_ja.length();p++){
                        JSONObject acase_jo=acases_ja.optJSONObject(p);
                        int acase_server_id=acase_jo.optInt("id");
                        int acase_index=acase_jo.optInt("index");
                        String image_path=acase_jo.optString("image_path");


                        JSONArray afeelings=acase_jo.optJSONArray("feelings");


                        Bitmap m=null;
                        try{
                            DownloadImage di=new DownloadImage();
                            m=di.execute("/aces/download/"+acase_server_id).get();
                            int j=0;
                        }
                        catch (ExecutionException|InterruptedException e){



                        }
                        byte[] imageBytes=Utilities.convertBitmapToBytesArray(m);
                        Acase acase=new Acase(acase_index,acase_server_id,imageBytes);
                        acaseArrayList.add(acase);


                    }

                    Thread t =new Thread() {
                        @Override
                        public void run() {
                            databaseManager.testDatabase.daoAccess().insertAce(ace);

                            for (Acase a :acaseArrayList
                                    ) {
                                databaseManager.testDatabase.daoAccess().insertAcase(a);

                            }

                            int id = ace.getId();

                        }
                    };
                    t.start();



                    //DatabaseManager.getInstance(getContext()).testDatabase.daoAccess().insertAce(ace);


                }

                catch (JSONException e){

                    System.out.println(e.getMessage());
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error


                if (error.networkResponse==null){

                    System.out.println("CONNECTION ERROR");

                }
                else{

                    System.out.println("UNKNOWN ERROR");

                }
            }
        });

    }

}
