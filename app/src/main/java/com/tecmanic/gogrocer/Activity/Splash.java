package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.KEY_ID;
import static com.tecmanic.gogrocer.Config.BaseURL.MyPrefreance;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    SharedPreferences sharedPreferences;
    Session_management session_management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setFinishOnTouchOutside(true);
        session_management = new Session_management(Splash.this);
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
            //getActivity().finish();
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(this)){
            Toast.makeText(getApplicationContext(),"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            // Log.e("Neha","Gps already enabled");
            Toast.makeText(getApplicationContext(),"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            // Log.e("Neha","Gps already enabled");
            Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if(session_management.isLoggedIn()){
                    Intent intent1 =new Intent(Splash.this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
                else {
                    Intent intent1 =new Intent(Splash.this,LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(7 * 1000);  //30 * 1000
            locationRequest.setFastestInterval(5 * 1000); //5 * 1000
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Splash.this,REQUEST_LOCATION);

                                // getActivity().finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }
}

