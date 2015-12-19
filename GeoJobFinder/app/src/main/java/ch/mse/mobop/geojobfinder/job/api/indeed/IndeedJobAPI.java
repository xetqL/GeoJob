package ch.mse.mobop.geojobfinder.job.api.indeed;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.IJobOffer;

/**
 * Created by xetqL on 19/12/2015.
 */
public class IndeedJobAPI extends JobAPI {
    public static final String developeryKey = "958671519160857";

    public IndeedJobAPI(String developerKey) {
        super(developerKey);
    }

    public IndeedJobAPI(){
        super(IndeedJobAPI.developeryKey);
    }

    @Override
    public List<IJobOffer> retrieveJobOffers(CountryCode country, String location) {
        String rawData = executeRequest(new IndeedJobRequestBuilder(country, location, developerKey).withLimit(100).build());
        final List<IJobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
        }catch(JSONException jsonException){
            return jobOffers;
        }
        return jobOffers;
    }


    @Override
    public List<IJobOffer> retrieveJobOffersAndFilterByTags(CountryCode country, String location, String... tags) {
        String rawData = executeRequest(new IndeedJobRequestBuilder(country, location, developerKey).withLimit(100).withTags(tags).build());
        final List<IJobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
        }catch(JSONException jsonException){
            return jobOffers;
        }
        return jobOffers;
    }

    /**
     * Search job offers in a given area. The input can change in function of the location model (GPS ?, zip code ? etc.)
     *
     * @param location
     * @return
     */
    @Override
    public List<IJobOffer> retrieveJobOffersInArea(CountryCode country, String location, int radius) {
        String rawData = executeRequest(new IndeedJobRequestBuilder(country, location, developerKey).withLimit(100).build());
        final List<IJobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
        }catch(JSONException jsonException){
            return jobOffers;
        }
        return jobOffers;
    }

    @Override
    public String executeRequest(String request) {
        return null;
    }
}
