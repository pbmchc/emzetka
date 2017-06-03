package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.utils.ImageUtils;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.List;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class ScheduleItemsAdapter extends RecyclerView.Adapter<ScheduleItemsAdapter.ViewHolder> {

    private List<Stop> stopList;
    private Context mContext;
    private boolean endOfLine;
    private int currentBusStopTime;
    private int callerPosition=-1;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView busStopName;
        public TextView busStopTime;
        public ImageView imageView;
        public TextView minLabel;
        public ImageView clockImage;

        public ViewHolder(final View itemView) {

            super(itemView);

            busStopName = (TextView) itemView.findViewById(R.id.busStopName);
            busStopTime = (TextView) itemView.findViewById(R.id.busStopTime);
            imageView = (ImageView) itemView.findViewById(R.id.imageStop);
            minLabel = (TextView) itemView.findViewById(R.id.minLabel);
            clockImage = (ImageView) itemView.findViewById(R.id.clockImage);
        }
    }

    public ScheduleItemsAdapter(Context context, List<Stop> stops, int currentStopTime, boolean isEndOfLine) {
        stopList = stops;
        mContext = context;
        currentBusStopTime = currentStopTime;
        endOfLine = isEndOfLine;
    }

    public ScheduleItemsAdapter(Context context, List<Stop> stops, int currentStopTime, boolean isEndOfLine, int caller) {
        stopList = stops;
        mContext = context;
        currentBusStopTime = currentStopTime;
        endOfLine = isEndOfLine;
        callerPosition = caller;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ScheduleItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View busStopsView = inflater.inflate(R.layout.item_schedule_stop, parent, false);
        return new ViewHolder(busStopsView);
    }

    @Override
    public void onBindViewHolder(ScheduleItemsAdapter.ViewHolder viewHolder, int position) {
        Stop stop = stopList.get(position);
        TextView stopNameTxt = viewHolder.busStopName;
        stopNameTxt.setText(stop.getStopName());
        TextView stopTimeTxt = viewHolder.busStopTime;

        if(StringUtils.tryParse(stop.getStopTime()) && currentBusStopTime >= 0){
            int timeDifference = Integer.parseInt(stop.getStopTime()) - currentBusStopTime;
            if(timeDifference < 0)
                stopTimeTxt.setText(" - ");
            else
                stopTimeTxt.setText(String.valueOf(timeDifference));
        }
        else
            stopTimeTxt.setText(" - ");

        ImageView im = viewHolder.imageView;
        if(callerPosition>=0){
            ImageView clockImage = viewHolder.clockImage;
            TextView minLabel = viewHolder.minLabel;
            if(stopList.size() > 5 && position==0){
                viewHolder.setIsRecyclable(false);
                ImageUtils.setBorderImageResource(im, position);
            }
            if(position<callerPosition){
                viewHolder.setIsRecyclable(false);
                stopTimeTxt.setVisibility(View.GONE);
                clockImage.setVisibility(View.GONE);
                minLabel.setVisibility(View.GONE);
                stopNameTxt.setTextColor(Color.LTGRAY);
            }
           else if(callerPosition == position){
                viewHolder.setIsRecyclable(false);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.itemView.setBackground(viewHolder.itemView.getResources().getDrawable(R.drawable.chosen_stop_rect));
                    stopNameTxt.setTextColor(Color.WHITE);
                    stopTimeTxt.setTextColor(Color.WHITE);
                    minLabel.setTextColor(Color.WHITE);
                    clockImage.setBackgroundResource(R.drawable.ic_access_time_white_24dp);
                }
            }
        }
        if (position == stopList.size()-1 && endOfLine){
            viewHolder.setIsRecyclable(false);
            ImageUtils.setBorderImageResource(im, position+1);
        }
    }

    @Override
    public int getItemCount() {
        return stopList.size();
    }
}
