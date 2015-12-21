package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 21/12/2015.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class APIRequestExecutor extends AsyncTask<Tuple<JobAPI,JobRequest>, Void, List<JobOffer>> {
    public SupportMapFragment googleMap;

    public APIRequestExecutor(SupportMapFragment googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected List<JobOffer> doInBackground(Tuple<JobAPI, JobRequest>... params) {
        Log.d("doInBackground","used");
        JobAPI api = params[0].api;
        JobRequest req = params[0].request;
        Log.d("req", req.getRequest());
        try{
            return api.retrieveJobOffersFromRequest(req);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<JobOffer> result) {
        Log.d("onPostExecute","used");
        for(JobOffer offer : result){
            Log.d("Job offer", offer.toString());
            googleMap.getMap().addMarker(new MarkerOptions().position(offer.getLocationAsLatLng()).title("a job offer"));
        }
        super.onPostExecute(result);
    }
}
