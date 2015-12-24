package ch.mse.mobop.geojobfinder;

import android.content.Context;

import android.content.Intent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobAPI;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;

public class EntryPointActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, StoreJobOfferComponent {

    private final Location mLastLocation = new Location("");
    private final Map<Marker, JobOffer> currentJobOffers = new HashMap<>();

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
        mapFragment.getMap().setOnMarkerClickListener(this);
        mapFragment.getMap().setOnInfoWindowClickListener(this);


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
                        double [] w = new double[]{1D};
                        final JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(currentLoc, indeedAPI.developerKey).withLimit(100).withRadius(1).build();
                        new APIRequestExecutor(getApplicationContext(), mapFragment, EntryPointActivity.this).execute(new Tuple<>(indeedAPI, new JobRequest[]{req}));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void storeJobOffer(Marker m, JobOffer j) {
        currentJobOffers.put(m, j);
    }

    @Override
    public void removeJobOffer(JobOffer j) {
        if(!currentJobOffers.containsValue(j)) return;
        for(Map.Entry<Marker, JobOffer> e : currentJobOffers.entrySet()){
            if(e.getValue().equals(j)) {
                currentJobOffers.remove(e.getKey());
                return;
            }
        }
    }

    @Override
    public JobOffer findJobOfferFromMarker(Marker m) {
        if(!currentJobOffers.containsKey(m)) return null;
        for(Map.Entry<Marker, JobOffer> e : currentJobOffers.entrySet()){
            if(e.getKey().equals(m)) {
                return e.getValue();
            }
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng geneva = new LatLng(46.2, 6.15);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geneva, 10.0f));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
