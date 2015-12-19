package ch.mse.mobop.geojobfinder.job.api;

import java.net.URL;

/**
 * Created by xetqL on 19/12/2015.
 */
public interface IJobOffer {

    void apply(); //apply to job offer, TBD

    String getJobTitle();

    String getGPSLocation(); //get the GPS coordinate as LAT;LON

    CountryCode getCountry();

    boolean isInCountry(CountryCode country);

    String getAPILocation(); //get the name of the api that has the job offer

    String getContact();     //get the contact email

    URL getProposalURL();

}
