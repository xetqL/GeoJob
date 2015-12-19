package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 19/12/2015.
 */
public interface JobOffer {

    void apply(); //apply to job offer, TBD

    String getGPSLocation(); //get the GPS coordinate as LAT;LON

    boolean isInCountry(String country);

    String getAPILocation(); //get the name of the api that has the job offer

    String getContact();     //get the contact email

}
