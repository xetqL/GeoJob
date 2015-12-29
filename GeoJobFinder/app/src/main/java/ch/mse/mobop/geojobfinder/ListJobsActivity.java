package ch.mse.mobop.geojobfinder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
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
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.utils.wrapper.ListViewWrapper;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;

public class ListJobsActivity extends AppCompatActivity implements StoreJobOfferComponent<Integer> {

    private ListViewWrapper jobList;

    private Map<Integer, JobOffer> currentJobOffers = new HashMap<>();
    private Location currentLoc;
    private LocationManager locationManager;
    private String[] tags;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_jobs);
        final ListView lw  = (ListView) findViewById(R.id.joblist);
        lw.setAdapter(new ArrayAdapter<JobOffer>(getApplicationContext(), R.layout.row, R.id.text, new ArrayList<JobOffer>()));
        jobList = new ListViewWrapper(lw);
        Intent i = getIntent();
        currentLoc = (Location) i.getParcelableExtra("last_known_location");
        radius = (int) i.getIntExtra("request_radius", 1);
        tags = i.getStringArrayExtra("request_tags");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            CompleteLocation completeLocation = CompleteLocation.retrieveFromGPS(getApplicationContext(), currentLoc, IndeedCountryCode.class);
            final JobAPI indeedAPI = new IndeedJobAPI();
            final JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(completeLocation, indeedAPI.developerKey).withLimit(100).withRadius(radius).withTags(tags).build();
            new APIRequestExecutor<ListView, Integer>(getApplicationContext(), jobList, this).execute(new Tuple<>(indeedAPI, new JobRequest[]{req}));
        }catch(ClassNotFoundException|IOException cnfe){
            cnfe.printStackTrace();
        }
    }

    @Override
    public void storeJobOffer(Integer m, JobOffer j) {
        currentJobOffers.put(m, j);
    }

    @Override
    public void clearJobOffers() {
        ArrayAdapter<JobOffer> arrayAdapter = (ArrayAdapter<JobOffer>) jobList.getWrappedObject().getAdapter();
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
        currentJobOffers.clear();
    }

    @Override
    public void removeJobOffer(JobOffer j) {
        if (!currentJobOffers.containsValue(j)) return;
        for (Map.Entry<Integer, JobOffer> e : currentJobOffers.entrySet()) {
            if (e.getValue().equals(j)) {
                ArrayAdapter<JobOffer> arrayAdapter = (ArrayAdapter<JobOffer>) jobList.getWrappedObject().getAdapter();
                arrayAdapter.remove(e.getValue());
                arrayAdapter.notifyDataSetChanged();
                currentJobOffers.remove(e.getKey());
                return;
            }
        }
    }

    @Override
    public JobOffer findJobOfferFromIndex(Integer m) {
        if (!currentJobOffers.containsKey(m)) return null;
        for (Map.Entry<Integer, JobOffer> e : currentJobOffers.entrySet()) {
            if (e.getKey().equals(m)) {
                return e.getValue();
            }
        }
        return null;
    }

    @Override
    public void set(Map<Integer, JobOffer> data) {
        clearJobOffers();
        this.currentJobOffers = data;
    }
}
