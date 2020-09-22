package com.example.e440.menu.wally_original;

import android.content.Context;
import android.util.Log;

import org.kaldi.Assets;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechRecognizer;
import org.kaldi.Vosk;

import java.io.EOFException;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class RecognizerManager {
    static {
        System.loadLibrary("kaldi_jni");
    }
    static RecognizerManager instance;


    WeakReference<WallyOriginalActivity> activityReference;
    Model model;
    SpeechRecognizer recognizer;

    public static RecognizerManager Setup(WallyOriginalActivity activity){
        if (instance == null){
            instance = new RecognizerManager(activity);
            instance.loadModel();
        }
        return instance;
    }

    public static RecognizerManager getInstance(){
        return instance;
    }

    private RecognizerManager(WallyOriginalActivity activity){
        this.activityReference = new WeakReference<>(activity);
    }

    void loadModel(){
        try{
            Assets assets = new Assets(activityReference.get());
            File assetDir = assets.syncAssets();
            Log.d("KaldiDemo", "Sync files in the folder " + assetDir.toString());
            Vosk.SetLogLevel(0);
            model = new Model(assetDir.toString() + "/model-spanish");

        }catch (IOException e){
            Log.d("ERROR RecognizerManager",e.getMessage());
        }
    }

    void recognizeMic(RecognitionListener listener){

        if (recognizer == null) {
            try{
                recognizer = new SpeechRecognizer(model);
                recognizer.addListener(listener);

            }catch (IOException e){
                Log.getStackTraceString(e);

            }

        }

        recognizer.startListening();

    }

    void stop(){
        if(recognizer == null){
            return;
        }

        recognizer.stop();
        recognizer = null;

    }

}
