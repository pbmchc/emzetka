package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Line;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Piotrek on 2017-06-01.
 */
public class AvailableLinesProvider {

    public interface OnAvailableLinesReceivedListener {
        void onAvailableLinesReady(List<Line> lines);
        void onAvailableLinesError();
    }

    OnAvailableLinesReceivedListener listener;
    private Call<List<Line>> call;

    public AvailableLinesProvider(OnAvailableLinesReceivedListener listener) {
        this.listener = listener;
    }

    public void getAvailableLines(String stop, boolean isDestination) {
        String destination = isDestination ? "1" : "0";
        call = BusRestClient.getClient().getLinesByStop(stop, destination);
        call.enqueue(new Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, Response<List<Line>> response) {
                listener.onAvailableLinesReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {
                if (!call.isCanceled())
                    listener.onAvailableLinesError();
            }
        });
    }

    public void cancelOngoingRequest() {
        call.cancel();
    }
}
