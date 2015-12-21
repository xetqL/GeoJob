package ch.mse.mobop.geojobfinder.job.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by xetqL on 19/12/2015.
 */

public abstract class JobAPI {

    public final String developerKey;

    protected JobAPI(String developerKey) {
        this.developerKey = developerKey;
    }

    public abstract List<JobOffer> retrieveJobOffers(CountryCode country, String location)  throws IOException;


    public abstract List<JobOffer> retrieveJobOffersAndFilterByTags(CountryCode country, String location, String... tags)  throws IOException;


    public abstract List<JobOffer> retrieveJobOffersInArea(CountryCode country, String location, int radius)  throws IOException;

    public abstract List<JobOffer> retrieveJobOffersFromRequest(JobRequest request) throws IOException;

    public abstract String executeRequest(JobRequest request) throws IOException;

}
