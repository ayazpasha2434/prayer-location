package com.example.root.helloagain;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by Ayaz on 30/8/15.
 */
public class ApiHandler {

    private DefaultRetryPolicy defaultRetryPolicy;
    //private final String END_POINT = BuildConfig.SERVER_URL;

    private final String TAG = ApiHandler.class.getSimpleName();

    public  static String VOLLEY_CONFIG_TAG = "Config";
    public  static String VOLLEY_ABOUT_TAG = "About";
    public  static String VOLLEY_LOGIN_TAG = "Login";
    public  static String VOLLEY_BSTATUS_TAG = "Booking Status";
    public  static String VOLLEY_LOCPOLL_TAG = "LocPoll";

    public ApiHandler() {
        defaultRetryPolicy = new DefaultRetryPolicy(
                Constants.CONNECTION_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public void get_About(LatLng latLng, VolleyTasksListener listener){

        String latitudeLongitude = latLng.latitude + "," + latLng.longitude;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
            latitudeLongitude +"&radius=2000&types=mosque&key=AIzaSyAgcEbfxnb9bnb9MK90g7eC-fdNcJoClEk";

        Log.d(TAG, "URL = "+url);

        AppController.getInstance().addToRequestQueue(
                new VolleyRequest(listener, url,VOLLEY_ABOUT_TAG)
                        .setRetryPolicy(defaultRetryPolicy), VOLLEY_ABOUT_TAG);
    }

    public void getJSONObjectRequest(Context context) {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = "http://api.androidhive.info/volley/person_object.json";

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
