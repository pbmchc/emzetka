package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Coordinate;
import com.pbmchc.emzetka.models.Stop;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Piotrek on 2017-06-01.
 */
public class NearbyStopsProvider {

    public interface OnNearbyStopsReceivedListener {
        void onNearbyStopsReady(List<Stop> nearbyStops);
        void onNearbyStopsError();
    }

    private OnNearbyStopsReceivedListener listener;

    public NearbyStopsProvider(OnNearbyStopsReceivedListener listener) {
        this.listener = listener;
    }

    public void getNearbyStops(Coordinate coordinate) {
        Call<List<Stop>> call = BusRestClient.getClient().getNearbyStops(
                coordinate.getSouth(), coordinate.getNorth(),
                coordinate.getWest(), coordinate.getEast());
        call.enqueue(new Callback<List<Stop>>() {
            @Override
            public void onResponse(Call<List<Stop>> call, Response<List<Stop>> response) {
                    listener.onNearbyStopsReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Stop>> call, Throwable t) {
                listener.onNearbyStopsError();
            }
        });
    }
}
