package ch.mse.mobop.geojobfinder.job.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedCountryCode;

/**
 * Created by xetqL on 21/12/2015.
 */
public class CompleteLocation implements Parcelable {
    private final Location gpsLocation;
    private final CountryCode countryCode;
    private final String city;

    public CompleteLocation(Location gpsLocation, CountryCode countryCode, String city) {
        this.gpsLocation = gpsLocation;
        this.countryCode = countryCode;
        this.city = city;
    }

    public Location getGpsLocation() {
        return gpsLocation;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public String getCity() {
        return city;
    }

    public static CompleteLocation retrieveFromGPS(Context context, Location l, Class targetCountryCodeAPI) throws IOException, ClassNotFoundException {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        if (!geo.isPresent()) {
            Log.d("Geocoder", "Google APIs are not installed");
        }
        List<Address> addresses = geo.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
        if (addresses.isEmpty()) {
            return null;
        } else {
            Log.d("CITY", addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +
                    ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            Log.d("CountryCode", IndeedCountryCode.valueOf(addresses.get(0).getCountryCode()).toString());
            if(targetCountryCodeAPI == IndeedCountryCode.class){
                return new CompleteLocation(l, IndeedCountryCode.valueOf(addresses.get(0).getCountryCode()), addresses.get(0).getLocality());
            }//add more if needed
            throw new ClassNotFoundException(targetCountryCodeAPI.getName() + "is not an available CountryCode class");
        }
    }

    public static CompleteLocation buildFromValue(Location gpsLocation, String city, CountryCode countryCode){
        return new CompleteLocation(gpsLocation, countryCode, city);
    }

    public LatLng toLatLng(){
        return new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
    }

    @Override
    public String toString() {
        return city + " " + countryCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.gpsLocation, 0);
        dest.writeSerializable(this.countryCode);
        dest.writeString(this.city);
    }

    protected CompleteLocation(Parcel in) {
        this.gpsLocation = in.readParcelable(Location.class.getClassLoader());
        this.countryCode = (CountryCode) in.readSerializable();
        this.city = in.readString();
    }

    public static final Parcelable.Creator<CompleteLocation> CREATOR = new Parcelable.Creator<CompleteLocation>() {
        public CompleteLocation createFromParcel(Parcel source) {
            return new CompleteLocation(source);
        }

        public CompleteLocation[] newArray(int size) {
            return new CompleteLocation[size];
        }
    };
}
