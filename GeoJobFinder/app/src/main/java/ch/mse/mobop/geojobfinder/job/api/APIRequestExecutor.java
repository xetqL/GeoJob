package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 21/12/2015.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.mse.mobop.geojobfinder.job.utils.wrapper.JobDisplayerWrapper;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;

public class APIRequestExecutor<W, A> extends AsyncTask<Tuple<JobAPI, JobRequest[]>, Void, List<JobOffer>> {
    public final JobDisplayerWrapper<W, A> displayerWrapper;
    public final Context context;
    public final StoreJobOfferComponent<A> storingComponent;
    private static final Random rand = new Random();

    public APIRequestExecutor(Context context, JobDisplayerWrapper<W, A> displayerWrapper, StoreJobOfferComponent<A> storingComponent) {
        this.displayerWrapper = displayerWrapper;
        this.context = context;
        this.storingComponent = storingComponent;
    }

    @Override
    protected List<JobOffer> doInBackground(Tuple<JobAPI, JobRequest[]>... params) {
        List<JobOffer> results = new ArrayList<>();
        try {
            for (Tuple<JobAPI, JobRequest[]> t : params) {
                JobAPI api = t.k;
                JobRequest[] requestArray = t.v;
                results.addAll( api.retrieveJobOffersFromRequests(requestArray) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally { //finally return the results
            return results;
        }
    }

    @Override
    protected void onPostExecute(List<JobOffer> result) {
        for (JobOffer offer : result) {
            storingComponent.storeJobOffer( displayerWrapper.add(offer), offer );
        }
        super.onPostExecute(result);
    }
}
