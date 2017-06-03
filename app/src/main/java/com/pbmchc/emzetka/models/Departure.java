package com.pbmchc.emzetka.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class Departure {

    @SerializedName("stop_line_id")
    private String stopLineId;
    @SerializedName("d_hours")
    private String departureHour;
    @SerializedName("d_minutes")
    private String departureMinutes;
    @SerializedName("d_day")
    private String departureDay;

    public String getStopLineId() {
        return stopLineId;
    }

    public void setStopLineId(String stop_line_id) {
        this.stopLineId = stop_line_id;
    }

    public String getDepartureHour() {
        return departureHour;
    }

    public String getDepartureMinutes() {
        return departureMinutes;
    }

    public String getDepartureDay() {
        return departureDay;
    }

    public void setDepartureHours(String d_hours) {
        this.departureHour = d_hours;
    }

    public void setDepartureMinutes(String d_minutes) {
        this.departureMinutes = d_minutes;
    }

    @Override
    public String toString(){
        return departureHour + ":" + departureMinutes + " DAY: " + stopLineId + "\n";
    }
}
