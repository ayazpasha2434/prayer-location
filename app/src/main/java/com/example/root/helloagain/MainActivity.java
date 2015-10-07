package com.example.root.helloagain;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.helloagain.model.BabySitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends Activity implements LocationListener {

    private static final long LOCATION_REFRESH_TIME = 0;
    private static final long LOCATION_REFRESH_DISTANCE = 1000;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private GoogleMap googleMap;
    private ApiHandler mApiHandler = null;

    private Map<String, BabySitter> babySitters;
    BabySitter babySitter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {

            finish();
        }

        setContentView(R.layout.activity_main);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        mApiHandler = new ApiHandler();

        try {

            if (googleMap == null) {

                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            googleMap.setMyLocationEnabled(true);

            //googleMap.setTrafficEnabled(true);

            googleMap.setIndoorEnabled(true);

            googleMap.setBuildingsEnabled(true);


            googleMap.getUiSettings().setZoomControlsEnabled(true);
/*
            googleMap.addMarker(new MarkerOptions().
                    position(MyLatLng1).alpha(0.7f).title("Sitter 1"));

            googleMap.addMarker(new MarkerOptions().
                    position(MyLatLng2).alpha(0.7f).title("Sitter 2"));

            googleMap.addMarker(new MarkerOptions().
                    position(MyLatLng3).alpha(0.7f).title("Sitter 3"));

            googleMap.addMarker(new MarkerOptions().
                    position(MyLatLng4).alpha(0.7f).title("Sitter 4"));

            googleMap.addMarker(new MarkerOptions().
                    position(MyLatLng5).alpha(0.7f).title("Sitter 5"));
*/
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            Criteria criteria = new Criteria();

            String bestProvider = mLocationManager.getBestProvider(criteria, true);
            Location location = mLocationManager.getLastKnownLocation(bestProvider);

            if (location != null) {

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                getNearestMosques(new LatLng(location.getLatitude(), location.getLongitude()));

            }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_DISTANCE,
                    LOCATION_REFRESH_TIME, this);

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLng centerOfMap = googleMap.getCameraPosition().target;
                    double latitude = centerOfMap.latitude;
                    double longitude = centerOfMap.longitude;

                    getNearestMosques(new LatLng(latitude, longitude));
                }
            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    babySitter = babySitters.get(marker.getTitle());

                    marker.showInfoWindow();
                    return true;
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    //Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                    /*RelativeLayout markerDetails = (RelativeLayout) findViewById(R.id.rl_marker_details);
                    markerDetails.setBackgroundColor(Color.BLACK);
                    markerDetails.setVisibility(View.VISIBLE);

                    TextView tvMarkerDetails = (TextView) findViewById(R.id.tv_marker_details);
                    tvMarkerDetails.setText(marker.getTitle());

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    markerDetails.setVisibility(View.GONE);*/

                    marker.hideInfoWindow();

                    // custom dialog
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.profile_layout);
                    dialog.setTitle(marker.getTitle());

                    TextView addressTV = (TextView) dialog.findViewById(R.id.text_2);
                    addressTV.setText(babySitter.getAddress().trim());

                    // set the custom dialog components - text, image and button
/*
                    TextView text = (TextView) dialog.findViewById(R.id.text);
                    text.setText("Android custom dialog example! Android custom dialog example! " +
                            "Android custom dialog example! Android custom dialog example! Android custom dialog example!" +
                            "Android custom dialog example! Android custom dialog example! Android custom dialog example!" +
                            "Android custom dialog example! Android custom dialog example! Android custom dialog example!");
                    ImageView image = (ImageView) dialog.findViewById(R.id.image);
                    image.setImageResource(R.drawable.icon);
*/

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                    // if button is clicked, close the custom dialog
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "We will call you shortly", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();


                        }
                    });

                    dialog.show();

                }
            });

            googleMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));

            /*googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {

                    try {
                        popUpWindow.setVisibility(View.VISIBLE);
                        Stores store = storeList.get(Integer.parseInt(marker
                                .getSnippet()));

                        // set details
                        email.setText(store.getEmail());
                        phoneNo.setText(store.getPhone());
                        address.setText(store.getAddress());

                        // setting test value to phone number
                        tempString = store.getPhone();
                        SpannableString spanString = new SpannableString(tempString);
                        spanString.setSpan(new UnderlineSpan(), 0,
                                spanString.length(), 0);
                        phoneNo.setText(spanString);

                        // setting test value to email
                        tempStringemail = store.getEmail();

                        SpannableString spanString1 = new SpannableString(tempStringemail);
                        spanString1.setSpan(new UnderlineSpan(), 0, spanString1.length(), 0);
                        email.setText(spanString1);

                        storeLat = store.getLatitude();
                        storelng = store.getLongtitude();

                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e("ArrayIndexOutOfBoundsException", " Occured");
                    }

                }
            });
            */
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*if (item.getItemId() == R.id.legal) {
            startActivity(new Intent(this, LoginActivity.class));

            return(true);
        }*/

        return true;
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {

        Marker marker = googleMap.addMarker(new MarkerOptions().position(MyLatLng).title("Hello"));

    }*/

    @Override
    public void onLocationChanged(Location location) {

/*        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        getNearestMosques(latLng);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_DISTANCE,
                LOCATION_REFRESH_TIME, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean isGooglePlayServicesAvailable() {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == status) {

            return true;
        } else {

            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void getNearestMosques(LatLng latLng) {

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=12.9817,77.6117&radius=2000&types=mosque&key=AIzaSyAgcEbfxnb9bnb9MK90g7eC-fdNcJoClEk&
        // pagetoken=

        /*final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();*/

        /*mApiHandler.get_About(latLng, new VolleyTasksListener<JSONObject>() {

            @Override
            public void handleResult(JSONObject response) {

                pDialog.hide();

                try {

                    Log.i(Constants.TAG, "response: " + response);
                    googleMap.clear();

                    JSONArray masjidArr = response.getJSONArray("results");

                    if (masjidArr != null && masjidArr.length() != 0) {

                        for (int i = 0; i < masjidArr.length(); i++) {

                            JSONObject masjidObj = masjidArr.getJSONObject(i);

                            if (masjidObj != null && masjidObj.getJSONObject("geometry") != null) {

                                String masjidName = masjidObj.getString("name");

                                JSONObject masjidLoc = masjidObj.getJSONObject("geometry");

                                if (masjidLoc.getJSONObject("location") != null) {

                                    JSONObject masjid = masjidLoc.getJSONObject("location");

                                    googleMap.addMarker(new MarkerOptions().
                                            position(new LatLng(masjid.getDouble("lat"), masjid.getDouble("lng"))).alpha(0.7f).title(masjidName).icon(
                                        BitmapDescriptorFactory.fromResource(R.drawable.icon)));

                                }

                            }

                        }

                    }

                    *//*if (status.equalsIgnoreCase("success")) {

                        JSONObject responsejson = response
                                .getJSONObject("response_data");

                        Log.i(Constants.TAG, "response: " + responsejson.getString("address")
                                + responsejson.getString("phone"));

                    }*//*
                } catch (Exception e) {
                    Log.e(Constants.TAG, "exception:" + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void handleError(Exception e) {

                e.printStackTrace();
            }
        });*/

        mApiHandler.getBabySitters(latLng, new VolleyTasksListener<JSONArray>() {

            @Override
            public void handleResult(JSONArray response) {

                //pDialog.hide();

                try {

                    Log.i(Constants.TAG, "response: " + response);
                    googleMap.clear();

                    JSONArray babySitterArr = response;//.getJSONArray("results");

                    if (babySitterArr != null && babySitterArr.length() != 0) {

                        babySitters = new HashMap<String, BabySitter>();

                        double babySitterLat = 0.0;
                        double babySitterLon = 0.0;

                        String babySitterCat = "";
                        String babySitterAdd = "";

                        String babySitterYearOfEst = "2010";

                        for (int i = 0; i < babySitterArr.length(); i++) {

                            JSONObject babySitterObj = babySitterArr.getJSONObject(i);

                            //if (masjidObj != null && masjidObj.getJSONObject("geometry") != null) {

                                String babySitterName = babySitterObj.getString("name");


                                if(babySitterObj.has("latitude") && babySitterObj.has("longitude")) {

                                    babySitterLat = babySitterObj.getDouble("latitude");
                                    babySitterLon = babySitterObj.getDouble("longitude");
                                }

                                if(babySitterObj.has("category")) {

                                    babySitterCat = babySitterObj.getString("category");
                                }

                                if(babySitterObj.has("address")) {

                                    babySitterAdd = babySitterObj.getString("address");
                                }

                                if(babySitterObj.has("year_of_est")) {

                                    babySitterYearOfEst = babySitterObj.getString("year_of_est");
                                }

                                if (babySitterLat != 0.0 && babySitterLon != 0.0) {

                                    BabySitter babySitter = new BabySitter(babySitterName, babySitterCat,
                                            babySitterLat, babySitterLon, babySitterAdd,
                                            babySitterYearOfEst, babySitterObj.getDouble("distance"));

                                    babySitters.put(babySitterName, babySitter);

                                    int randomInt = randInt(1, 6);

                                    switch(randomInt) {

                                        case 1: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_1)));
                                            break;

                                        case 2: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_2)));
                                            break;

                                        case 3: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_3)));
                                            break;

                                        case 4: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_4)));
                                            break;

                                        case 5: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_5)));
                                            break;

                                        case 6:googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.baby_sitter_6)));
                                            break;

                                        default: googleMap.addMarker(new MarkerOptions().
                                                position(new LatLng(babySitterLat, babySitterLon)).title(babySitterName).icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.babysitter)));
                                            break;

                                    }

                                }

                            //}

                        }

                    }

                    /*if (status.equalsIgnoreCase("success")) {

                        JSONObject responsejson = response
                                .getJSONObject("response_data");

                        Log.i(Constants.TAG, "response: " + responsejson.getString("address")
                                + responsejson.getString("phone"));

                    }*/
                } catch (Exception e) {
                    Log.e(Constants.TAG, "exception:" + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void handleError(Exception e) {

                e.printStackTrace();
            }
        });
    }

    class PopupAdapter implements GoogleMap.InfoWindowAdapter {
        LayoutInflater inflater=null;

        PopupAdapter(LayoutInflater inflater) {
            this.inflater=inflater;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return(null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            View popup=inflater.inflate(R.layout.info_window_layout, null);

            TextView tv=(TextView) popup.findViewById(R.id.info_tv);

            tv.setText(marker.getTitle());

            /*tv=(TextView)popup.findViewById(R.id.snippet);
            tv.setText(marker.getSnippet());*/

            return(popup);
        }
    }

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}