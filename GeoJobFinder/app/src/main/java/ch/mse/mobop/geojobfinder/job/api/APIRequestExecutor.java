package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 21/12/2015.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class APIRequestExecutor extends AsyncTask<Tuple<JobAPI,JobRequest>, Void, List<JobOffer>> {
    public SupportMapFragment googleMap;
    public Context context;
    private static final Random rand = new Random();
    public APIRequestExecutor(Context context, SupportMapFragment googleMap) {
        this.googleMap = googleMap;
        this.context = context;
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
        ClusterManager<JobOffer> mClusterManager = new ClusterManager<JobOffer>(context, googleMap.getMap());
        googleMap.getMap().setOnMarkerClickListener(mClusterManager);
        googleMap.getMap().setOnCameraChangeListener(mClusterManager);

        for(JobOffer offer : result){
            Log.d("Job offer", offer.toString());
            double offsetX = rand.nextInt(10) / 6000D, offsetY = rand.nextInt(10) / 6000D;
            googleMap.getMap().addMarker(new MarkerOptions().position(offer.getLocationAsLatLng(offsetX,offsetY)).title(offer.getJobTitle()));

        }
        super.onPostExecute(result);
    }
}
