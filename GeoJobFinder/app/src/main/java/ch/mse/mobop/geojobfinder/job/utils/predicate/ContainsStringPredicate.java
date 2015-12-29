package ch.mse.mobop.geojobfinder.job.utils.predicate;

/**
 * Created by xetqL on 19/12/2015.
 */
public class ContainsStringPredicate implements Predicate<String>{
    private String willBeCheckedInString;

    public ContainsStringPredicate(String whatChecksPredicate){
        this.willBeCheckedInString = whatChecksPredicate;
    }

    @Override
    public boolean matchPredicate(String value) {
        return value.contains(willBeCheckedInString);
    }
}
