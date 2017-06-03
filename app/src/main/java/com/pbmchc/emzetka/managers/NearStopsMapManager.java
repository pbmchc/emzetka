package com.pbmchc.emzetka.managers;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.pbmchc.emzetka.models.Coordinate;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.providers.NearbyStopsProvider;

import java.util.ArrayList;
import java.util.List;


public class NearStopsMapManager implements GoogleMap.OnInfoWindowClickListener,
        NearbyStopsProvider.OnNearbyStopsReceivedListener{

    private static final LatLng DEFAULT_START = new LatLng(53.69440024, 17.5569252);
    private static final float DEFAULT_ZOOM = 14.0F;
    private static final double DISTANCE_FROM_USER = 650;

    private NearbyStopsProvider provider;
    private Activity mContext;
    private GoogleMap nearStopsMap;
    private List<Marker> markers;
    private LatLng center;
    private Marker userMarker;
    private OnMapReadyListener listener;

    public NearStopsMapManager(Activity context, GoogleMap map) {
        mContext = context;
        provider = new NearbyStopsProvider(this);
        listener = (OnMapReadyListener) context;
        nearStopsMap = map;
        nearStopsMap.getUiSettings().setMapToolbarEnabled(false);
        nearStopsMap.setOnInfoWindowClickListener(this);
        nearStopsMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .zoom(DEFAULT_ZOOM).target(DEFAULT_START).build()));
    }

    public void getNearStops(LatLng center){
        this.center = center;
        Coordinate coordinate = new Coordinate();
        coordinate.setNorth(SphericalUtil.computeOffset(center, DISTANCE_FROM_USER, 0).latitude);
        coordinate.setEast(SphericalUtil.computeOffset(center, DISTANCE_FROM_USER, 90).longitude);
        coordinate.setSouth(SphericalUtil.computeOffset(center, DISTANCE_FROM_USER, 180).latitude);
        coordinate.setWest(SphericalUtil.computeOffset(center, DISTANCE_FROM_USER, 270).longitude);
        provider.getNearbyStops(coordinate);
    }

    public void setCameraPosition(List<Stop> stops){
        if (stops.size() >= 1) {
            double multiplier = stops.size() == 1 ? 0.3 : 0.05;
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int height = mContext.getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * multiplier);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(calculateBounds(), width, height, padding);
            nearStopsMap.animateCamera(cameraUpdate);
        }
        else
            nearStopsMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(CameraPosition.builder().zoom(14).target(center).build()));
    }

    private LatLngBounds calculateBounds(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        builder.include(center);
        return builder.build();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        listener.onInfoWindowClicked(marker);
    }

    @Override
    public void onNearbyStopsReady(List<Stop> nearbyStops) {
        setupNearbyStopsMap(nearbyStops);
        listener.onMapContentReady(nearbyStops.size());
        setCameraPosition(nearbyStops);
    }

    @Override
    public void onNearbyStopsError() {
        listener.onMapContentError();
    }

    private void setupNearbyStopsMap(List<Stop> nearbyStops) {

        nearStopsMap.clear();
        markers = new ArrayList<>();

        userMarker = nearStopsMap.addMarker(new MarkerOptions()
                .position(center)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_static)));

        if (nearbyStops != null) {
            for (Stop stop : nearbyStops) {
                if (stop.getLongitude() > 0 && stop.getLatitude() > 0) {
                    double distance = SphericalUtil.computeDistanceBetween(center,
                            new LatLng(stop.getLatitude(), stop.getLongitude()));
                    String description = "Odległość:  " + String.valueOf((int) Math.floor(distance)) + "m";
                    Marker marker = nearStopsMap.addMarker(new MarkerOptions()
                            .position(new LatLng(stop.getLatitude(), stop.getLongitude()))
                            .title(stop.getStopName())
                            .snippet(description));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.emzetka_marker));
                    marker.setTag(stop.getStopId());
                    markers.add(marker);
                }
            }
            nearStopsMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }
                @Override
                public View getInfoContents(Marker arg0) {
                    View v = mContext.getLayoutInflater().inflate(R.layout.info_window_near_stops_map_layout, null);
                    TextView stopName = (TextView) v.findViewById(R.id.infoName);
                    TextView stopDist = (TextView) v.findViewById(R.id.infoDistance);
                    stopName.setText(arg0.getTitle());
                    stopDist.setText(arg0.getSnippet());
                    return v;
                }
            });
        }
        else {
            nearStopsMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            nearStopsMap.animateCamera(CameraUpdateFactory.newLatLng(center));
        }
    }

    public void updateUserMarkerPosition(LatLng newPosition) {
        if (userMarker == null)
            getNearStops(newPosition);
        else
            userMarker.setPosition(newPosition);
    }

    public interface OnMapReadyListener {
        void onMapContentReady(int stopCount);
        void onInfoWindowClicked(Marker marker);
        void onMapContentError();
    }
}
