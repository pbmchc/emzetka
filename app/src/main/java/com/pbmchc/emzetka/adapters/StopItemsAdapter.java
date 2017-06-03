package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2016-12-29.
 */
public class StopItemsAdapter extends RecyclerView.Adapter<StopItemsAdapter.ViewHolder> implements Filterable {

    private List<Stop> stopList;
    public List<Stop> filteredStopList;
    private StopFilter stopFilter;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView busStopName;

        public ViewHolder(final View itemView) {
            super(itemView);

            busStopName = (TextView) itemView.findViewById(R.id.busStopName);

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

    public StopItemsAdapter(Context context, List<Stop> stops) {
        stopList = stops;
        filteredStopList = new ArrayList<>();
        filteredStopList.addAll(stops);
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public StopItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View busStopsView = inflater.inflate(R.layout.item_stop, parent, false);
        return new ViewHolder(busStopsView);
    }

    @Override
    public void onBindViewHolder(StopItemsAdapter.ViewHolder viewHolder, int position) {
        Stop stop = filteredStopList.get(position);
        TextView stopNameTxt = viewHolder.busStopName;
        stopNameTxt.setText(stop.getStopName());
    }

    public Stop getItem(int position){
        return filteredStopList.get(position);
    }

    @Override
    public int getItemCount() {
        return filteredStopList.size();
    }

    @Override
    public Filter getFilter(){
        if(stopFilter == null)
            stopFilter = new StopFilter(this);
        return stopFilter;
    }


    public class StopFilter extends Filter {

        private StopItemsAdapter adapter;
        private List<Stop> filteredStops;

        public StopFilter(StopItemsAdapter adapter){
            super();
            this.adapter = adapter;
            filteredStops = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            filteredStops.clear();
            FilterResults results = new FilterResults();

            if (constraint.length() == 0)
                filteredStops.addAll(stopList);
            else{
                String pattern = constraint.toString().toLowerCase().trim();
                for(Stop bs : stopList){
                    if(bs.getStopName().toLowerCase().contains(pattern))
                        filteredStops.add(bs);
                }
            }
            results.values = filteredStops;
            results.count = filteredStops.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            adapter.filteredStopList.clear();
            adapter.filteredStopList.addAll((ArrayList<Stop>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
