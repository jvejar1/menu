package com.example.e440.menu.wally_original;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    static SharedPreferencesManager instance;
    static private String BT_DEVICE_ID_PREF= "preferred_address";
    private static String FILENAME = "bluetooth_preferences";
    private Context mContext;

    public static SharedPreferencesManager getInstance(Context context){
        if (instance == null){

            instance = new SharedPreferencesManager(context);
        }

        return instance;

    }
    private SharedPreferencesManager(Context context){
        mContext = context;

    }

    void setSlaveBluetoothDeviceAddress(String bluetoothDeviceId){

        SharedPreferences btSharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = btSharedPreferences.edit();
        editor.putString(BT_DEVICE_ID_PREF,bluetoothDeviceId);
        editor.commit();
    }

    public String getSlaveBluetoothDeviceAdress(){

        SharedPreferences btSharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String btDeviceId  = btSharedPreferences.getString(BT_DEVICE_ID_PREF, null);
        return btDeviceId;
    }

    public boolean btDeviceNeedsToBeSetted(){
        SharedPreferences btSharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        boolean btDeviceNeedsToBeSetted = !btSharedPreferences.contains(BT_DEVICE_ID_PREF);
        return btDeviceNeedsToBeSetted;
    }


}
