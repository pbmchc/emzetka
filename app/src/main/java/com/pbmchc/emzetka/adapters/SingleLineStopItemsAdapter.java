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
public class SingleLineStopItemsAdapter extends RecyclerView.Adapter<SingleLineStopItemsAdapter.ViewHolder> {

    private List<Stop> stopList;
    private Context mContext;
    private OnItemClickListener listener;
    private int callerPosition=-1;

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

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

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    public SingleLineStopItemsAdapter(Context context, List<Stop> stops) {
        stopList = stops;
        mContext = context;
    }

    public SingleLineStopItemsAdapter(Context context, List<Stop> stops, int caller){
        stopList = stops;
        mContext = context;
        callerPosition = caller;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public SingleLineStopItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View busStopsView = inflater.inflate(R.layout.item_single_line_stop, parent, false);
        return new ViewHolder(busStopsView);
    }

    @Override
    public void onBindViewHolder(SingleLineStopItemsAdapter.ViewHolder viewHolder, int position) {

        Stop stop = stopList.get(position);
        TextView stopNameTxt = viewHolder.busStopName;
        stopNameTxt.setText(stop.getStopName());
        TextView stopTimeTxt = viewHolder.busStopTime;
        stopTimeTxt.setText(StringUtils.fixStopTimeForDisplaying(stop.getStopTime()));

        ImageView im = viewHolder.imageView;
        if (position==0 || position == stopList.size()-1) {
            viewHolder.setIsRecyclable(false);
            ImageUtils.setBorderImageResource(im, position);
        }

        if(callerPosition != -1 && stop.getStopOrder() == callerPosition){
            viewHolder.setIsRecyclable(false);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.itemView.setBackground(viewHolder.itemView.getResources().getDrawable(R.drawable.chosen_stop_rect));
                stopNameTxt.setTextColor(Color.WHITE);
                stopTimeTxt.setTextColor(Color.WHITE);
                TextView minLabel = viewHolder.minLabel;
                minLabel.setTextColor(Color.WHITE);
                ImageView clockImage = viewHolder.clockImage;
                clockImage.setBackgroundResource(R.drawable.ic_access_time_white_24dp);
            }
        }
    }
    @Override
    public int getItemCount() {
        return stopList.size();
    }
}
