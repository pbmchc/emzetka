package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Stop;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Piotrek on 2017-06-01.
 */
public class StopsProvider {

    public interface OnStopsReceivedListener {
        void onStopsReady(List<Stop> stops);
        void onStopsError();
    }

    private OnStopsReceivedListener listener;

    public StopsProvider(OnStopsReceivedListener listener){
        this.listener = listener;
    }

    public void getStops() {
        Call<List<Stop>> call = BusRestClient.getClient().getStops();
        call.enqueue(new Callback<List<Stop>>() {
            @Override
            public void onResponse(Call<List<Stop>> call, Response<List<Stop>> response) {
                listener.onStopsReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Stop>> call, Throwable t) {
                listener.onStopsError();
            }
        });
    }
}
