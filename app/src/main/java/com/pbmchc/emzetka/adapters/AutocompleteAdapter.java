package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2017-01-15.
 */
public class AutocompleteAdapter extends ArrayAdapter<Stop> {

    private Context mContext;
    private StopFilter stopFilter;
    private List<Stop> stops;
    private List<Stop> fullStops;

    public AutocompleteAdapter(Context context, int viewResourceId, List<Stop> stopList) {
        super(context, viewResourceId, stopList);
        mContext = context;
        stops = stopList;
        fullStops = new ArrayList<>();
        fullStops.addAll(stops);
    }

    @Override
    public int getCount() {
        return stops.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_stop, null);
            viewHolder = new ViewHolder();

            viewHolder.txt = (TextView) convertView.findViewById(R.id.busStopName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt.setText(stops.get(position).getStopName());
        return convertView;
    }

    public class ViewHolder {
        public TextView txt;
    }

    @Override
    public Filter getFilter(){
        if(stopFilter == null)
            stopFilter = new StopFilter();
        return stopFilter;
    }

    public class StopFilter extends Filter {

        private List<Stop> filteredStops;

        public StopFilter(){
            super();
            filteredStops = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint){

            filteredStops.clear();
            FilterResults results = new FilterResults();

            if (constraint == null)
                filteredStops.addAll(fullStops);
            else{
                String pattern = constraint.toString().toLowerCase().trim();
                for(Stop bs : fullStops){
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
            if(results.count > 0) {
                clear();
                addAll((ArrayList<Stop>) results.values);
                notifyDataSetChanged();
            }
        }
    }
}