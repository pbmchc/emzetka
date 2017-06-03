package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Departure;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeparturesProvider {

    public interface OnDeparturesReceivedListener {
        void onDeparturesReady(List<Departure> departures);
        void onNoDeparturesAvailable();
        void onDeparturesError();
    }

    private OnDeparturesReceivedListener listener;
    private Call<List<Departure>> call;

    public DeparturesProvider(OnDeparturesReceivedListener listener) {
        this.listener = listener;
    }

    public void getDepartures(String stopId, int day) {
        call = BusRestClient.getClient().getDepartures(stopId, day);
        call.enqueue(new Callback<List<Departure>>() {
            @Override
            public void onResponse(Call<List<Departure>> call, Response<List<Departure>> response) {
                if(response.body() == null || response.body().toString().equals("[]"))
                    listener.onNoDeparturesAvailable();
                else
                    listener.onDeparturesReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Departure>> call, Throwable t) {
                if (!call.isCanceled())
                    listener.onDeparturesError();
            }
        });
    }

    public void cancelOngoingRequest() {
        call.cancel();
    }
}
