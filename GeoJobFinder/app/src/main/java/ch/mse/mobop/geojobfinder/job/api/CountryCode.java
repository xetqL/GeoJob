package ch.mse.mobop.geojobfinder.job.api;

import android.content.Context;
import android.location.Location;

import java.io.Serializable;

/**
 * Created by xetqL on 19/12/2015.
 */
public interface CountryCode extends Serializable{

    String getCode();

    String name();

}
