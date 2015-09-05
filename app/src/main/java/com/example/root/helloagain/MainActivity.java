package com.example.root.helloagain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends Activity implements LocationListener {

    static final LatLng MyLatLng1 = new LatLng(12.954563, 77.639840);
    static final LatLng MyLatLng2 = new LatLng(12.946784, 77.653272);
    static final LatLng MyLatLng3 = new LatLng(12.944651, 77.642114);
    static final LatLng MyLatLng4 = new LatLng(12.956236, 77.651041);
    static final LatLng MyLatLng5 = new LatLng(12.946909, 77.638638);

    private static final long LOCATION_REFRESH_TIME = 0;
    private static final long LOCATION_REFRESH_DISTANCE = 1000;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private GoogleMap googleMap;
    private ApiHandler mApiHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {

            finish();
        }

        setContentView(R.layout.activity_main);

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

                    marker.showInfoWindow();
                    return true;
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
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
                }
            });

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        mApiHandler.get_About(latLng, new VolleyTasksListener<JSONObject>() {

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
}