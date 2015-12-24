package ch.mse.mobop.geojobfinder.job.utils;

/**
 * Created by xetqL on 21/12/2015.
 */
public class Tuple<K,V> {
    public K k;
    public V v;

    public Tuple(K api, V request) {
        this.k = api;
        this.v = request;
    }
}
