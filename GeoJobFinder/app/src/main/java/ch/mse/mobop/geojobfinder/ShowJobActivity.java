package ch.mse.mobop.geojobfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;

public class ShowJobActivity extends AppCompatActivity {

    private String jobKey, jobTitle, jobCompany, jobSnippet;
    private double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        final TextView jTitle = (TextView) findViewById(R.id.jobTitleText),
                jCompany = (TextView) findViewById(R.id.companyText),
                jLocation = (TextView) findViewById(R.id.locationText),
                jSnippet = (TextView) findViewById(R.id.snippetText);
        jobTitle = i.getStringExtra("job_title");
        jobCompany = i.getStringExtra("job_company");
        jobSnippet = i.getStringExtra("job_snippet");
        jTitle.setText(jobTitle);
        jCompany.setText(jobCompany);
        CountryCode countryCode = (CountryCode) i.getSerializableExtra("job_country");
        jLocation.setText(i.getStringExtra("job_city") + " " +countryCode.name() + " "+ countryCode.getCode());
        jSnippet.setText(jobSnippet);
        jobKey = i.getStringExtra("job_uniquekey");

        lat = i.getDoubleExtra("job_lat", 46.12);
        lon = i.getDoubleExtra("job_lon", 6.15);
    }
}
