package com.yahoo.palagummi.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;
    TextView latTextView;
    TextView lngTextView;
    TextView accTextView;
    TextView altTextView;
    TextView addressTextView;


    public void updateLocationInfo(Location location) {
        Log.i("Location is ", location.toString());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationListening();
        }
    }


    public void startLocationListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location is ", location.toString());

                latTextView = (TextView) findViewById(R.id.latTextView);
                lngTextView = (TextView) findViewById(R.id.lngTextView);
                accTextView = (TextView) findViewById(R.id.accTextView);
                altTextView = (TextView) findViewById(R.id.altTextView);
                addressTextView = (TextView) findViewById(R.id.addressTextView);

                // Format the LatLng to 4 decimel places
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(4);

                latTextView.setText("Latitude: " + df.format(location.getLongitude()));
                lngTextView.setText("Longitude: " + df.format(location.getLatitude()));
                accTextView.setText("Accuracy: " + df.format(location.getAccuracy()));
                altTextView.setText("Altitude: " + df.format(location.getAltitude()));

                String address = "Address not available";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(listAddresses!= null && listAddresses.size() > 0) {
                        address = "";
                        if (listAddresses.get(0).getSubThoroughfare() != null)
                            address += listAddresses.get(0).getSubThoroughfare() + " ";
                        if (listAddresses.get(0).getThoroughfare() != null)
                            address += listAddresses.get(0).getThoroughfare() + ",\n";
                        if (listAddresses.get(0).getLocality() != null)
                            address += listAddresses.get(0).getLocality() + ",\n";
                        if (listAddresses.get(0).getCountryName() != null)
                            address += listAddresses.get(0).getCountryName() + ", ";
                        if (listAddresses.get(0).getPostalCode() != null)
                            address += listAddresses.get(0).getPostalCode();
                    }
                    addressTextView.setText("Address:\n" + address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation != null) updateLocationInfo(lastKnownLocation);
        }
    }
}
