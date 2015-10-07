package com.example.root.helloagain;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by root on 30/8/15.
 */
public class VolleyArrayRequest extends JsonArrayRequest {

    //private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mPreferencesEditor;
    private static String showRating;
    private static String mUrl;
    private Map<String, String> postdata = null;

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(JSONArray response) {
        super.deliverResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return super.getBodyContentType();
    }

    @Override
    public byte[] getBody() {
        return super.getBody();
    }

    public VolleyArrayRequest(final VolleyTasksListener listener, String url, final String method_name){
        super(Method.GET, url,null, new Response.Listener<JSONArray>() {

            private final String TAG = VolleyArrayRequest.class.getSimpleName();

            @Override
            public void onResponse(JSONArray result) {
                Log.d(TAG, method_name + " : Response = " + result);
                listener.handleResult(result);
            }
        }, new Response.ErrorListener() {

            private final String TAG = VolleyArrayRequest.class.getSimpleName();

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, method_name +" : Error : "+ error.getMessage() +" Error Stacktrace : "+error.getLocalizedMessage());
                listener.handleError(error);
            }
        });
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return postdata != null ? postdata : super.getParams();
    }
}
