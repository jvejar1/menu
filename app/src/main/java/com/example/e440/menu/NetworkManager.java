package com.example.e440.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class NetworkManager implements Executor{

    public void execute(Runnable r ){
        Thread t = new Thread(r);
        t.start();

    }

    private static NetworkManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public static final String BASE_URL = "http://192.168.0.102:3000/";

    private static String token =  "";

    private NetworkManager(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkManager(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void getTest(String test_link, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        String url = BASE_URL + test_link;
        makeApiCall(Request.Method.GET, url, null,listener, errorListener);
    }







    public void login(final String username, final String password, final Response.Listener<JSONObject> responseListener,
                       Response.ErrorListener errorListener) throws JSONException {

        String url = BASE_URL + "login.json";

        JSONObject payload = new JSONObject();
        payload.put("email", username);
        payload.put("password", password);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject headers = response.optJSONObject("headers");
                        token = headers.optString("Authorization", null);
                        CredentialsManager.getInstance(null).saveToken(token);
                        CredentialsManager.getInstance(null).saveCredentials(username,password);
                        responseListener.onResponse(response);
                    }
                }, errorListener){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        mRequestQueue.add(jsonObjectRequest);
    }




    public void getForms(Response.Listener<JSONObject> listener,
                         Response.ErrorListener  errorListener){

        String url = BASE_URL + "forms/";
        makeApiCall(Request.Method.GET, url, null,listener, errorListener);
    }


    public void getGoogle(Response.Listener<JSONObject> listener,
                         Response.ErrorListener  errorListener){

        String url = "www.google.cl";
        makeApiCall(Request.Method.GET, url, null,listener, errorListener);
    }


    public void getAll(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        String url =  BASE_URL + "/get_all";
        makeApiCall(Request.Method.GET, url, null,listener, errorListener);
    }


    public void getAcesInfo(Response.Listener<JSONObject> listener,
                         Response.ErrorListener  errorListener){

        String url = BASE_URL + "/aces/get_current_test_data";
        makeApiCall(Request.Method.GET, url, null,listener, errorListener);
    }

    private void makeApiCall(int method, String url, JSONObject payload, Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener){

        JsonObjectArrayRequest jsonObjectRequest = new JsonObjectArrayRequest
                (method, url, payload, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }










}