package ch.mse.mobop.geojobfinder.job.api.indeed;

import com.google.common.base.Joiner;

import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobRequestBuilder;

/**
 * Created by xetqL on 19/12/2015.
 */
public class IndeedJobRequestBuilder extends JobRequestBuilder {
    private final String apiUrl = "http://api.indeed.com/ads/apisearch?";

    public IndeedJobRequestBuilder(CountryCode country, String location, String developerKey) {
        httpRequest.put("co", country.getCode());
        httpRequest.put("l", location);
        httpRequest.put("publisher", developerKey);
        httpRequest.put("chnnl", "");
        httpRequest.put("format", "json");
    }

    @Override
    public JobRequestBuilder withTags(String... tags) {
        Joiner join = Joiner.on("AND").skipNulls();
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
    public String build() {
        StringBuilder request = new StringBuilder(apiUrl);
        for (Map.Entry<String, String> param : httpRequest.entrySet()){
            request.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }
        request.deleteCharAt(request.length()-1);
        clear();
        return request.toString();
    }

    private void clear(){
        httpRequest.clear();
    }
}
