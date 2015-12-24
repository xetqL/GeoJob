package ch.mse.mobop.geojobfinder.job.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by xetqL on 24/12/2015.
 */
public class GoogleMapUtils {

    public static MarkerOptions getMarkerOptions(String title, LatLng position, String snippet){
        return new MarkerOptions().title(title).position(position).snippet(snippet);
    }
}
