package ch.mse.mobop.geojobfinder.job.utils.predicate;

/**
 * Created by xetqL on 19/12/2015.
 */
public class MultiplePredicates<A> implements Predicate<A> {
    Predicate<A>[] predicateList;

    public MultiplePredicates(Predicate<A>... predicates){
        this.predicateList = predicates;
    }

    @Override
    public boolean matchPredicate(A mustMatchPredicate) {
        boolean hasMatchedPredicate = true;
        for(Predicate<A> p : predicateList){
            hasMatchedPredicate &= p.matchPredicate(mustMatchPredicate);
        }
        return hasMatchedPredicate;
    }
}
