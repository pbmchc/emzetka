package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Schedule;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2017-01-04.
 */
public class FavouritesItemAdapter extends RecyclerView.Adapter<FavouritesItemAdapter.ViewHolder> {

    private List<Schedule> schedules;
    private Context mContext;
    private OnItemClickListener listener;
    private SparseBooleanArray selectedItems;

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
        void onItemLongClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lineNumber;
        public TextView stopName;
        public TextView direction;

        public ViewHolder(View itemView) {
            super(itemView);

            lineNumber = (TextView) itemView.findViewById(R.id.lineName);
            stopName = (TextView) itemView.findViewById(R.id.stopName);
            direction = (TextView) itemView.findViewById(R.id.stopDirection);

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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onItemLongClick(view, position);
                    }
                    return true;
                }
            });
        }
    }

    public FavouritesItemAdapter(Context context, List<Schedule> scheduleList) {
        schedules = scheduleList;
        mContext = context;
        selectedItems = new SparseBooleanArray();
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public FavouritesItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View favouritesView = inflater.inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(favouritesView);
    }

    @Override
    public void onBindViewHolder(FavouritesItemAdapter.ViewHolder viewHolder, int position) {
        Schedule schedule = schedules.get(position);
        TextView lineNameTxt = viewHolder.lineNumber;
        lineNameTxt.setText(schedule.getLineNumber());
        TextView directionTxt = viewHolder.direction;
        directionTxt.setText(schedule.getDirection());
        TextView stopName = viewHolder.stopName;
        stopName.setText(schedule.getStopName());
        viewHolder.itemView.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void deleteItem(int position){
        schedules.remove(position);
        notifyItemRemoved(position);
    }

    public void toggleSelection(int position){
        if (selectedItems.get(position, false))
            selectedItems.delete(position);
        else
            selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public void clearSelections(){
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i = 0; i<selectedItems.size(); i++){
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
}
