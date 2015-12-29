package ch.mse.mobop.geojobfinder.job.api;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * Created by xetqL on 24/12/2015.
 */
public interface StoreJobOfferComponent<T> {
    void storeJobOffer(T m, JobOffer j);

    void removeJobOffer(JobOffer j);

    JobOffer findJobOfferFromIndex(T m);

    void clearJobOffers();

    void set(Map<T, JobOffer> data);
}
