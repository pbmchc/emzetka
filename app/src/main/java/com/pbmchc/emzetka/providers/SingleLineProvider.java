package com.pbmchc.emzetka.providers;

import android.util.Log;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Stop;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleLineProvider {

    public interface OnStopListReceivedListener {
        void onStopListReady(List<Stop> stops);
        void onStopListError();
    }

    OnStopListReceivedListener listener;

    public SingleLineProvider(OnStopListReceivedListener listener) {
        this.listener = listener;
    }

    public void getStopsByLine(String line) {
        Call<List<Stop>> call = BusRestClient.getClient().getBusStopsByLine(line);
        call.enqueue(new Callback<List<Stop>>() {
            @Override
            public void onResponse(Call<List<Stop>> call, Response<List<Stop>> response) {
                listener.onStopListReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Stop>> call, Throwable t) {
                listener.onStopListError();
            }
        });
    }
}
