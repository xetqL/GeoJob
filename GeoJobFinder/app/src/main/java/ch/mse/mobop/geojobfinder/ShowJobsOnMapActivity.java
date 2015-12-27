package ch.mse.mobop.geojobfinder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobAPI;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;

public class ShowJobsOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, StoreJobOfferComponent {

    private final Map<Marker, JobOffer> currentJobOffers = new HashMap<>();
    private Location currentLoc;
    private LocationManager locationManager;
    private GoogleMap gmap;
    private String[] tags;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jobs_on_map);
        Intent i = getIntent();
        final int time = 600;           // mSecond
        final int distance = 150;       // m.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.allJobsMap);
        currentLoc = (Location) i.getParcelableExtra("last_known_location");
        radius = (int) i.getIntExtra("request_radius", 1);
        tags = i.getStringArrayExtra("request_tags");

        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnInfoWindowClickListener(this);
        mapFragment.getMap().setOnMarkerClickListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gmap = mapFragment.getMap();
        refreshMap(gmap, currentLoc, radius, tags);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {}

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
        if(job == null) return;
        i.putExtra("selected_job", job);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.jobs_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.refreshMap:
                currentLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                refreshMap(gmap, currentLoc, radius, tags);
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMap(GoogleMap gmap, Location currentLoc, int radius, String... tags){
        gmap.clear();
        try {
            CompleteLocation completeLocation = CompleteLocation.retrieveFromGPS(getApplicationContext(), currentLoc, IndeedCountryCode.class);

            final JobAPI indeedAPI = new IndeedJobAPI();
            final JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(completeLocation, indeedAPI.developerKey).withLimit(100).withRadius(radius).withTags(tags).build();

            GoogleMapUtils.displayRequestsResultsOnMap(getApplicationContext(), gmap, completeLocation, ShowJobsOnMapActivity.this, new Tuple<>(indeedAPI, new JobRequest[]{req}));

            gmap.addMarker(GoogleMapUtils.getMarkerOptions("Me", completeLocation.toLatLng(), "My position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(completeLocation.toLatLng(), 14f));
        }catch (ClassNotFoundException|IOException cnfe){
            cnfe.printStackTrace();
        }

    }
}
