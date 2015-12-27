package ch.mse.mobop.geojobfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;

public class EntryPointActivity extends AppCompatActivity implements LocationListener, StoreJobOfferComponent{

    private final Location mLastLocation = new Location("");
    private final Map<Marker, JobOffer> currentJobOffers = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_point);
        //geneva
        mLastLocation.setLongitude(6.15);
        mLastLocation.setLatitude(46.2);
        final TextView radiusValue = (TextView) findViewById(R.id.radiusValue);

        final SeekBar sb = (SeekBar) findViewById(R.id.radius);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final int time = 10; // mSecond
        final int distance = 1; // m.
        int off = 0;
        // Check if GPS is enabled
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){ // if GPS not enabled
            //alert user for enabling GPS or leave app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Application requires GPS, please turn on GPS")
                   .setPositiveButton("Turn on GPS", new DialogInterface.OnClickListener(){

                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                           startActivity(onGPS);
                       }
                   })
                   .setNegativeButton("Leave application", new DialogInterface.OnClickListener(){

                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Toast.makeText(getApplicationContext(), "Application is terminating...", Toast.LENGTH_LONG).show();
                           android.os.Process.killProcess(android.os.Process.myPid());
                       }
                   }).create().show();
        }
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for (String p : locationManager.getProviders(true)) Log.d("Available provider", p);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, time, distance, this);

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
