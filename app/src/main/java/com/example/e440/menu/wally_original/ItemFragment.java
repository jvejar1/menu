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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e440.menu.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        void OnAllItemsFinished();
        
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

    private static String ARG_ITEM_TEXT = "item_statement";
    private static String ARG_ITEM_SERVER_ID= "item_id";
    private static String ARG_ENCODED_IMAGE="item_img";
    private static String ARG_IS_FINISH_ITEM ="is_finish_item";

    EditText answerEditText;

    static String CHRONOMETER_IS_RUNNING_ARG = "chronometer_is_running";
    static String LATENCY_SECONDS_ARG = "latency_seconds";

    private boolean chronometerIsRunning = false;
    private OnFragmentInteractionListener mListener;

    private Integer latencySeconds = 0;
    private String encodedImage = null;
    private Integer id = 1;
    private View inflatedView;
    private Chronometer chron;
    ViewModel model;
    ItemAnswer itemAnswer;

    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(int itemServerId, String itemText, String imageBase64Encoded, boolean isFinishItem) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FINISH_ITEM, isFinishItem);
        args.putInt(ARG_ITEM_SERVER_ID, itemServerId);
        args.putString(ARG_ITEM_TEXT, itemText);
        args.putString(ARG_ENCODED_IMAGE, imageBase64Encoded);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ITEM_SERVER_ID);
            encodedImage = getArguments().getString(ARG_ENCODED_IMAGE);
        }
        if(savedInstanceState!=null){
            latencySeconds = savedInstanceState.getInt(LATENCY_SECONDS_ARG);
            chronometerIsRunning = savedInstanceState.getBoolean(CHRONOMETER_IS_RUNNING_ARG);
        }else{
            chronometerIsRunning = false;
            model = new ViewModelProvider(requireActivity()).get(ViewModel.class);
            ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());
            latencySeconds = itemAnswer.getLatencySeconds();
        }*/
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


        return;
        /*chron = inflatedView.findViewById(R.id.chronometer);
        chron.setFormat("");
        chron.setText(latencySeconds + "s");

        if(chronometerIsRunning){
            chron.start();
            Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
            Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
            startChronButton.setVisibility(View.INVISIBLE);
            stopChronButton.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String savedInstanceStateSTR = savedInstanceState == null?"null":savedInstanceState.toString();
        savedInstanceStateSTR = "savedInstanceState: "+ savedInstanceStateSTR;
        Log.d("ITM_FRGMNT_ON_CRTVW", savedInstanceStateSTR);

        model = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        if(model.CurrentItemIsThanksItem()){

            inflatedView = inflater.inflate(R.layout.fragment_assent, container, false);
            TextView itemTextTextView = inflatedView.findViewById(R.id.itemTextTextView);
            itemTextTextView.setTextSize(150);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            itemTextTextView.setLayoutParams(layoutParams);
            //itemTextTextView.setGravity(Gravity.CENTER_VERTICAL);
            itemTextTextView.setText("Muchas Gracias");

        }else{

            inflatedView =  inflater.inflate(R.layout.fragment_assent, container, false);
            FrameLayout chronFrameLayout =(FrameLayout) inflatedView.findViewById(R.id.chronometerFrameLayout);
            chronFrameLayout.setVisibility(View.GONE);

            WallyOriginalItem item = model.GetCurrentItem();

            Context ctx = getContext();
            if (item.itemTypeId == 1){
                //assent item

                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);

                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                layoutParams.leftMargin = 10;
                layoutParams.rightMargin = 10;
                Button noButton = new Button(getContext());
                noButton.setTextSize(20);
                noButton.setText("Rechazar");
                noButton.setLayoutParams(layoutParams);

                Button yesButton = new Button(getContext());
                yesButton.setTextSize(20);
                yesButton.setText("Aceptar");
                yesButton.setLayoutParams(layoutParams);

                linearLayout.addView(noButton);
                linearLayout.addView(yesButton);

                frameLayout.addView(linearLayout);
                //frameLayout.addView(gridLayout);
            }else if (item.itemTypeId == 2){
                //instruction without input
                inflatedView =  inflater.inflate(R.layout.fragment_assent, container, false);
                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);

                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                layoutParams.leftMargin = 10;
                layoutParams.rightMargin = 10;

            }else if (item.itemTypeId == 3){
                //aces
                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);
                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            }else if (item.itemTypeId == 4){
                //wally instruction item
            }else if (item.itemTypeId == 5){
                //wally item
            }
            else if (item.itemTypeId == 6){
                //corsi essay
            }
            else if (item.itemTypeId == 7){
                //corsi item
            }
            else if (item.itemTypeId == 8){
                //corsi reversed essay
            }
            else if (item.itemTypeId == 9){
                //corsi reversed item
            }

            else if (item.itemTypeId == 10) {
                //hnf essay
            }

            else if (item.itemTypeId == 11) {
                //hnf item
            }

            else if (item.itemTypeId == 12) {
                //fonotest example
            }

            else if (item.itemTypeId == 13) {
                //fonotest item
            }
            else if (item.itemTypeId == 14){
                //open answer item
            }

            else{
                //item open answered with chronometer, wally original
                chronFrameLayout.setVisibility(View.VISIBLE);
                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);
                EditText textView = new EditText(ctx);
                textView.setId(R.id.wallyOriginalAnswerEditText);
                textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setHeight(200);
                answerEditText = textView;
                frameLayout.addView(textView);

                answerEditText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
                ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());

                answerEditText.setText(itemAnswer.getAnswer());
                //listeners
                answerEditText.addTextChangedListener(new TextWatcher() {
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


                answerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
                startChronButton.setOnClickListener(this.startChronometerListener);

                Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
                stopChronButton.setOnClickListener(this.stopChronometerListener);

                Button resetChronButton = inflatedView.findViewById(R.id.wallyOriginalResetChronometerButton);
                resetChronButton.setOnClickListener(this.resetChronometerButtonListener);
                startChronButton.setFocusableInTouchMode(true);
                startChronButton.requestFocusFromTouch();

                chron = inflatedView.findViewById(R.id.chronometer);
                chron.setOnChronometerTickListener(this);


            }

                TextView textView = inflatedView.findViewById(R.id.itemTextTextView);
                textView.setText(item.getText());

                String itemCountInfo = model.GetCurrentItemIndex()+1 + "/" + model.getItemsCount();

                TextView itemIndexInfoTextView = inflatedView.findViewById(R.id.itemIndexInfo);
                itemIndexInfoTextView.setText(itemCountInfo);

                if (item.getImagePath() != null){
                    try{
                        File file = new File(item.getImagePath());
                        FileInputStream fileInputStream = new FileInputStream( file);
                        int fileSize = (int) file.length();
                        byte[] byteArr = new byte[fileSize];
                        fileInputStream.read(byteArr);

                        Bitmap decodedByte = BitmapFactory.decodeByteArray(byteArr, 0, fileSize);
                        Log.d("ByteARr", Integer.toString(byteArr.length));
                        ImageView imageView =  inflatedView.findViewById(R.id.wallyOriginalImageView);
                        imageView.setImageBitmap(decodedByte);

                        String imgAsBase64 = Base64.encodeToString(byteArr, Base64.DEFAULT);

                        encodedImage = imgAsBase64;
                    }catch (FileNotFoundException e){

                        e.printStackTrace();

                    }catch (IOException e){

                        e.printStackTrace();
                    }
                }else{

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.answerFrameLayout);

                    textView.setLayoutParams(layoutParams); }
            }

        /*Button finishButton = inflatedView.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(this);*/

        //buttons in the navbar
        Button nextButton = inflatedView.findViewById(R.id.itemNextButton);
        nextButton.setOnClickListener(this.nextButtonListener);

        Button backButton = inflatedView.findViewById(R.id.itemFragmentBackButton);
        backButton.setOnClickListener(this.backButtonClickListener);

        ImageView bluetoothLogoImageView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        bluetoothLogoImageView.setOnLongClickListener(this.bluetoothLogoLongClickListener);

        bluetoothLogoImageView.setOnClickListener(this.bluetoothLogoClickListener);

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

            if (model.CurrentItemIsThanksItem()) {
                mListener.OnAllItemsFinished();
                return;
            } else if (model.GetCurrentItem().itemTypeId == 1) {
                /*if (itemAnswer.answersChoices.get(0) )*/
                //mListener.OnFinishRequest();
                mListener.onItemAnswered(1, "", 0);
                return;
            }

            final String answer = ((EditText) inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
            final ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());
            itemAnswer.setAnswer(answer.toString());
            itemAnswer.setLatencySeconds(latencySeconds);
            mListener.onItemAnswered(id, answer.toString(), model.GetCurrentItemIndex());

        }
    };

    View.OnClickListener backButtonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (model.CurrentItemIsThanksItem()){
                mListener.OnItemBack();
                return;
            }

            final String answer = ((EditText)inflatedView.findViewById(R.id.wallyOriginalAnswerEditText)).getText().toString();
            final ItemAnswer itemAnswer = model.getAnswer(model.GetCurrentItemIndex());
            itemAnswer.setAnswer(answer.toString());
            itemAnswer.setLatencySeconds(latencySeconds);
            mListener.OnItemBack();

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
