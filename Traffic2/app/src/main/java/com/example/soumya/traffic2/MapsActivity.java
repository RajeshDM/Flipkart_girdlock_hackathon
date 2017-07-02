package com.example.soumya.traffic2;import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.soumya.traffic2.app.AppController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private String url = "http://192.168.43.220:5000/id/";
    ArrayList<Marker> markers = new ArrayList<Marker>();
    private int[]  j = new int[8];
    private int[]  p = new int[8];
    private int numclick = 1;

    private static String TAG = MapsActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    // temporary string to show the parsed response
    private String jsonResponse;

    //int n = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        j[0] = 1;
        j[1] = 1;
        j[2] = 1;
        j[3] = 1;
        j[4] = 1;
        j[5] = 1;
        j[6] = 1;
        j[7] = 1;
        p[0] = 4;
        p[1] = 4;
        p[2] = 4;
        p[3] = 4;
        p[4] = 4;
        p[5] = 4;
        p[6] = 4;
        p[7] = 4;
        //j[4] = 1;
        //j[5] = 1;
        //j[6] = 1;
        //j[7] = 1;
       //public boolean onMarkerClick(final Marker marker) {

    //}
    }
    private void makeJsonObjectRequest(final String URl) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object

                    String density = response.getString("density");
                    String speed = response.getString("speed");

                    jsonResponse = "";
                    jsonResponse += "Density: " + density + "\n\n";
                    jsonResponse += "Speed: " + speed + "kmph\n\n";
                    System.out.println(jsonResponse);

                    //txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        mMap.setOnMarkerClickListener(this);
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        BitmapDescriptor icons = BitmapDescriptorFactory.fromResource(R.drawable.photo_camera);
        //Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        LatLng silkboard = new LatLng(12.9175, 77.6242);
        markers.add(0, mMap.addMarker(new MarkerOptions().position(silkboard).icon(icons).title("Marker in Silk board")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(silkboard));

        LatLng mgroad = new LatLng(12.9766, 77.5993);
        markers.add(1, mMap.addMarker(new MarkerOptions().position(mgroad).icon(icons).title("Marker in MG Road")));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(mgroad));

        /*LatLng koramangala = new LatLng(12.9362, 77.6162);
        markers.add(2, mMap.addMarker(new MarkerOptions().position(koramangala).icon(icons).title("Marker in Koramangala")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(koramangala));*/

       LatLng majestic = new LatLng(12.9787, 77.5724);
        markers.add(2, mMap.addMarker(new MarkerOptions().position(majestic).icon(icons).title("Marker in Majestic")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(majestic));

       /* LatLng madiwala = new LatLng(12.9232, 77.6244);
        markers.add(4, mMap.addMarker(new MarkerOptions().position(madiwala).icon(icons).title("Marker in Madiwala")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(madiwala));

        LatLng konapanna = new LatLng(12.8557, 77.6649);
        markers.add(5, mMap.addMarker(new MarkerOptions().position(konapanna).icon(icons).title("Marker in Konapanna Agrahara")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(konapanna));*/

        LatLng magadi = new LatLng(12.981, 77.5357);
        markers.add(3, mMap.addMarker(new MarkerOptions().position(magadi).icon(icons).title("Marker in Magadi Road")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(magadi));

       /* LatLng tumkur = new LatLng(13.0284, 77.5409);
        markers.add(3, mMap.addMarker(new MarkerOptions().position(tumkur).icon(icons).title("Marker in Tumkur Flyover")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tumkur));*/

        //boolean b = onMarkerClick(marker1);
        //System.out.println(b);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public boolean onMarkerClick(Marker marker) {
        //int i;

            for(int i = 0; i < markers.size(); i++)
            {
                if (marker.equals(markers.get(i))) {

                        String Url = url + Integer.toString(i+1) + "/time/" + Integer.toString(j[i]);
                        String Url_image = url + Integer.toString(i+1) + "/get_image/" + Integer.toString(j[i]);
                        System.out.println(i + ", value at i is " + j[i]) ;
                        if(j[i]<10) j[i]++;
                            else j[i] = 1;
                        /*j[i] += 1;
                        if (j[i] == (p[i]+1))
                            j[i]=1;*/
                        makeJsonObjectRequest(Url);
                        Intent intent = new Intent(MapsActivity.this, CongestionInfo.class);
                        intent.putExtra("Marker", jsonResponse);
                        intent.putExtra("urlimage",Url_image);
                        System.out.println(Url_image + "HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                        startActivity(intent);


                    //}
                }
            }


        /*for(int i = 0; i < markers.size(); i++) {
            if (marker.equals(markers.get(i))) {
                String Url = url + Integer.toString(i+1) + "/time/" + Integer.toString(j[i]);
                String Url_image = url + Integer.toString(i+1) + "/get_image/" + Integer.toString(j[i]);
                System.out.println(i + ", value at i is " + j[i]) ;
                j[i] += 1;
                if (j[i] == p[i])
                    j[i] =1;
                makeJsonObjectRequest(Url);
                Intent intent = urlimagenew Intent(MapsActivity.this, CongestionInfo.class);
                intent.putExtra("Marker", jsonResponse);
                intent.putExtra("",Url_image);
                //System.out.println(jsonResponse + "HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                startActivity(intent);
                //break;
            }
        }*/
            return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}

