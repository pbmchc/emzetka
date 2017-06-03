package com.pbmchc.emzetka.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class Line implements Parcelable {

    @SerializedName("id_line")
    private String lineId;
    @SerializedName("f_station")
    private String startStation;
    @SerializedName("l_station")
    private String endStation;
    @SerializedName("name_line")
    private String lineName;


    public String getLineId() {
        return lineId;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public String getLineName() {
        return lineName;
    }

    @Override
    public String toString(){
        return lineId + " " + startStation + "/" + endStation + ", linia" + lineName + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lineId);
        parcel.writeString(startStation);
        parcel.writeString(endStation);
        parcel.writeString(lineName);
    }

    protected Line(Parcel in) {
        lineId = in.readString();
        startStation = in.readString();
        endStation = in.readString();
        lineName = in.readString();
    }

    public static final Parcelable.Creator<Line> CREATOR =
            new Parcelable.Creator<Line>() {
                public Line createFromParcel(Parcel in) {
                    return new Line(in);
                }

                public Line[] newArray(int size) {
                    return new Line[size];
                }
            };
}
