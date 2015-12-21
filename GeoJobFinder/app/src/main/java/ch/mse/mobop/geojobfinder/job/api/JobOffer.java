package ch.mse.mobop.geojobfinder.job.api;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.net.URL;

/**
 * Created by xetqL on 19/12/2015.
 */
public abstract class JobOffer implements ClusterItem{

    public abstract String getJobTitle();

    public abstract Location getGPSLocation(); //get the GPS coordinate as LAT;LON

    public abstract CountryCode getCountry();

    public abstract LatLng getLocationAsLatLng(double offsetX, double offsetY);

    public abstract LatLng getLocationAsLatLng();


    public abstract boolean isInCountry(CountryCode country);

    public abstract String getAPILocation(); //get the name of the api that has the job offer

    public abstract URL getProposalURL();

    public abstract LatLng getPosition();
}
