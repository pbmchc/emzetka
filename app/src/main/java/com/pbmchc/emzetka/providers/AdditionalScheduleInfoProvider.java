package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Legend;
import com.pbmchc.emzetka.models.Stop;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalScheduleInfoProvider {

    public interface OnScheduleAdditionalDataReceivedListener {
        void onLegendReady(List<Legend> legends);
        void onRestOfLineReady(List<Stop> stops);
        void onScheduleAdditionalDataError();
    }

    private OnScheduleAdditionalDataReceivedListener listener;

    public AdditionalScheduleInfoProvider(OnScheduleAdditionalDataReceivedListener listener) {
        this.listener = listener;
    }

    public void getLegend(String stopId) {
        Call<List<Legend>> call = BusRestClient.getClient().getLegend(stopId);
        call.enqueue(new Callback<List<Legend>>() {
            @Override
            public void onResponse(Call<List<Legend>> call, Response<List<Legend>> response) {
                listener.onLegendReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Legend>> call, Throwable t) {
                listener.onScheduleAdditionalDataError();
            }
        });
    }

    public void getRestOfLine(String stopId) {
        Call<List<Stop>> busStopCall = BusRestClient.getClient().getRestOfLine(stopId, "from");
        busStopCall.enqueue(new Callback<List<Stop>>() {
            @Override
            public void onResponse(Call<List<Stop>> call, final Response<List<Stop>> response) {
                listener.onRestOfLineReady(response.body());
            }

            @Override
            public void onFailure(Call<List<Stop>> call, Throwable t) {
                listener.onScheduleAdditionalDataError();
            }
        });
    }
}
