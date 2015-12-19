package ch.mse.mobop.geojobfinder.job.api;

import java.util.List;

/**
 * Created by xetqL on 19/12/2015.
 */

public abstract class JobAPI {

    protected final String developerKey;

    protected JobAPI(String developerKey) {
        this.developerKey = developerKey;
    }

    //no search, just get it all. Should not be used, maybe too long to process
    public abstract List<IJobOffer> retrieveJobOffers(CountryCode country, String location);

    /**
     * If a field match a predicate, return it
     *
     * @param predicates
     * @return
     */
    public abstract List<IJobOffer> retrieveJobOffersAndFilterByTags(CountryCode country, String location, String... tags);

    /**
     * Search job offers in a given area. The input can change in function of the location model (GPS ?, zip code ? etc.)
     *
     * @param location
     * @return
     */
    public abstract List<IJobOffer> retrieveJobOffersInArea(CountryCode country, String location, int radius);

    /**
     * Execute the request and returns the raw data.
     *
     * @param request
     * @return
     */
    public abstract String executeRequest(String request);

}
