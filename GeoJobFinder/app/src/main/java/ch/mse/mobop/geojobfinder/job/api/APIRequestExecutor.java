package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 21/12/2015.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;
import ch.mse.mobop.geojobfinder.job.utils.Tuple;

public class APIRequestExecutor extends AsyncTask<Tuple<JobAPI,JobRequest[]>, Void, List<JobOffer>> {
    public final GoogleMap googleMap;
    public final Context context;
    public final StoreJobOfferComponent markerClickListener;
    private static final Random rand = new Random();

    public APIRequestExecutor(Context context, GoogleMap googleMap, StoreJobOfferComponent markerClickListener) {
        this.googleMap = googleMap;
        this.context = context;
        this.markerClickListener = markerClickListener;
    }

    @Override
    protected List<JobOffer> doInBackground(Tuple<JobAPI, JobRequest[]>... params) {
        JobAPI api = params[0].k;
        JobRequest[] requestArray = params[0].v;
        try{
            return api.retrieveJobOffersFromRequests(requestArray);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<JobOffer> result) {
        for(JobOffer offer : result){
            double offsetX = rand.nextInt(100) / 180000D, offsetY = rand.nextInt(100) / 180000D;
            markerClickListener.storeJobOffer(
                    googleMap.addMarker(GoogleMapUtils.getMarkerOptions(offer.getJobTitle(), offer.getLocationAsLatLng(offsetX, offsetY), offer.getSnippet())),
                    offer
            );
        }
        super.onPostExecute(result);
    }
}
