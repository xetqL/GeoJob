package ch.mse.mobop.geojobfinder.job.utils.wrapper;

import java.util.List;
import java.util.Map;

import ch.mse.mobop.geojobfinder.job.api.JobOffer;

/**
 * Created by xetqL on 29/12/2015.
 */
public interface JobDisplayerWrapper<W, R> {
    R add(JobOffer offer);
    Map<R, JobOffer> addAll(List<JobOffer> offers);
    void remove(R element);
    void clear();
    W getWrappedObject();
}
