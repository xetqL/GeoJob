package ch.mse.mobop.geojobfinder.job.utils;

/**
 * Created by xetqL on 19/12/2015.
 */
public class ContainsStringPredicate implements Predicate<String>{
    private String willBeCheckedInString;

    public ContainsStringPredicate(String whatChecksPredicate){
        this.willBeCheckedInString = whatChecksPredicate;
    }

    @Override
    public boolean matchPredicate(String text) {
        return text.contains(willBeCheckedInString);
    }
}
