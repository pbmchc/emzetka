package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.pbmchc.emzetka.models.UpcomingDeparture;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.models.Departure;
import com.pbmchc.emzetka.utils.StringUtils;
import com.pbmchc.emzetka.utils.DateUtils;

import java.util.List;

/**
 * Created by Piotrek on 2016-12-30.
 */
public class SingleStopDepartureItemAdapter extends RecyclerView.Adapter<SingleStopDepartureItemAdapter.ViewHolder> {

    private List<UpcomingDeparture> dTimes;
    private List<Line> dLines;
    private Context mContext;
    private OnItemClickListener listener;
    private Animation blinkAnimation;

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView hours;
        public TextView lineName;
        public TextView direction;
        public TextView preciseTime;

        public ViewHolder(View itemView) {

            super(itemView);
            hours = (TextView) itemView.findViewById(R.id.hours);
            lineName = (TextView) itemView.findViewById(R.id.lineName);
            direction = (TextView) itemView.findViewById(R.id.direction);
            preciseTime = (TextView) itemView.findViewById(R.id.preciseTime);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    public SingleStopDepartureItemAdapter(Context context, List<UpcomingDeparture> times, List<Line> lines) {
        dTimes = times;
        dLines = lines;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public SingleStopDepartureItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View upcomingDepView = inflater.inflate(R.layout.item_single_stop_departure, parent, false);
        setUpBlinkAnimation();
        return new ViewHolder(upcomingDepView);
    }

    @Override
    public void onBindViewHolder(SingleStopDepartureItemAdapter.ViewHolder viewHolder, int position) {

        TextView hours = viewHolder.hours;
        TextView lineName = viewHolder.lineName;
        TextView lineDirection = viewHolder.direction;
        TextView preciseTime = viewHolder.preciseTime;

        Departure dt = dTimes.get(position);
        Line bl = dLines.get(position);
        int timeLeft = 0;
        String[] times = dt.getDepartureMinutes().split(" ");
        for (String time : times) {
            timeLeft = DateUtils.calculateTimeLeft(time, dt.getDepartureHour());
        }
        if (timeLeft == 0){
            viewHolder.itemView.findViewById(R.id.timeContainer).setBackgroundColor(Color.argb(190, 255, 121, 64));
            hours.setText(mContext.getString(R.string.leaving_now));
            hours.setTextSize(16.0f);
            hours.setAllCaps(true);
            hours.setAnimation(blinkAnimation);
            hours.setTypeface(hours.getTypeface(), Typeface.BOLD);
            viewHolder.itemView.findViewById(R.id.labelHours).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.labelMinutes).setVisibility(View.GONE);
        }
        else if (timeLeft < 0)
            viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0,0));
        else{
            if (timeLeft < 2)
                viewHolder.itemView.findViewById(R.id.timeContainer).setBackgroundColor(Color.argb(190, 255, 121, 64));
            String timeLeftString = String.valueOf(timeLeft);
            hours.setText(timeLeftString);
        }
        lineName.setText(StringUtils.fixLineNameForDisplaying(bl.getLineName()));
        lineDirection.setText(bl.getEndStation());
        String depTime = dt.getDepartureHour() + ":" + dt.getDepartureMinutes();
        preciseTime.setText(depTime);
    }

    @Override
    public int getItemCount() {
        return dTimes.size();
    }

    protected void setUpBlinkAnimation(){
        blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(400);
        blinkAnimation.setStartOffset(20);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
    }

}
