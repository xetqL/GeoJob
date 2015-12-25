package ch.mse.mobop.geojobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ch.mse.mobop.geojobfinder.job.api.CountryCode;

public class ViewJobActivity extends AppCompatActivity {

    private String jobKey, jobTitle, jobCompany, jobSnippet;
    private double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Should redirect on the company website", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        final TextView jTitle = (TextView) findViewById(R.id.jobTitleText),
                jCompany = (TextView) findViewById(R.id.companyText),
                jSnippet = (TextView) findViewById(R.id.snippetText);
        jobTitle = i.getStringExtra("job_title");
        jobCompany = i.getStringExtra("job_company");
        jobSnippet = i.getStringExtra("job_snippet");
        jTitle.setText(jobTitle);
        jCompany.setText(jobCompany);

        jSnippet.setText(jobSnippet);
        jobKey = i.getStringExtra("job_uniquekey");

        lat = i.getDoubleExtra("job_lat", 46.12);
        lon = i.getDoubleExtra("job_lon", 6.15);
    }
}
