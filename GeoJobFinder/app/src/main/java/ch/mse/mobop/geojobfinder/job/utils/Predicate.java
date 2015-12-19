package ch.mse.mobop.geojobfinder.job.utils;

/**
 * Created by xetqL on 19/12/2015.
 */
public interface Predicate<A> {
    boolean matchPredicate(A value);
}
