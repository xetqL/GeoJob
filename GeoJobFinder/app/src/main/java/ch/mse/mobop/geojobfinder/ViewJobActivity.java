package ch.mse.mobop.geojobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;
import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;

public class ViewJobActivity extends AppCompatActivity implements OnMapReadyCallback{

    private JobOffer jobOffer;

    /*private String jobKey, jobTitle, jobCompany, jobSnippet;
    private double lat,lon;
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Should redirect on the company website", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        final TextView jTitle = (TextView) findViewById(R.id.jobTitleText),
                jCompany = (TextView) findViewById(R.id.companyText),
                jSnippet = (TextView) findViewById(R.id.snippetText);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewJobMap);

        //get job infos
        jobOffer = (IndeedJobOffer) i.getParcelableExtra("selected_job");

        /*jobTitle    = i.getStringExtra("job_title");
        jobCompany  = i.getStringExtra("job_company");
        jobSnippet  = i.getStringExtra("job_snippet");
        jobKey      = i.getStringExtra("job_uniquekey");
        lat         = i.getDoubleExtra("job_lat", 46.12);
        lon         = i.getDoubleExtra("job_lon", 6.15);*/

        //set graphical components
        jTitle.setText(jobOffer.getJobTitle());
        jCompany.setText(jobOffer.getCompany());
        jSnippet.setText(jobOffer.getSnippet());

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(GoogleMapUtils.getMarkerOptions(jobOffer.getJobTitle(), jobOffer.getLocationAsLatLng(), jobOffer.getSnippet()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobOffer.getLocationAsLatLng(), 16f));
    }
}
