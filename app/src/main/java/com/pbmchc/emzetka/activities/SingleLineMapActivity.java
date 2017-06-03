package com.pbmchc.emzetka.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.utils.StringUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleLineMapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    @BindView(R.id.lineNumber) TextView lineTxt;
    @BindView(R.id.lineDirection) TextView directionTxt;

    private List<Stop> stops;
    private String direction;
    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_line_map);
        ButterKnife.bind(this);

        Bundle receivedBundle = getIntent().getExtras();
        String lineName = receivedBundle.getString("line");
        direction = receivedBundle.getString("direction");

        if (getSupportActionBar()!= null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineTxt.setText(StringUtils.fixLineNameForDisplaying(lineName));
        directionTxt.setText(direction);

        stops = receivedBundle.getParcelableArrayList("stops");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        progressBar.setVisibility(View.GONE);
        markers = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(15)
                .color(Color.argb(100, 255, 156, 88))
                .geodesic(true);

        int index = 0;
        for (Stop s : stops){
            if (s.getLongitude() > 0 && s.getLatitude() > 0)
            {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(s.getLatitude(), s.getLongitude()))
                        .title(s.getStopName()));
                if (index == 0)
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.emzetka_marker_start));
                else
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.emzetka_marker));
                marker.setTag(s.getStopLineId());
                markers.add(marker);
                polylineOptions.add(new LatLng(s.getLatitude(), s.getLongitude()));
                index++;
            }
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.info_window_line_map_layout, null);
                TextView stopName = (TextView) v.findViewById(R.id.infoName);
                stopName.setText(arg0.getTitle());
                return v;
            }
        });

        setupMapLayout(googleMap);
        googleMap.addPolyline(polylineOptions);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private void setupMapLayout(GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(false);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels-300;
        int padding = (int) (width * 0.1);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(calculateBounds(), width, height, padding));
    }

    protected LatLngBounds calculateBounds(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        String stopLineId = (String) marker.getTag();
        Intent intent = new Intent(SingleLineMapActivity.this, ScheduleActivity.class);
        Bundle extras = new Bundle();
        Schedule schedule = new Schedule();
        schedule.setDirection(direction);
        schedule.setStopName(marker.getTitle());
        schedule.setScheduleId(stopLineId);
        schedule.setLineNumber(StringUtils.extractLineNumber(stopLineId));
        extras.putParcelable("schedule", schedule);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
