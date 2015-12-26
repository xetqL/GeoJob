package ch.mse.mobop.geojobfinder.job.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.StoreJobOfferComponent;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobAPI;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobRequestBuilder;

/**
 * Created by xetqL on 24/12/2015.
 */
public class GoogleMapUtils {

    public static MarkerOptions getMarkerOptions(String title, LatLng position, String snippet){
        return new MarkerOptions().title(title).position(position).snippet(snippet);
    }

    public static CompleteLocation displayJobsOnMap(GoogleMap map, Location bestLastLocation, Context context, StoreJobOfferComponent storeJobOfferComponent) {

        double lon = bestLastLocation.getLongitude();
        double lat = bestLastLocation.getLatitude();
        //Get address based on location
        try {
            CompleteLocation currentLoc = CompleteLocation.retrieveFromGPS(context, bestLastLocation, IndeedCountryCode.class);
            if (lon != 0 && lat != 0) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 12f));
                final JobAPI indeedAPI = new IndeedJobAPI();
                final JobRequest req = (JobRequest) IndeedJobRequestBuilder.create(currentLoc, indeedAPI.developerKey).withLimit(100).withRadius(1).build();
                new APIRequestExecutor(context, map, storeJobOfferComponent).execute(new Tuple<>(indeedAPI, new JobRequest[]{req}));
                return currentLoc;
            }
            return null;
        } catch (Exception e) {
            Log.w("GPS", "No GPS value acquired");
        }
        return null;
    }
}
