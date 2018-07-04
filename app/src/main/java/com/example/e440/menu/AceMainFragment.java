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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    HashMap<Integer,Integer> button_id_by_feeling_number=new HashMap<>();
    Button highlighted_feeling_button=null;
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

                int feeling_number=v[index];
                String new_text=Ace.feelings_by_number.get(feeling_number);
                b.setText(new_text);

                button_id_by_feeling_number.put(feeling_number,b.getId());
                if(nextAcaseIndex==0){
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button button=(Button) view;
                            int feeling_number=Ace.feelings_by_name.get(button.getText());
                            result.put(Integer.toString(current_acase.getServer_id()),feeling_number);
                            change_highlighted_feeling_button((Button)view);
                            //change color
                        }
                    });

                }

            }

    }

    void change_highlighted_feeling_button(Button new_highlighted_button){
        if(new_highlighted_button==highlighted_feeling_button){

            return;
        }
        new_highlighted_button.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
        if (highlighted_feeling_button!=null){
            highlighted_feeling_button.setBackgroundResource(R.drawable.ace_default_feeling_button);

        }
        highlighted_feeling_button=new_highlighted_button;
    }

    void displayNextCase(){

        if(nextAcaseIndex==acases_ids.length){
            JSONObject payload=new JSONObject();
            try{
                payload.putOpt("responses",new JSONObject(result));
                payload.putOpt("test_id",ace.getServer_id());


                mCallback.backFromTest(payload);
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return;


        }

        else {

            byte[] byteArray=current_acase.getImage_bytes();
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
            Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
            caseImageView.setImageBitmap(bitmap);
            reorderFeelings();


            TextView descriptionTextView=inflatedView.findViewById(R.id.aceDescriptionTextView);
            descriptionTextView.setText(current_acase.getDescription());
            if(result.containsKey(Integer.toString(current_acase.getServer_id()))){
                int feeling_number_to_restore=result.get(Integer.toString(current_acase.getServer_id()));
                //find the button with the feeling text
                Button selected_button=inflatedView.findViewById(button_id_by_feeling_number.get(feeling_number_to_restore));
                change_highlighted_feeling_button(selected_button);
            }
            else{

                if(highlighted_feeling_button!=null){

                    highlighted_feeling_button.setBackgroundResource(R.drawable.ace_default_feeling_button);
                }
            }
            nextAcaseIndex += 1;

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

}
