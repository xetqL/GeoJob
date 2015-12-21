package ch.mse.mobop.geojobfinder;

import android.content.Context;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.JobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.api.Tuple;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobAPI;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.utils.APIResponsesUtils;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;

public class EntryPointActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener{

    private final Location mLastLocation = new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_point);
        final Button b = (Button) findViewById(R.id.btn_search);
        mLastLocation.setLongitude(0D);
        mLastLocation.setLatitude(0D);

        final TextView latitude = (TextView) findViewById(R.id.latitude);
        final TextView longitude = (TextView) findViewById(R.id.longitude);
        final TextView city = (TextView) findViewById(R.id.city);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Criteria locationCriteria = new Criteria();
        final int time     = 10; // mSecond
        final int distance = 1; // m.

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for(String p : locationManager.getProviders(true)) Log.d("Available provider", p);

        locationCriteria.setAccuracy(Criteria.ACCURACY_LOW);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        locationCriteria.setSpeedRequired(true);

        String provider = locationManager.getBestProvider(locationCriteria, true);

        Log.d("Location provider", provider);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, time, distance, this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lon = mLastLocation.getLongitude();
                double lat = mLastLocation.getLatitude();
                longitude.setText(String.valueOf(lon));
                latitude.setText(String.valueOf(lat));
                //Get address base on location
                try{
                    CompleteLocation currentLoc = CompleteLocation.retrieveFromGPS(EntryPointActivity.this.getApplicationContext(), mLastLocation, IndeedCountryCode.class);
                    city.setText(currentLoc.getCity() + " " + currentLoc.getCountryCode());
                    if(lon != 0 && lat != 0) {
                        mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 10f));
                        JobAPI indeedAPI = new IndeedJobAPI();
                        JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(currentLoc, indeedAPI.developerKey).build();
                        new APIRequestExecutor(mapFragment).execute(new Tuple<>(indeedAPI, req));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng geneva = new LatLng(46.2, 6.15);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geneva, 10.0f));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
