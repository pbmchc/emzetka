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
public class LinesProvider {

    public interface OnLinesReceivedListener {
        void onLinesReady(List<Line> lines);
        void onLinesError();
    }

    OnLinesReceivedListener listener;

    public LinesProvider(OnLinesReceivedListener listener) {
        this.listener = listener;
    }

    public void getLines() {
        Call<List<Line>> callBusLines = BusRestClient.getClient().getBusLines();
        callBusLines.enqueue(new Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, Response<List<Line>> response) {
                listener.onLinesReady(response.body());
            }
            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {
                listener.onLinesError();
            }
        });
    }
}
