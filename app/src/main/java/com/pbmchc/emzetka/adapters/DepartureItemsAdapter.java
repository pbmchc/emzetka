package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pbmchc.emzetka.activities.ScheduleDialogActivity;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Departure;
import com.pbmchc.emzetka.models.Legend;

import java.util.List;

/**
 * Created by Piotrek on 2016-12-18.
 */
public class DepartureItemsAdapter extends RecyclerView.Adapter<DepartureItemsAdapter.ViewHolder> {

    private List<Departure> departureList;
    private Context mContext;
    private List<Legend> legends;
    private String upcomingHours;
    private String upcomingMinutes;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView hoursText;
        public TextView minutesText;
        public LinearLayout timeContainer;
        public FrameLayout hourContainer;

        public ViewHolder (final View itemView){
            super(itemView);
            hoursText = (TextView) itemView.findViewById(R.id.hours);
            minutesText = (TextView) itemView.findViewById(R.id.minutes);
            timeContainer = (LinearLayout) itemView.findViewById(R.id.departureLinear);
            hourContainer = (FrameLayout) itemView.findViewById(R.id.hourFrame);
        }
    }
    
    public DepartureItemsAdapter(Context context, List<Departure> departures, List<Legend> legendsList){
        mContext = context;
        departureList = departures;
        legends = legendsList;
    }

    public DepartureItemsAdapter(Context context, List<Departure> departures, List<Legend> legendsList, String hours, String minutes){
        mContext = context;
        departureList = departures;
        legends = legendsList;
        upcomingHours = hours;
        upcomingMinutes = minutes;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public DepartureItemsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View departuresView = inflater.inflate(R.layout.item_departure, parent, false);
        return new ViewHolder(departuresView);
    }

    @Override
    public void onBindViewHolder(DepartureItemsAdapter.ViewHolder viewHolder, int position) {

        Departure departure = departureList.get(position);
        LinearLayout timeContainer = viewHolder.timeContainer;
        FrameLayout hourContainer = viewHolder.hourContainer;
        TextView hoursTextView = viewHolder.hoursText;
        if(position % 2 != 0) {
            timeContainer.setBackgroundColor(mContext.getResources().getColor(R.color.departureGray));
            hourContainer.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }
        TextView minutesTextView = viewHolder.minutesText;
        hoursTextView.setText(departure.getDepartureHour());
        minutesTextView.setText("");

        String [] records = departure.getDepartureMinutes().split(" ");

        for (String record : records){
            minutesTextView.append(createDepartureSpan(record, departure));
            minutesTextView.append("   ");
        }
        minutesTextView.setMovementMethod(new LinkMovementMethod());}

    @Override
    public int getItemCount() {
        return departureList.size();
    }

    public SpannableString createDepartureSpan(final String record,
                                               final Departure departure){
        SpannableString departureSpan = new SpannableString(record);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(mContext, ScheduleDialogActivity.class);
                intent.putExtra("hours", departure.getDepartureHour());
                intent.putExtra("minutes", record);
                intent.putExtra("legend", createDepartureLegend(record));
                mContext.startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                if (upcomingHours != null && upcomingHours.equals(departure.getDepartureHour()) && upcomingMinutes.equals(record)){
                    ds.bgColor = Color.argb(30, 255, 121, 64);
                    ds.setFakeBoldText(true);
                }
                else
                    ds.setColor(Color.DKGRAY);
            }
        };
        departureSpan.setSpan(clickableSpan, 0, departureSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return departureSpan;
    }

    public String createDepartureLegend(String record){
        String chosenLegend = "";
        String legend = record.replaceAll("\\d+" , "").toLowerCase();
        if (legends != null) {
            for(Legend l : legends){
                if (legend.contains(l.getSymbol().toLowerCase())){
                    chosenLegend += l.toString();
                }
            }
        }
        return chosenLegend;
    }
}
