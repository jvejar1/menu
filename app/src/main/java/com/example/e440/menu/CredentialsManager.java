package com.example.e440.menu;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by e440 on 20-03-18.
 */

public class CredentialsManager {
    static CredentialsManager credentialsManager;
    static String preferencesFileName = "Credentials";
    public SharedPreferences preferences;
    static String IS_FIRST_RUN_KEY = "first_run_1";
    public CredentialsManager(Context context){
        this.preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    }
    public static synchronized CredentialsManager getInstance(Context context){
        if (credentialsManager==null){
            credentialsManager =new CredentialsManager(context.getApplicationContext());
        }
        return credentialsManager;

    }

    String getUserName(){

        this.getToken();
        String user_name = this.preferences.getString("user", null);
        return user_name;
    }


    boolean isFirstRun(){
        boolean first_run=this.preferences.getBoolean(IS_FIRST_RUN_KEY,true);
        if(first_run){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_FIRST_RUN_KEY,false );
            editor.commit();

        }

        return first_run;


    }

    boolean getTestAvailability(String test_name){
        return this.preferences.getBoolean(test_name,false);
    }

    void setTestAvailability(String test_name, boolean available){
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putBoolean(test_name, available);
        editor.commit();
    }

    boolean AllTestAreReady(){
      boolean ace_ready=this.preferences.getBoolean(Utilities.ACE_NAME,false);
      boolean corsi_ready=this.preferences.getBoolean(Utilities.CORSI_NAME,false);
      boolean wally_ready=this.preferences.getBoolean(Utilities.WALLY_NAME,false);
      boolean fonotest_ready=this.preferences.getBoolean(Utilities.FONOTEST_NAME,false);
      boolean hnf_ready=this.preferences.getBoolean(Utilities.HNF_NAME,false);
      return (ace_ready &&
              corsi_ready &&
              wally_ready &&
              fonotest_ready &&
              hnf_ready);
    }

    boolean setAllTestUnready(){

        boolean isReady = false;
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putBoolean(Utilities.ACE_NAME, isReady);
        editor.putBoolean(Utilities.CORSI_NAME, isReady);
        editor.putBoolean(Utilities.WALLY_NAME, isReady);
        editor.putBoolean(Utilities.FONOTEST_NAME, isReady);
        editor.putBoolean(Utilities.HNF_NAME, isReady);
        editor.commit();
        return true;

    }

    String getUserPassword(){
        String user_password = this.preferences.getString("password", null);
        return user_password;
    }
    String getToken(){
        String token = this.preferences.getString("token", null);
        return token;
    }

    void saveToken(String token){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();
    }

    void saveCredentials(String user_name, String user_password){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", user_name);
        editor.putString("password",user_password);
        editor.commit();
    }

    void saveUserId(long userId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("userId", userId);
        editor.commit();
    }

    public long getUserId(){
        return preferences.getLong("userId", 0);
    }


    void destroyCredentials(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}


