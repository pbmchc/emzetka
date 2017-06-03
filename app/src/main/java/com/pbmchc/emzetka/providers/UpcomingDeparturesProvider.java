package com.pbmchc.emzetka.providers;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.models.Departure;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.models.UpcomingDeparture;
import com.pbmchc.emzetka.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpcomingDeparturesProvider {


    public interface OnUpcomingDeparturesReceivedListener {
        void onUpcomingDeparturesReady(List<UpcomingDeparture> departures, List<Line> lines);
        void onNoUpcomingDeparturesAvailable();
        void onUpcomingDeparturesError();
    }

    private OnUpcomingDeparturesReceivedListener listener;
    private Call<List<Departure>> call;
    private Call<List<Line>> innerCall;

    public UpcomingDeparturesProvider(OnUpcomingDeparturesReceivedListener listener) {
        this.listener = listener;
    }

    public void getUpcomingDepartures(String stop, String hoursFrom, String hoursTo, int day) {
        call = BusRestClient.getClient()
                .getClosestDepartures(stop, hoursFrom, hoursTo, day);
        call.enqueue(new Callback<List<Departure>>() {
            @Override
            public void onResponse(Call<List<Departure>> call, Response<List<Departure>> response) {
                Date date = new Date();
                List <UpcomingDeparture> cleansedDepartures = new ArrayList<>();
                if (response.body() == null || response.body().toString().equals("[]"))
                    listener.onNoUpcomingDeparturesAvailable();
                else {
                    for (Departure d : response.body()) {
                        String[] times = d.getDepartureMinutes().split(" ");
                        for (String time : times) {
                            int minutes, hours;
                            if (!time.equals("")) {
                                String shortTime = time.substring(0, 2);
                                minutes = DateUtils.parseTimeString(shortTime);
                                hours = DateUtils.parseTimeString(d.getDepartureHour());
                                if (hours == date.getHours()) {
                                    if(date.getMinutes() <= minutes)
                                        cleansedDepartures.add(createCleansedDeparture(d, time, minutes, date));
                                } else
                                    cleansedDepartures.add(createCleansedDeparture(d, time, minutes+60, date));
                            }
                        }
                    }
                    if (cleansedDepartures.size() > 0) {
                        sortDepartures(cleansedDepartures);
                        getCorrespondingLines(cleansedDepartures);
                    }
                    else
                        listener.onNoUpcomingDeparturesAvailable();
                }
            }
            @Override
            public void onFailure(Call<List<Departure>> call, Throwable t) {
                if (!call.isCanceled())
                    listener.onUpcomingDeparturesError();
            }
        });
    }

    private void getCorrespondingLines(final List<UpcomingDeparture> departures) {
        innerCall = BusRestClient.getClient().getBusLines();
        innerCall.enqueue(new Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, Response<List<Line>> response) {
                List<Line> cleansedLines = new ArrayList<>();
                for (UpcomingDeparture d : departures) {
                    String lineId = d.getStopLineId().substring(7);
                    for(Line l : response.body()){
                        if (lineId.equals(l.getLineId()))
                            cleansedLines.add(l);
                    }
                }
                listener.onUpcomingDeparturesReady(departures, cleansedLines);
            }
            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {
                if (!innerCall.isCanceled())
                    listener.onUpcomingDeparturesError();
            }
        });
    }

    private UpcomingDeparture createCleansedDeparture(Departure dt, String time, int minutes, Date date){
        UpcomingDeparture newTime = new UpcomingDeparture();
        newTime.setDepartureHours(dt.getDepartureHour());
        newTime.setDepartureMinutes(time);
        newTime.setTimeLeft(minutes - date.getMinutes());
        newTime.setStopLineId(dt.getStopLineId());
        return newTime;
    }

    private void sortDepartures(List<UpcomingDeparture> departures) {
        Collections.sort(departures, new Comparator<UpcomingDeparture>() {
            @Override
            public int compare(final UpcomingDeparture object1, final UpcomingDeparture object2) {
                if (object1.getTimeLeft() == object2.getTimeLeft())
                    return 0;
                return object1.getTimeLeft() < object2.getTimeLeft() ? -1 : 1;
            }
        });
    }

    public void cancelOngoingRequests() {
        call.cancel();
        if (innerCall != null)
            innerCall.cancel();
    }

}
