package ch.mse.mobop.geojobfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ch.mse.mobop.geojobfinder.job.utils.wrapper.GoogleMapWrapper;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;

public class ShowJobsOnMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        StoreJobOfferComponent<Marker>, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private Map<Marker, JobOffer> currentJobOffers = new HashMap<>();
    private Location currentLoc;
    private LocationManager locationManager;
    private GoogleMapWrapper gmap;
    private String[] tags;
    private int radius;

    private final BroadcastReceiver mReceiverRefreshMap = new BroadcastReceiver() { //refresh map when location is updated from main activity
        @Override
        public void onReceive(Context context, Intent intent) {
            currentLoc = (Location) intent.getParcelableExtra("last_known_location");
            refreshMap(gmap, currentLoc, radius, tags);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jobs_on_map);
        Intent i = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.allJobsMap);
        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnInfoWindowClickListener(this);
        mapFragment.getMap().setOnMarkerClickListener(this);
        gmap = new GoogleMapWrapper(mapFragment.getMap());

        switch (getCallingActivity().getClassName()) {
            case "ch.mse.mobop.geojobfinder.EntryPointActivity":
                LocalBroadcastManager.getInstance(ShowJobsOnMapActivity.this).registerReceiver(mReceiverRefreshMap, new IntentFilter("location_update"));
                currentLoc = (Location) i.getParcelableExtra("last_known_location");
                radius = (int) i.getIntExtra("request_radius", 1);
                tags = i.getStringArrayExtra("request_tags");
                refreshMap(gmap, currentLoc, radius, tags);
                break;
            case "ch.mse.mobop.geojobfinder.ListJobsActivity":
                List<JobOffer> jobs = i.getParcelableArrayListExtra("selected_jobs");
                currentLoc = (Location) i.getParcelableExtra("last_known_location");
                currentJobOffers = gmap.addAll(jobs);
                LatLng ltlg = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                gmap.getWrappedObject().addMarker(GoogleMapUtils.getMarkerOptions("Me", ltlg, "My position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                gmap.getWrappedObject().moveCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 14f));
                break;
            default:
                finish();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    @Override
    public void storeJobOffer(Marker m, JobOffer j) {
        currentJobOffers.put(m, j);
    }

    @Override
    public void clearJobOffers() {
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
    public JobOffer findJobOfferFromIndex(Marker m) {
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
        Intent i = new Intent(this, ViewJobOnWebActivity.class);
        IndeedJobOffer job = (IndeedJobOffer) findJobOfferFromIndex(marker);
        if (job == null) return;
        i.putExtra("selected_job", job);
        startActivity(i);
    }



    @Override
    public void set(Map<Marker, JobOffer> data) {
        clearJobOffers();
        this.currentJobOffers = data;
    }

    private void refreshMap(GoogleMapWrapper gmap, Location curLoc, int radius, String... tags) {
        try {
            gmap.getWrappedObject().clear();
            CompleteLocation completeLocation = CompleteLocation.retrieveFromGPS(getApplicationContext(), curLoc, IndeedCountryCode.class);
            final JobAPI indeedAPI = new IndeedJobAPI();
            final JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(completeLocation, indeedAPI.developerKey).withLimit(100).withRadius(radius).withTags(tags).build();
            GoogleMapUtils.displayRequestsResultsOnMap(getApplicationContext(), gmap, completeLocation, ShowJobsOnMapActivity.this, new Tuple<>(indeedAPI, new JobRequest[]{req}));
            gmap.getWrappedObject().addMarker(GoogleMapUtils.getMarkerOptions("Me", completeLocation.toLatLng(), "My position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            gmap.getWrappedObject().moveCamera(CameraUpdateFactory.newLatLngZoom(completeLocation.toLatLng(), 14f));
        } catch (ClassNotFoundException | IOException cnfe) {
            cnfe.printStackTrace();
        }
    }
}
