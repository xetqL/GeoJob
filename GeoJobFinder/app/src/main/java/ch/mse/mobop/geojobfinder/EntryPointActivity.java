package ch.mse.mobop.geojobfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.StrTokenizer;

import java.util.List;
import java.util.StringTokenizer;

public class EntryPointActivity extends AppCompatActivity implements LocationListener{

    private final Location mLastLocation = new Location("");
    private SeekBar sb;
    private EditText editTags;
    private boolean canNotifyForLocation = false;
    private ProgressDialog waitOnGPSDialog;
    private static final int GPS_REQUEST_CODE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_point);
        //geneva
        mLastLocation.setLongitude(6.15);
        mLastLocation.setLatitude(46.2);

        editTags = (EditText) findViewById(R.id.tags);

        final TextView radiusValue = (TextView) findViewById(R.id.radiusValue);

        sb = (SeekBar) findViewById(R.id.radius);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int LIMIT = 1;
                if (progress < LIMIT) {
                    seekBar.setProgress(LIMIT);
                }else {
                    radiusValue.setText(String.valueOf(progress)+" km");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final int time = 300;           // mSecond
        final int distance = 150;       // m.
        int off = 0;
        // Check if GPS is enabled
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){
            // if GPS not enabled
            //alert user for enabling GPS or leave app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Application requires GPS, please turn on GPS")
                   .setPositiveButton("Turn on GPS", new DialogInterface.OnClickListener(){

                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                           startActivityForResult(onGPS, EntryPointActivity.GPS_REQUEST_CODE);

                       }
                   })
                   .setNegativeButton("Leave application", new DialogInterface.OnClickListener(){
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Toast.makeText(getApplicationContext(), "Application is terminating...", Toast.LENGTH_LONG).show();
                           android.os.Process.killProcess(android.os.Process.myPid());
                       }
                   }).create().show();
        } else {
            waitOnGPSDialog = ProgressDialog.show(EntryPointActivity.this, "Retrieving GPS position", "Please wait...", true);
        }

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());
        Intent intent = new Intent("location_update").putExtra("last_known_location", mLastLocation);
        LocalBroadcastManager.getInstance(EntryPointActivity.this).sendBroadcast(intent);
        if(waitOnGPSDialog != null && waitOnGPSDialog.isShowing()) {
            waitOnGPSDialog.dismiss();
            waitOnGPSDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EntryPointActivity.GPS_REQUEST_CODE) waitOnGPSDialog = ProgressDialog.show(EntryPointActivity.this, "Retrieving GPS position", "Please wait...", true);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_jobs_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        String rawTags = editTags.getText().toString();
        StrTokenizer st = new StrTokenizer(rawTags, ' ');
        st.setIgnoreEmptyTokens(true);
        Intent i;
        switch (item.getItemId()) {
            case R.id.searchJob:
                i = new Intent(this, ListJobsActivity.class);
                i.putExtra("last_known_location", mLastLocation);
                i.putExtra("request_radius", sb.getProgress());
                i.putExtra("request_tags", st.getTokenArray());
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
