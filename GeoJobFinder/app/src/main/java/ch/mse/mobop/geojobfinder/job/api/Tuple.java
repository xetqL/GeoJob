package ch.mse.mobop.geojobfinder.job.api;

/**
 * Created by xetqL on 21/12/2015.
 */
public class Tuple<K,V> {
    K api;
    V request;

    public Tuple(K api, V request) {
        this.api = api;
        this.request = request;
    }
}
