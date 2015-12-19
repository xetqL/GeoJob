package ch.mse.mobop.geojobfinder.job.api;

import java.util.List;

import ch.mse.mobop.geojobfinder.job.utils.MultiplePredicates;

/**
 * Created by xetqL on 19/12/2015.
 */

public interface JobAPI {

    //no search, just get it all. Should not be used, maybe too long to process
    List<JobOffer> retrieveAllJobOffers();

    /**
     * If a field match a predicate, return it
     * @param predicates
     * @return
     */
    List<JobOffer> searchJobOffersByPredicates(MultiplePredicates<String> predicates);

    /**
     * Search job offers in a given area. The input can change in function of the location model (GPS ?, zip code ? etc.)
     * @param location
     * @return
     */
    List<JobOffer> searchJobOffersInArea(String location);

}
