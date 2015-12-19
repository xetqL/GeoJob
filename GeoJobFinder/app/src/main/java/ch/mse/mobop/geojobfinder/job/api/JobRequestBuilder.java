package ch.mse.mobop.geojobfinder.job.api;

import java.util.HashMap;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.utils.MultiplePredicates;

/**
 * Created by xetqL on 19/12/2015.
 */
public abstract class JobRequestBuilder {
    protected final Map<String, String> httpRequest = new HashMap<>();

    public abstract JobRequestBuilder withTags(String... tags);
    public abstract JobRequestBuilder withLimit(int limit);
    public abstract JobRequestBuilder withRadius(int radius);
    public abstract String build();
}
