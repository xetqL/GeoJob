package ch.mse.mobop.geojobfinder.job.api.indeed;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;

/**
 * Created by xetqL on 19/12/2015.
 */
public enum IndeedCountryCode implements CountryCode {
    US("United States"),
    AR("Argentina"),
    AU("Australia"),
    AT("Austria"),
    BH("Bahrain"),
    BE("Belgium"),
    CA("Canada"),
    CL("Chile"),
    CN("China"),
    CO("Colombia"),
    CZ("Czech Republic"),
    DK("Denmark"),
    FI("Finland"),
    FR("France"),
    DE("Germany"),
    GR("Greece"),
    HK("Hong Kong"),
    HU("Hungary"),
    IN("India"),
    ID("Indonesia"),
    IE("Ireland"),
    IL("Israel"),
    IT("Italy"),
    JP("Japan"),
    KR("Korea"),
    KW("Kuwait"),
    LU("Luxembourg"),
    MY("Malaysia"),
    MX("Mexico"),
    NL("Netherlands"),
    NZ("New Zealand"),
    NO("Norway"),
    OM("Oman"),
    PK("Pakistan"),
    PE("Peru"),
    PH("Philippines"),
    PL("Poland"),
    PT("Portugal"),
    QA("Qatar"),
    RO("Romania"),
    RU("Russia"),
    SA("Saudi Arabia"),
    SG("Singapore"),
    ZA("South Africa"),
    ES("Spain"),
    SE("Sweden"),
    CH("Switzerland"),
    TW("Taiwan"),
    TR("Turkey"),
    AE("United Arab Emirates"),
    GB("United Kingdom"),
    VE("Venezuela");

    private final String name;

    IndeedCountryCode(String s) {
        name = s;
    }

    public String getCode() {
        return name();
    }

    public String toString() {
        return this.name;
    }
}
