package com.example.e440.menu.wally_original;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.e440.menu.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kaldi.Assets;
import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechRecognizer;
import org.kaldi.Vosk;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment implements OnKeyPressedListener, Chronometer.OnChronometerTickListener {

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemAnswered(int itemId, String answer, int itemIndex);

        void OnItemBack();
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        ;
        latencySeconds +=1;
        chron.setText(Integer.toString(latencySeconds));
    }

    void resetChronometer(){
        latencySeconds = 0;
        chron.setText(Integer.toString(latencySeconds));
    }


    public void OnKeyPressed(int keyCode) {
        if(editText.hasFocus() && CHRONOMETER_STARTED){

            chron.stop();
            CHRONOMETER_STARTED = false;

            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            stopChronButton.setVisibility(View.INVISIBLE);
            Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
            startChronButton.setVisibility(View.VISIBLE);

        }
        if(editText.hasFocus() && keyCode == KeyEvent.KEYCODE_ENTER){


            //delete all line breaks inserted by ENTER key
            int cursorPosition = editText.getSelectionStart();
            int indexCharToDelete = cursorPosition-1;
            while(indexCharToDelete>0){
                char c = editText.getText().charAt(indexCharToDelete);
                if (c == '\n'){

                    editText.setText(editText.getText().delete(indexCharToDelete, indexCharToDelete+1));

                }
                indexCharToDelete--;
            }
            //editText.setText(editText.getText().delete(cursorPosition-1, cursorPosition));

            //give focus to next button to save answer
            Button nextButton = inflatedView.findViewById(R.id.itemNextButton);
            nextButton.setFocusableInTouchMode(true);
            nextButton.requestFocus();

        }
    }

    EditText editText;
    static boolean CHRONOMETER_STARTED = false;
    private OnFragmentInteractionListener mListener;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    private static String ARG_ITEM_TEXT = "item_statement";
    private static String ARG_ITEM_SERVER_ID= "item_id";
    private static String ARG_ENCODED_IMAGE="item_img";

    private String text = "Enunciado pregunta";
    private String answer = null;
    private int latencySeconds = 0;
    private String encodedImage = null;
    private Integer id = null;
    private View inflatedView;
    private Chronometer chron;

    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(int itemServerId, String itemText, String imageBase64Encoded) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_SERVER_ID, itemServerId);
        args.putString(ARG_ITEM_TEXT, itemText);
        args.putString(ARG_ENCODED_IMAGE, imageBase64Encoded);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_ITEM_TEXT);
            id = getArguments().getInt(ARG_ITEM_SERVER_ID);
            encodedImage = getArguments().getString(ARG_ENCODED_IMAGE);

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        inflatedView = view;

        editText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
        //put the question text
        TextView textView = view.findViewById(R.id.wallyOriginalItemTextView);
        textView.setText(text);

        //draw the image
        final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",")  + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imageView =  view.findViewById(R.id.wallyOriginalImageView);
        imageView.setImageBitmap(decodedByte);

        //buttons listeners
        Button startChronButton = view.findViewById(R.id.wallyOriginalStartChronometerButton);
        startChronButton.setOnClickListener(this.startChronometerListener);

        Button stopChronButton = view.findViewById(R.id.wallyOriginalStopChronometerButton);
        stopChronButton.setOnClickListener(this.stopChronometerListener);

        Button resetChronButton = view.findViewById(R.id.wallyOriginalResetChronometerButton);
        resetChronButton.setOnClickListener(this.resetChronometerButtonListener);
        //buttons in the navbar
        Button nextButton = view.findViewById(R.id.itemNextButton);
        nextButton.setOnClickListener(this.nextButtonListener);

        Button backButton = view.findViewById(R.id.itemFragmentBackButton);
        backButton.setOnClickListener(this.backButtonClickListener);

        chron = inflatedView.findViewById(R.id.chronometer);
        chron.setOnChronometerTickListener(this);
        chron.setFormat("");


        startChronButton.requestFocusFromTouch();
        return view;
    }

    View.OnClickListener startChronometerListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            chron.start();
            CHRONOMETER_STARTED = true;

            v.setVisibility(View.INVISIBLE);
            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            stopChronButton.setVisibility(View.VISIBLE);

            //give focus to EditText;
            View answerEditText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
            answerEditText.requestFocus();
            Context ctx = (Context) mListener;
            final InputMethodManager inputMethodManager = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(answerEditText, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
    };

    View.OnClickListener stopChronometerListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            chron.stop();
            CHRONOMETER_STARTED = false;


            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            stopChronButton.setVisibility(View.INVISIBLE);
            Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
            startChronButton.setVisibility(View.VISIBLE);

        }
    };

    View.OnClickListener resetChronometerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            resetChronometer();
        }
    };

    View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mListener.onItemAnswered(id, answer, 0);

        }
    };

    View.OnClickListener backButtonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            mListener.OnItemBack();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }





}
