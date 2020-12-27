package com.example.e440.menu.wally_original;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e440.menu.R;


import java.util.ArrayList;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment implements Chronometer.OnChronometerTickListener, View.OnClickListener{

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemAnswered(int itemId, String answer, int itemIndex);

        void OnItemBack();

        void OnFinishRequest();
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        ;
        latencySeconds +=1;
        chron.setText(Integer.toString(latencySeconds) + "s");
    }

    void resetChronometer(){
        latencySeconds = 0;
        chron.setText(Integer.toString(latencySeconds) + "s");
    }


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




    EditText editText;

    static String CHRONOMETER_IS_RUNNING_ARG = "chronometer_is_running";
    static String LATENCY_SECONDS_ARG = "latency_seconds";

    private boolean chronometerIsRunning = false;
    private OnFragmentInteractionListener mListener;

    private String answer = null;
    private Integer latencySeconds = 0;
    private String encodedImage = null;
    private Integer id = null;
    private View inflatedView;
    private Chronometer chron;
    ViewModel model;

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
            id = getArguments().getInt(ARG_ITEM_SERVER_ID);
            encodedImage = getArguments().getString(ARG_ENCODED_IMAGE);
        }
        if(savedInstanceState!=null){

            latencySeconds = savedInstanceState.getInt(LATENCY_SECONDS_ARG);
            chronometerIsRunning = savedInstanceState.getBoolean(CHRONOMETER_IS_RUNNING_ARG);

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHRONOMETER_IS_RUNNING_ARG, chronometerIsRunning);
        outState.putInt(LATENCY_SECONDS_ARG, latencySeconds);
        Log.d("ITM_FRGMT SAVED_INSTANC", outState.toString());

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState == null){
            return;
        }

        chron.setText(latencySeconds.toString());
        if(chronometerIsRunning){
            chron.start();
        }
        Log.d("ITM_FRGMT VW_STT_RSTRD", savedInstanceState.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String savedInstanceStateSTR = savedInstanceState == null?"null":savedInstanceState.toString();
        savedInstanceStateSTR = "savedInstanceState: "+ savedInstanceStateSTR;
        Log.d("ITM_FRGMNT_ON_CRTVW", savedInstanceStateSTR);
        model = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        WallyOriginalItem item = model.GetCurrentItem();

        ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());

        inflatedView =  inflater.inflate(R.layout.fragment_item_2, container, false);


        //editText.setInputType(InputType.TYPE_NULL);
        //put the question text
        TextView textView = inflatedView.findViewById(R.id.wallyOriginalItemTextView);
        textView.setText(item.getText());

        TextView itemIndexInfoTextView = inflatedView.findViewById(R.id.itemIndexInfo);
        String itemCountInfo = model.GetCurrentItemIndex() + "/" + model.getItemsCount();
        itemIndexInfoTextView.setText(itemCountInfo);

        //draw the image
        final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",")  + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imageView =  inflatedView.findViewById(R.id.wallyOriginalImageView);
        imageView.setImageBitmap(decodedByte);


        latencySeconds = itemAnswer.getLatencySeconds();

        chron = inflatedView.findViewById(R.id.chronometer);
        chron.setFormat("");
        chron.setText(itemAnswer.getLatencySeconds() + "s");
        chron.setText(Integer.toString(itemAnswer.getLatencySeconds()));

        editText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
        editText.setText(itemAnswer.getAnswer());

        //listeners
        editText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(chronometerIsRunning){

                    chron.stop();
                    chronometerIsRunning = false;

                    Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
                    stopChronButton.setVisibility(View.INVISIBLE);
                    Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
                    startChronButton.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_NEXT){

                    if(chronometerIsRunning){
                        chron.stop();
                        chronometerIsRunning = false;
                        Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
                        stopChronButton.setVisibility(View.INVISIBLE);
                        Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
                        startChronButton.setVisibility(View.VISIBLE);
                    }


                    final String answer = ((EditText)inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
                    final ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Confirmar Respuesta");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button

                            itemAnswer.setAnswer(answer.toString());
                            itemAnswer.setLatencySeconds(latencySeconds);
                            mListener.onItemAnswered(id, answer.toString(), model.GetCurrentItemIndex());

                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface d) {
                            Button negative = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            negative.setFocusable(true);
                            negative.setFocusableInTouchMode(true);
                            negative.requestFocus();
                        }
                    });
                    dialog.show();



                }
                return false;
            }
        });

        Button finishButton = inflatedView.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(this);

        Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
        startChronButton.setOnClickListener(this.startChronometerListener);

        Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
        stopChronButton.setOnClickListener(this.stopChronometerListener);

        Button resetChronButton = inflatedView.findViewById(R.id.wallyOriginalResetChronometerButton);
        resetChronButton.setOnClickListener(this.resetChronometerButtonListener);
        //buttons in the navbar
        Button nextButton = inflatedView.findViewById(R.id.itemNextButton);
        nextButton.setOnClickListener(this.nextButtonListener);

        Button backButton = inflatedView.findViewById(R.id.itemFragmentBackButton);
        backButton.setOnClickListener(this.backButtonClickListener);

        ImageView bluetoothLogoImageView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        bluetoothLogoImageView.setOnLongClickListener(this.bluetoothLogoLongClickListener);

        bluetoothLogoImageView.setOnClickListener(this.bluetoothLogoClickListener);


        chron = inflatedView.findViewById(R.id.chronometer);
        chron.setOnChronometerTickListener(this);

        startChronButton.setFocusableInTouchMode(true);
        startChronButton.requestFocusFromTouch();

        return inflatedView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finishButton){
            String answer = ((EditText)inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
            ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());
            itemAnswer.setAnswer(answer);
            itemAnswer.setLatencySeconds(latencySeconds);
            mListener.OnFinishRequest();
        }
    }

    View.OnClickListener bluetoothLogoClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
            ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
            ImageTask imageTask = new ImageTask(encodedImage, localView);
            myBluetoothService.sendMessage(imageTask);
        }
    };

    View.OnLongClickListener bluetoothLogoLongClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {

            final MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
            final ArrayList<String> bondedDevicesNames = myBluetoothService.GetBondedDevicesNames();

            final ArrayList<String> bondedDevicesAdresses = myBluetoothService.GetBondedDevicesAddresses();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Seleccione un dispositivo Bluetooth emparejado:");

            builder.setItems(bondedDevicesNames.toArray(new String[bondedDevicesNames.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String preferredBtDeviceAddress = bondedDevicesAdresses.get(which);
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());
                    sharedPreferencesManager.setSlaveBluetoothDeviceAddress(preferredBtDeviceAddress);
                    myBluetoothService.finish();

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
    };

    View.OnClickListener startChronometerListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            v.setFocusableInTouchMode(false);
            chron.start();
            chronometerIsRunning = true;

            v.setVisibility(View.INVISIBLE);
            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            stopChronButton.setVisibility(View.VISIBLE);

            //give focus to EditText;
            View answerEditText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
            answerEditText.requestFocus();
            Context ctx = (Context) mListener;
            /*final InputMethodManager inputMethodManager = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(answerEditText, InputMethodManager.SHOW_IMPLICIT);
            */
            return;

        }
    };

    View.OnClickListener stopChronometerListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            chron.stop();
            chronometerIsRunning = false;


            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            stopChronButton.setVisibility(View.INVISIBLE);
            Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
            startChronButton.setVisibility(View.VISIBLE);

        }
    };

    View.OnClickListener resetChronometerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context)mListener);
            builder.setMessage("¿Resetear Cronómetro?")
                    .setCancelable(true)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            resetChronometer();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    ;
            AlertDialog alert = builder.create();
            alert.show();


        }
    };

    View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String answer = ((EditText)inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
            final ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());

            if(!answer.equals(itemAnswer.getAnswer()) || !latencySeconds.equals(itemAnswer.getLatencySeconds()) || answer.equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Confirmar Respuesta");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        itemAnswer.setAnswer(answer.toString());
                        itemAnswer.setLatencySeconds(latencySeconds);
                        mListener.onItemAnswered(id, answer.toString(), model.GetCurrentItemIndex());

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        Button negative = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        negative.setFocusable(true);
                        negative.setFocusableInTouchMode(true);
                        negative.requestFocus();
                    }
                });
                dialog.show();

            }else{

                mListener.onItemAnswered(id, answer.toString(), model.GetCurrentItemIndex());

            }


        }
    };

    View.OnClickListener backButtonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {



            final String answer = ((EditText)inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
            final ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());

            if( !answer.equals(itemAnswer.getAnswer() )|| !latencySeconds.equals(itemAnswer.getLatencySeconds())){


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("¿Guardar cambio antes de volver?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        itemAnswer.setAnswer(answer.toString());
                        itemAnswer.setLatencySeconds(latencySeconds);
                        mListener.OnItemBack();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.OnItemBack();
                    }
                });
                builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }else{

                mListener.OnItemBack();
            }

            //model.insertAnswer(itemAnswer, model.GetCurrentItemIndex());


        }
    };



    @Override
    public void onResume() {
        super.onResume();

        MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
        ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        ImageTask imageTask = new ImageTask(encodedImage, localView);
        myBluetoothService.sendMessage(imageTask);
        // Refresh the state of the +1 button each time the activity receives focus.
    }

    @Override
    public void onPause() {
        super.onPause();

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
