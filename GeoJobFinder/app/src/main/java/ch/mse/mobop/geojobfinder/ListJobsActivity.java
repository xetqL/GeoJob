package ch.mse.mobop.geojobfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.JobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobAPI;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;
import ch.mse.mobop.geojobfinder.job.utils.wrapper.ListViewWrapper;

public class ListJobsActivity extends AppCompatActivity implements StoreJobOfferComponent<Integer>, AbsListView.OnScrollListener {

    private ListViewWrapper jobList;
    private boolean authorizedToRetrieveSeveralJobOffers = true;

    private Map<Integer, JobOffer> currentJobOffers = new HashMap<>();
    private Location currentLoc;
    private LocationManager locationManager;
    private String[] tags;
    private int radius;
    private CompleteLocation completeLocation;
    private final JobAPI indeedAPI = new IndeedJobAPI();
    private JobRequestBuilder reqBuilder;

    public int dp2px(float dips) {
        return (int) (dips * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_jobs);
        final SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.joblist);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(android.R.drawable.ic_menu_send);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        IndeedJobOffer job = (IndeedJobOffer) findJobOfferFromIndex(position);
                        Intent i = new Intent(ListJobsActivity.this, ViewJobOnWebActivity.class);
                        if (job == null) return true;
                        i.putExtra("selected_job", job);
                        startActivity(i);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        listView.setAdapter(new ArrayAdapter<JobOffer>(getApplicationContext(), R.layout.row, R.id.text, new ArrayList<JobOffer>()));
        listView.setOnScrollListener(this);
        jobList = new ListViewWrapper((ListView) listView);
        Intent i = getIntent();
        currentLoc = (Location) i.getParcelableExtra("last_known_location");
        radius = (int) i.getIntExtra("request_radius", 1);
        tags = i.getStringArrayExtra("request_tags");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            completeLocation = CompleteLocation.retrieveFromGPS(getApplicationContext(), currentLoc, IndeedCountryCode.class);
            reqBuilder = IndeedJobRequestBuilder.create(completeLocation, indeedAPI.developerKey).withLimit(25).withRadius(radius).withTags(tags);
            new APIRequestExecutor<ListView, Integer>(getApplicationContext(), jobList, this).execute(new Tuple<>(indeedAPI, new JobRequest[]{(JobRequest) reqBuilder.build()}));
        } catch (ClassNotFoundException | IOException cnfe) {
            cnfe.printStackTrace();
        } finally {

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && (totalItemCount % IndeedJobAPI.JOB_OFFER_LIMIT_BY_REQUEST) == 0 && totalItemCount > 0 && authorizedToRetrieveSeveralJobOffers) {
            final JobRequest req = (JobRequest) reqBuilder.startFrom(totalItemCount).build();
            new APIRequestExecutor<ListView, Integer>(getApplicationContext(), jobList, this).execute(new Tuple<>(indeedAPI, new JobRequest[]{req}));
            authorizedToRetrieveSeveralJobOffers = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        authorizedToRetrieveSeveralJobOffers = true;
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
