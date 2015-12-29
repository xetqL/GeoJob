package ch.mse.mobop.geojobfinder.job.api.indeed;

import android.location.Location;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;
import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.utils.APIResponsesUtils;
import ch.mse.mobop.geojobfinder.job.api.CompleteLocation;

/**
 * Created by xetqL on 21/12/2015.
 */
public class IndeedJobOffer extends JobOffer {
    private final String apiLocation = "indeed";

    private IndeedJobOffer(String jobKey, CompleteLocation location, String jobTitle, String snippet, String proposalURL, String company) throws MalformedURLException {
        super(UUID.randomUUID(), jobKey, jobTitle, company, location, snippet, new URL(proposalURL));
    }

    protected IndeedJobOffer(Parcel in) {
        super(in);
    }

    @Override
    public String getAPILocation() {
        return "indeed";
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
                response.getString("jobkey"),
                CompleteLocation.buildFromValue(loc, APIResponsesUtils.decode(response.getString("city")), IndeedCountryCode.valueOf(response.getString("country"))),
                response.getString("jobtitle"),
                response.getString("url"),
                response.getString("snippet"),
                response.getString("source")
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
                response.getString("jobkey"),
                CompleteLocation.buildFromValue(loc, APIResponsesUtils.decode(response.getString("city")), IndeedCountryCode.valueOf(response.getString("country"))),
                response.getString("jobtitle"),
                response.getString("snippet"),
                response.getString("url"),
                response.getString("source")
        );
        return res;
    }

    @Override
    public LatLng getPosition() {
        return location.toLatLng();
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, 0);
        dest.writeString(this.jobTitle);
        dest.writeString(this.snippet);
        dest.writeString(this.company);
        dest.writeString(this.jobKey);
        dest.writeSerializable(this.proposalURL);
        dest.writeSerializable(this.appUniqueIdentifier);
    }


    public static final Creator<JobOffer> CREATOR = new Creator<JobOffer>() {
        public JobOffer createFromParcel(Parcel source) {
            return new IndeedJobOffer(source);
        }

        public JobOffer[] newArray(int size) {
            return new JobOffer[size];
        }
    };
}
