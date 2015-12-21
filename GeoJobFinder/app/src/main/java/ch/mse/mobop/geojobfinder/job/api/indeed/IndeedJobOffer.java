package ch.mse.mobop.geojobfinder.job.api.indeed;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.utils.APIResponsesUtils;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;

/**
 * Created by xetqL on 21/12/2015.
 */
public class IndeedJobOffer implements JobOffer {
    private final CompleteLocation location;
    private final String JobTitle, apiLocation = "indeed", snippet;
    private final URL proposalURL;

    private IndeedJobOffer(CompleteLocation location, String jobTitle, String snippet, String proposalURL) throws MalformedURLException {
        this.location = location;
        this.JobTitle = jobTitle;
        this.proposalURL = new URL(proposalURL);
        this.snippet = snippet;
    }

    @Override
    public String getJobTitle() {
        return null;
    }

    @Override
    public Location getGPSLocation() {
        return location.getGpsLocation();
    }

    @Override
    public CountryCode getCountry() {
        return location.getCountryCode();
    }

    @Override
    public LatLng getLocationAsLatLng() {
        return location.toLatLng();
    }

    @Override
    public boolean isInCountry(CountryCode country) {
        return location.getCountryCode() == country;
    }

    @Override
    public String getAPILocation() {
        return "indeed";
    }

    @Override
    public URL getProposalURL() {
        return proposalURL;
    }

    public static JobOffer buildFromAPIResponse(String rawResponse) throws IOException, JSONException {
        JobOffer res = null;

        JSONObject response = (JSONObject) new JSONTokener(rawResponse).nextValue();

        double lat = response.getDouble("latitude"),
                lon = response.getDouble("longitude");
        Location loc = new Location("job");
        loc.setLongitude(lon);
        loc.setLatitude(lat);

        res = new IndeedJobOffer(
                CompleteLocation.buildFromValue(loc, APIResponsesUtils.decode(response.getString("city")), IndeedCountryCode.valueOf(response.getString("country"))),
                response.getString("jobtitle"),
                response.getString("url"),
                response.getString("snippet")
        );
        return res;
    }

    public static JobOffer buildFromAPIResponse(JSONObject jsonResponse) throws IOException, JSONException {
        JobOffer res = null;
        JSONObject response = jsonResponse;
        double lat = response.getDouble("latitude"),
                lon = response.getDouble("longitude");
        Location loc = new Location("job");
        loc.setLongitude(lon);
        loc.setLatitude(lat);

        res = new IndeedJobOffer(
                CompleteLocation.buildFromValue(loc, APIResponsesUtils.decode(response.getString("city")), IndeedCountryCode.valueOf(response.getString("country"))),
                response.getString("jobtitle"),
                response.getString("snippet"),
                response.getString("url")
        );

        return res;
    }

    @Override
    public String toString() {
        return "IndeedJobOffer{" +
                "location=" + location +
                ", JobTitle='" + JobTitle + '\'' +
                ", apiLocation='" + apiLocation + '\'' +
                ", snippet='" + snippet + '\'' +
                ", proposalURL=" + proposalURL +
                '}';
    }
}
