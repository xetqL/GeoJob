package ch.mse.mobop.geojobfinder.job.api.indeed;

import com.google.common.base.Joiner;

import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;
import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobRequest;
import ch.mse.mobop.geojobfinder.job.api.JobRequestBuilder;

/**
 * Created by xetqL on 19/12/2015.
 */
public class IndeedJobRequestBuilder extends JobRequestBuilder<JobRequest> {
    private final String apiUrl = "http://api.indeed.com/ads/apisearch?", location, developerKey;
    private final CountryCode co;

    private void init() {
        httpRequest.put("co", this.co.getCode());
        httpRequest.put("l", this.location);
        httpRequest.put("publisher", this.developerKey);
        httpRequest.put("chnnl", "");
        httpRequest.put("format", "json");
        httpRequest.put("v", "2");
        httpRequest.put("latlong","1");
    }

    private IndeedJobRequestBuilder(CountryCode country, String location, String developerKey) {
        this.co = country;
        this.location=location;
        this.developerKey=developerKey;
        init();
    }

    @Override
    public JobRequestBuilder withTags(String... tags) {
        Joiner join = Joiner.on("+").skipNulls();
        httpRequest.put("q", join.join(tags));
        return this;
    }

    @Override
    public JobRequestBuilder withLimit(int limit) {
        httpRequest.put("limit", String.valueOf(limit));
        return this;
    }

    @Override
    public JobRequestBuilder withRadius(int radius) {
        httpRequest.put("radius", String.valueOf(radius));
        return this;
    }

    @Override
    public JobRequestBuilder startFrom(int startFrom) {
        httpRequest.put("start", String.valueOf(startFrom));
        return this;
    }

    @Override
    public JobRequest build() {
        StringBuilder request = new StringBuilder(apiUrl);
        for (Map.Entry<String, String> param : httpRequest.entrySet()){
            request.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }
        request.deleteCharAt(request.length() - 1);
        return new JobRequest(request.toString());
    }

    @Override
    public void clear(){
        httpRequest.clear();
        init();
    }

    public static JobRequestBuilder create(CountryCode country, String location, String developerKey){
        return new IndeedJobRequestBuilder(country, location, developerKey);
    }

    public static JobRequestBuilder create(CompleteLocation location, String developerKey){
        return new IndeedJobRequestBuilder(location.getCountryCode(), location.getCity(), developerKey);
    }

}
