package com.pbmchc.emzetka.models;

/**
 * Created by Piotrek on 2017-06-02.
 */
public class UpcomingDeparture extends Departure {

    private int timeLeft;


    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int d_timeLeft) {
        this.timeLeft = d_timeLeft;
    }
}
