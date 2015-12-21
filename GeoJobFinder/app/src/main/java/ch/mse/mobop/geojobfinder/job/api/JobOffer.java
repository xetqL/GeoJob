package ch.mse.mobop.geojobfinder.job.api;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;

/**
 * Created by xetqL on 19/12/2015.
 */
public interface JobOffer {

    String getJobTitle();

    Location getGPSLocation(); //get the GPS coordinate as LAT;LON

    CountryCode getCountry();

    LatLng getLocationAsLatLng();

    boolean isInCountry(CountryCode country);

    String getAPILocation(); //get the name of the api that has the job offer

    URL getProposalURL();

}
