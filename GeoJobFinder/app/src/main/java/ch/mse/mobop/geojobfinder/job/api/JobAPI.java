package ch.mse.mobop.geojobfinder.job.api;

import java.util.List;

import ch.mse.mobop.geojobfinder.job.utils.MultiplePredicates;

/**
 * Created by xetqL on 19/12/2015.
 */

public interface JobAPI {

    List<JobOffer> retrieveAllJobOffers(); //no search, just get it all

    /**
     * If a field match a predicate, return it
     * @param predicates
     * @return
     */
    List<JobOffer> searchJobOfferByPredicates(MultiplePredicates<String> predicates);

}
