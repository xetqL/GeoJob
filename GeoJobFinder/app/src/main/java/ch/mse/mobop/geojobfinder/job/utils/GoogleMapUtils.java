package ch.mse.mobop.geojobfinder.job.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.utils.wrapper.GoogleMapWrapper;

/**
 * Created by xetqL on 24/12/2015.
 */
public class GoogleMapUtils {

    private static final Random rand = new Random();

    public static MarkerOptions getMarkerOptions(String title, LatLng position, String snippet){
        return new MarkerOptions().title(title).position(position).snippet(snippet);
    }

    public static MarkerOptions nextMarkerOptions(JobOffer offer){
        double offsetX = rand.nextInt(100) / 180000D, offsetY = rand.nextInt(100) / 180000D;
        return new MarkerOptions().title(offer.getJobTitle()).position(offer.getLocationAsLatLng(offsetX, offsetY)).snippet( offer.getSnippet() );
    }

    public static void displayRequestsResultsOnMap(Context context, GoogleMapWrapper map, CompleteLocation lastKnownLocation, StoreJobOfferComponent<Marker> storingComponent, Tuple<JobAPI, JobRequest[]>... requests){
        LatLng ltlg = lastKnownLocation.toLatLng();
        if(ltlg.latitude != 0 && ltlg.longitude != 0){
            new APIRequestExecutor<GoogleMap, Marker>(context, map, storingComponent).execute(requests);
        }
    }
}
