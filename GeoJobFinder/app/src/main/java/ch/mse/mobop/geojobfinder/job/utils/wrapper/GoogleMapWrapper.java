package ch.mse.mobop.geojobfinder.job.utils.wrapper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.utils.GoogleMapUtils;

/**
 * Created by xetqL on 29/12/2015.
 */
public class GoogleMapWrapper implements JobDisplayerWrapper<GoogleMap, Marker> {
    private final GoogleMap displayer;

    public GoogleMapWrapper(GoogleMap displayer) {
        this.displayer = displayer;
    }

    @Override
    public Marker add(JobOffer offer) {
        return displayer.addMarker( GoogleMapUtils.nextMarkerOptions(offer) );
    }

    @Override
    public Map<Marker, JobOffer> addAll(List<JobOffer> offers) {
        Map<Marker, JobOffer> r = new HashMap<>();
        for(JobOffer j : offers)
            r.put( add(j), j);
        return r;
    }

    @Override
    public void remove(Marker element) {
        element.remove();
    }

    @Override
    public void clear() {
        displayer.clear();
    }

    @Override
    public GoogleMap getWrappedObject() {
        return displayer;
    }
}
