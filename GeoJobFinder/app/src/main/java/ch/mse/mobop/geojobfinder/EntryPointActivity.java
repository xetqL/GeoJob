package ch.mse.mobop.geojobfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.util.List;
import java.util.Locale;

public class EntryPointActivity extends AppCompatActivity {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private Location mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_point);
        final Button b = (Button) findViewById(R.id.btn_search);

        final TextView latitude = (TextView) findViewById(R.id.latitude);
        final TextView longitude = (TextView) findViewById(R.id.longitude);
        final TextView city = (TextView) findViewById(R.id.city);

        final String context = Context.LOCATION_SERVICE;
        final LocationManager locationManager = (LocationManager) getSystemService(context);
        final Criteria locationCriteria = new Criteria();
        final int time     = 5000; // mSecond
        final int distance = 100; // m.
        for(String p : locationManager.getProviders(true)) Log.d("Available provider", p);
        locationCriteria.setAccuracy(Criteria.ACCURACY_LOW);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);

        final String provider = locationManager.getBestProvider(locationCriteria, true);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        Log.d("Location provider", provider);

        locationManager.requestLocationUpdates(provider, time, distance, locationListener);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lon = mLastLocation.getLongitude();
                double lat = mLastLocation.getLatitude();
                longitude.setText(String.valueOf(mLastLocation.getLongitude()));
                latitude.setText(String.valueOf(mLastLocation.getLatitude()));
                //Get address base on location
                try{
                    Geocoder geo = new Geocoder(EntryPointActivity.this.getApplicationContext(), Locale.getDefault());
                    if(!geo.isPresent()){
                        Log.d("geocoder", "is not present");
                        throw new Exception("DAMN BRO GEOCODER IS NOT PRESENT");
                    }
                    List<Address> addresses = geo.getFromLocation(lat, lon, 1);
                    if (addresses.isEmpty()) {
                        city.setText("Waiting for Location");
                    }
                    else {
                        if (addresses.size() > 0) {
                            Log.d("CITY", addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +
                                    ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            city.setText(addresses.get(0).getLocality());
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
