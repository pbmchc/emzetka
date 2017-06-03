package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Stop;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Piotrek on 2017-06-01.
 */
public class OppositeStopProvider {

    public interface OnOppositeStopReceived {
        void onOppositeStopFound(String oppositeStopId);
        void onNoOppositeStopFound();
        void onOppositeStopError();
    }

    private OnOppositeStopReceived listener;

    public OppositeStopProvider(OnOppositeStopReceived listener) {
        this.listener = listener;
    }

    public void getOppositeStop(String stopName, String stopId) {
        Call<Stop> call = BusRestClient.getClient().getOppositeStop(stopName, stopId);
        call.enqueue(new Callback<Stop>() {
            @Override
            public void onResponse(Call<Stop> call, Response<Stop> response) {
                if (response.body() != null)
                    listener.onOppositeStopFound(response.body().getStopId());
                else
                    listener.onNoOppositeStopFound();
            }
            @Override
            public void onFailure(Call<Stop> call, Throwable t) {
                listener.onOppositeStopError();
            }
        });
    }
}
