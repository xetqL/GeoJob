package ch.mse.mobop.geojobfinder.job.api;

import java.io.IOException;
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

    protected abstract List<JobOffer> getEmptyJobList();


    /**
     * Generic function that process request on an API, merge results and finally return
     * the whole list of job offer.
     * @param requests
     * @return List of job requests
     * @throws IOException
     */
    public List<JobOffer> retrieveJobOffersFromRequests(JobRequest... requests) throws IOException{
        List<JobOffer> jobList = getEmptyJobList();
        for(JobRequest req : requests){
            jobList.addAll(retrieveJobOffersFromRequest(req));
        }
        return jobList;
    }

    public abstract String executeRequest(JobRequest request) throws IOException;

}
