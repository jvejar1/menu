package com.example.e440.menu.wally_original;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
public class ItemFragment extends Fragment implements RecognitionListener{
    @Override
    public void onPartialResult(String s) {

    }

    @Override
    public void onResult(String s) {
        try{
            JSONObject json = new JSONObject(s);
            //JSONArray result = json.getJSONArray("result");
            String text = json.getString("text");
            inferred_text += " "+ text;

        }catch(JSONException e){


        }

        TextView resultTextView = inflatedView.findViewById(R.id.wallyOriginalInferredTextView);
        resultTextView.setText(inferred_text);

        EditText resultEditText = inflatedView.findViewById(R.id.wallyOriginalInferredEditText);
        resultEditText.setText(inferred_text);

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
    // TODO: Rename parameter arguments, choose names that match

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
    private String inferred_text = "";
    private String encodedImage = null;
    private Integer id = null;
    private View inflatedView;
    private SpeechRecognizer recognizer;
    private Model model;
    private boolean startRecording = false;

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

    Button confirmTextButton;
    Button editButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        inflatedView = view;

        confirmTextButton = inflatedView.findViewById(R.id.wallyOriginalConfirmTextButton);
        confirmTextButton.setOnClickListener(onConfirmTextButtonListener);
        confirmTextButton.setVisibility(View.INVISIBLE);

        editButton = inflatedView.findViewById(R.id.wallyOriginalEditInferredTextButton);


        View inferredResultBox = view.findViewById(R.id.wallyOriginalInferredResultBox);
        inferredResultBox.setVisibility(View.GONE);

        View inferredTextEdit = view.findViewById(R.id.wallyOriginalInferredEditText);
        inferredTextEdit.setVisibility(View.GONE);

        View editInferredTextButton = view.findViewById(R.id.wallyOriginalEditInferredTextButton);
        editInferredTextButton.setOnClickListener(editInferredTextButtonListener);


        Button startRecordingButton = view.findViewById(R.id.wallyOriginalStartRecordingButton);
        startRecordingButton.setOnClickListener(startButtonListener);

        Button stopRecordingButton = view.findViewById(R.id.wallyOriginalStopRecordingButton);
        stopRecordingButton.setOnClickListener(stopButtonListener);
        stopRecordingButton.setVisibility(View.GONE);




        //put the question text
        TextView textView = view.findViewById(R.id.wallyOriginalItemTextView);
        textView.setText(text);

        //draw the image
        final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",")  + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imageView =  view.findViewById(R.id.wallyOriginalImageView);
        imageView.setImageBitmap(decodedByte);

        Button nextButton = view.findViewById(R.id.itemNextButton);
        nextButton.setOnClickListener(this.nextButtonListener);

        Button backButton = view.findViewById(R.id.itemFragmentBackButton);
        backButton.setOnClickListener(this.backButtonClickListener);

        return view;
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<Activity> activityReference;
        WeakReference<ItemFragment> itemFragment;

        SetupTask(Activity activity, ItemFragment fragment ) {
            this.activityReference = new WeakReference<>(activity);
            itemFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Log.d("KaldiDemo", "Sync files in the folder " + assetDir.toString());

                Vosk.SetLogLevel(0);

                itemFragment.get().model = new Model(assetDir.toString() + "/model-spanish");
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                //activityReference.get().setErrorState(String.format(activityReference.get().getString(R.string.failed), result));
            } else {
                //activityReference.get().setUiState(STATE_READY);
            }
        }
    }

    View.OnClickListener editInferredTextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {



            final EditText inferredTextEditText = inflatedView.findViewById(R.id.wallyOriginalInferredEditText);
            inferredTextEditText.post(new Runnable() {
                @Override
                public void run() {
                    inferredTextEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) ((Activity)mListener).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
            });


            inferredTextEditText.setVisibility(View.VISIBLE);
            inflatedView.findViewById(R.id.wallyOriginalInferredTextView).setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
            confirmTextButton.setVisibility(View.VISIBLE);
            //
        }
    };

    View.OnClickListener onConfirmTextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            final TextView inferredTextEdit = inflatedView.findViewById(R.id.wallyOriginalInferredEditText);

            inferredTextEdit.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) ((Activity)mListener).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inferredTextEdit.getWindowToken(), 0);

                }
            });


            String confirmedText = inferredTextEdit.getText().toString();

            TextView inferredTextView = inflatedView.findViewById(R.id.wallyOriginalInferredTextView);
            inferredTextView.setText(confirmedText);

            inferredTextEdit.setVisibility(View.INVISIBLE);
            inferredTextView.setVisibility(View.VISIBLE);

            confirmTextButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.VISIBLE);


        }
    };

    View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            recognizeMicrophone();

            view.setVisibility(View.GONE);
            inflatedView.findViewById(R.id.wallyOriginalStopRecordingButton).setVisibility(View.VISIBLE);

        }
    };

    void recognizeMicrophone(){
        RecognizerManager.getInstance().recognizeMic(this);

    }


    View.OnClickListener stopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecognizerManager.getInstance().stop();
            view.setVisibility(View.GONE);
            TextView inferredTextView = inflatedView.findViewById(R.id.wallyOriginalInferredTextView);
            inferredTextView.setText(inferred_text);
            TextView inferredTextEdit = inflatedView.findViewById(R.id.wallyOriginalInferredEditText);
            inferredTextEdit.setText(inferred_text);

            View inferredResultBox = inflatedView.findViewById(R.id.wallyOriginalInferredResultBox);
            inferredResultBox.setVisibility(View.VISIBLE);

            //show the results
        }
    };

    View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mListener.onItemAnswered(id, inferred_text, 0);

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemAnswered(int itemId, String answer, int itemIndex);

        void OnItemBack();
    }

}
