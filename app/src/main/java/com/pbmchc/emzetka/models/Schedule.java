package com.pbmchc.emzetka.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Piotrek on 2017-01-04.
 */
public class Schedule implements Parcelable {

    private String scheduleId;
    private String stopName;
    private String lineNumber;
    private String direction;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(scheduleId);
        parcel.writeString(stopName);
        parcel.writeString(lineNumber);
        parcel.writeString(direction);
    }

    public Schedule(){}

    protected Schedule(Parcel in) {
        scheduleId = in.readString();
        stopName = in.readString();
        lineNumber = in.readString();
        direction = in.readString();
    }

    public static final Parcelable.Creator<Schedule> CREATOR =
            new Parcelable.Creator<Schedule>() {
                public Schedule createFromParcel(Parcel in) {
                    return new Schedule(in);
                }

                public Schedule[] newArray(int size) {
                    return new Schedule[size];
                }
            };
}
