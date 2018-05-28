package com.example.e440.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    NetworkManager networkManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Intent intent = getIntent();
        networkManager= NetworkManager.getInstance(this);
    }

    public void returnToMain(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }


    public void loginButtonClick(View view){


        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        try {
            networkManager.login("esteban.vejar@hotmail.com", "1234567890", new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    returnToMain();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error


                    if (error.networkResponse==null){

                        System.out.println("CONNECTION ERROR");

                    }
                    else if(error.networkResponse.statusCode==400){

                        System.out.println("INVALID EMAIL OR PASSWORD");

                    }
                    else{

                        System.out.println("UNKNOWN ERROR");

                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
      /*  Context context = getApplicationContext();
        //Get the user and password to check if they are valids

        EditText userEditText = (EditText) findViewById(R.id.userEditText);
        String user = userEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();


        //
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(user);

        if(mat.matches() && password.trim().length()>0){

            System.out.println("Valid email address");

            CredentialsManager credentialsManager = new CredentialsManager(context);
            credentialsManager.saveCredentials(user,password);
            returnToMain();

        }else{

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context,"Campos invalidos",duration);
            toast.show();
        }
*/

        //

        //Assuming that the inputs are valid,store the data to shared preferences



    }



    public void validateCredentials(View view){

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();

    }





}

