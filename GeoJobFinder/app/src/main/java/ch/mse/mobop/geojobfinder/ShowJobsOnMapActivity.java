package ch.mse.mobop.geojobfinder;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;
import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;

public class ShowJobsOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, StoreJobOfferComponent{

    private final Map<Marker, JobOffer> currentJobOffers = new HashMap<>();
    private Location currentLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jobs_on_map);
        Intent i = getIntent();
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.allJobsMap);
        currentLoc = (Location) i.getParcelableExtra("last_known_location");
        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnInfoWindowClickListener(this);
        mapFragment.getMap().setOnMarkerClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ltlg = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
        GoogleMapUtils.displayJobsOnMap(googleMap, currentLoc, getApplicationContext(), this);
        googleMap.addMarker(GoogleMapUtils.getMarkerOptions("Me", ltlg, "My position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 14f));
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
        if(job == null) return;
        i.putExtra("selected_job", job);
        startActivity(i);
    }
}
