package ch.mse.mobop.geojobfinder.job.api;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by xetqL on 24/12/2015.
 */
public interface StoreJobOfferComponent  extends GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    void storeJobOffer(Marker m, JobOffer j);

    void removeJobOffer(JobOffer j);

    JobOffer findJobOfferFromMarker(Marker m);
    @Override
    boolean onMarkerClick(Marker marker);

    @Override
    void onInfoWindowClick(Marker marker);
}
