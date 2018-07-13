package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

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
    Button back_button;
    Handler handler=new Handler();
    Ace ace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.test_fragment, container, false);
        databaseManager = DatabaseManager.getInstance(getContext());
        caseImageView = inflatedView.findViewById(R.id.caseImageView);
        feelingsButtonsLinearLayout=inflatedView.findViewById(R.id.feelingsButtonsLinearLayout);

        back_button = inflatedView.findViewById(R.id.backButton);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextAcaseIndex-=2;
                LoadAceData lad=new LoadAceData();
                lad.execute();
            }});


        Button nextButton = inflatedView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.containsKey(Integer.toString(current_acase.getServer_id()))) {
                    LoadAceData lad=new LoadAceData();
                    lad.execute();

                } else {

                    for(final Button b:feelingsButtons){

                        b.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                        b.setEnabled(false);
                        back_button.setEnabled(false);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                b.setBackgroundResource(R.drawable.ace_default_feeling_button);
                                b.setEnabled(true);
                                back_button.setEnabled(true);
                            }
                        },250);
                    }
                    //restrict

                }
            }
        });

        LoadAceData lad=new LoadAceData();
        lad.execute();
        return inflatedView;
    }



    public void reorderFeelings(){
        Set<Integer> a=Ace.male_feelings_by_number.keySet();
        Integer[] v=new Integer[a.size()];
        a.toArray(v);
        Collections.shuffle(Arrays.asList(v));


        for (int index=0;index<feelingsButtonsLinearLayout.getChildCount();index++
                )

        {
                Button b = (Button) feelingsButtonsLinearLayout.getChildAt(index);

                if(!feelingsButtons.contains(b)){

                    feelingsButtons.add(b);
                }
                int feeling_number=v[index];
                String new_text="";
                if(current_acase.getSex()== Acase.MALE_CHAR){
                    new_text=Ace.male_feelings_by_number.get(feeling_number);


                }
                else{
                    new_text=Ace.female_feelings_by_number.get(feeling_number);

                }
                b.setText(new_text);
                button_id_by_feeling_number.put(feeling_number,b.getId());
                if(nextAcaseIndex==0){
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button button=(Button) view;
                            int feeling_number=Ace.feelings_by_name.get(button.getText());
                            result.put(Integer.toString(current_acase.getServer_id()),feeling_number);
                            reset_feelings_buttons_to_default_bg();
                            view.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                            //change color
                        }
                    });

                }

            }

    }

    void reset_feelings_buttons_to_default_bg(){

        for (Button b:feelingsButtons){
            b.setBackgroundResource(R.drawable.ace_default_feeling_button);

        }
    }

    void displayNextCase(){

        if(nextAcaseIndex==0){
            back_button.setVisibility(View.INVISIBLE);
        }
        else{
            back_button.setVisibility(View.VISIBLE);
        }
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
            if(current_acase.getSex()==Acase.MALE_CHAR){
                descriptionTextView.setText("¿Él se siente...?");

            }else{

                descriptionTextView.setText("¿Ella se siente...?");

            }
            reset_feelings_buttons_to_default_bg();
            if(result.containsKey(Integer.toString(current_acase.getServer_id()))){
                int feeling_number_to_restore=result.get(Integer.toString(current_acase.getServer_id()));
                //find the button with the feeling text
                Button selected_button=inflatedView.findViewById(button_id_by_feeling_number.get(feeling_number_to_restore));

                selected_button.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
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
