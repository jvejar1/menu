package com.example.e440.menu.wally_original;

import android.content.Context;
import android.util.Log;

import com.example.e440.menu.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class InstrumentsManager {

    private static InstrumentsManager Instance;
    private Context context;
    private List<ItemsBank> instruments;
    public void setInstruments(List<ItemsBank> instruments){
        this.instruments = instruments;
        File folder = new File( this.context.getFilesDir(), "/instrumentos.ser");
        folder.mkdirs();
        String folderPath = folder.getPath();
        File file = new File(this.context.getFilesDir(), "/instruments.ser");

        try {
            file.createNewFile();
            FileOutputStream fOut2 = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fOut2);
            out.writeObject(instruments);
            out.close();
            Log.d("INSTRUMENTS MANAGER", "Serialized data to "+ file.getPath());
            ;}catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public static synchronized InstrumentsManager getInstance(Context context){
        if (Instance == null){
            Instance = new InstrumentsManager(context);
            Instance.LoadFromFile();
        }
        return Instance;
    }
    private InstrumentsManager(Context context){
        this.context = context;

    }

    public List<ItemsBank> getInstruments(){
        if (this.instruments == null){
            //loadFromFile
            this.instruments = new ArrayList<>();
        }
        return this.instruments;
    }

    public void LoadFromFile(){
        try {
            FileInputStream fileIn = new FileInputStream(context.getFilesDir() + "/instruments.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            instruments = (List<ItemsBank>) in.readObject();
            in.close();
            fileIn.close();
            Log.d("INSTRUMENTS MANAGER", "Restored data from: " + fileIn.toString());

        } catch (IOException i) {
            i.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
        }
    }
}
