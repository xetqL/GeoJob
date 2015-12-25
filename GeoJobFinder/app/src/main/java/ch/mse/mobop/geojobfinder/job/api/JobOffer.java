package ch.mse.mobop.geojobfinder.job.api;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URL;
import java.util.UUID;

/**
 * Created by xetqL on 19/12/2015.
 */
public abstract class JobOffer implements ClusterItem{

    protected final CompleteLocation location;
    protected final String jobTitle, snippet, company, jobKey;
    protected final URL proposalURL;
    protected final UUID appUniqueIdentifier;

    public JobOffer(UUID appUnique, String jobKey, String jobTitle, String company, CompleteLocation location, String snippet, URL proposalURL) {
        this.location    = location;
        this.jobKey      = jobKey;
        this.company     = company;
        this.snippet     = snippet;
        this.jobTitle    = jobTitle;
        this.proposalURL = proposalURL;
        this.appUniqueIdentifier = appUnique;
    }

    public UUID getAppUniqueIdentifier() {
        return appUniqueIdentifier;
    }

    public CompleteLocation getLocation() {
        return location;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getCompany() {
        return company;
    }

    public String getJobKey() {
        return jobKey;
    }

    public URL getProposalURL() {
        return proposalURL;
    }

    public Location getGPSLocation(){
        return location.getGpsLocation();
    }

    public CountryCode getCountry(){
        return location.getCountryCode();
    }

    public LatLng getLocationAsLatLng(){
        return location.toLatLng();
    }

    public boolean isInCountry(CountryCode country){
        return country == this.location.getCountryCode();
    }

    public LatLng getLocationAsLatLng(double offsetX, double offsetY) {
        return new LatLng(
                location.getGpsLocation().getLatitude() + offsetX,
                location.getGpsLocation().getLongitude() + offsetY
        );
    }

    public LatLng getPosition(){
        return location.toLatLng();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JobOffer offer = (JobOffer) o;

        return new EqualsBuilder()
                .append(jobKey, offer.jobKey)
                .append(appUniqueIdentifier, offer.appUniqueIdentifier)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(jobKey)
                .append(appUniqueIdentifier)
                .toHashCode();
    }

    public abstract String getAPILocation(); //get the name of the api that has the job offer
}
