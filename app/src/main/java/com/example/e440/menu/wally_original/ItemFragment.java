package com.example.e440.menu.wally_original;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.e440.menu.DatabaseManager;
import com.example.e440.menu.R;
import com.example.e440.menu.ResponseRequest;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment implements Chronometer.OnChronometerTickListener{

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemAnswered(int itemId, String answer, int itemIndex);
        void OnItemBack();
        void OnNavigateToThanksRequest();
        void OnRequestPopFragment();
        void OnRequestSaveSendAndClose(Evaluation evaluation, Long studentServerId);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        ;
        itemAnswer.latencySeconds +=1;
        chronometer.setText(Integer.toString(itemAnswer.latencySeconds) + "s");
    }

    void resetChronometer(){
        itemAnswer.latencySeconds = 0;
        Chronometer chron = inflatedView.findViewById(R.id.chronometer);
        chron.setText(Integer.toString(itemAnswer.latencySeconds) + "s");
    }

    public ItemFragment() {
        // Required empty public constructor
    }

    private static String ARG_ITEM_TEXT = "item_statement";
    private static String ARG_ITEM_SERVER_ID= "item_id";
    private static String ARG_ENCODED_IMAGE="item_img";
    private static String ARG_IS_FINISH_ITEM ="is_finish_item";
    private static String ARG_ACCEPT_ASSENT = "accept_assent";

    private static String ARG_NOT_ACCEPT_ASSENT = "not_accept_assent";

    private static String ARG_ASSENT_DECIDED = "assent_decided";
    private static String ARG_ASSENT_ACCEPTED ="assent_accepted";

    private static String ARG_IS_INSTRUCTION_ITEM = "is_isntruction_item";
    private boolean assentAccepted = false;
    private boolean assentDecided = false;
    EditText answerEditText;

    static String CHRONOMETER_IS_RUNNING_ARG = "chronometer_is_running";

    private boolean chronometerIsRunning = false;
    private OnFragmentInteractionListener mListener;

    private Integer id = 1;
    private View inflatedView;
    ViewModel model;
    ItemAnswer itemAnswer;

    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(int itemServerId, String itemText, String imageBase64Encoded, boolean isFinishItem, boolean isInstructionItem) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FINISH_ITEM, isFinishItem);
        args.putInt(ARG_ITEM_SERVER_ID, itemServerId);
        args.putString(ARG_ITEM_TEXT, itemText);
        args.putBoolean(ARG_IS_INSTRUCTION_ITEM, isInstructionItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState!=null) {
            chronometerIsRunning = savedInstanceState.getBoolean(CHRONOMETER_IS_RUNNING_ARG);
            assentAccepted = savedInstanceState.getBoolean(ARG_ASSENT_ACCEPTED);
            assentDecided = savedInstanceState.getBoolean(ARG_ASSENT_DECIDED);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHRONOMETER_IS_RUNNING_ARG, chronometerIsRunning);

        outState.putBoolean(ARG_ASSENT_DECIDED, assentDecided);
        outState.putBoolean(ARG_ASSENT_ACCEPTED, assentAccepted);

        //outState.putInt(LATENCY_SECONDS_ARG, latencySeconds);
        Log.d("ITM_FRGMT SAVED_INSTANC", outState.toString());


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        return;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String savedInstanceStateSTR = savedInstanceState == null?"null":savedInstanceState.toString();
        savedInstanceStateSTR = "savedInstanceState: "+ savedInstanceStateSTR;
        Log.d("ITM_FRGMNT_ON_CRTVW", savedInstanceStateSTR);

        boolean isFinishItem = getArguments().getBoolean(ARG_IS_FINISH_ITEM, false);

        boolean isInstructionItem = getArguments().getBoolean(ARG_IS_INSTRUCTION_ITEM);
        inflatedView = inflater.inflate(R.layout.fragment_item_real, container, false);
        FrameLayout chronFrameLayout =(FrameLayout) inflatedView.findViewById(R.id.chronometerFrameLayout);
        chronFrameLayout.setVisibility(View.GONE);

        final Button nextButton = inflatedView.findViewById(R.id.itemNextButton);
        nextButton.setOnClickListener(this.nextButtonListener);

        Button backButton = inflatedView.findViewById(R.id.itemFragmentBackButton);
        backButton.setOnClickListener(this.backButtonClickListener);

        ImageView bluetoothLogoImageView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        bluetoothLogoImageView.setOnLongClickListener(this.bluetoothLogoLongClickListener);

        bluetoothLogoImageView.setOnClickListener(this.bluetoothLogoClickListener);
        TextView itemTextTextView = inflatedView.findViewById(R.id.itemTextTextView);
        final Context ctx = getContext();
        if(isFinishItem){

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnRequestPopFragment();
                }
            });
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Evaluation evaluation = model.getEvaluation();
                    evaluation.setAsFinished();
                    Long studentServerId = evaluation.getStudentId();
                    mListener.OnRequestSaveSendAndClose(evaluation, studentServerId);
                }
            });

            itemTextTextView.setTextSize(120);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            itemTextTextView.setLayoutParams(layoutParams);
            //itemTextTextView.setGravity(Gravity.CENTER_VERTICAL);
            itemTextTextView.setText(R.string.thanks);

        }

        else if (model.getCurrentItemIndex().equals(WallyOriginalActivity.SpecialItemIndex.INSTRUCTION_ITEM))
        {
          TextView textView = inflatedView.findViewById(R.id.itemTextTextView);
          textView.setText(model.getInstructionText());
          nextButtonListener = new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  mListener.onItemAnswered(0,"",0);
              }
          };
        }
        else if(model.getCurrentItemIndex().equals(WallyOriginalActivity.SpecialItemIndex.ASSENT_ITEM)){

            backButton.setVisibility(View.GONE);
            FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);

            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            final Button noButton = new Button(getContext());

            final Button yesButton = new Button(getContext());
            noButton.setTextSize(20);
            noButton.setText("Rechazar");
            noButton.setLayoutParams(layoutParams);

            noButton.setBackgroundResource(R.drawable.ace_default_feeling_button);
            yesButton.setBackgroundResource(R.drawable.ace_default_feeling_button);


            if (assentDecided){
                nextButton.setEnabled(true);

                if (assentAccepted){
                    yesButton.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                }else{
                    noButton.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                }
            }


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    assentDecided = true;
                    assentAccepted = false;

                    view.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                    nextButton.setEnabled(true);
                    yesButton.setBackgroundResource(R.drawable.ace_default_feeling_button);

                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assentDecided = true;
                    assentAccepted = true;
                    nextButton.setEnabled(true);
                    view.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                    noButton.setBackgroundResource(R.drawable.ace_default_feeling_button);


                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (assentAccepted){
                        mListener.onItemAnswered(1,"",1);
                    }else{
                        mListener.OnNavigateToThanksRequest();
                    }
                }
            });
            yesButton.setTextSize(20);
            yesButton.setText("Aceptar");
            yesButton.setLayoutParams(layoutParams);

            linearLayout.addView(noButton);
            linearLayout.addView(yesButton);

            frameLayout.addView(linearLayout);
        }
        else{

            final WallyOriginalItem item = model.GetCurrentItem();
            itemAnswer = model.generateAnswerForView(model.getCurrentItemIndex());
            TextView textView = inflatedView.findViewById(R.id.itemTextTextView);
            ImageView imageView =  inflatedView.findViewById(R.id.wallyOriginalImageView);
            if (item.itemTypeId == null){
                //wally original
                chronFrameLayout = (FrameLayout) inflatedView.findViewById(R.id.chronometerFrameLayout);
                chronFrameLayout.setVisibility(View.VISIBLE);

                Integer orientation = getResources().getConfiguration().orientation;
                if (orientation.equals(Configuration.ORIENTATION_LANDSCAPE)) {
                    RelativeLayout.LayoutParams imgViewParams;

                    if(item.getImagePath()!=null){
                        imgViewParams = new RelativeLayout.LayoutParams(300, 300 );
                    }else{
                        imgViewParams = new RelativeLayout.LayoutParams(0, 0 );
                    }
                    imgViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    imageView.setLayoutParams(imgViewParams);

                    RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textViewParams.addRule(RelativeLayout.LEFT_OF, imageView.getId());
                    textView.setLayoutParams(textViewParams);
                    ;
                }

                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);
                frameLayout.setPadding(10,10,10,10);
                EditText editText = new EditText(ctx);
                editText.setId(R.id.wallyOriginalAnswerEditText);
                editText.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                editText.setHeight(150);
                //ViewGroup.LayoutParams editTextParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 250);
                //editText.setLayoutParams(editTextParams);
                answerEditText = editText;
                frameLayout.addView(editText);

                answerEditText = inflatedView.findViewById(R.id.wallyOriginalAnswerEditText);
                final ItemAnswer itemAnswer = model.getAnswer(model.getCurrentItemIndex());

                answerEditText.setText(itemAnswer.getAnswer());
                answerEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        model.getAnswer(model.
                                getCurrentItemIndex()).setAnswer(s.toString());
                        if(chronometerIsRunning){
                            Chronometer chron = inflatedView.findViewById(R.id.chronometer);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Confirmar Respuesta");
                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    ItemAnswer answer = model.getAnswer(model.getCurrentItemIndex());
                                    if (answer.getAnswer().endsWith("\n")){
                                        answer.answer = answer.getAnswer().substring(0, answer.getAnswer().length()-1);
                                    }

                                    mListener.onItemAnswered(id, "", model.getCurrentItemIndex());
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
                answerEditText.setBackgroundResource(R.drawable.edit_text_selector);

                Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
                startChronButton.setOnClickListener(this.startChronometerListener);

                Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
                stopChronButton.setOnClickListener(this.stopChronometerListener);

                Button resetChronButton = inflatedView.findViewById(R.id.wallyOriginalResetChronometerButton);
                resetChronButton.setOnClickListener(this.resetChronometerButtonListener);
                startChronButton.setFocusableInTouchMode(true);
                startChronButton.requestFocusFromTouch();

                Chronometer chron = inflatedView.findViewById(R.id.chronometer);
                chron.setText(itemAnswer.latencySeconds + "s");
                chron.setOnChronometerTickListener(this);

                if(chronometerIsRunning){
                    //chron.start();
                    //Button startChronButton = inflatedView.findViewById(R.id.wallyOriginalStartChronometerButton);
                    // Button stopChronButton = inflatedView.findViewById(R.id.wallyOriginalStopChronometerButton);
                    // startChronButton.setVisibility(View.INVISIBLE);
                    //stopChronButton.setVisibility(View.VISIBLE);
                }
                //frameLayout.addView(gridLayout);
            }
            else if (item.getItemTypeId().equals(101)){
                inflatedView = inflater.inflate(R.layout.fragment_item_101, container, false);
                TextView instructionTextView = inflatedView.findViewById(R.id.itemTextTextView);
                instructionTextView.setText(item.getText());
                Button btnNavNext=inflatedView.findViewById(R.id.nav_next_btn);
                btnNavNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemAnswered(0,"",0);
                    }
                });

                if (model.getCurrentItemIndex() == 0){
                    Button navBackBtn = inflatedView.findViewById(R.id.nav_prev_btn);
                    navBackBtn.setVisibility(View.INVISIBLE);
                }else{
                    Button navBackBtn = inflatedView.findViewById(R.id.nav_prev_btn);
                    navBackBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.OnItemBack();
                        }
                    });
                }

                return inflatedView;
            }
            else if (item.getItemTypeId().equals(102)){
                inflatedView = inflater.inflate(R.layout.fragment_item_102, container, false);

                //get the file bytes
                String parentPath = ctx.getFilesDir().getAbsolutePath();
                String imgAbsPath;
                if (item.getImagePath().contains(parentPath)){
                    imgAbsPath = item.getImagePath();
                }else{
                    imgAbsPath = parentPath.concat("/"+item.getImagePath());
                }

                ImageView itemImgVw = inflatedView.findViewById(R.id.itemImageView);
                Bitmap bm = BitmapFactory.decodeFile(imgAbsPath);
                itemImgVw.setImageBitmap(bm);

                TextView instructionTextView = inflatedView.findViewById(R.id.itemTextTextView);
                instructionTextView.setText(item.getText());
                Button btnNavNext=inflatedView.findViewById(R.id.nav_next_btn);
                btnNavNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemAnswered(0,"",0);

                    }
                });

                Button btnNavPrev = inflatedView.findViewById(R.id.nav_prev_btn);
                btnNavPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.OnItemBack();
                    }
                });
                return inflatedView;
            }

            else if (item.itemTypeId == 2){
                inflatedView = inflater.inflate(R.layout.fragment_item_generic, container, false);

                String itemText = item.getDescription();
                TextView tv = inflatedView.findViewById(R.id.itemTextTextView);
                tv.setText(itemText);
                LinearLayout choicesLayout = inflatedView.findViewById(R.id.choicesLayout);
                Button navPrevBtn = inflatedView.findViewById(R.id.nav_prev_btn);
                navPrevBtn.setOnClickListener(this.backButtonClickListener);
                Button navNextBtn = inflatedView.findViewById(R.id.nav_next_btn);
                navNextBtn.setVisibility(View.INVISIBLE);
                navNextBtn.setOnClickListener(this.nextButtonListener);
                final List<ItemChoice> choices = item.getChoices();
                final int itemIdx = model.getCurrentItemIndex();
                final ArrayList<LinearLayout> choiceLayouts = new ArrayList<LinearLayout>();
                final Handler h = new Handler();
                for(int i=0; i<item.choices.size(); i++) {
                    ItemChoice choice = choices.get(i);
                    final int choiceIdx = i;
                    final LinearLayout linearLayout = new LinearLayout(ctx);
                    model.getChoiceSelectedFlag(itemIdx, choiceIdx).observe(ItemFragment.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                linearLayout.setBackgroundColor(Color.GRAY);
                            }else{
                                linearLayout.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                    });
                    Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.person_icon_2);
                    File file = new File(ctx.getFilesDir(),choice.getPicturePath());
                    int fileSize = (int)file.length();
                    try{
                        Log.d("Try loading file", String.format("can_read: %s, path: %s, bytes:%d, is_file: %s, is_dir:%s", file.canRead(),file.getAbsolutePath(), fileSize, file.isFile(), file.isDirectory()));
                        FileInputStream fileInputStream = new FileInputStream(file);
                        bitMap = BitmapFactory.decodeStream(fileInputStream);
                        fileInputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.weight = 1;
                    layoutParams.rightMargin=4;
                    layoutParams.leftMargin=4;

                    linearLayout.setId(i);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setLayoutParams(layoutParams);
                    choiceLayouts.add(linearLayout);
                    choicesLayout.addView(linearLayout);

                    ImageView imageViewChoice = new ImageView(ctx);
                    imageViewChoice.setCropToPadding(true);
                    imageViewChoice.setAdjustViewBounds(true);
                    imageViewChoice.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    imageViewChoice.setLayoutParams(imgLayoutParams);
                    linearLayout.addView(imageViewChoice);
                    imageViewChoice.setImageBitmap(bitMap);

                    String text = String.format("%s", choice.getText());
                    TextView textViewChoice = new TextView(ctx);
                    int textSize = 22;
                    textViewChoice.setTextSize(textSize);
                    textViewChoice.setPadding(10,10,5,0);
                    textViewChoice.setTextColor(Color.BLACK);
                    textViewChoice.setLayoutParams(imgLayoutParams);
                    textViewChoice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewChoice.setText(text);


                    //choicesLayout.addView(textViewChoice);
                    linearLayout.addView(textViewChoice);
                    linearLayout.setVisibility(View.INVISIBLE);
                    linearLayout.setClickable(false);

                    Long[] showHideProgram = model.getShowHideProgram(itemIdx, choiceIdx);
                    h.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     linearLayout.setClickable(true);
                                                     linearLayout.setVisibility(View.VISIBLE);
                                                 }
                                             }
                            , showHideProgram[0]);
                }

                for (int i=0; i<item.choices.size(); i++){
                    final int choiceIdx = i;
                    LinearLayout choiceLayout = choiceLayouts.get(i);
                    View.OnClickListener choiceClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view){
                            for (int j=0; j<item.choices.size(); j++){
                                choiceLayouts.get(j).setClickable(false);
                            }
                            model.userTouchesChoice(itemIdx, choiceIdx);
                            long delayMillisNextItem =500;
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onItemAnswered(0,"",0);
                                }
                            }, delayMillisNextItem);
                        }
                    };
                    choiceLayout.setOnClickListener(choiceClickListener);
                }

                return inflatedView;

            }else if (item.itemTypeId == 3){
                //aces
                FrameLayout frameLayout = inflatedView.findViewById(R.id.answerFrameLayout);
                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                final int nChoices = item.choices.size();
                final List<Button> buttons = new ArrayList<>();
                for (int i = 0; i<nChoices; i++){
                    ItemChoice choice = item.choices.get(i);
                    String choiceText = choice.getText();
                    Button choiceBtn = new Button(ctx);
                    choiceBtn.setText(choiceText);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.weight = 1;
                    choiceBtn.setLayoutParams(layoutParams);
                    linearLayout.addView(choiceBtn);
                    buttons.add(choiceBtn);
                }

                for (int i = 0; i< nChoices; i++){
                    final Button b  = buttons.get(i);
                    b.setBackgroundResource(R.drawable.ace_default_feeling_button);
                    ItemChoice choice = item.choices.get(i);
                    final int choiceId = choice.id;
                    if (itemAnswer.answersChoices.contains(choiceId)){
                        b.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);
                    }else
                    {
                        b.setBackgroundResource(R.drawable.ace_default_feeling_button);
                    }

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*itemAnswer.answersChoices.clear();
                            itemAnswer.answersChoices.add(choiceId);

                            for (int idxBtn = 0; idxBtn< nChoices; idxBtn++){
                                Button otherButton = buttons.get(idxBtn);
                                //otherButton.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorTransparent));
                                otherButton.setBackgroundResource(R.drawable.ace_default_feeling_button);
                            }

                            b.setBackgroundResource(R.drawable.ace_highlighted_feeling_button_bg);*/

                        }
                    });
                }

                frameLayout.addView(linearLayout);

            }


            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setText(item.getText());

            String itemCountInfo = model.getCurrentItemIndex()+1 + "/" + model.getItemsCount();

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

                    imageView.setImageBitmap(decodedByte);

                    String imgAsBase64 = Base64.encodeToString(byteArr, Base64.DEFAULT);

                }catch (FileNotFoundException e){

                    e.printStackTrace();

                }catch (IOException e){

                    e.printStackTrace();
                }
            }else{

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.answerFrameLayout);

                textView.setLayoutParams(layoutParams);
            }
        }

        /*Button finishButton = inflatedView.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(this);*/

        //buttons in the navbar

        return inflatedView;
    }


    View.OnClickListener bluetoothLogoClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
            ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
            ImageTask imageTask = new ImageTask(null, localView);
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

            Button b = (Button)v;
            b.setFocusableInTouchMode(false);

            Chronometer chron = inflatedView.findViewById(R.id.chronometer);
            chron.start();
            chronometerIsRunning = true;

            b.setVisibility(View.INVISIBLE);
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
            Chronometer chron = inflatedView.findViewById(R.id.chronometer);
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
            mListener.onItemAnswered(1,"",2);

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

        MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
        ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        ImageTask imageTask = new ImageTask(null, localView);
        myBluetoothService.sendMessage(imageTask);
        // Refresh the state of the +1 button each time the activity receives focus.
    }

    @Override
    public void onPause() {
        super.onPause();
        Chronometer chron = inflatedView.findViewById(R.id.chronometer);
        if (chron!=null){
            chron.stop();
            chronometerIsRunning = false;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            model = new ViewModelProvider(requireActivity()).get(ViewModel.class);
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
