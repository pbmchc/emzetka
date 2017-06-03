package com.pbmchc.emzetka.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class Stop implements Parcelable {

    @SerializedName("id_stop_line")
    private String stopLineId;
    @SerializedName("stop_time")
    private String stopTime;
    private Double latitude;
    private Double longitude;
    @SerializedName("name_stop")
    private String stopName;
    @SerializedName("id_stop")
    private String stopId;
    @SerializedName("stop_order")
    private int stopOrder;

    public int getStopOrder() {
        return stopOrder;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopLineId() {
        return stopLineId;
    }

    public String getStopTime() {
        return stopTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getStopId(){
        return stopId;
    }

    @Override
    public String toString(){
        return stopName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(stopLineId);
        parcel.writeString(stopTime);
        parcel.writeString(stopName);
        parcel.writeString(stopId);
        parcel.writeInt(stopOrder);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    protected Stop(Parcel in) {
        stopLineId = in.readString();
        stopTime = in.readString();
        stopName = in.readString();
        stopId = in.readString();
        stopOrder = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Stop> CREATOR =
            new Parcelable.Creator<Stop>() {
                public Stop createFromParcel(Parcel in) {
                    return new Stop(in);
                }

                public Stop[] newArray(int size) {
                    return new Stop[size];
                }
            };
}
