package ch.mse.mobop.geojobfinder.job.utils.wrapper;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenuAdapter;

import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.JobOffer;

/**
 * Created by xetqL on 29/12/2015.
 */
public class ListViewWrapper implements JobDisplayerWrapper<ListView, Integer> {
    private final ListView displayer;

    public ListViewWrapper(ListView displayer) {
        this.displayer = displayer;
    }

    @Override
    public Integer add(JobOffer offer) {
        ArrayAdapter<JobOffer> jobOfferArrayAdapter = (ArrayAdapter<JobOffer>) ((SwipeMenuAdapter) displayer.getAdapter()).getWrappedAdapter();
        jobOfferArrayAdapter.add(offer);
        jobOfferArrayAdapter.notifyDataSetChanged();
        return jobOfferArrayAdapter.getCount() - 1;
    }

    @Override
    public Map<Integer, JobOffer> addAll(JobOffer... offers) {
        Map<Integer, JobOffer> r = new HashMap<>();
        ArrayAdapter<JobOffer> jobOfferArrayAdapter = (ArrayAdapter<JobOffer>) ((SwipeMenuAdapter) displayer.getAdapter()).getWrappedAdapter();
        int count;
        for(JobOffer offer : offers) {
            count = jobOfferArrayAdapter.getCount();
            jobOfferArrayAdapter.add(offer);
            r.put(count, offer);
        }
        jobOfferArrayAdapter.notifyDataSetChanged();
        return r;
    }

    @Override
    public void remove(Integer element) {
        ArrayAdapter<JobOffer> jobOfferArrayAdapter = (ArrayAdapter<JobOffer>) ((SwipeMenuAdapter) displayer.getAdapter()).getWrappedAdapter();
        jobOfferArrayAdapter.remove( jobOfferArrayAdapter.getItem(element)  );
        jobOfferArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void clear() {
        ArrayAdapter<JobOffer> jobOfferArrayAdapter = (ArrayAdapter<JobOffer>) ((SwipeMenuAdapter) displayer.getAdapter()).getWrappedAdapter();
        jobOfferArrayAdapter.clear();
    }

    @Override
    public ListView getWrappedObject() {
        return displayer;
    }
}
