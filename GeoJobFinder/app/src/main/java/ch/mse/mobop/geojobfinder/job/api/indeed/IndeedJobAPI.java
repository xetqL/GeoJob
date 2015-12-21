package ch.mse.mobop.geojobfinder.job.api.indeed;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.mse.mobop.geojobfinder.job.api.APIRequestExecutor;
import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobAPI;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.utils.APIResponsesUtils;

/**
 * Created by xetqL on 19/12/2015.
 */
public class IndeedJobAPI extends JobAPI {
    public static final String developeryKey = "958671519160857";

    public IndeedJobAPI(String developerKey) {
        super(developerKey);
    }

    public IndeedJobAPI() {
        super(IndeedJobAPI.developeryKey);
    }

    @Override
    public List<JobOffer> retrieveJobOffers(CountryCode country, String location) throws IOException {
        String rawData = executeRequest((JobRequest) IndeedJobRequestBuilder.create(country, location, developerKey).withLimit(100).build());
        final List<JobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
            JSONArray resultArray = response.getJSONArray("result");
            for (int i = 0; i < resultArray.length(); i++) {
                jobOffers.add(IndeedJobOffer.buildFromAPIResponse((JSONObject) resultArray.get(i)));
            }
        } catch (JSONException jsonException) {
            return jobOffers;
        }
        return jobOffers;
    }


    @Override
    public List<JobOffer> retrieveJobOffersAndFilterByTags(CountryCode country, String location, String... tags) throws IOException {
        String rawData = executeRequest((JobRequest) IndeedJobRequestBuilder.create(country, location, developerKey).withLimit(100).withTags(tags).build());
        final List<JobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
        } catch (JSONException jsonException) {
            return jobOffers;
        }
        return jobOffers;
    }

    @Override
    public List<JobOffer> retrieveJobOffersInArea(CountryCode country, String location, int radius) throws IOException {
        String rawData = executeRequest((JobRequest) IndeedJobRequestBuilder.create(country, location, developerKey).withLimit(100).build());
        final List<JobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
            JSONArray resultArray = response.getJSONArray("result");
            for (int i = 0; i < resultArray.length(); i++) {
                jobOffers.add(IndeedJobOffer.buildFromAPIResponse((JSONObject) resultArray.get(i)));
            }
        } catch (JSONException jsonException) {
            return jobOffers;
        }
        return jobOffers;
    }

    @Override
    public List<JobOffer> retrieveJobOffersFromRequest(JobRequest request) throws IOException {
        String rawData = executeRequest(request);
        final List<JobOffer> jobOffers = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONTokener(rawData).nextValue();
            JSONArray resultArray = response.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                jobOffers.add(IndeedJobOffer.buildFromAPIResponse((JSONObject) resultArray.get(i)));
            }
            Log.d("JobOffersRequestSize", String.valueOf(jobOffers.size()));
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return jobOffers;
        }
        return jobOffers;
    }

    @Override
    public String executeRequest(JobRequest request) throws IOException {
        URL url = new URL(request.getRequest());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String response = null;
        try {
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = APIResponsesUtils.readStream(in);
        } finally {
            conn.disconnect();
        }
        return response;
    }

}