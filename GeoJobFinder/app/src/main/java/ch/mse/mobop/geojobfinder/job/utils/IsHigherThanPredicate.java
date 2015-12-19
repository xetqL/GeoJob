package ch.mse.mobop.geojobfinder.job.utils;

/**
 * Created by xetqL on 19/12/2015.
 */
public class IsHigherThanPredicate implements Predicate<Comparable> {
    private Number predicateValue;

    public IsHigherThanPredicate(Number whatChecksPredicate){
        this.predicateValue = whatChecksPredicate;
    }

    @Override
    public boolean matchPredicate(Comparable value) {
        return value.compareTo(predicateValue) > 0;
    }
}
