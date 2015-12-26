package ch.mse.mobop.geojobfinder;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;
import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;
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

        final int time = 10; // mSecond
        final int distance = 1; // m.
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Application requires GPS, would you turn on GPS ?")
                   .setPositiveButton("ok", new DialogInterface.OnClickListener(){

                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                           startActivity(onGPS);
                       }
                   })
                   .setNegativeButton("no", new DialogInterface.OnClickListener(){

                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Toast.makeText(getApplicationContext(), "Application is terminating...", Toast.LENGTH_LONG).show();
                           android.os.Process.killProcess(android.os.Process.myPid());
                       }
                   }).create().show();
        }
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for (String p : locationManager.getProviders(true)) Log.d("Available provider", p);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, this);
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this);
        else
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, time, distance, this);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            clearJobOffers();
            longitude.setText(String.valueOf(mLastLocation.getLongitude()));
            latitude.setText(String.valueOf(mLastLocation.getLatitude()));
            CompleteLocation cLoc = GoogleMapUtils.displayJobsOnMap(mapFragment.getMap(), mLastLocation, getApplicationContext(), EntryPointActivity.this);
            if(cLoc != null) city.setText(cLoc.toString());
            }
        });
    }

    @Override
    public void storeJobOffer(Marker m, JobOffer j) {
        currentJobOffers.put(m, j);
    }

    @Override
    public void clearJobOffers() {
        for (Marker m : currentJobOffers.keySet()) m.remove();
        currentJobOffers.clear();
    }

    @Override
    public void removeJobOffer(JobOffer j) {
        if (!currentJobOffers.containsValue(j)) return;
        for (Map.Entry<Marker, JobOffer> e : currentJobOffers.entrySet()) {
            if (e.getValue().equals(j)) {
                currentJobOffers.remove(e.getKey());
                return;
            }
        }
    }

    @Override
    public JobOffer findJobOfferFromMarker(Marker m) {
        if (!currentJobOffers.containsKey(m)) return null;
        for (Map.Entry<Marker, JobOffer> e : currentJobOffers.entrySet()) {
            if (e.getKey().equals(m)) {
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
        Intent i = new Intent(this, ViewJobActivity.class);
        IndeedJobOffer job = (IndeedJobOffer) findJobOfferFromMarker(marker);
        i.putExtra("selected_job", job);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_jobs_menu, menu);
        return true;
    }

    private <A extends JobOffer> Collection<A> castToConcreteJobOffer(Collection<JobOffer> originalCol, Class<A> cls){
        Collection<A> rCol = new ArrayList<>();
        for(JobOffer j : originalCol){
            rCol.add(cls.cast(j));
        }
        return rCol;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.gotoMap:
                Intent i = new Intent(this, ShowJobsOnMapActivity.class);
                i.putExtra("last_known_location", mLastLocation);
                startActivity(i);
                return true;
        }
        return true;
    }
}
