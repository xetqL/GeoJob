package ch.mse.mobop.geojobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ch.mse.mobop.geojobfinder.job.api.JobOffer;
import ch.mse.mobop.geojobfinder.job.api.indeed.IndeedJobOffer;

public class ViewJobActivity extends AppCompatActivity {

    private JobOffer jobOffer;

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
        //get job infos
        jobOffer = (IndeedJobOffer) i.getParcelableExtra("selected_job");

        //set graphical components
        jTitle.setText(jobOffer.getJobTitle());
        jCompany.setText(jobOffer.getCompany());
        jSnippet.setText(jobOffer.getSnippet());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


}
